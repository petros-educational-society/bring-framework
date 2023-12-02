package com.petros;

import com.petros.bringframework.context.annotation.AnnotationConfigApplicationContext;
import com.petros.config.JavaConfig;
import com.petros.services.HelloWorld2;
import com.petros.services.PrototypeTest;
import com.petros.services.SayHello;
import com.petros.services.Test;
import com.petros.services.configtest.UserService;
import lombok.extern.log4j.Log4j2;

import java.util.Arrays;

@Log4j2
public class BringDemo {
    public static void main(String[] args) {

        final var annotationConfigApplicationContext
//                = new AnnotationConfigApplicationContext( "com.petros");
                = new AnnotationConfigApplicationContext(JavaConfig.class);

        var ms = annotationConfigApplicationContext
                .getBean(Test.class);

        Integer[] arr = {5, 8, 0, 1, 4, -3};
        log.info("Before: {}", Arrays.toString(arr));
        ms.testMerge(arr);
        log.info("After: {}", Arrays.toString(arr));

        var sayHello = annotationConfigApplicationContext.getBean(SayHello.class);
        sayHello.print();

        var helloWorld2 = annotationConfigApplicationContext.getBean(HelloWorld2.class);
        helloWorld2.apply();

        JavaConfig bean = annotationConfigApplicationContext.getBean(JavaConfig.class);
        log.info("JavaConfig proxy: {}",bean.toString());

        //should be converted to test
        UserService userService = annotationConfigApplicationContext.getBean(UserService.class);
        userService.processUser("Maksym");

        var prototype1 = annotationConfigApplicationContext.getBean(PrototypeTest.class);
        var prototype2 = annotationConfigApplicationContext.getBean(PrototypeTest.class);
        log.info("PrototypeTest: {}", prototype1.equals(prototype2));
    }
}
