package com.petros.bringframework.beans.config.beans;

import com.petros.bringframework.beans.factory.annotation.Value;
import lombok.Data;

@Data
public class ValueAnnotationTestBean {

    @Value(value = "autowiredValue")
    private String autowiredValue;
}
