package com.web.petros.controller;

import com.petros.bringframework.beans.factory.annotation.InjectPlease;
import com.petros.bringframework.beans.factory.annotation.Value;
import com.petros.bringframework.context.annotation.Component;
import com.petros.bringframework.web.context.annotation.RequestMapping;
import com.petros.bringframework.web.context.annotation.RequestParam;
import com.petros.bringframework.web.context.annotation.RestController;
import com.petros.bringframework.web.servlet.support.common.RequestMethod;
import com.web.petros.service.NasaApiService;

import java.io.IOException;

/**
 * @author Viktor Basanets
 * @Project: bring-framework
 */
@Component
@RestController
public class NasaPictureController {

    @InjectPlease
    private NasaApiService service;

    @Value(value = "api.key")
    private String apiKey;

    @RequestMapping(path = "/api/nasa/photos/the-largest", method = RequestMethod.GET)
    public byte[] getLargestPhoto(@RequestParam(name = "sol") String sol) throws IOException {
        return service.getLargestPicture(Integer.parseInt(sol), apiKey).bytes();
    }
}
