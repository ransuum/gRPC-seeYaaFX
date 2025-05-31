package org.parent.grpcserviceseeyaa.configuration.letter;

import com.google.protobuf.Empty;
import com.seeYaa.proto.email.configuration.movedletter.MovedLetterConfigurationGrpc;
import com.seeYaa.proto.email.configuration.movedletter.SetLetterTypeRequest;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.parent.grpcserviceseeyaa.entity.MovedLetter;
import org.parent.grpcserviceseeyaa.exception.NotFoundException;
import org.parent.grpcserviceseeyaa.repository.LetterRepository;
import org.parent.grpcserviceseeyaa.repository.MovedLetterRepository;
import org.parent.grpcserviceseeyaa.repository.UserRepository;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MovedLetterConfigurationImpl extends MovedLetterConfigurationGrpc.MovedLetterConfigurationImplBase {
    private final LetterRepository letterRepository;
    private final MovedLetterRepository movedLetterRepository;
    private final UserRepository userRepository;

    @Override
    public void setLetterType(SetLetterTypeRequest request, StreamObserver<Empty> responseObserver) {
            final var user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new NotFoundException("User not found"));

            final var movedLetterEntity = movedLetterRepository.findByLetterId(request.getLetterId())
                    .map(movedLetter -> {
                        movedLetterRepository.delete(movedLetter);
                        letterRepository.updateActiveLetterById(request.getLetterId(), true);
                        return movedLetter;
                    }).orElseGet(() -> {
                        letterRepository.updateActiveLetterById(request.getLetterId(), false);
                        final var letter = letterRepository.findById(request.getLetterId())
                                .orElseThrow(() -> new NotFoundException("Letter not found"));

                        final var movedLetterBuild = MovedLetter.builder()
                                .letter(letter)
                                .typeOfLetter(request.getType())
                                .movedBy(user)
                                .build();
                        return movedLetterRepository.save(movedLetterBuild);
                    });

            log.info("Moved letter with id {} to {}", movedLetterEntity.getLetter().getId(), request.getType());
            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
    }
}
