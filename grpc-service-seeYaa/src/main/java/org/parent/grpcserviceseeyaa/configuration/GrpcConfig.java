package org.parent.grpcserviceseeyaa.configuration;

import com.seeYaa.proto.email.configuration.movedletter.MovedLetterConfigurationGrpc;
import com.seeYaa.proto.email.service.answer.AnswerServiceGrpc;
import com.seeYaa.proto.email.service.letter.LetterServiceGrpc;
import com.seeYaa.proto.email.service.movedletter.MovedLetterServiceGrpc;
import com.seeYaa.proto.email.service.storage.StorageServiceGrpc;
import com.seeYaa.proto.email.service.users.UsersServiceGrpc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.grpc.client.GrpcChannelFactory;

@Configuration
public class GrpcConfig {
    private static final String SERVER = "0.0.0.0:9090";

    @Bean
    public UsersServiceGrpc.UsersServiceBlockingStub user(GrpcChannelFactory channels) {
        return UsersServiceGrpc.newBlockingStub(channels.createChannel(SERVER));
    }

    @Bean
    public AnswerServiceGrpc.AnswerServiceBlockingStub answer(GrpcChannelFactory channels) {
        return AnswerServiceGrpc.newBlockingStub(channels.createChannel(SERVER));
    }

    @Bean
    public LetterServiceGrpc.LetterServiceBlockingStub letter(GrpcChannelFactory channels) {
        return LetterServiceGrpc.newBlockingStub(channels.createChannel(SERVER));
    }

    @Bean
    public MovedLetterServiceGrpc.MovedLetterServiceBlockingStub movedLetter(GrpcChannelFactory channels) {
        return MovedLetterServiceGrpc.newBlockingStub(channels.createChannel(SERVER));
    }

    @Bean
    public StorageServiceGrpc.StorageServiceBlockingStub storage(GrpcChannelFactory channels) {
        return StorageServiceGrpc.newBlockingStub(channels.createChannel(SERVER));
    }

    @Bean
    @Primary
    public MovedLetterConfigurationGrpc.MovedLetterConfigurationBlockingStub movedLetterServiceBlockingStub(GrpcChannelFactory channels) {
        return MovedLetterConfigurationGrpc.newBlockingStub(channels.createChannel(SERVER));
    }
}
