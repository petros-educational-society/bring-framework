package com.petros.bringframework.core.io;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.net.URI;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;

/**
 * Interface for a resource descriptor that abstracts from the actual
 * type of underlying resource, such as a file or class path resource
 */
public interface Resource extends InputStreamSource {

    int BUFFER_SIZE = 8192;

    /**
     * Determine whether this resource actually exists in physical form
     */
    boolean exists();

    /**
     * Indicate whether non-empty contents of this resource can be read
     */
    default boolean isReadable() {
        return exists();
    }

    /**
     * Indicate whether this resource represents a handle with an open stream
     */
    default boolean isOpen() {
        return false;
    }

    /**
     * Determine whether this resource represents a file in a file system
     */
    default boolean isFile() {
        return false;
    }

    /**
     * Return a URL handle for this resource
     * @throws IOException if the resource cannot be resolved as URL
     */
    URL getURL() throws IOException;

    /**
     * Return a URI handle for this resource.
     * @throws IOException if the resource cannot be resolved as URI
     */
    URI getURI() throws IOException;

    /**
     * Return a File handle for this resource.
     * @throws java.io.FileNotFoundException if the resource cannot be resolved as
     * absolute file path, i.e. if the resource is not available in a file system
     * @throws IOException in case of general resolution/reading failures
     */
    File getFile() throws IOException;

    /**
     * Return a {@link ReadableByteChannel}.
     * <p>It is expected that each call creates a <i>fresh</i> channel.
     * <p>The default implementation returns {@link Channels#newChannel(InputStream)}
     * with the result of {@link #getInputStream()}.
     * @return the byte channel for the underlying resource (must not be {@code null})
     * @throws java.io.FileNotFoundException if the underlying resource doesn't exist
     * @throws IOException if the content channel could not be opened
     */
    default ReadableByteChannel readableChannel() throws IOException {
        return Channels.newChannel(getInputStream());
    }

    /**
     * Return the contents of this resource as a byte array.
     * @return the contents of this resource as byte array
     * @throws java.io.FileNotFoundException if the resource cannot be resolved as
     * absolute file path, i.e. if the resource is not available in a file system
     * @throws IOException in case of general resolution/reading failures
     */
    default byte[] getContentAsByteArray() throws IOException {
        var is = getInputStream();
        if (is == null) {
            return new byte[0];
        }
        try (is) {
            return is.readAllBytes();
        }
    }

    /**
     * Returns the contents of this resource as a string, using the specified
     * charset.
     * @param charset the charset to use for decoding
     * @return the contents of this resource as a {@code String}
     * @throws java.io.FileNotFoundException if the resource cannot be resolved as
     * absolute file path, i.e. if the resource is not available in a file system
     * @throws IOException in case of general resolution/reading failures
     */
    default String getContentAsString(Charset charset) throws IOException {
        try(Reader in = new InputStreamReader(getInputStream(), charset);
            StringWriter out = new StringWriter(BUFFER_SIZE)) {
            char[] buffer = new char[BUFFER_SIZE];
            int charsRead;
            while ((charsRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, charsRead);
            }
            out.flush();
            return out.toString();
        }
    }

    /**
     * Determine the content length for this resource.
     * @throws IOException if the resource cannot be resolved
     * (in the file system or as some other known physical resource type)
     */
    long contentLength() throws IOException;

    /**
     * Determine the last-modified timestamp for this resource.
     * @throws IOException if the resource cannot be resolved
     * (in the file system or as some other known physical resource type)
     */
    long lastModified() throws IOException;

    /**
     * Create a resource relative to this resource.
     * @param relativePath the relative path (relative to this resource)
     * @return the resource handle for the relative resource
     * @throws IOException if the relative resource cannot be determined
     */
    Resource createRelative(String relativePath) throws IOException;

    /**
     * Determine the filename for this resource. Implementations are encouraged to return the filename unencoded
     */
    @Nullable
    String getFilename();

    /**
     * Return a description for this resource,
     * to be used for error output when working with the resource.
     * <p>Implementations are also encouraged to return this value
     * from their {@code toString} method
     */
    String getDescription();

}

