package org.parent.grpcserviceseeyaa.service;

import com.google.protobuf.Empty;
import com.seeYaa.proto.email.Letter;
import com.seeYaa.proto.email.TypeOfLetter;
import com.seeYaa.proto.email.service.letter.*;
import com.seeyaa.proto.email.configuration.movedletter.SetLetterTypeRequest;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import org.parent.grpcserviceseeyaa.configuration.letter.MovedLetterConfigurationImpl;
import org.parent.grpcserviceseeyaa.exception.NotFoundException;
import org.parent.grpcserviceseeyaa.mapper.LetterMapper;
import org.parent.grpcserviceseeyaa.repository.LetterRepository;
import org.parent.grpcserviceseeyaa.repository.UserRepository;
import org.parent.grpcserviceseeyaa.util.time.TimeProtoUtil;
import org.springframework.grpc.server.service.GrpcService;
import org.springframework.security.access.prepost.PreAuthorize;

import java.time.LocalDateTime;

@GrpcService
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_USER')")
public class LetterService extends LetterServiceGrpc.LetterServiceImplBase {
    private final MovedLetterConfigurationImpl movedLetterConfiguration;
    private final LetterRepository letterRepository;
    private final UserRepository userRepository;

    @Override
    public void sendLetter(LetterRequest request, StreamObserver<Letter> responseObserver) {
        try {
            final var usersBy = userRepository.findByEmail(request.getUserByEmail())
                    .orElseThrow(() -> new NotFoundException("User not found"));
            final var letter = userRepository.findByEmail(request.getUserToEmail())
                    .map(userTo -> letterRepository.save(
                            org.parent.grpcserviceseeyaa.entity.Letter.builder()
                                    .userBy(usersBy)
                                    .userTo(userTo)
                                    .text(request.getText())
                                    .topic(request.getTopic())
                                    .activeLetter(Boolean.TRUE)
                                    .createdAt(TimeProtoUtil.toProto(LocalDateTime.now()))
                                    .build())
                    ).orElseThrow(() -> new NotFoundException("User not found"));

            responseObserver.onNext(LetterMapper.INSTANCE.toLetterProto(letter));
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(
                    io.grpc.Status.INTERNAL
                            .withDescription("Unexpected server error ")
                            .withCause(e)
                            .asRuntimeException());
        }
    }

    @Override
    public void setLetterToSpam(LetterIdEmailRequest request, StreamObserver<Empty> responseObserver) {
        movedLetterConfiguration.setLetterType(SetLetterTypeRequest.newBuilder()
                .setLetterId(request.getLetterId())
                .setEmail(request.getEmail())
                .setType(TypeOfLetter.SPAM)
                .build(), responseObserver);

        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }

    @Override
    public void setLetterToGarbage(LetterIdEmailRequest request, StreamObserver<Empty> responseObserver) {
        movedLetterConfiguration.setLetterType(SetLetterTypeRequest.newBuilder()
                .setLetterId(request.getLetterId())
                .setEmail(request.getEmail())
                .setType(TypeOfLetter.GARBAGE)
                .build(), responseObserver);

        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }

    @Override
    public void findById(LetterIdRequest request, StreamObserver<LetterWithAnswers> responseObserver) {
        try {
            final var letter = letterRepository.findById(request.getId())
                    .orElseThrow(() -> new NotFoundException("No letter found with id: " + request.getId()));

            responseObserver.onNext(LetterMapper.INSTANCE.toLetterWithAnswersProto(letter));
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(
                    io.grpc.Status.INTERNAL
                            .withDescription("Unexpected server error ")
                            .withCause(e)
                            .asRuntimeException());
        }

    }

    @Override
    public void findAllSentByTopic(TopicSearchRequestBy request, StreamObserver<LetterList> responseObserver) {
        final var allByTopicContainingAndUserBy = letterRepository.findAllByTopicContainingAndUserBy(
                request.getTopic(),
                userRepository.findByEmail(request.getUserByEmail()).orElse(null))
                .stream()
                .map(LetterMapper.INSTANCE::toLetterProto)
                .toList();

        responseObserver.onNext(LetterList.newBuilder()
                .addAllLetters(allByTopicContainingAndUserBy)
                .build());
        responseObserver.onCompleted();
    }

    @Override
    public void findAllInboxByTopic(TopicSearchRequestTo request, StreamObserver<LetterList> responseObserver) {
        final var allByTopicContainingAndUserBy = letterRepository.findAllByTopicContainingAndUserTo(
                        request.getTopic(),
                        userRepository.findByEmail(request.getUserToEmail()).orElse(null))
                .stream()
                .map(LetterMapper.INSTANCE::toLetterProto)
                .toList();

        responseObserver.onNext(LetterList.newBuilder()
                .addAllLetters(allByTopicContainingAndUserBy)
                .build());
        responseObserver.onCompleted();
    }

    @Override
    public void deleteLetterById(LetterIdRequest request, StreamObserver<Empty> responseObserver) {
        letterRepository.findById(request.getId()).ifPresent(letterRepository::delete);

        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }
}
