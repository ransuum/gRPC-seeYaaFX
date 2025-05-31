package org.parent.grpcserviceseeyaa.configuration.validator;

import jakarta.validation.Valid;
import org.parent.grpcserviceseeyaa.dto.*;

public interface GrpcValidatorService {
    void validateLetter(@Valid LetterRequestDto requestDto);
    void validSignIn(@Valid SignInRequestDto signInRequestDto);
    void validSignUp(@Valid SignUpRequestDto signUpRequestDto);
    void validEditProfile(@Valid EditRequestDto editRequestDto);
    void validAnswer(@Valid AnswerRequestDto answerRequestDto);
}
