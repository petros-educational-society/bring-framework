package com.web.petros.service;

import com.petros.bringframework.web.http.ResponseEntity;

/**
 * @author Viktor Basanets
 * @Project: bring-framework
 */
public interface PictureService {
    ResponseEntity<byte[]> getLargestPhoto(int sol);
}
