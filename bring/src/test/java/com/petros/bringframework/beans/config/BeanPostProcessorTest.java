package com.petros.bringframework.beans.config;

import com.petros.bringframework.beans.config.beans.AutowiredCandidate;
import com.petros.bringframework.beans.config.beans.AutowiredCandidateImpl;
import com.petros.bringframework.beans.config.beans.AutowiredCandidateImpl2;
import com.petros.bringframework.beans.config.beans.DestroyPleaseAnnotationTestBean;
import com.petros.bringframework.beans.config.beans.InitPleaseAnnotationTestBean;
import com.petros.bringframework.beans.config.beans.InjectPleaseAnnotationTestBean;
import com.petros.bringframework.beans.config.beans.ValueAnnotationTestBean;
import com.petros.bringframework.beans.factory.BeanFactory;
import com.petros.bringframework.beans.factory.config.AutowiredAnnotationBeanPostProcessor;
import com.petros.bringframework.beans.factory.config.InitDestroyAnnotationBeanPostProcessor;
import com.petros.bringframework.beans.factory.support.NoSuchBeanDefinitionException;
import com.petros.bringframework.beans.factory.support.NoUniqueBeanDefinitionException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class BeanPostProcessorTest {

    @Mock
    private BeanFactory beanFactory;

    private AutowiredAnnotationBeanPostProcessor autowiredAnnotationBeanPostProcessor;
    private InitDestroyAnnotationBeanPostProcessor initDestroyAnnotationBeanPostProcessor;
    private final PrintStream originalOut = System.out;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

    @BeforeEach
    void setUp() {
        autowiredAnnotationBeanPostProcessor = new AutowiredAnnotationBeanPostProcessor();
        initDestroyAnnotationBeanPostProcessor = new InitDestroyAnnotationBeanPostProcessor();
        autowiredAnnotationBeanPostProcessor.setBeanFactory(beanFactory);
        System.setOut(new PrintStream(outputStreamCaptor));
    }

    @AfterEach
    public void tearDown() {
        System.setOut(originalOut);
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
        InitPleaseAnnotationTestBean bean = new InitPleaseAnnotationTestBean();

        initDestroyAnnotationBeanPostProcessor.postProcessBeforeInitialization(bean, "initPleaseAnnotationTestBean");

        assertTrue(outputStreamCaptor.toString().trim().contains("To begin, let me say:"));
    }

    @Test
    void postProcessWithDestroyPleaseAnnotation() {
        DestroyPleaseAnnotationTestBean bean = new DestroyPleaseAnnotationTestBean();

        initDestroyAnnotationBeanPostProcessor.postProcessBeforeDestruction(bean, "destroyPleaseAnnotationTestBean");

        assertTrue(outputStreamCaptor.toString().trim().contains("Good bye! Keep safe!"));
    }
}

