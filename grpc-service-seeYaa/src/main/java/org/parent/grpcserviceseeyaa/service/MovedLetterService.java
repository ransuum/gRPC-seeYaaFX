package org.parent.grpcserviceseeyaa.service;

import com.seeYaa.proto.email.TypeOfLetter;
import com.seeYaa.proto.email.service.movedletter.EmailRequest;
import com.seeYaa.proto.email.service.movedletter.LetterList;
import com.seeYaa.proto.email.service.movedletter.MovedLetterServiceGrpc;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.parent.grpcserviceseeyaa.entity.MovedLetter;
import org.parent.grpcserviceseeyaa.entity.Users;
import org.parent.grpcserviceseeyaa.exception.NotFoundException;
import org.parent.grpcserviceseeyaa.mapper.LetterMapper;
import org.parent.grpcserviceseeyaa.repository.MovedLetterRepository;
import org.parent.grpcserviceseeyaa.repository.UserRepository;
import org.springframework.grpc.server.service.GrpcService;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class MovedLetterService extends MovedLetterServiceGrpc.MovedLetterServiceImplBase {
    private final MovedLetterRepository movedLetterRepo;
    private final UserRepository usersRepo;

    @Override
    @Transactional(readOnly = true)
    public void getSpamLetters(EmailRequest request, StreamObserver<LetterList> responseObserver) {
        try {
            final var letters = usersRepo.findByEmail(request.getEmail())
                    .map(movedBy -> movedLetterRepo.findAllByMovedByAndTypeOfLetter(movedBy, TypeOfLetter.SPAM)
                            .stream()
                            .map(MovedLetter::getLetter)
                            .map(LetterMapper.INSTANCE::toLetterProto)
                            .toList())
                    .orElseThrow(() -> new NotFoundException("User not found id=" + request.getEmail()));

            responseObserver.onNext(LetterList.newBuilder().addAllLetters(letters).build());
            responseObserver.onCompleted();
        } catch (Exception ex) {
            log.error("Error sending letter", ex);
            responseObserver.onError(
                    Status.INTERNAL
                            .withDescription(ex.getMessage())
                            .withCause(ex)
                            .asRuntimeException());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public void getGarbageLetters(EmailRequest request, StreamObserver<LetterList> responseObserver) {
        final var letters = usersRepo.findByEmail(request.getEmail())
                .map(movedBy -> movedLetterRepo.findAllByMovedByAndTypeOfLetter(movedBy, TypeOfLetter.GARBAGE)
                        .stream()
                        .map(MovedLetter::getLetter)
                        .map(LetterMapper.INSTANCE::toLetterProto)
                        .toList())
                .orElseThrow(() ->
                        Status.NOT_FOUND
                                .withDescription("User not found id=" + request.getEmail())
                                .asRuntimeException());

        responseObserver.onNext(LetterList.newBuilder().addAllLetters(letters).build());
        responseObserver.onCompleted();
    }

    @Override
    @Transactional(readOnly = true)
    public void getSentLetters(EmailRequest request, StreamObserver<LetterList> responseObserver) {
        try {
            final var user = usersRepo.findByEmail(request.getEmail())
                    .orElseThrow(() -> new NotFoundException("User not found id=" + request.getEmail()));

            final var lettersProto = user.getSendLetters()
                    .stream()
                    .map(LetterMapper.INSTANCE::toLetterProto)
                    .toList();
            responseObserver.onNext(LetterList.newBuilder().addAllLetters(lettersProto).build());
            responseObserver.onCompleted();
        } catch (Exception ex) {
            log.error("Error sending letter", ex);
            responseObserver.onError(
                    Status.INTERNAL
                            .withDescription(ex.getMessage())
                            .withCause(ex)
                            .asRuntimeException()
            );
        }
    }

    @Override
    @Transactional(readOnly = true)
    public void getInboxLetters(EmailRequest request, StreamObserver<LetterList> responseObserver) {
        final var letters = usersRepo.findByEmail(request.getEmail())
                .map(Users::getMyLetters)
                .orElseThrow(() ->
                        Status.NOT_FOUND
                                .withDescription("User not found id=" + request.getEmail())
                                .asRuntimeException());

        final var lettersProto = letters.stream()
                .map(LetterMapper.INSTANCE::toLetterProto)
                .toList();
        responseObserver.onNext(LetterList.newBuilder().addAllLetters(lettersProto).build());
        responseObserver.onCompleted();
    }
}