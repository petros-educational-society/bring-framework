package com.petros.services;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class HelloWorld2 {

    private final String name;

    public HelloWorld2(String name) {
        this.name = name;
    }


    public void apply() {
        log.info("Hello {} from HelloWorld2", name);
    }

}
