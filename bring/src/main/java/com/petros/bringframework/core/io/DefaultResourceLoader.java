package com.petros.bringframework.core.io;

import com.petros.bringframework.util.ClassUtils;
import lombok.NoArgsConstructor;

import javax.annotation.Nullable;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@NoArgsConstructor
public class DefaultResourceLoader implements ResourceLoader {

    @Nullable
    private ClassLoader classLoader;

    private final Map<Class<?>, Map<Resource, ?>> resourceCaches = new ConcurrentHashMap<>(4);

    public DefaultResourceLoader(@Nullable ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public Resource getResource(String location) {
        Objects.requireNonNull(location, "Location must not be null");

//  todo: add process logic of all needed ProtocolResolver(s) to apply necessary protocol-resolving behaviour
//        for (ProtocolResolver protocolResolver : getProtocolResolvers()) {
//            Resource resource = protocolResolver.resolve(location, this);
//            if (resource != null) {
//                return resource;
//            }
//        }

        if (location.startsWith("/")) {
            return getResourceByPath(location);
        }

        if (location.startsWith(CLASSPATH_URL_PREFIX)) {
            return new ClassPathResource(location.substring(CLASSPATH_URL_PREFIX.length()), getClassLoader());
        }

        try {
            return new FileUrlResource(new URL(location));
        } catch (MalformedURLException ignored) {
            return getResourceByPath(location);
        }
    }

    @Override
    @Nullable
    public ClassLoader getClassLoader() {
        return (this.classLoader != null ? this.classLoader : ClassUtils.getDefaultClassLoader());
    }

    public void setClassLoader(@Nullable ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @SuppressWarnings("unchecked")
    public <T> Map<Resource, T> getResourceCache(Class<T> valueType) {
        return (Map<Resource, T>) this.resourceCaches.computeIfAbsent(valueType, key -> new ConcurrentHashMap<>());
    }

    public void clearResourceCaches() {
        this.resourceCaches.clear();
    }

    protected Resource getResourceByPath(String path) {
        return new ClassPathContextResource(path, getClassLoader());
    }

    protected static class ClassPathContextResource extends ClassPathResource {

        public ClassPathContextResource(String path, @Nullable ClassLoader classLoader) {
            super(path, classLoader);
        }

        public String getPathWithinContext() {
            return getPath();
        }

        @Override
        public Resource createRelative(String relativePath) {
            var pathToUse = Paths.get(getPath()).relativize(Paths.get(relativePath)).toString();
            return new ClassPathContextResource(pathToUse, getClassLoader());
        }
    }
}
