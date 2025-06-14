package org.parent.configuration.file;

import lombok.NonNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class PathMultipartFile implements MultipartFile {
    private final File file;

    public PathMultipartFile(File file) {
        this.file = file;
    }

    @Override
    public String getOriginalFilename() {
        return file.getName();
    }

    @Override
    public long getSize() {
        return file.length();
    }

    @Override
    public byte @NonNull [] getBytes() throws IOException {
        return Files.readAllBytes(file.toPath());
    }

    @Override
    public @NonNull InputStream getInputStream() throws IOException {
        return new FileInputStream(file);
    }
}
