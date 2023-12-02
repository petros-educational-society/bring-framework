package com.web.petros.controller;

import com.petros.bringframework.context.annotation.Component;
import com.petros.bringframework.web.context.annotation.PathVariable;
import com.petros.bringframework.web.context.annotation.RequestBody;
import com.petros.bringframework.web.context.annotation.RequestMapping;
import com.petros.bringframework.web.context.annotation.RestController;
import com.petros.bringframework.web.servlet.support.common.RequestMethod;
import com.web.petros.controller.dto.Teammate;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Serhii Dorodko
 */
@RestController
@Component
public class TeammateController {

    private final Map<Integer, Teammate> team = new HashMap<>();
    private final AtomicInteger counter = new AtomicInteger();

    @RequestMapping(path = "/teammate/{id}", method = RequestMethod.GET)
    public Teammate getTeammate(@PathVariable(name = "id") String id) {
            return team.get(Integer.valueOf(id));
    }

    @RequestMapping(path = "/teammate", method = RequestMethod.POST)
    public void createTeammate(@RequestBody Teammate teammate, HttpServletResponse response) {
        int id = counter.getAndIncrement();
        team.put(id, teammate);
        response.setHeader("location", "/teammate/" + id);
    }

}
