package com.petros.services.configtest;

import com.petros.bringframework.context.annotation.Component;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
public class NotificationServiceImpl implements NotificationService {

    private String email = "default";

    @Override
    public void notifyUser(String user) {
        log.info("Sending a message to {} with email {}", user, email);
    }

    public NotificationServiceImpl setEmail(String email) {
        this.email = email;
        return this;
    }
}
