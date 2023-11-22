package com.petros.bringframework.util;

import lombok.SneakyThrows;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;

import static java.util.List.of;
import static java.util.Objects.requireNonNull;

public class ResourceUtils {
    public static final String URL_PROTOCOL_FILE = "file";
    public static final String URL_PROTOCOL_VFSFILE = "vfsfile";
    public static final String URL_PROTOCOL_VFS = "vfs";

    public static boolean isFileURL(URL url) {
        return of(URL_PROTOCOL_FILE, URL_PROTOCOL_VFSFILE, URL_PROTOCOL_VFS).contains(url.getProtocol());
    }

    @SneakyThrows
    public static File getFile(URL resourceUrl, String description) throws FileNotFoundException {
        requireNonNull(resourceUrl, "Resource URL must not be null");

        if (!URL_PROTOCOL_FILE.equals(resourceUrl.getProtocol())) {
            throw new FileNotFoundException(description + " cannot be resolved to absolute file path " +
                    "because it does not reside in the file system: " + resourceUrl);
        }
        return new File(toURI(resourceUrl).getSchemeSpecificPart());
    }

    public static URI toURI(URL url) throws URISyntaxException {
        return toURI(url.toString());
    }

    public static URI toURI(String location) throws URISyntaxException {
        return new URI(Paths.get(location).toString().replace(" ", "%20"));
    }

}
