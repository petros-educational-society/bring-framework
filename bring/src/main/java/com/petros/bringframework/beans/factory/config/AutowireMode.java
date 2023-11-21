package com.petros.bringframework.beans.factory.config;

import lombok.Getter;

/**
 * Enumerates the autowiring modes that specify how beans should be autowired.
 * Autowiring can be done by property name, by the type of the property, by constructor, or not at all.
 *
 * @author "Maksym Oliinyk"
 */
public enum AutowireMode {

    AUTOWIRE_NO(0),
    AUTOWIRE_BY_NAME(1),
    AUTOWIRE_BY_TYPE(2),
    AUTOWIRE_CONSTRUCTOR(3),
    AUTOWIRE_AUTODETECT(4);

    @Getter
    private final int value;

    AutowireMode(int value) {
        this.value = value;
    }

    public static AutowireMode valueOf(int value) {
        for (AutowireMode mode : values()) {
            if (mode.value == value) {
                return mode;
            }
        }
        throw new IllegalArgumentException("No such autowire mode " + value);
    }
}
