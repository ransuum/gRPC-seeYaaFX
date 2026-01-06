package org.parent.ui;

import com.seeYaa.proto.email.FileType;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.kordamp.ikonli.javafx.FontIcon;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FileUiUtils {

    public static Node createFileTypeIcon(FileType type) {
        var icon = new FontIcon();
        switch (type) {
            case IMAGE -> icon.setIconLiteral("fas-file-image");
            case WORD -> icon.setIconLiteral("fas-file-word");
            case PDF -> icon.setIconLiteral("fas-file-pdf");
            case AUDIO -> icon.setIconLiteral("fas-file-audio");
            case VIDEO -> icon.setIconLiteral("fas-file-video");
            default -> icon.setIconLiteral("fas-file");
        }
        icon.getStyleClass().add("file-icon");
        return icon;
    }

    public static String formatFileSize(Long size) {
        return switch (size) {
            case Long s when s < 1024L -> size + " B";
            case Long s when s < 1024L * 1024L -> String.format("%.1f KB", size / 1024.0);
            case Long s when s < 1024L * 1024L * 1024L -> String.format("%.1f MB", size / (1024.0 * 1024));
            default -> String.format("%.1f GB", size / (1024.0 * 1024 * 1024));
        };
    }

    public static StackPane createAvatar(String firstName) {
        var avatar = new StackPane();
        avatar.getStyleClass().add("avatar");
        var initials = new Label(firstName.substring(0, 1).toUpperCase());
        initials.getStyleClass().add("avatar-initials");

        avatar.getChildren().add(initials);
        return avatar;
    }
}
