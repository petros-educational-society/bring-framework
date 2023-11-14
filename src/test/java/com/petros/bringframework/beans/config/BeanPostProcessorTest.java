package com.petros.bringframework.beans.config;

import com.petros.bringframework.beans.config.beans.*;
import com.petros.bringframework.beans.factory.BeanFactory;
import com.petros.bringframework.beans.factory.config.AutowiredAnnotationBeanPostProcessor;
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

@ExtendWith(MockitoExtension.class)
public class BeanPostProcessorTest {

    @Mock
    private BeanFactory beanFactory;

    private AutowiredAnnotationBeanPostProcessor processor;

    @BeforeEach
    void setUp() {
        processor = new AutowiredAnnotationBeanPostProcessor(beanFactory);
    }

    @Test
    void postProcessPropertyValuesWithInjectPlease() {
        InjectPleaseAnnotationTestBean bean = new InjectPleaseAnnotationTestBean();

        AutowiredCandidate autowiredCandidateImpl = new AutowiredCandidateImpl();
        Mockito.when(beanFactory.getBeansOfType(AutowiredCandidate.class))
                .thenReturn(Collections.singletonMap("autowiredCandidateImpl", autowiredCandidateImpl));

        processor.postProcessBeforeInitialization(bean, "testBean");

        assertEquals(autowiredCandidateImpl, bean.getAutowiredCandidate());
    }

    @Test
    void postProcessPropertyValuesWithNonExistingAutowiredCandidate() {
        InjectPleaseAnnotationTestBean bean = new InjectPleaseAnnotationTestBean();

        NoSuchBeanDefinitionException exception = assertThrows(
                NoSuchBeanDefinitionException.class,
                () -> processor.postProcessBeforeInitialization(bean, "testBean")
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
                () -> processor.postProcessBeforeInitialization(bean, "testBean")
        );

        assertTrue(exception.getMessage().contains("Expected single matching bean but found 2"));
    }

    @Test
    void postProcessPropertyValuesWithValue() {
        ValueAnnotationTestBean bean = new ValueAnnotationTestBean();

        processor.postProcessBeforeInitialization(bean, "testBean");

        assertEquals("value", bean.getAutowiredValue());
    }
}

