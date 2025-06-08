package org.parent.grpcserviceseeyaa.service;

import com.google.protobuf.Empty;
import com.seeYaa.proto.email.Letter;
import com.seeYaa.proto.email.TypeOfLetter;
import com.seeYaa.proto.email.configuration.movedletter.SetLetterTypeRequest;
import com.seeYaa.proto.email.service.letter.*;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.parent.grpcserviceseeyaa.configuration.letter.MovedLetterConfigurationImpl;
import org.parent.grpcserviceseeyaa.mapper.LetterMapper;
import org.parent.grpcserviceseeyaa.repository.LetterRepository;
import org.parent.grpcserviceseeyaa.repository.UserRepository;
import org.parent.grpcserviceseeyaa.security.SecurityService;
import org.springframework.grpc.server.service.GrpcService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class LetterService extends LetterServiceGrpc.LetterServiceImplBase {
    private final MovedLetterConfigurationImpl movedLetterConfiguration;
    private final LetterRepository letterRepository;
    private final UserRepository userRepository;
    private final SecurityService securityService;

    @Override
    @PreAuthorize("hasRole('ROLE_USER')")
    public void sendLetter(LetterRequest request, StreamObserver<Letter> responseObserver) {
        final var usersBy = userRepository.findByEmail(request.getUserByEmail())
                .orElseThrow(() -> Status.NOT_FOUND
                        .withDescription("You are not logged in")
                        .asRuntimeException());
        final var letter = userRepository.findByEmail(request.getUserToEmail())
                .map(userTo -> letterRepository.save(
                        org.parent.grpcserviceseeyaa.entity.Letter.builder()
                                .userBy(usersBy)
                                .userTo(userTo)
                                .text(request.getText())
                                .topic(request.getTopic())
                                .activeLetter(Boolean.TRUE)
                                .createdAt(LocalDateTime.now())
                                .deleteTime(LocalDateTime.now())
                                .watched(Boolean.FALSE)
                                .build())
                ).orElseThrow(() -> Status.NOT_FOUND
                        .withDescription("User (to) not found")
                        .asRuntimeException());

        final var letterProto = LetterMapper.INSTANCE.toLetterProto(letter);
        responseObserver.onNext(letterProto);
        responseObserver.onCompleted();
    }

    @Override
    @PreAuthorize("hasRole('ROLE_USER')")
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
    @PreAuthorize("hasRole('ROLE_USER')")
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
    @Transactional
    @PreAuthorize("hasRole('ROLE_USER')")
    public void findById(LetterIdRequest request, StreamObserver<Letter> responseObserver) {
        var letter = letterRepository.findById(request.getId())
                .orElseThrow(() -> Status.NOT_FOUND
                        .withDescription("Letter not found")
                        .asRuntimeException());
        if (!letter.getUserBy().getEmail().equals(securityService.getCurrentUserEmail())
                && letter.getWatched().equals(Boolean.FALSE)) {
            securityService.getCurrentUserEmail();
            letter.setWatched(Boolean.TRUE);
            letter = letterRepository.save(letter);
        }
        responseObserver.onNext(LetterMapper.INSTANCE.toLetterProto(letter));
        responseObserver.onCompleted();
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ROLE_USER')")
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
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ROLE_USER')")
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
    @Transactional
    @PreAuthorize("hasRole('ROLE_USER')")
    public void deleteLetterById(LetterIdRequest request, StreamObserver<Empty> responseObserver) {
        letterRepository.findById(request.getId()).ifPresent(letterRepository::delete);

        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ROLE_USER')")
    public void countOfInboxesLetter(LetterByEmail request, StreamObserver<LetterCount> responseObserver) {
        final long count = letterRepository.countUnwatchedInboxLetters(securityService.getCurrentUserEmail());
        responseObserver.onNext(LetterCount.newBuilder().setCount(count).build());
        responseObserver.onCompleted();
    }
}
