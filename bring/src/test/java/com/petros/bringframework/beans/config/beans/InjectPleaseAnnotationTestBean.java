package com.petros.bringframework.beans.config.beans;

import com.petros.bringframework.beans.factory.annotation.InjectPlease;
import lombok.Data;

@Data
public class InjectPleaseAnnotationTestBean {

    @InjectPlease
    private AutowiredCandidate autowiredCandidate;
}
