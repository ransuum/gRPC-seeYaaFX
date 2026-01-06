package org.parent.grpcserviceseeyaa.configuration.validator;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.parent.grpcserviceseeyaa.dto.*;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Validated
@Service
public class GrpcValidatorService {

    public void validateLetter(@Valid LetterRequestDto requestDto) {
        log.info("Validating letter request {}", requestDto);

    }

    public void validSignIn(@Valid SignInRequestDto signInRequestDto) {
        log.info("Validating signIn request: {}", signInRequestDto);
    }

    public void validSignUp(@Valid SignUpRequestDto signUpRequestDto) {
        log.info("Validating signUp request: {}", signUpRequestDto);
    }

    public void validEditProfile(@Valid EditRequestDto editRequestDto) {
        log.info("Validating edit profile: {}", editRequestDto);
    }

    public void validAnswer(@Valid AnswerRequestDto answerRequestDto) {
        log.info("Validating answer request: {}", answerRequestDto);
    }
}
