<h1 id="head-id" style="text-align: center">PETROS BRING PROJECT</h1>

<h2 id="introduction-id" style="text-align: center; line-height: 4">1. Introduction</h2>

The project was created as an educational part of the [Bobocode Ultimate 3.0](https://www.bobocode.com/java-ultimate-3-0) course.
The general idea of the project is to write a personal framework that repeats the general functionality of [Spring Framework](https://spring.io/projects/spring-framework). The main features are the [IoC container](https://docs.spring.io/spring-framework/docs/3.2.x/spring-framework-reference/html/beans.html) and the [DispatcherServlet](https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-servlet.html).
All the work was done by our [team](#team-id).

<h3 id="ioc-container-id" style="text-align: center; line-height: 4">1.1. IoC container</h3>

As part of the IoS implementation of the container, the following functionality was implemented:
- application context
- scanning all classes marked as [@Component](#dictionary-id) and [@Configuration](#dictionary-id) to create [BeanDefinitions](#dictionary-id)
- creation [Beans](#dictionary-id) from BeanDefinitions, including autowired Beans
- exception handling
- covered by tests
- implemented a lot of annotations: [@DestroyPlease](#dictionary-id), [@InitPlease](#dictionary-id), [@InjectPlease](#dictionary-id), [@Value](#dictionary-id), [@Bean](#dictionary-id), [@Component](#dictionary-id), [@ComponentScan](#dictionary-id), [@Configuration](#dictionary-id), [@DependsOn](#dictionary-id), [@Description](#dictionary-id), [@Primary](#dictionary-id), [@Role](#dictionary-id), [@Scope](#dictionary-id)



<h3 id="dispatcher-servlet-id" style="text-align: center; line-height: 4">1.2. Dispatcher Servlet</h3>

As part of the IoS implementation of the Dispatcher Servlet, the following functionality was implemented:
- application use embedded [Tomcat](https://tomcat.apache.org/)
- application can can receive and process [POST](https://en.wikipedia.org/wiki/HTTP#Request_methods) and [GET](https://en.wikipedia.org/wiki/HTTP#Request_methods) requests
- exception handling
- covered by tests
- implemented a lot of annotations: [@PathVariable](#dictionary-id), [@RequestBody](#dictionary-id), [@RequestHeader](#dictionary-id), [@RequestMapping](#dictionary-id), [@RequestParam](#dictionary-id), [@ResponseBody](#dictionary-id), [@RestController](#dictionary-id)



<h2 id="opportunities-id" style="text-align: center; line-height: 4">2. Opportunities</h2>

Bring Framework can be used as dependency. It will ensure the start of your Java application based on Tomcat and provide the ability to send and receive HTTP requests.

<h2 id="how-to-id" style="text-align: center; line-height: 4">3. How to</h2>


3.1. Create jar

3.2. Add jar as dependency

3.3. Create you own application

<h2 id="dictionary-id" style="text-align: center; line-height: 4" >4. Dictionary</h2>

- @Component - Indicates that an annotated class is a "component". Such classes are considered as candidates for auto-detection when using annotation-based configuration and classpath scanning.
- @Configuration - 
- @ComponentScan - Configures component scanning directives for use with @Configuration classes.
- @Bean -
- @DestroyPlease - Used on a method as a  callback notification to signal that the instance is in the  process of being removed by the container.
- @InitPlease - Used on a method that needs to be executed after dependency injection is done to perform any initialization.
- @InjectPlease - Marks a constructor, field, setter method, or config method as to be autowired by framework's dependency injection facilities.
- @Value - Used at the field or method/constructor parameter level that indicates a default value expression for the annotated element.
- @DependsOn - Beans on which the current bean depends.
- @Description - Adds a textual description to bean definitions derived from
- @Primary -
- @Role - Indicates the 'role' hint for a given bean.
- @Scope - Indicates the name of a scope to use for instances of the annotated type
- @PathVariable -
- @RequestBody -
- @RequestHeader -
- @RequestMapping -
- @RequestParam -
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

Many thanks to our mentor [Mykola Demchenko](https://github.com/mykolad4), without whom this project would have been much worse.
