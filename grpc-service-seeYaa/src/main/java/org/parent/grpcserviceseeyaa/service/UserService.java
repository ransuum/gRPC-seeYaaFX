package org.parent.grpcserviceseeyaa.service;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.google.protobuf.Empty;
import com.seeYaa.proto.email.Users;
import com.seeYaa.proto.email.service.users.*;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.parent.grpcserviceseeyaa.mapper.UserMapper;
import org.parent.grpcserviceseeyaa.repository.UserRepository;
import org.parent.grpcserviceseeyaa.security.rolechecker.Authorize;
import org.parent.grpcserviceseeyaa.security.rsa.RsaCredentials;
import org.parent.grpcserviceseeyaa.security.SecurityService;
import org.parent.grpcserviceseeyaa.security.contextholder.AuthenticationObject;
import org.parent.grpcserviceseeyaa.security.contextholder.AuthenticationStore;
import org.springframework.grpc.server.service.GrpcService;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class UserService extends UsersServiceGrpc.UsersServiceImplBase {
    private final UserRepository userRepository;
    private final SecurityService securityService;
    private final AuthenticationStore authenticationStore;
    private final RsaCredentials rsaCredentials;

    @Override
    public void signUp(SignUpRequest request, StreamObserver<Empty> responseObserver) {
        var savedUser = org.parent.grpcserviceseeyaa.entity.Users.newBuilder()
                .setEmail(request.getEmail())
                .setPassword(BCrypt.withDefaults().hashToString(12, request.getPassword().toCharArray()))
                .setFirstname(request.getFirstname())
                .setLastname(request.getLastname())
                .setUsername(request.getUsername())
                .setRoles(Set.of("ROLE_USER"))
                .build();

        userRepository.save(savedUser);

        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }

    @Override
    @Authorize("hasRole('ROLE_USER')")
    @Transactional(readOnly = true)
    public void getCurrentUser(Empty request, StreamObserver<Users> responseObserver) {
        var user = userRepository.findByEmail(securityService.getCurrentUserEmail())
                .orElseThrow(() -> Status.NOT_FOUND.withDescription("You are not logged in").asRuntimeException());

        responseObserver.onNext(UserMapper.INSTANCE.toUserProto(user));
        responseObserver.onCompleted();
    }

    @Override
    @Authorize("hasRole('ROLE_USER')")
    @Transactional(readOnly = true)
    public void getCurrentUserExtended(Empty request, StreamObserver<UserWithLetters> responseObserver) {
        var user = userRepository.findByEmail(securityService.getCurrentUserEmail())
                .orElseThrow(() -> Status.NOT_FOUND.withDescription("You are not logged in").asRuntimeException());

        responseObserver.onNext(UserMapper.INSTANCE.toUserWithLettersProto(user));
        responseObserver.onCompleted();
    }

    @Override
    @Authorize("hasRole('ROLE_USER')")
    public void editProfile(EditProfileRequest request, StreamObserver<Empty> responseObserver) {
       var users = userRepository.findByEmail(securityService.getCurrentUserEmail())
                .orElseThrow(() -> Status.NOT_FOUND.withDescription("You are not logged in").asRuntimeException());

        if (!request.getFirstname().equals(users.getFirstname())) users.setFirstname(request.getFirstname());
        if (!request.getPassword().equals("N") && BCrypt.verifyer().verify(request.getPassword().toCharArray(), users.getPassword()).verified)
            users.setPassword(BCrypt.withDefaults().hashToString(12, request.getPassword().toCharArray()));
        if (!request.getLastname().equals(users.getLastname())) users.setLastname(request.getLastname());
        if (!request.getUsername().equals(users.getUsername())) users.setUsername(request.getUsername());
        userRepository.save(users);

        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }

    @Override
    public void authentication(SignInRequest request, StreamObserver<SignInResponse> responseObserver) {
        try {
            String[] creds = rsaCredentials.decrypt(request.getCredentialsBase64());
            String email = creds[0];
            String password = creds[1];

            var user = userRepository.findByEmail(email)
                    .filter(u -> BCrypt.verifyer().verify(password.toCharArray(), u.getPassword()).verified)
                    .orElseThrow(() -> Status.NOT_FOUND.withDescription("Invalid credentials").asRuntimeException());

            authenticationStore.set(new AuthenticationObject(email, user.getRoles()));

            responseObserver.onNext(SignInResponse.newBuilder()
                    .setEmail(email)
                    .build());
            responseObserver.onCompleted();

        } catch (StatusRuntimeException sre) {
            responseObserver.onError(sre);
        } catch (Exception e) {
            responseObserver.onError(Status.UNAUTHENTICATED
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        }
    }
}
