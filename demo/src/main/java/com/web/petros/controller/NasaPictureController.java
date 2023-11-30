package com.web.petros.controller;

import com.petros.bringframework.beans.factory.annotation.InjectPlease;
import com.petros.bringframework.context.annotation.Component;
import com.petros.bringframework.web.context.annotation.RequestMapping;
import com.petros.bringframework.web.context.annotation.RequestParam;
import com.petros.bringframework.web.context.annotation.RestController;
import com.petros.bringframework.web.servlet.support.common.RequestMethod;
import com.web.petros.service.NasaApiService;

import java.io.IOException;

import static java.lang.String.format;

/**
 * @author Viktor Basanets
 * @Project: bring-framework
 */
@Component
@RestController
public class NasaPictureController {

    @InjectPlease
    private NasaApiService service;

    @RequestMapping(path = "/api/nasa/photos/the-largest", method = RequestMethod.GET)
    public byte[] getLargestPhoto(@RequestParam(name = "sol") String sol) throws IOException {
        var apiKey = "hKfg7MJKtyIf7kiPZ6fHrlkw7yKh3BRZQdLgHxBR";
        var url = format("https://api.nasa.gov/mars-photos/api/v1/rovers/curiosity/photos?sol=%s&api_key=hKfg7MJKtyIf7kiPZ6fHrlkw7yKh3BRZQdLgHxBR", sol);
        var response = service.getLargestPicture(Integer.parseInt(sol), apiKey);
        return response.bytes();
    }
}
