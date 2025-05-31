package org.parent.grpcserviceseeyaa.service;

import com.seeYaa.proto.email.TypeOfLetter;
import com.seeYaa.proto.email.service.movedletter.EmailRequest;
import com.seeYaa.proto.email.service.movedletter.LetterList;
import com.seeYaa.proto.email.service.movedletter.MovedLetterServiceGrpc;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.parent.grpcserviceseeyaa.mapper.LetterMapper;
import org.parent.grpcserviceseeyaa.repository.LetterRepository;
import org.springframework.grpc.server.service.GrpcService;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class MovedLetterService extends MovedLetterServiceGrpc.MovedLetterServiceImplBase {
    private final LetterRepository letterRepository;

    @Override
    @Transactional(readOnly = true)
    public void getSpamLetters(EmailRequest request, StreamObserver<LetterList> responseObserver) {
        final var letters = letterRepository.findAllLettersMovedByUser(request.getEmail(), TypeOfLetter.SPAM)
                .stream()
                .map(LetterMapper.INSTANCE::toLetterProto)
                .toList();

        responseObserver.onNext(LetterList.newBuilder().addAllLetters(letters).build());
        responseObserver.onCompleted();

    }

    @Override
    @Transactional(readOnly = true)
    public void getGarbageLetters(EmailRequest request, StreamObserver<LetterList> responseObserver) {
        final var letters = letterRepository.findAllLettersMovedByUser(request.getEmail(), TypeOfLetter.GARBAGE)
                .stream()
                .map(LetterMapper.INSTANCE::toLetterProto)
                .toList();

        responseObserver.onNext(LetterList.newBuilder().addAllLetters(letters).build());
        responseObserver.onCompleted();
    }

    @Override
    @Transactional(readOnly = true)
    public void getSentLetters(EmailRequest request, StreamObserver<LetterList> responseObserver) {
        final var letters = letterRepository.findSentActiveByUser(request.getEmail())
                .stream()
                .map(LetterMapper.INSTANCE::toLetterProto)
                .toList();
        responseObserver.onNext(LetterList.newBuilder().addAllLetters(letters).build());
        responseObserver.onCompleted();
    }

    @Override
    @Transactional(readOnly = true)
    public void getInboxLetters(EmailRequest request, StreamObserver<LetterList> responseObserver) {
        final var letters = letterRepository.findInboxActiveByUser(request.getEmail())
                .stream()
                .map(LetterMapper.INSTANCE::toLetterProto)
                .toList();
        responseObserver.onNext(LetterList.newBuilder().addAllLetters(letters).build());
        responseObserver.onCompleted();
    }
}