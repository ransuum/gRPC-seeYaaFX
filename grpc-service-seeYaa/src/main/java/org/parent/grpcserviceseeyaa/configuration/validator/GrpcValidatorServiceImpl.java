package org.parent.grpcserviceseeyaa.configuration.validator;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.parent.grpcserviceseeyaa.dto.*;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Validated
@Service
public class GrpcValidatorServiceImpl implements GrpcValidatorService {

    @Override
    public void validateLetter(@Valid LetterRequestDto requestDto) {
        log.info("Validating letter request {}", requestDto);

    }

    @Override
    public void validSignIn(@Valid SignInRequestDto signInRequestDto) {
        log.info("Validating signIn request: {}", signInRequestDto);
    }

    @Override
    public void validSignUp(@Valid SignUpRequestDto signUpRequestDto) {
        log.info("Validating signUp request: {}", signUpRequestDto);
    }

    @Override
    public void validEditProfile(@Valid EditRequestDto editRequestDto) {
        log.info("Validating edit profile: {}", editRequestDto);
    }

    @Override
    public void validAnswer(@Valid AnswerRequestDto answerRequestDto) {
        log.info("Validating answer request: {}", answerRequestDto);
    }
}
