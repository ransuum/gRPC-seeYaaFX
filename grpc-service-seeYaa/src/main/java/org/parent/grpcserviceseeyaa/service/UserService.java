package org.parent.grpcserviceseeyaa.service;

import com.google.protobuf.Empty;
import com.seeYaa.proto.email.Users;
import com.seeYaa.proto.email.service.users.EditProfileRequest;
import com.seeYaa.proto.email.service.users.SignUpRequest;
import com.seeYaa.proto.email.service.users.UserWithLetters;
import com.seeYaa.proto.email.service.users.UsersServiceGrpc;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import org.parent.grpcserviceseeyaa.mapper.UserMapper;
import org.parent.grpcserviceseeyaa.repository.UserRepository;
import org.parent.grpcserviceseeyaa.security.SecurityService;
import org.springframework.grpc.server.service.GrpcService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static org.parent.grpcserviceseeyaa.util.fieldvalidation.FieldUtil.isValid;

@GrpcService
@RequiredArgsConstructor
public class UserService extends UsersServiceGrpc.UsersServiceImplBase {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final SecurityService securityService;

    @Override
    public void signUp(SignUpRequest request, StreamObserver<Empty> responseObserver) {
        final var savedUser = org.parent.grpcserviceseeyaa.entity.Users.newBuilder()
                .setEmail(request.getEmail())
                .setPassword(passwordEncoder.encode(request.getPassword()))
                .setFirstname(request.getFirstname())
                .setLastname(request.getLastname())
                .setUsername(request.getUsername())
                .build();

        userRepository.save(savedUser);

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
        final var users = userRepository.findByEmail(securityService.getCurrentUserEmail())
                .orElseThrow(() -> Status.NOT_FOUND
                        .withDescription("You are not logged in")
                        .asRuntimeException());

        if (isValid(request.getFirstname())) users.setFirstname(request.getFirstname());
        if (isValid(request.getPassword())) users.setPassword(passwordEncoder.encode(request.getPassword()));
        if (isValid(request.getLastname())) users.setLastname(request.getLastname());
        if (isValid(request.getUsername())) users.setUsername(request.getUsername());
        userRepository.save(users);

        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }
}
