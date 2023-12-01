package com.web.petros.controller;

import com.petros.bringframework.context.annotation.Component;
import com.petros.bringframework.web.context.annotation.RequestBody;
import com.petros.bringframework.web.context.annotation.RequestMapping;
import com.petros.bringframework.web.context.annotation.RestController;
import com.petros.bringframework.web.servlet.support.common.RequestMethod;
import com.web.petros.controller.dto.User;

/**
 * @author Serhii Dorodko
 */
@RestController
@Component
public class UserController {

    @RequestMapping(path = "/user", method = RequestMethod.POST)
    public User getUser(@RequestBody User user) {
        return user;
    }
}
