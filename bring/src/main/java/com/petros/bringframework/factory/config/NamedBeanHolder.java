package com.petros.bringframework.factory.config;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * A simple holder for a given bean name plus bean instance.
 *
 * @author "Maksym Oliinyk"
 */
@Getter
@AllArgsConstructor
public class NamedBeanHolder<I> {
    private final String beanName;

    private final I beanInstance;
}
