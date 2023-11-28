package com.petros.bringframework.beans.config;

import com.petros.bringframework.beans.config.beans.*;
import com.petros.bringframework.beans.factory.BeanFactory;
import com.petros.bringframework.beans.factory.config.AutowiredAnnotationBeanPostProcessor;
import com.petros.bringframework.beans.factory.config.InitDestroyAnnotationBeanPostProcessor;
import com.petros.bringframework.beans.factory.support.NoSuchBeanDefinitionException;
import com.petros.bringframework.beans.factory.support.NoUniqueBeanDefinitionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BeanPostProcessorTest {

    @Mock
    private BeanFactory beanFactory;

    private AutowiredAnnotationBeanPostProcessor autowiredAnnotationBeanPostProcessor;
    private InitDestroyAnnotationBeanPostProcessor initDestroyAnnotationBeanPostProcessor;

    @BeforeEach
    void setUp() {
        autowiredAnnotationBeanPostProcessor = new AutowiredAnnotationBeanPostProcessor();
        initDestroyAnnotationBeanPostProcessor = new InitDestroyAnnotationBeanPostProcessor();
        autowiredAnnotationBeanPostProcessor.setBeanFactory(beanFactory);
    }

    @Test
    void postProcessPropertyValuesWithInjectPlease() {
        InjectPleaseAnnotationTestBean bean = new InjectPleaseAnnotationTestBean();

        AutowiredCandidate autowiredCandidateImpl = new AutowiredCandidateImpl();
        Mockito.when(beanFactory.getBeansOfType(AutowiredCandidate.class))
                .thenReturn(Collections.singletonMap("autowiredCandidateImpl", autowiredCandidateImpl));

        autowiredAnnotationBeanPostProcessor.postProcessBeforeInitialization(bean, "injectPleaseAnnotationTestBean");

        assertEquals(autowiredCandidateImpl, bean.getAutowiredCandidate());
    }

    @Test
    void postProcessPropertyValuesWithNonExistingAutowiredCandidate() {
        InjectPleaseAnnotationTestBean bean = new InjectPleaseAnnotationTestBean();

        NoSuchBeanDefinitionException exception = assertThrows(
                NoSuchBeanDefinitionException.class,
                () -> autowiredAnnotationBeanPostProcessor.postProcessBeforeInitialization(bean, "injectPleaseAnnotationTestBean")
        );

        assertEquals("No bean named 'com.petros.bringframework.beans.config.beans.AutowiredCandidate' available", exception.getMessage());
    }

    @Test
    void postProcessPropertyValuesWithMoreThenOneAutowiredCandidate() {
        InjectPleaseAnnotationTestBean bean = new InjectPleaseAnnotationTestBean();

        AutowiredCandidate autowiredCandidateImpl = new AutowiredCandidateImpl();
        AutowiredCandidate autowiredCandidateImpl2 = new AutowiredCandidateImpl2();
        Mockito.when(beanFactory.getBeansOfType(AutowiredCandidate.class))
                .thenReturn(Map.of("autowiredCandidateImpl", autowiredCandidateImpl,
                        "autowiredCandidateImpl2", autowiredCandidateImpl2));

        NoUniqueBeanDefinitionException exception = assertThrows(
                NoUniqueBeanDefinitionException.class,
                () -> autowiredAnnotationBeanPostProcessor.postProcessBeforeInitialization(bean, "injectPleaseAnnotationTestBean")
        );

        assertTrue(exception.getMessage().contains("Expected single matching bean but found 2"));
    }

    @Test
    void postProcessPropertyValuesWithValue() {
        ValueAnnotationTestBean bean = new ValueAnnotationTestBean();

        autowiredAnnotationBeanPostProcessor.postProcessBeforeInitialization(bean, "valueAnnotationTestBean");

        assertEquals("value", bean.getAutowiredValue());
    }

    @Test
    void postProcessWithInitPleaseAnnotation() {
        InitPleaseAnnotationTestBean bean = Mockito.spy(new InitPleaseAnnotationTestBean());

        initDestroyAnnotationBeanPostProcessor.postProcessBeforeInitialization(bean, "initPleaseAnnotationTestBean");

        verify(bean, times(1)).init();
    }

    @Test
    void postProcessWithDestroyPleaseAnnotation() {
        DestroyPleaseAnnotationTestBean bean = Mockito.spy(new DestroyPleaseAnnotationTestBean());

        initDestroyAnnotationBeanPostProcessor.postProcessBeforeDestruction(bean, "destroyPleaseAnnotationTestBean");

        verify(bean).after();
    }
}

