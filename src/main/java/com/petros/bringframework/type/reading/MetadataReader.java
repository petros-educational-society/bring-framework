package com.petros.bringframework.type.reading;

import com.petros.bringframework.beans.factory.config.AnnotationMetadata;
import com.petros.bringframework.beans.factory.config.ClassMetadata;

/**
 * Simple facade for accessing class metadata, as read by reflection api
 *
 * @author "Maksym Oliinyk"
 */
public interface MetadataReader {

    /**
     * Read basic class metadata for the underlying class.
     */
    ClassMetadata getClassMetadata();

    /**
     * Read full annotation metadata for the underlying class,
     * including metadata for annotated methods.
     */
    AnnotationMetadata getAnnotationMetadata();


}
