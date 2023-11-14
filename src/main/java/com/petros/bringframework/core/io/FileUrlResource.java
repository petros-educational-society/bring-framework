package com.petros.bringframework.core.io;

import lombok.SneakyThrows;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import static java.util.Objects.nonNull;

public class FileUrlResource extends AbstractResource implements Resource {

    @Nullable
    private volatile File file;
    private final URL url;
    public FileUrlResource(URL url) {
        this.url = url;
    }

    @Override
    public URL getURL() throws IOException {
        return null;
    }

    @Override
    public URI getURI() throws IOException {
        return null;
    }

    @Override
    public File getFile() throws IOException {
        File file = this.file;
        if (file != null) {
            return file;
        }
        file = getFile();
        this.file = file;
        return file;
    }

    public boolean isWritable() {
        try {
            File file = getFile();
            return (file.canWrite() && !file.isDirectory());
        }
        catch (IOException ex) {
            return false;
        }
    }

    public OutputStream getOutputStream() throws IOException {
        return Files.newOutputStream(getFile().toPath());
    }

    public WritableByteChannel writableChannel() throws IOException {
        return FileChannel.open(getFile().toPath(), StandardOpenOption.WRITE);
    }

    @Override
    public Resource createRelative(String relativePath) throws MalformedURLException {
        return new FileUrlResource(createRelativeURL(relativePath));
    }

    @Nullable
    @Override
    public String getFilename() {
        return nonNull(file) ? file.getName() : super.getFilename();
    }

    @Override
    public String getDescription() {
        return file.getAbsolutePath();
    }

    @SneakyThrows
    protected URL createRelativeURL(String relativePath) throws MalformedURLException {
        if (relativePath.startsWith("/")) {
            relativePath = relativePath.substring(1);
        }
        return Paths.get(url.toURI())
                .relativize(Paths.get(relativePath))
                .toUri()
                .toURL();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return null;
    }
}
