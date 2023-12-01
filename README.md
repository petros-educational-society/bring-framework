<h1 id="head-id" style="text-align: center">PETROS BRING PROJECT</h1>

<h2 id="introduction-id" style="text-align: center; line-height: 4">1. Introduction</h2>

The project was created as an educational part of the [Bobocode Ultimate 3.0](https://www.bobocode.com/java-ultimate-3-0) course.
The general idea of the project is to write a personal framework that repeats the general functionality of [Spring Framework](https://spring.io/projects/spring-framework). The main features are the [IoC container](https://docs.spring.io/spring-framework/docs/3.2.x/spring-framework-reference/html/beans.html) and the [DispatcherServlet](https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-servlet.html).
All the work was done by our [team](#team-id).

<h2 id="ioc-container-id" style="text-align: center; line-height: 4">2. IoC container</h2>

As part of the IoC implementation of the container, the following functionality was implemented:
- application context
- scanning all classes marked as [@Component](#dictionary-id) and [@Configuration](#dictionary-id) to create [BeanDefinitions](#dictionary-id)
- creation [Beans](#dictionary-id) from BeanDefinitions, including autowired Beans
- exception handling
- covered by tests
- implemented a lot of annotations: [@DestroyPlease](#dictionary-id), [@InitPlease](#dictionary-id), [@InjectPlease](#dictionary-id), [@Value](#dictionary-id), [@Bean](#dictionary-id), [@Component](#dictionary-id), [@ComponentScan](#dictionary-id), [@Configuration](#dictionary-id), [@DependsOn](#dictionary-id), [@Description](#dictionary-id), [@Primary](#dictionary-id), [@Role](#dictionary-id), [@Scope](#dictionary-id)

<h3 id="ioc-container-id" style="text-align: left; line-height: 4">2.1. IoC container</h3>

#### 2.1.1 What Is Inversion of Control?
Inversion of Control (IoC), also known as Dependency Injection (DI), s a principle in software engineering where objects 
specify what other objects they need to function (their dependencies) in various ways. These dependencies can be provided 
through constructors, factory method parameters, or by setting properties on the object after it's made or obtained from 
a factory method. Instead of the object itself creating or finding these needed objects, the IoC container takes on this 
responsibility and supplies the necessary dependencies when it creates the object (or bean).
This approach is called "Inversion of Control" because it flips the traditional method where objects themselves manage their dependencies.

The advantages of this architecture are:

* decoupling the execution of a task from its implementation
* making it easier to switch between different implementations
* greater modularity of a program
* greater ease in testing a program by isolating a component or mocking its dependencies, and allowing components to communicate through contracts

#### 2.1.2 What Is a Bean?
In summary, a bean is an object that is instantiated, assembled, and otherwise managed  by an IoC container, which simplifies many aspects of application development,
including resource management, dependency resolution, and lifecycle management.

#### 2.1.3 Container overview
The `com.petros.bringframework.context.ApplicationContext` interface acts as the IoC (Inversion of Control) container. 
Its main roles include creating, setting up, and putting together the beans mentioned earlier. 
This container figures out which objects it needs to create, how to set them up, and how to assemble them by interpreting 
configuration metadata. This metadata, which provides the necessary instructions, can be expressed through Java annotations or directly in Java code.

The `AnnotationConfigApplicationContext` serves as the main implementation of the `ApplicationContext` interface.

#### 2.1.4 Instantiating the container by using AnnotationConfigApplicationContext
To create the non-web application context, use `AnnotationConfigApplicationContext` which is tailored for Java-based configuration using annotations. 
This implementation is capable of accepting only [@Configuration] classes.
These [@Configuration] classes contain methods annotated with [@Bean], which provide the definitions for the creation of beans.

###### **You can use [@Configuration] classes as input when instantiating an AnnotationConfigApplicationContext**
```java
public static void main(String[] args) {
        final var annotationConfigApplicationContext
                = new AnnotationConfigApplicationContext(JavaConfig.class);

        var ms = annotationConfigApplicationContext.getBean(Test.class);
        ms.test();
}
```
###### Building the Container Programmatically by Using register(Class<?>… configs)
1. Start by creating a new instance of `AnnotationConfigApplicationContext` with the default constructor. At this point, the context is created but not yet fully configured with bean definitions.
```java
public static void main(String[] args) {
        final var annotationConfigApplicationContext
                = new AnnotationConfigApplicationContext();
}
```
2. Use the `register` method to add one or more classes annotated with `@Configuration` to the context. 
The register method is flexible, allowing you to dynamically choose which configurations include in your context. 
This method mandatory, if you want to use the `AnnotationConfigApplicationContext` constructor without parameters. 
```java
        var.register(JavaConfig.class, Config.class);
```
3. Manually Refresh the Context. After registering all necessary configurations and beans, manually call the refresh method. 
This step is crucial as it triggers the context to initialize the beans, perform dependency injection, and execute any other lifecycle processes.
The refresh operation is only needed once, after all registrations are done, to finalize the context setup.
```java
        var.refresh();
        var ms = annotationConfigApplicationContext.getBean(Test.class);
        ms.test();
}
```
###### Don't forget about component scanning
To enable component scanning, you must annotate your `@Configuration` class with `@ComponentScan`.
We knew that IoC container needed some classes. That's why for the detection and registration of beans in your application's classpath, 
don't forget about `@ComponentScan` annotation with the appropriate path to your packages ;)
```java
@Configuration
@ComponentScan(basePackages = "com.petros") 
public class JavaConfig  {

}
```

###### Building the Container Programmatically by Using scan(String… basePackages)
The `AnnotationConfigApplicationContext(String... basePackages)` constructor is a convenient way to initialize your application context. 
It allows you to specify one or more base packages for component scanning, enabling context to automatically detect and register beans.

To use this constructor, simply provide the base package names as arguments. Bring will then scan these packages for classes annotated with `@Component`, `@RestController`, or other stereotypes and register them as beans in the application context.
```java
       final var annotationConfigApplicationContext = new AnnotationConfigApplicationContext( "com.petros");

        var ms = annotationConfigApplicationContext.getBean(Test.class);
```

#### 2.1.5 Using the container
Once you have an instance of the `ApplicationContext`, you can use it to retrieve your beans.
The `ApplicationContext` is the interface for an advanced factory capable of maintaining a registry of different beans and their dependencies. 
Using the method `<T> T getBean(Class<T> requiredType)` you can retrieve instances of your beans.

The ApplicationContext enables you to read bean and access them as follows:
```java
        var ms = annotationConfigApplicationContext.getBean(Test.class);
        ms.test();
```

### 2.2  Bean overview
The objects that form the backbone of your application and that are managed by the IoC container are called beans. 
A bean is an object that is instantiated, assembled, and otherwise managed by a IoC container. 
These beans are created with the configuration metadata that you supply to the container.
For each bean, Bring will create a bean definition to hold the bean's configuration metadata.

Bean definition contains the information called configuration metadata, which is needed for the container to know the following:
* How to create a bean
* Bean's lifecycle details
* Bean's dependencies

###### 2.2.1 Instantiating Beans Using Annotations
Beans can be instantiated and managed by the IoC container using the following annotations: `@Bean`, `@Component`, and `@RestController`. 
Understanding how to use these annotations is crucial for effective application development.

Using `@Bean` <br>
Context: Primarily used in `@Configuration` annotated classes.<br>
Purpose: Defines a method as a bean producer. Each method annotated with `@Bean` produces a bean to be managed by the container.<br>
Customization: Allows custom logic for bean instantiation, making it suitable for more complex bean setup.

```java
@Configuration
public class AppConfig {
    @Bean
    public MyService myService() {
        return new MyServiceImpl();
    }
}
```
In this example, myService() method defines a bean of type MyService.<br>

Using `@Component`<br>
Context: Used on class level for automatic detection and registration of beans.<br>
Purpose: Marks a class as a component. When you use component scanning, context automatically detects and instantiates `@Component` annotated classes.<br>

```java
@Component
public class MyComponent {
// Class body
}
```
Here, `MyComponent` is automatically detected and instantiated by container.

Using `@RestController`<br>
Context: bean used for building RESTful web services.<br>
Usage: Ideal for creating RESTful web controllers.<br>

```java
@RestController("/hello")
public class MyRestController {
    public String hello() { 
        return "Hello World";
    }
}
```
In this example, `MyRestController` is a REST controller bean handling HTTP GET requests.

###### 2.2.2 Instantiating Multiple Beans of the Same Class
Bring framework allow to create multiple beans of the same class. But it will work for now only if one of the beans is marked as `@Primary`.
The simplest and easiest way to create multiple beans of the same class using java configuration
```java 
@Configuration
public class JavaConfig {
    @Bean
    @Primary
    public MergeSort personOne() {
        return new SequentiallyBasedRecursiveMergeSort();
    }

    @Bean
    public MergeSort personTwo() {
        return new ForkJoinPoolBasedRecursiveMergeSort();
    }
}
```
Here, `@Bean` instantiates two beans with ids the same as the method names, and registers them within the BeanFactory.
The `@Primary` annotation is used to indicate that a specific bean should be given preference when multiple candidates are qualified to autowire a single property.
Without `@Primary`, if there are multiple beans of the same type in the container, context doesn't know which one to inject or retrieve and will throw a `NoUniqueBeanDefinitionException`.

Another approach to create multiple beans, use the `@Component` annotation. You need create multiple subclasses that extend the superclass.
```java 
@Component
public class ForkJoinPoolBasedRecursiveMergeSort extends MergeSort {

    public ForkJoinPoolBasedRecursiveMergeSort() {
        super();
    }
}

@Component
public class SequentiallyBasedRecursiveMergeSort extends MergeSort {

    public SequentiallyBasedRecursiveMergeSort() {
        super();
    }
}
```
###### 2.2.3 Controlling Bean Creation Order with @DependsOn Annotation
Bring, by default, manages beans’ lifecycle and arranges their initialization order.
But, we can still customize it based on our needs using `@DependsOn` annotation. 
We should use this annotation for specifying bean dependencies. Bring guarantees that the defined beans will be 
initialized before attempting an initialization of the current bean.
```java
@Component
@DependsOn({"sequentiallyBasedRecursiveMergeSort"})
public class Test {}
```
In case of missing dependency, context throws a `NoSuchBeanDefinitionException`. <br>
```java
@Component
@DependsOn({"not exist"})
public class Test {}
```
In case of circular dependency, it throws `BeanCreationException` and highlights that the beans have a circular dependency <br>
```java
@Component
@DependsOn({"sequentiallyBasedRecursiveMergeSort"})
public class Test {}

@Component
@DependsOn({"test"})
public class SequentiallyBasedRecursiveMergeSort {}
```

###### 2.2.4 Using @Scope Annotation

### 2.3 Dependency injection
**Core Concept** <br>

**Dependency:** In software, a dependency is when one object (or class) relies on another to function correctly.<br>
**Injection:** Instead of an object creating or finding its dependencies, these dependencies are "injected" into the object by an external controller (the IoC container in Spring), typically at runtime.<br>

For the components to be processed as those that must be injected, it is necessary to specify the annotation `@InjectPlease` on the constructor or on the class property
###### 2.3.1 Constructor Injection: Dependencies are provided through class constructors.
Constructor-based DI is accomplished by the container invoking a constructor with a number of arguments, each representing a dependency.
```java
@Component
public class RetrofitNasaApiService implements NasaApiService {

    private final NasaApiClient nasaApiClient;
    private final MarsApiClient marsApiClient;

    @InjectPlease
    public RetrofitNasaApiService(NasaApiClient nasaApiClient,
                                  MarsApiClient marsApiClient) {
        this.nasaApiClient = nasaApiClient;
        this.marsApiClient = marsApiClient;
    }

}
```
If there are primitives in the constructor arguments, they will be initialized with default values.
Only one constructor can be annotated as requiring injection, otherwise Bring cannot resolve a bean for wiring, and it will throw an exception `BeanCreationException` with message ` Multiple autowired constructors found ...`.
###### 2.3.1 Property Injection: @InjectPlease on Properties



<h2 id="dispatcher-servlet-id" style="text-align: center; line-height: 4">3. Dispatcher Servlet</h2>

As part of the IoS implementation of the Dispatcher Servlet, the following functionality was implemented:
- application use embedded [Tomcat](https://tomcat.apache.org/)
- application can can receive and process [POST](https://en.wikipedia.org/wiki/HTTP#Request_methods) and [GET](https://en.wikipedia.org/wiki/HTTP#Request_methods) requests
- exception handling
- covered by tests
- implemented a lot of annotations: [@PathVariable](#dictionary-id), [@RequestBody](#dictionary-id), [@RequestHeader](#dictionary-id), [@RequestMapping](#dictionary-id), [@RequestParam](#dictionary-id), [@ResponseBody](#dictionary-id), [@RestController](#dictionary-id)



<h2 id="opportunities-id" style="text-align: center; line-height: 4">4. Opportunities</h2>

Bring Framework can be used as dependency. It will ensure the start of your Java application based on Tomcat and provide the ability to send and receive HTTP requests.

<h2 id="how-to-id" style="text-align: center; line-height: 4">3. How to</h2>


3.1. Create jar

3.2. Add jar as dependency

3.3. Create you own application

<h2 id="dictionary-id" style="text-align: center; line-height: 4" >5. Dictionary</h2>

- @Component - Indicates that an annotated class is a "component". Such classes are considered as candidates for auto-detection when using annotation-based configuration and classpath scanning.
- @Configuration - Indicating that an object is a source of bean definitions.
- @ComponentScan - Configures component scanning directives for use with @Configuration classes.
- @Bean - Indicates that a method produces a bean to be managed by the Bring container.
- @DestroyPlease - Used on a method as a  callback notification to signal that the instance is in the  process of being removed by the container.
- @InitPlease - Used on a method that needs to be executed after dependency injection is done to perform any initialization.
- @InjectPlease - Marks a constructor, field, setter method, or config method as to be autowired by framework's dependency injection facilities.
- @Value - Used at the field or method/constructor parameter level that indicates a default value expression for the annotated element.
- @DependsOn - Beans on which the current bean depends.
- @Description - Adds a textual description to bean definitions derived from.
- @Primary - Indicates that a bean should be given preference when multiple candidates are qualified.
- @Role - Indicates the 'role' hint for a given bean.
- @Scope - Indicates the name of a scope to use for instances of the annotated type
- @PathVariable - Marks a method parameter as being bound to a URI template variable.
- @RequestBody - Marks a method parameter as being bound to the body of the HTTP request. This annotation indicates that the method parameter should be populated with the contents of the HTTP request body.
- @RequestHeader - Marks a method parameter as being bound to a specific header of the HTTP request. This annotation indicates that the method parameter should be populated with the value of the specified header from the HTTP request.
- @RequestMapping - An annotation for mapping web requests onto methods in request-handling classes. Used to specify the path and HTTP request method for handling a particular request.
- @RequestParam - Annotation used to bind web request parameters to method parameters. Indicates that a method parameter should be bound to a web request parameter.
- @ResponseBody - Indicates a method return value should be bound to the web response body.
- @RestController - A convenience annotation that is itself annotated with Types that carry this annotation are treated as controllers where @ResponseBody semantics by default.
- BeanDefinition - Describes a bean instance, which has property values, constructor argument values, and further information supplied by concrete implementations.
- [Bean](https://docs.spring.io/spring-framework/reference/core/beans/definition.html)


<h2 id="team-id" style="text-align: center; line-height: 4">5. Team</h2>
1. [Viktor Basanets](https://github.com/ViktorBasanets)
2. [Marina Vasiuk](https://github.com/marishkavasiuk)
3. [Oleksii Skachkov](https://github.com/hamster4n)
4. [Vadym Vovk](https://github.com/vadymvovk)
5. [Maksym Oliinyk](https://github.com/WHALE88)
6. [Sergiy Dorodko](https://github.com/serhiidorodko)

Many thanks to our mentor [Mykola Demchenko](https://github.com/mykolad4), without whom this project would have been much worse... or better:)
