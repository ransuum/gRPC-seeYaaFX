package org.parent.configuration.file;

import org.springframework.core.io.InputStreamSource;
import org.springframework.lang.Nullable;

import java.io.IOException;

public interface MultipartFile extends InputStreamSource {

    @Nullable
    String getOriginalFilename();

    long getSize();

    byte[] getBytes() throws IOException;
}
