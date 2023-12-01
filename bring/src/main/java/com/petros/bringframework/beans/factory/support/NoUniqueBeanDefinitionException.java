package com.petros.bringframework.beans.factory.support;

import com.petros.bringframework.beans.BeansException;
import com.petros.bringframework.util.ClassUtils;

import javax.annotation.Nullable;
import java.util.Collection;

/**
 * Exception thrown when a {@code BeanFactory} is asked for a bean instance for which
 * multiple matching candidates have been found when only one matching bean was expected.
 *
 * @author "Vasiuk Maryna"
 */
public class NoUniqueBeanDefinitionException extends BeansException {

    private final int numberOfBeansFound;

    @Nullable
    private final Collection<String> beanNamesFound;

    public NoUniqueBeanDefinitionException(Collection<String> beanNamesFound) {
        super("Expected single matching bean but found " + beanNamesFound.size() + ": " +
                ClassUtils.collectionToCommaDelimitedString(beanNamesFound));
        this.numberOfBeansFound = beanNamesFound.size();
        this.beanNamesFound = beanNamesFound;
    }

    public NoUniqueBeanDefinitionException(int numberOfBeansFound, String msg) {
        super(msg);
        this.numberOfBeansFound = numberOfBeansFound;
        this.beanNamesFound = null;
    }
}
