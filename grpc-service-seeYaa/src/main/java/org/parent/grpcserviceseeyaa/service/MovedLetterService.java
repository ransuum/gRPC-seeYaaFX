package org.parent.grpcserviceseeyaa.service;

import com.seeYaa.proto.email.TypeOfLetter;
import com.seeYaa.proto.email.service.movedletter.EmailRequest;
import com.seeYaa.proto.email.service.movedletter.MovedLetterList;
import com.seeYaa.proto.email.service.movedletter.MovedLetterServiceGrpc;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import org.parent.grpcserviceseeyaa.mapper.MovedLetterMapper;
import org.parent.grpcserviceseeyaa.repository.MovedLetterRepository;
import org.parent.grpcserviceseeyaa.repository.UserRepository;
import org.springframework.grpc.server.service.GrpcService;
import org.springframework.security.access.prepost.PreAuthorize;

@GrpcService
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_USER')")
public class MovedLetterService extends MovedLetterServiceGrpc.MovedLetterServiceImplBase {
    private final MovedLetterRepository movedLetterRepo;
    private final UserRepository usersRepo;

    @Override
    public void getSpamLetters(EmailRequest request, StreamObserver<MovedLetterList> responseObserver) {
        final var letters = usersRepo.findByEmail(request.getEmail())
                .map(movedBy -> movedLetterRepo.findAllByMovedByAndTypeOfLetter(movedBy, TypeOfLetter.SPAM)
                        .stream()
                        .map(MovedLetterMapper.INSTANCE::toMovedLetterProto)
                        .toList())
                .orElseThrow(() ->
                        Status.NOT_FOUND
                                .withDescription("User not found id=" + request.getEmail())
                                .asRuntimeException());

        responseObserver.onNext(MovedLetterList.newBuilder().addAllLetters(letters).build());
        responseObserver.onCompleted();
    }

    @Override
    public void getGarbageLetters(EmailRequest request, StreamObserver<MovedLetterList> responseObserver) {
        final var letters = usersRepo.findByEmail(request.getEmail())
                .map(movedBy -> movedLetterRepo.findAllByMovedByAndTypeOfLetter(movedBy, TypeOfLetter.GARBAGE)
                        .stream()
                        .map(MovedLetterMapper.INSTANCE::toMovedLetterProto)
                        .toList())
                .orElseThrow(() ->
                        Status.NOT_FOUND
                                .withDescription("User not found id=" + request.getEmail())
                                .asRuntimeException());

        responseObserver.onNext(MovedLetterList.newBuilder().addAllLetters(letters).build());
        responseObserver.onCompleted();
    }

    @Override
    public void getSentLetters(EmailRequest request, StreamObserver<MovedLetterList> responseObserver) {
        final var letters = usersRepo.findByEmail(request.getEmail())
                .map(movedBy -> movedLetterRepo.findAllByMovedByAndTypeOfLetter(movedBy, TypeOfLetter.SENT)
                        .stream()
                        .map(MovedLetterMapper.INSTANCE::toMovedLetterProto)
                        .toList())
                .orElseThrow(() ->
                        Status.NOT_FOUND
                                .withDescription("User not found id=" + request.getEmail())
                                .asRuntimeException());

        responseObserver.onNext(MovedLetterList.newBuilder().addAllLetters(letters).build());
        responseObserver.onCompleted();
    }

    @Override
    public void getInboxLetters(EmailRequest request, StreamObserver<MovedLetterList> responseObserver) {
        final var letters = usersRepo.findByEmail(request.getEmail())
                .map(movedBy -> movedLetterRepo.findAllByMovedByAndTypeOfLetter(movedBy, TypeOfLetter.INBOXES)
                        .stream()
                        .map(MovedLetterMapper.INSTANCE::toMovedLetterProto)
                        .toList())
                .orElseThrow(() ->
                        Status.NOT_FOUND
                                .withDescription("User not found id=" + request.getEmail())
                                .asRuntimeException());

        responseObserver.onNext(MovedLetterList.newBuilder().addAllLetters(letters).build());
        responseObserver.onCompleted();
    }
}