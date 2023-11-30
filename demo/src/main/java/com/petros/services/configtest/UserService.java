package com.petros.services.configtest;

import com.petros.bringframework.beans.factory.annotation.InjectPlease;
import com.petros.bringframework.context.annotation.Component;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
public class UserService {

    private NotificationService notificationServiceImpl;

    @InjectPlease
    public UserService(NotificationService notificationService) {
        this.notificationServiceImpl = notificationService;
    }

    public void processUser(String user) {
        log.info("Start processing user: {}", user);
        notificationServiceImpl.notifyUser(user);
        log.info("End processing user: {}", user);
    }

}
