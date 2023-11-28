package com.web.petros.controller;

import com.petros.bringframework.beans.factory.annotation.InjectPlease;
import com.petros.bringframework.web.context.annotation.RequestMapping;
import com.petros.bringframework.web.context.annotation.RequestParam;
import com.petros.bringframework.web.context.annotation.RestController;
import com.petros.bringframework.web.http.ResponseEntity;
import com.petros.bringframework.web.servlet.support.common.RequestMethod;
import com.web.petros.service.PictureService;

/**
 * @author Viktor Basanets
 * @Project: bring-framework
 */
@RestController
public class PictureController {
    @InjectPlease
    private PictureService pictureService;

    @RequestMapping(path = "/api/nasa/photos/largest", method = RequestMethod.GET)
    public ResponseEntity<byte[]> getLargest(@RequestParam(name = "sol") int sol) {
        return pictureService.getLargestPhoto(sol);
    }
}
