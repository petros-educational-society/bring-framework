package com.petros.bringframework.beans.factory.config;

import javax.annotation.Nullable;
import java.util.LinkedHashSet;
import java.util.Set;

import static java.util.Objects.requireNonNull;

/**
 * Represents a user-defined {@link Configuration @Configuration} class.
 *
 * @author "Vasiuk Maryna"
 */
final class ConfigurationClass {

    private final AnnotationMetadata metadata;

    @Nullable
    private String beanName;

    private final Set<ConfigurationClass> importedBy = new LinkedHashSet<>(1);

    public ConfigurationClass(Class<?> clazz, String beanName) {
        requireNonNull(beanName, "Bean name must not be null");
        this.metadata = new ReflectionAnnotationMetadata(clazz);
        this.beanName = beanName;
    }

    @Override
    public String toString() {
        return "ConfigurationClass: beanName '" + this.beanName;
    }

    public boolean isImported() {
        return !this.importedBy.isEmpty();
    }

    public void mergeImportedBy(ConfigurationClass otherConfigClass) {
        this.importedBy.addAll(otherConfigClass.importedBy);
    }

    public AnnotationMetadata getMetadata() {
        return this.metadata;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }
}
