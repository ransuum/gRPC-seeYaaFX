package org.parent.grpcserviceseeyaa.service;

import com.google.protobuf.Empty;
import com.seeYaa.proto.email.service.answer.AnswerServiceGrpc;
import com.seeYaa.proto.email.service.answer.CreateAnswerRequest;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import org.parent.grpcserviceseeyaa.configuration.validator.GrpcValidatorService;
import org.parent.grpcserviceseeyaa.dto.AnswerRequestDto;
import org.parent.grpcserviceseeyaa.entity.Answer;
import org.parent.grpcserviceseeyaa.exception.NotFoundException;
import org.parent.grpcserviceseeyaa.repository.AnswerRepository;
import org.parent.grpcserviceseeyaa.repository.LetterRepository;
import org.parent.grpcserviceseeyaa.repository.UserRepository;
import org.springframework.grpc.server.service.GrpcService;

import java.time.LocalDateTime;

@GrpcService
@RequiredArgsConstructor
public class AnswerService extends AnswerServiceGrpc.AnswerServiceImplBase {
    private final AnswerRepository answerRepository;
    private final LetterRepository letterRepo;
    private final UserRepository usersRepo;
    private final GrpcValidatorService grpcValidatorService;

    @Override
    public void createAnswer(CreateAnswerRequest request, StreamObserver<Empty> responseObserver) {
        grpcValidatorService.validAnswer(new AnswerRequestDto(request.getText()));
        final var letter = letterRepo.findById(request.getLetterId())
                .orElseThrow(() -> new NotFoundException("Letter not found"));

        final var users = usersRepo.findByEmail(request.getEmailBy())
                .orElseThrow(() -> new NotFoundException("User not found"));

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
