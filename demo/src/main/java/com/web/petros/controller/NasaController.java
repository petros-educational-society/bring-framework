package com.web.petros.controller;

import com.petros.bringframework.context.annotation.Component;
import com.petros.bringframework.web.context.annotation.RequestMapping;

/**
 * @author Viktor Basanets
 * @Project: bring-framework
 */
@Component
public class NasaController {
    @RequestMapping(path = "/largest", method = "GET")
    public String getLargestPhoto() {
        return "catch the largest photo";
    }
}
