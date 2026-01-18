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

    private static final String USER_NOT_LOGGED_IN = "User is not logged in";
    private static final String INVALID_CREDENTIALS = "Invalid email or password";

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
                .orElseThrow(() -> Status.NOT_FOUND.withDescription(USER_NOT_LOGGED_IN).asRuntimeException());

        responseObserver.onNext(UserMapper.INSTANCE.toUserProto(user));
        responseObserver.onCompleted();
    }

    @Override
    @Authorize("hasRole('ROLE_USER')")
    @Transactional(readOnly = true)
    public void getCurrentUserExtended(Empty request, StreamObserver<UserWithLetters> responseObserver) {
        var user = userRepository.findByEmail(securityService.getCurrentUserEmail())
                .orElseThrow(() -> Status.NOT_FOUND.withDescription(USER_NOT_LOGGED_IN).asRuntimeException());

        responseObserver.onNext(UserMapper.INSTANCE.toUserWithLettersProto(user));
        responseObserver.onCompleted();
    }

    @Override
    @Authorize("hasRole('ROLE_USER')")
    public void editProfile(EditProfileRequest request, StreamObserver<Empty> responseObserver) {
       var users = userRepository.findByEmail(securityService.getCurrentUserEmail())
                .orElseThrow(() -> Status.NOT_FOUND.withDescription(USER_NOT_LOGGED_IN).asRuntimeException());

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
            if (request.getCredentialsBase64().isBlank()) {
                throw Status.UNAUTHENTICATED.withDescription("Credentials required").asRuntimeException();
            }
            String[] creds = rsaCredentials.decrypt(request.getCredentialsBase64());
            String email = creds[0];
            String password = creds[1];
            java.util.Arrays.fill(creds, null);

            var user = userRepository.findByEmail(email)
                    .orElseThrow(() -> {
                        log.warn("Failed login attempt for email: {}", email);
                        return Status.UNAUTHENTICATED.withDescription(INVALID_CREDENTIALS).asRuntimeException();
                    });
            if (!BCrypt.verifyer().verify(password.toCharArray(), user.getPassword()).verified) {
                log.warn("Invalid password for email: {}", email);
                throw Status.UNAUTHENTICATED.withDescription(INVALID_CREDENTIALS).asRuntimeException();
            }
            authenticationStore.set(new AuthenticationObject(email, user.getRoles()));

            log.info("User authenticated successfully: {}", email);
            responseObserver.onNext(SignInResponse.newBuilder()
                    .setEmail(email)
                    .build());
            responseObserver.onCompleted();
        } catch (StatusRuntimeException sre) {
            responseObserver.onError(sre);
        } catch (IllegalArgumentException e) {
            log.error("Invalid credential format", e);
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription("Invalid credential format")
                    .asRuntimeException());
        } catch (Exception e) {
            log.error("Authentication error", e);
            responseObserver.onError(Status.UNAUTHENTICATED
                    .withDescription(INVALID_CREDENTIALS)
                    .asRuntimeException());
        }
    }
}
