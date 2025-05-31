package org.parent.grpcserviceseeyaa.service;

import com.google.protobuf.Empty;
import com.seeYaa.proto.email.Users;
import com.seeYaa.proto.email.service.users.EditProfileRequest;
import com.seeYaa.proto.email.service.users.SignUpRequest;
import com.seeYaa.proto.email.service.users.UserWithLetters;
import com.seeYaa.proto.email.service.users.UsersServiceGrpc;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.parent.grpcserviceseeyaa.configuration.validator.GrpcValidatorService;
import org.parent.grpcserviceseeyaa.dto.EditRequestDto;
import org.parent.grpcserviceseeyaa.dto.SignUpRequestDto;
import org.parent.grpcserviceseeyaa.mapper.UserMapper;
import org.parent.grpcserviceseeyaa.repository.UserRepository;
import org.parent.grpcserviceseeyaa.security.SecurityService;
import org.springframework.grpc.server.service.GrpcService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class UserService extends UsersServiceGrpc.UsersServiceImplBase {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final SecurityService securityService;
    private final GrpcValidatorService grpcValidatorService;

    @Override
    public void signUp(SignUpRequest request, StreamObserver<Empty> responseObserver) {
        try {
        grpcValidatorService.validSignUp(new SignUpRequestDto(
                request.getEmail(), request.getUsername(), request.getPassword(),
                request.getFirstname(), request.getLastname()));
        final var savedUser = org.parent.grpcserviceseeyaa.entity.Users.newBuilder()
                .setEmail(request.getEmail())
                .setPassword(passwordEncoder.encode(request.getPassword()))
                .setFirstname(request.getFirstname())
                .setLastname(request.getLastname())
                .setUsername(request.getUsername())
                .build();

            userRepository.save(savedUser);
        } catch (ConstraintViolationException e) {
            String msg = e.getConstraintViolations().stream()
                    .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                    .reduce((a, b) -> a + "; " + b)
                    .orElse(e.getMessage());

            responseObserver.onError(io.grpc.Status.INVALID_ARGUMENT
                    .withDescription(msg)
                    .asRuntimeException());
        } catch (Exception ex) {
            responseObserver.onError(io.grpc.Status.INTERNAL
                    .withDescription("Internal server error: " + ex.getMessage())
                    .asRuntimeException());
        }

        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }

    @Override
    @Transactional(readOnly = true)
    public void getCurrentUser(Empty request, StreamObserver<Users> responseObserver) {
        final var user = userRepository.findByEmail(securityService.getCurrentUserEmail())
                .orElseThrow(() -> Status.NOT_FOUND
                        .withDescription("You are not logged in")
                        .asRuntimeException());

        responseObserver.onNext(UserMapper.INSTANCE.toUserProto(user));
        responseObserver.onCompleted();
    }

    @Override
    @Transactional(readOnly = true)
    public void getCurrentUserExtended(Empty request, StreamObserver<UserWithLetters> responseObserver) {
        final var user = userRepository.findByEmail(securityService.getCurrentUserEmail())
                .orElseThrow(() -> Status.NOT_FOUND
                        .withDescription("You are not logged in")
                        .asRuntimeException());

        responseObserver.onNext(UserMapper.INSTANCE.toUserWithLettersProto(user));
        responseObserver.onCompleted();
    }

    @Override
    public void editProfile(EditProfileRequest request, StreamObserver<Empty> responseObserver) {
        grpcValidatorService.validEditProfile(new EditRequestDto(
                request.getFirstname(), request.getLastname(), request.getUsername(), request.getPassword()));
        final var users = userRepository.findByEmail(securityService.getCurrentUserEmail())
                .orElseThrow(() -> Status.NOT_FOUND
                        .withDescription("You are not logged in")
                        .asRuntimeException());

        if (!request.getFirstname().equals(users.getFirstname())) users.setFirstname(request.getFirstname());
        if (!request.getPassword().equals("N") && !passwordEncoder.matches(users.getPassword(), request.getPassword()))
            users.setPassword(passwordEncoder.encode(request.getPassword()));
        if (!request.getLastname().equals(users.getLastname())) users.setLastname(request.getLastname());
        if (!request.getUsername().equals(users.getUsername())) users.setUsername(request.getUsername());
        userRepository.save(users);

        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }
}
