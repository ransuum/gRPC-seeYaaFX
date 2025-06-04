package org.parent.grpcserviceseeyaa.service;

import com.google.protobuf.Empty;
import com.seeYaa.proto.email.service.answer.AnswerServiceGrpc;
import com.seeYaa.proto.email.service.answer.CreateAnswerRequest;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import org.parent.grpcserviceseeyaa.entity.Answer;
import org.parent.grpcserviceseeyaa.repository.AnswerRepository;
import org.parent.grpcserviceseeyaa.repository.LetterRepository;
import org.parent.grpcserviceseeyaa.repository.UserRepository;
import org.parent.grpcserviceseeyaa.security.SecurityService;
import org.springframework.grpc.server.service.GrpcService;

import java.time.LocalDateTime;

@GrpcService
@RequiredArgsConstructor
public class AnswerService extends AnswerServiceGrpc.AnswerServiceImplBase {
    private final AnswerRepository answerRepository;
    private final LetterRepository letterRepo;
    private final UserRepository usersRepo;
    private final SecurityService securityService;

    @Override
    public void createAnswer(CreateAnswerRequest request, StreamObserver<Empty> responseObserver) {
        final var letter = letterRepo.findById(request.getLetterId())
                .orElseThrow(() -> Status.NOT_FOUND
                        .withDescription("Letter not found")
                        .asRuntimeException());

        final var users = usersRepo.findByEmail(securityService.getCurrentUserEmail())
                .orElseThrow(() -> Status.NOT_FOUND
                        .withDescription("You are not logged in")
                        .asRuntimeException());

        answerRepository.save(Answer.builder()
                .answerText(request.getText())
                .currentLetter(letter)
                .userByAnswered(users)
                .createdAt(LocalDateTime.now())
                .build());

        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }
}
