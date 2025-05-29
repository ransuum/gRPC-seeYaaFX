package org.parent;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Objects;

public class SeeYaaApplicationFX extends Application {
    private ConfigurableApplicationContext springContext;

    @Override
    public void init() {
        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
        springContext = new SpringApplicationBuilder(SpringSeeYaaGrpc.class).run();
    }

    @Override
    public void start(Stage stage) throws Exception {
        springContext.publishEvent(new StageReadyEvent(stage));

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/parent/controller/login.fxml"));
        fxmlLoader.setControllerFactory(springContext::getBean);
        String css = Objects.requireNonNull(this.getClass().getResource("controller/static/login.css")).toExternalForm();
        Scene scene = new Scene(fxmlLoader.load());
        scene.getStylesheets().add(css);
        stage.setScene(scene);
        stage.setTitle("SeeYaa");
        stage.show();
    }

    @Override
    public void stop() {
        springContext.close();
        Platform.exit();
    }

    public static class StageReadyEvent extends ApplicationEvent {
        public StageReadyEvent(Stage stage) {
            super(stage);
        }
        public Stage getStage() {
            return ((Stage) getSource());
        }
    }
}
