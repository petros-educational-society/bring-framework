package com.petros.bringframework.beans.factory.config;

/**
 * Enumerates the roles that a BeanDefinition can have within the PETROS framework,
 * indicating its level of importance within the application context.
 *
 * @author "Maksym Oliinyk"
 */
public enum BeanDefinitionRole {

    /**
     * Role hint indicating that a {@code BeanDefinition} is a major part
     * of the application. Typically corresponds to a user-defined bean.
     */
    ROLE_APPLICATION(0),

    /**
     * Role hint indicating that a {@code BeanDefinition} is a supporting
     * part of some larger configuration, typically an outer
     * {@link ComponentDefinition}.
     * {@code SUPPORT} beans are considered important enough to be aware
     * of when looking more closely at a particular
     * {@link ComponentDefinition},
     * but not when looking at the overall configuration of an application.
     */
    ROLE_SUPPORT(1),

    /**
     * Role hint indicating that a {@code BeanDefinition} is providing an
     * entirely background role and has no relevance to the end-user. This hint is
     * used when registering beans that are completely part of the internal workings
     * of a {@link ComponentDefinition}.
     */
    ROLE_INFRASTRUCTURE(2);

    private final int role;

    /**
     * Constructs a new BeanDefinitionRole with the given role value.
     *
     * @param role the value corresponding to the bean definition role.
     */
    BeanDefinitionRole(int role) {
        this.role = role;
    }

    /**
     * Gets the role value associated with this BeanDefinitionRole.
     *
     * @return the role value.
     */
    public int getRole() {
        return this.role;
    }

    public static BeanDefinitionRole valueOf(int role) {
        for (BeanDefinitionRole mode : values()) {
            if (mode.role == role) {
                return mode;
            }
        }
        throw new IllegalArgumentException("No such bean definition role " + role);
    }
}
