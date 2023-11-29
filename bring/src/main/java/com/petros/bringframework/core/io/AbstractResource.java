package com.petros.bringframework.core.io;

import lombok.extern.log4j.Log4j2;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.function.Supplier;

@Log4j2
public abstract class AbstractResource implements Resource {
    @Override
    public boolean exists() {
        if (isFile()) {
            try {
                return getFile().exists();
            }
            catch (IOException ex) {
                debug(() -> "Could not retrieve File for existence check of " + getDescription(), ex);
            }
        }
        try (var ignored = getInputStream()) {
            return true;
        }  catch (Throwable ex) {
            debug(() -> "Could not retrieve InputStream for existence check of " + getDescription(), ex);
            return false;
        }
    }

    @Override
    public boolean isReadable() {
        return exists();
    }

    @Override
    public boolean isOpen() {
        return false;
    }

    @Override
    public boolean isFile() {
        return false;
    }

    @Override
    public URL getURL() throws IOException {
        throw new FileNotFoundException(getDescription() + " cannot be resolved to URL");
    }

    /**
     * This implementation builds a URI based on the URL returned
     * by {@link #getURL()}.
     */
    @Override
    public URI getURI() throws IOException {
        var url = getURL();
        try {
            return url.toURI();
        }
        catch (URISyntaxException ex) {
            throw new IOException("Invalid URI [" + url + "]", ex);
        }
    }

    /**
     * This implementation returns {@link Channels#newChannel(InputStream)}
     * with the result of {@link #getInputStream()}.
     * <p>This is the same as in {@link Resource}'s corresponding default method
     * but mirrored here for efficient JVM-level dispatching in a class hierarchy
     */
    @Override
    public ReadableByteChannel readableChannel() throws IOException {
        return Channels.newChannel(getInputStream());
    }

    /**
     * This method reads the entire InputStream to determine the content length.
     * <p>For a custom subclass of {@code InputStreamResource}, we strongly
     * recommend overriding this method with a more optimal implementation, e.g.
     * checking File length, or possibly simply returning -1 if the stream can
     * only be read once
     */
    @Override
    public long contentLength() throws IOException {
        long size = 0;
        try (var is = getInputStream()) {
            byte[] buf = new byte[256];
            int read;
            while ((read = is.read(buf)) != -1) {
                size += read;
            }
            return size;
        } catch (IOException ex) {
            debug(() -> "Could not close content-length InputStream for " + getDescription(), ex);
        }
        return size;
    }

    /**
     * This implementation checks the timestamp of the underlying File,
     * if available
     */
    @Override
    public long lastModified() throws IOException {
        File fileToCheck = getFileForLastModifiedCheck();
        long lastModified = fileToCheck.lastModified();
        if (lastModified == 0L && !fileToCheck.exists()) {
            throw new FileNotFoundException(getDescription() +
                    " cannot be resolved in the file system for checking its last-modified timestamp");
        }
        return lastModified;
    }

    protected File getFileForLastModifiedCheck() throws IOException {
        return getFile();
    }

    /**
     * This implementation always returns {@code null},
     * assuming that this resource type does not have a filename.
     */
    @Override
    @Nullable
    public String getFilename() {
        return null;
    }

    private void debug(Supplier<String> message, Throwable ex) {
        if (log.isDebugEnabled()) {
            log.debug(message.get(), ex);
        }
    }

    @Override
    public boolean equals(@Nullable Object other) {
        return (this == other || (other instanceof Resource that &&
                getDescription().equals(that.getDescription())));
    }

    @Override
    public int hashCode() {
        return getDescription().hashCode();
    }

    @Override
    public String toString() {
        return getDescription();
    }
}
