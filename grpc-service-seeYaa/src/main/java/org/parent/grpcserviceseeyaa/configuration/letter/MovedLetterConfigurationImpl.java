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
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class MovedLetterConfigurationImpl extends MovedLetterConfigurationGrpc.MovedLetterConfigurationImplBase {
    private final LetterRepository letterRepository;
    private final MovedLetterRepository movedLetterRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void setLetterType(SetLetterTypeRequest request, StreamObserver<Empty> responseObserver) {
        final var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new NotFoundException("User not found"));
        final var letter = letterRepository.findById(request.getLetterId())
                .orElseThrow(() -> new NotFoundException("Letter not found"));

        final var movedOpt = movedLetterRepository.findByLetterAndMovedBy(letter, user);

        movedOpt.ifPresentOrElse(existing -> {
            if (existing.getTypeOfLetter() == request.getType()) {
                letter.setActiveLetter(Boolean.TRUE);
                letterRepository.save(letter);
                movedLetterRepository.delete(existing);
                log.info("Restored letter {} for user {} from {}", letter.getId(), user.getEmail(), request.getType());
            } else {
                existing.setTypeOfLetter(request.getType());
                letter.setActiveLetter(Boolean.FALSE);
                letterRepository.save(letter);
                movedLetterRepository.save(existing);
                log.info("Changed letter {} for user {} to {}", letter.getId(), user.getEmail(), request.getType());
            }
        }, () -> {
            letter.setActiveLetter(Boolean.FALSE);
            final var moved = MovedLetter.builder()
                    .letter(letter)
                    .typeOfLetter(request.getType())
                    .movedBy(user)
                    .build();
            movedLetterRepository.save(moved);
            log.info("Moved letter {} to {} for user {}", letter.getId(), request.getType(), user.getEmail());
        });

        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }
}
