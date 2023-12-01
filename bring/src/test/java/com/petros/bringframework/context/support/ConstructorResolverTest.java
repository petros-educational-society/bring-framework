package com.petros.bringframework.context.support;

import com.petros.bringframework.beans.exception.BeanCreationException;
import com.petros.bringframework.beans.exception.BeanInstantiationException;
import com.petros.bringframework.beans.exception.ImplicitlyAppearedSingletonException;
import com.petros.bringframework.beans.factory.BeanDefinitionStoreException;
import com.petros.bringframework.beans.factory.support.AbstractAutowireCapableBeanFactory;
import com.petros.bringframework.beans.factory.support.BeanWrapper;
import com.petros.bringframework.beans.support.GenericBeanDefinition;
import lombok.Getter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Constructor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class ConstructorResolverTest {

    @Mock
    private AbstractAutowireCapableBeanFactory beanFactory;
    @Mock
    private GenericBeanDefinition bd;

    private ConstructorResolver constructorResolver;

    @BeforeEach
    void setUp() {
        this.constructorResolver = new ConstructorResolver(beanFactory);
    }

    @Test
    void autowireConstructorShouldThrowAnExceptionWhenConstructorArrayParamIsNull() {
        Mockito.when(bd.getBeanClassName())
                .thenReturn(BringClass.class.getName());

        BeanCreationException exception = assertThrows(
                BeanCreationException.class,
                () -> constructorResolver.autowireConstructor("bringBean", this.bd, null, new Object[]{})
        );

        String expectedResult = String.format("Error creating bean with name '%s': Could not resolve matching constructor on bean class [%s]", "bringBean", BringClass.class.getName());
        assertEquals(expectedResult, exception.getMessage());
    }

    @Test
    void autowireConstructorShouldThrowAnExceptionWhenConstructorArrayParamIsEmpty() {
        Mockito.when(bd.getBeanClassName())
                .thenReturn(BringClass.class.getName());

        BeanCreationException exception = assertThrows(
                BeanCreationException.class,
                () -> constructorResolver.autowireConstructor("bringBean", this.bd, new Constructor<?>[]{}, new Object[]{})
        );

        String expectedResult = String.format("Error creating bean with name '%s': Could not resolve matching constructor on bean class [%s]", "bringBean", BringClass.class.getName());
        assertEquals(expectedResult, exception.getMessage());
    }

    @Test
    void autowireConstructorShouldThrowAnExceptionWhenConstructorArrayParamSizeMoreThatOne() {
        Mockito.when(bd.getBeanClassName())
                .thenReturn(BringClass.class.getName());

        Constructor<?>[] constructors = BringClass.class.getDeclaredConstructors();

        BeanCreationException exception = assertThrows(
                BeanCreationException.class,
                () -> constructorResolver.autowireConstructor("bringBean", this.bd, constructors, new Object[]{})
        );

        String expectedResult = String.format("Error creating bean with name '%s': Ambiguous constructor matches found on bean class [%s]", "bringBean", BringClass.class.getName());
        assertEquals(expectedResult, exception.getMessage());
    }

    @Test
    void autowireConstructorShouldCreateNewInstanceByDefaultConstructor() throws NoSuchMethodException {
        Constructor<?>[] constructors = new Constructor[]{BringClass.class.getDeclaredConstructor()};
        BeanWrapper resultBean = constructorResolver.autowireConstructor("bringBean", this.bd, constructors, new Object[]{});

        assertNotNull(resultBean);
        assertEquals(BringClass.class, resultBean.wrappedInstance().getClass());
    }

    @Test
    void autowireConstructorShouldCreateNewInstanceByConstructorWithParam() throws NoSuchMethodException {
        Mockito.when(beanFactory.getBean(String.class))
                .thenReturn("Petros");

        Constructor<?>[] constructors = new Constructor[]{BringClass.class.getDeclaredConstructor(String.class)};
        Object[] args = new Object[]{String.class};
        BeanWrapper resultBean = constructorResolver.autowireConstructor("bringBean", this.bd, constructors, args);

        assertNotNull(resultBean);
        assertEquals(BringClass.class, resultBean.wrappedInstance().getClass());
        assertEquals("Petros", ((BringClass) resultBean.wrappedInstance()).getName());
    }

    @Test
    void autowireConstructorShouldThrowAnExceptionWhenClassIsAbstract() {
        Constructor<?>[] constructors = AbstractBringClass.class.getDeclaredConstructors();

        BeanInstantiationException exception = assertThrows(
                BeanInstantiationException.class,
                () -> constructorResolver.autowireConstructor("bringBean", this.bd, constructors, new Object[]{})
        );

        String expectedResult = String.format("Failed to instantiate [%s]: Is it an abstract class?", AbstractBringClass.class.getName());
        assertEquals(expectedResult, exception.getMessage());
    }

    @Test
    void autowireConstructorShouldThrowAnExceptionWhenConstructorIsPrivate() {
        Constructor<?>[] constructors = PrivateConstrBringClass.class.getDeclaredConstructors();

        BeanInstantiationException exception = assertThrows(
                BeanInstantiationException.class,
                () -> constructorResolver.autowireConstructor("bringBean", this.bd, constructors, new Object[]{})
        );

        String expectedResult = String.format("Failed to instantiate [%s]: Is the constructor accessible?", PrivateConstrBringClass.class.getName());
        assertEquals(expectedResult, exception.getMessage());
    }

    @Test
    void autowireConstructorShouldThrowAnExceptionWhenIllegalArgumentForConstructor() throws NoSuchMethodException {
        Mockito.when(beanFactory.getBean(Integer.class))
                .thenReturn(0);

        Constructor<?>[] constructors = new Constructor[]{BringClass.class.getDeclaredConstructor(String.class)};
        Object[] args = new Object[]{Integer.class};

        BeanInstantiationException exception = assertThrows(
                BeanInstantiationException.class,
                () -> constructorResolver.autowireConstructor("bringBean", this.bd, constructors, args)
        );

        String expectedResult = String.format("Failed to instantiate [%s]: Illegal arguments for constructor", BringClass.class.getName());
        assertEquals(expectedResult, exception.getMessage());
    }

    @Test
    void autowireConstructorShouldThrowAnExceptionWhenConstructorThrowException() throws NoSuchMethodException {
        Mockito.when(beanFactory.getBean(Integer.class))
                .thenReturn(0);

        Constructor<?>[] constructors = new Constructor[]{BringClass.class.getDeclaredConstructor(Integer.class)};
        Object[] args = new Object[]{Integer.class};

        BeanInstantiationException exception = assertThrows(
                BeanInstantiationException.class,
                () -> constructorResolver.autowireConstructor("bringBean", this.bd, constructors, args)
        );

        String expectedResult = String.format("Failed to instantiate [%s]: Constructor threw exception", BringClass.class.getName());
        assertEquals(expectedResult, exception.getMessage());
    }

    @Getter
    private static class BringClass {

        private String name;

        public BringClass() {}

        public BringClass(String name) {
            this.name = name;
        }

        public BringClass(Integer numb) {
            throw new RuntimeException();
        }
    }

    private static abstract class AbstractBringClass {
        public AbstractBringClass() {}
    }

    private static class PrivateConstrBringClass {
        private PrivateConstrBringClass() {}
    }

    @Test
    void instantiateUsingFactoryMethodShouldThrowExceptionWhenFactoryBeanNameEqualsBeanName() {
        Mockito.when(bd.getFactoryBeanName())
                .thenReturn("bringBean");
        Mockito.when(bd.getResourceDescription())
                .thenReturn("some description");

        BeanDefinitionStoreException exception = assertThrows(
                BeanDefinitionStoreException.class,
                () -> constructorResolver.instantiateUsingFactoryMethod("bringBean", bd, new Object[]{})
        );

        String expectedResult = "Invalid bean definition with name 'bringBean' defined in some description: factory-bean reference points back to the same bean definition";
        assertEquals(expectedResult, exception.getMessage());
    }

    @Test
    void instantiateUsingFactoryMethodShouldThrowExceptionWhenSingeltonBeanAlreadyExist() {
        Mockito.when(bd.getFactoryBeanName())
                .thenReturn("getBringBean");
        Mockito.when(bd.isSingleton())
                .thenReturn(true);
        Mockito.when(beanFactory.containsSingleton("bringBean"))
                .thenReturn(true);

        ImplicitlyAppearedSingletonException exception = assertThrows(
                ImplicitlyAppearedSingletonException.class,
                () -> constructorResolver.instantiateUsingFactoryMethod("bringBean", bd, new Object[]{})
        );

        String expectedResult = "About-to-be-created singleton instance implicitly appeared through the creation of the factory bean that its bean definition points to";
        assertEquals(expectedResult, exception.getMessage());
    }

    @Test
    void instantiateUsingFactoryMethodShouldInvokeProperMethods() {
        Mockito.when(bd.getFactoryBeanName())
                .thenReturn("getBringBean");
        Mockito.when(bd.isSingleton())
                .thenReturn(true);
        Mockito.when(beanFactory.containsSingleton("bringBean"))
                .thenReturn(false);
        Mockito.when(beanFactory.getBean("getBringBean"))
                        .thenReturn(new Object());

        constructorResolver.instantiateUsingFactoryMethod("bringBean", bd, new Object[]{});

        Mockito.verify(beanFactory).registerDependentBean("getBringBean", "bringBean");
        Mockito.verify(beanFactory).instantiateBean(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
    }

}