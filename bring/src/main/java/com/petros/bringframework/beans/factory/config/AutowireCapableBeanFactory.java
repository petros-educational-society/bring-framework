package com.petros.bringframework.beans.factory.config;

import com.petros.bringframework.beans.factory.BeanFactory;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author "Maksym Oliinyk"
 */
public interface AutowireCapableBeanFactory extends BeanFactory {

    @RequiredArgsConstructor
    enum AutowireCapability {
        /**
         * Constant that indicates no externally defined autowiring
         */
        AUTOWIRE_NO(0),
        /**
         * Constant that indicates autowiring bean properties by name
         * (applying to all bean property setters)
         */
        AUTOWIRE_BY_NAME(1),
        /**
         * Constant that indicates autowiring bean properties by type
         * (applying to all bean property setters).
         */
        AUTOWIRE_BY_TYPE(2),
        /**
         * Constant that indicates autowiring the greediest constructor that
         * can be satisfied (involves resolving the appropriate constructor)
         */
        AUTOWIRE_CONSTRUCTOR(3);

        @Getter
        final int capability;
    }
}
