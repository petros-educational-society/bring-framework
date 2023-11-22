package com.petros.bringframework.core.io;

import com.petros.bringframework.util.ClassUtils;
import com.petros.bringframework.util.ObjectUtils;
import com.petros.bringframework.util.ResourceUtils;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;

import static java.lang.String.format;
import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;

public class ClassPathResource extends AbstractResource implements Resource {

    /**
     * Internal representation of the original path supplied by the user,
     * used for creating relative paths and resolving URLs and InputStreams
     */
    private final String path;

    private final String absolutePath;

    @Nullable
    private final ClassLoader classLoader;

    @Nullable
    private final Class<?> clazz;

    public ClassPathResource(String path) {
        this(path, (ClassLoader) null);
    }

    public ClassPathResource(String path, @Nullable ClassLoader classLoader) {
        requireNonNull(path, "Path must not be null");
        var pathToUse = Paths.get(path).normalize().toString();
        if (pathToUse.startsWith("/")) {
            pathToUse = pathToUse.substring(1);
        }
        this.path = pathToUse;
        this.absolutePath = pathToUse;
        this.classLoader = classLoader != null ? classLoader : ClassUtils.getDefaultClassLoader();
        this.clazz = null;
    }

    public ClassPathResource(String path, @Nullable Class<?> clazz) {
        requireNonNull(path, "Path must not be null");

        this.path = Paths.get(path).normalize().toString();
        var absolutePath = this.path;

        if (clazz != null && !absolutePath.startsWith("/")) {
            absolutePath = ClassUtils.classPackageAsResourcePath(clazz) + "/" + absolutePath;
        } else if (absolutePath.startsWith("/")) {
            absolutePath = absolutePath.substring(1);
        }

        this.absolutePath = absolutePath;
        this.classLoader = null;
        this.clazz = clazz;
    }

    public final String getPath() {
        return absolutePath;
    }

    @Nullable
    public final ClassLoader getClassLoader() {
        return nonNull(clazz) ? clazz.getClassLoader() : classLoader;
    }

    @Override
    public boolean exists() {
        return nonNull(resolveURL());
    }

    @Override
    public boolean isReadable() {
        return nonNull(resolveURL());
    }

    @Nullable
    protected URL resolveURL() {
        try {
            if (clazz != null) {
                return clazz.getResource(path);
            }
            else if (this.classLoader != null) {
                return classLoader.getResource(absolutePath);
            }
            else {
                return ClassLoader.getSystemResource(absolutePath);
            }
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }

    /**
     * This implementation opens an {@link InputStream} for the underlying class
     * path resource, if available.
     */
    @Override
    public InputStream getInputStream() throws IOException {
        InputStream is;
        if (this.clazz != null) {
            is = this.clazz.getResourceAsStream(this.path);
        }
        else if (this.classLoader != null) {
            is = this.classLoader.getResourceAsStream(this.absolutePath);
        }
        else {
            is = ClassLoader.getSystemResourceAsStream(this.absolutePath);
        }
        if (is == null) {
            throw new FileNotFoundException(getDescription() + " cannot be opened because it does not exist");
        }
        return is;
    }

    /**
     * This implementation returns a URL for the underlying class path resource,
     * if available.
     */
    @Override
    public URL getURL() throws IOException {
        URL url = resolveURL();
        if (url == null) {
            throw new FileNotFoundException(getDescription() + " cannot be resolved to URL because it does not exist");
        }
        return url;
    }

    @Override
    public URI getURI() throws IOException {
        URL url = getURL();
        try {
            return ResourceUtils.toURI(url);
        }
        catch (URISyntaxException ex) {
            throw new IOException("Invalid URI [" + url + "]", ex);
        }
    }

    @Override
    public File getFile() throws IOException {
        return ResourceUtils.getFile(getURL(), getDescription());
    }

    @Override
    public long contentLength() throws IOException {
        if (ResourceUtils.isFileURL(getURL())) {
            var file = getFile();
            long length = file.length();
            if (length == 0L && !file.exists()) {
                throw new FileNotFoundException(getDescription() +
                        " cannot be resolved in the file system for checking its content length");
            }
            return length;
        }

        return 0;
    }

    @Override
    public Resource createRelative(String relativePath) {
        var pathToUse = Paths.get(path).relativize(Paths.get(relativePath)).toString();
        return nonNull(clazz) ? new ClassPathResource(pathToUse, clazz) : new ClassPathResource(pathToUse, classLoader);
    }

    @Override
    @Nullable
    public String getFilename() {
        return Paths.get(absolutePath).getFileName().toString();
    }

    @Override
    public String getDescription() {
        return format("class path resource [%s]", absolutePath);
    }

    @Override
    public boolean equals(@Nullable Object other) {
        return this == other || (
                other instanceof ClassPathResource that &&
                        this.absolutePath.equals(that.absolutePath) &&
                        ObjectUtils.nullSafeEquals(getClassLoader(), that.getClassLoader())
        );
    }

    @Override
    public int hashCode() {
        return this.absolutePath.hashCode();
    }

}