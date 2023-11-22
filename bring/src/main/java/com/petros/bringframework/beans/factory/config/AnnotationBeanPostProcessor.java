package com.petros.bringframework.beans.factory.config;

/**
 *  BeanPostProcessor implementations may implement this sub-interface in order to post-process
 *  the beans firstly before applying other postprocessing logic:
 *  {@link com.petros.bringframework.beans.factory.config.AutowiredAnnotationBeanPostProcessor}
 *
 * @author "Vasiuk Maryna"
 */
public interface AnnotationBeanPostProcessor extends BeanPostProcessor {

}
