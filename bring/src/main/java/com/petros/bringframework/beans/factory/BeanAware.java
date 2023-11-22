package com.petros.bringframework.beans.factory;

/**
 * Interface to be implemented by beans that wish to be aware of their owning BeanFactory.
 *
 * @author "Vasiuk Maryna"
 */
public interface BeanAware {

    void setBeanFactory(BeanFactory beanFactory);
}
