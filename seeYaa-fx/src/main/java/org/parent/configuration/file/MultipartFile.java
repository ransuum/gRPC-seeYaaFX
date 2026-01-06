package org.parent.configuration.file;

import org.jspecify.annotations.NonNull;
import org.springframework.core.io.InputStreamSource;

import java.io.IOException;

public interface MultipartFile extends InputStreamSource {

    @NonNull String getOriginalFilename();

    long getSize();

    byte[] getBytes() throws IOException;
}
