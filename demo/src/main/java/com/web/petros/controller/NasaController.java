package com.web.petros.controller;

import com.petros.bringframework.context.annotation.Component;
import com.petros.bringframework.web.context.annotation.RequestMapping;
import com.petros.bringframework.web.servlet.support.common.RequestMethod;

/**
 * @author Viktor Basanets
 * @Project: bring-framework
 */
//@Component
public class NasaController {
    @RequestMapping(path = "/largest", method = RequestMethod.GET)
    public String getLargestPhoto() {
        return "catch the largest photo";
    }
}
