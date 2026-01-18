package org.parent;

import javafx.application.Application;
import javafx.stage.Stage;
import org.parent.grpcserviceseeyaa.configuration.GrpcConfig;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

@SpringBootApplication(
        scanBasePackageClasses = {GrpcConfig.class},
        scanBasePackages = {"org.parent"})
public class SpringSeeYaaGrpc {
    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        Application.launch(SeeYaaApplicationFX.class, args);
    }

    @Bean
    public Map<String, Stage> openStages() {
        return new HashMap<>();
    }
}
