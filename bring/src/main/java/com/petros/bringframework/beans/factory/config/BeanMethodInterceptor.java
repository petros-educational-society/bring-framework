package com.petros.bringframework.beans.factory.config;

import com.petros.bringframework.beans.factory.support.DefaultBeanFactory;
import com.petros.bringframework.beans.factory.support.SimpleInstantiationStrategy;
import com.petros.bringframework.context.annotation.BeanAnnotationHelper;
import com.petros.bringframework.core.AssertUtils;
import com.petros.bringframework.util.ClassUtils;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import net.bytebuddy.implementation.bind.annotation.*;
import org.apache.commons.lang3.ObjectUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * Interceptor for enhancing bean methods.
 *
 * @author "Maksym Oliinyk"
 */
@Log4j2
public class BeanMethodInterceptor {

    /**
     * Intercepts the invocation of bean methods.
     *
     * @param enhancedConfigInstance The enhanced configuration instance
     * @param beanMethod             The method being invoked
     * @param args                   The arguments passed to the method
     * @param superMethod            The super method
     * @return The result of the method invocation
     */
    @RuntimeType
    public static Object intercept(@This Object enhancedConfigInstance,
                                   @Origin Method beanMethod,
                                   @AllArguments Object[] args,
                                   @SuperMethod Method superMethod) throws Throwable {
        DefaultBeanFactory beanFactory = getBeanFactory(enhancedConfigInstance);
        String beanName = BeanAnnotationHelper.determineBeanNameFor(beanMethod);

        // Determine whether this bean is a scoped-proxy
        if (BeanAnnotationHelper.isScopedProxy(beanMethod)) {
            log.error("Scoped proxies are not supported yet");
            throw new UnsupportedOperationException("Scoped proxies are not supported yet");
        }

        //if (factoryContainsBean(beanFactory, BeanFactory.FACTORY_BEAN_PREFIX + beanName)){
        //todo inplement factory bean
        //}

        if (isCurrentlyInvokedFactoryMethod(beanMethod)) {
            // The factory is calling the bean method in order to instantiate and register the bean
            // (i.e. via a getBean() call) -> invoke the super implementation of the method to actually
            // create the bean instance.
            if (log.isInfoEnabled() &&
                    BeanFactoryPostProcessor.class.isAssignableFrom(beanMethod.getReturnType())) {
                log.info(String.format("@Bean method %s.%s is non-static and returns an object " +
                                "assignable to Spring's BeanFactoryPostProcessor interface. This will " +
                                "result in a failure to process annotations such as @Autowired, " +
                                "@Resource and @PostConstruct within the method's declaring " +
                                "@Configuration class. Add the 'static' modifier to this method to avoid " +
                                "these container lifecycle issues; see @Bean javadoc for complete details.",
                        beanMethod.getDeclaringClass().getSimpleName(), beanMethod.getName()));
            }
            return superMethod.invoke(enhancedConfigInstance, args);
        }

        return resolveBeanReference(beanMethod, args, beanFactory, beanName);
    }

    /**
     * Resolves a reference to a bean.
     *
     * @param beanMethod   The method being invoked
     * @param args         The arguments passed to the method
     * @param beanFactory  The bean factory instance
     * @param beanName     The name of the bean
     * @return The resolved bean instance
     * @throws UnsupportedOperationException Thrown if arguments are specified, overriding specified default arguments in the bean definition
     * @throws IllegalStateException        Thrown if the bean instance does not match the return type of the method
     */
    private static Object resolveBeanReference(Method beanMethod, Object[] args,
                                               DefaultBeanFactory beanFactory, String beanName) {
        boolean useArgs = !ObjectUtils.isEmpty(args);
        if (useArgs && beanFactory.isSingleton(beanName)) {
            // Stubbed null arguments just for reference purposes,
            // expecting them to be autowired for regular singleton references?
            // A safe assumption since @Bean singleton arguments cannot be optional...
            for (Object arg : args) {
                if (arg == null) {
                    useArgs = false;
                    break;
                }
            }
        }
        if (useArgs) {
            final String msg = "Arguments have been specified " +
                    "-> specifying explicit constructor arguments / factory method arguments, overriding the specified default arguments (if any) in the bean definition is not allowed";
            log.error(msg);
            throw new UnsupportedOperationException(msg);
        }
        Object beanInstance = beanFactory.getBean(beanName);
        if (!ClassUtils.isAssignableValue(beanMethod.getReturnType(), beanInstance)) {
            String msg = String.format("@Bean method %s.%s called as bean reference " +
                            "for type [%s] but overridden by non-compatible bean instance of type [%s].",
                    beanMethod.getDeclaringClass().getSimpleName(), beanMethod.getName(),
                    beanMethod.getReturnType().getName(), beanInstance.getClass().getName());
            throw new IllegalStateException(msg);

        }
        Method currentlyInvoked = SimpleInstantiationStrategy.getCurrentlyInvokedFactoryMethod();
        if (currentlyInvoked != null) {
            String outerBeanName = BeanAnnotationHelper.determineBeanNameFor(currentlyInvoked);
            beanFactory.registerDependentBean(beanName, outerBeanName);
        }
        return beanInstance;

    }

    /**
     * Retrieves the bean factory from the enhanced configuration instance.
     *
     * @param enhancedConfigInstance The enhanced configuration instance
     * @return The bean factory
     */
    @SneakyThrows
    private static DefaultBeanFactory getBeanFactory(Object enhancedConfigInstance) {
        final Field field = enhancedConfigInstance.getClass().getDeclaredField("beanFactory");
        AssertUtils.state(field != null, "Unable to find generated bean factory field");
        field.setAccessible(true);
        AssertUtils.state(field != null, "Unable to find generated bean factory field");
        Object beanFactory = field.get(enhancedConfigInstance);
        AssertUtils.state(beanFactory != null, "BeanFactory has not been injected into @Configuration class");
        AssertUtils.state(beanFactory instanceof DefaultBeanFactory,
                "Injected BeanFactory is not a ConfigurableBeanFactory");
        return (DefaultBeanFactory) beanFactory;
    }

    /**
     * Check whether the given method corresponds to the container's currently invoked
     * factory method. Compares method name and parameter types only in order to work
     * around a potential problem with covariant return types (currently only known
     * to happen on Groovy classes).
     */
    private static boolean isCurrentlyInvokedFactoryMethod(Method method) {
        Method currentlyInvoked = SimpleInstantiationStrategy.getCurrentlyInvokedFactoryMethod();
        return (currentlyInvoked != null && method.getName().equals(currentlyInvoked.getName()) &&
                Arrays.equals(method.getParameterTypes(), currentlyInvoked.getParameterTypes()));
    }

}
