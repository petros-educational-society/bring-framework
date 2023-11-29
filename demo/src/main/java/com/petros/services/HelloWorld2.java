package com.petros.services;

public class HelloWorld2 {

    private final String name;

    public HelloWorld2(String name) {
        this.name = name;
    }


    public void apply() {
        System.out.println("Hello" + name + " from HelloWorld2");
    }

}
