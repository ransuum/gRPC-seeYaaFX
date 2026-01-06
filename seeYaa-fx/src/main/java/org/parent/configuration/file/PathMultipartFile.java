package org.parent.configuration.file;

import org.jspecify.annotations.NullMarked;

import module java.base;

@NullMarked
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
    public byte[] getBytes() throws IOException {
        return Files.readAllBytes(file.toPath());
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new FileInputStream(file);
    }
}
