package com.web.petros.controller;

import com.petros.bringframework.context.annotation.Component;
import com.petros.bringframework.web.context.annotation.PathVariable;
import com.petros.bringframework.web.context.annotation.RequestBody;
import com.petros.bringframework.web.context.annotation.RequestHeader;
import com.petros.bringframework.web.context.annotation.RequestMapping;
import com.petros.bringframework.web.context.annotation.RequestParam;
import com.petros.bringframework.web.context.annotation.RestController;
import com.petros.bringframework.web.servlet.support.common.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@RestController
public class ControllerDummy {

    @RequestMapping(path = "/user", method = RequestMethod.HEAD)
    public void head(HttpServletResponse response){
        response.setStatus(HttpServletResponse.SC_OK);
    }

    @RequestMapping(path = "/user/{id}", method = RequestMethod.GET)
    public String getUser(@RequestParam(name = "name") String name,
                          @RequestHeader(name = "location") String location,
                          @PathVariable(name = "id") String id){
        return "Received parameter is " + name + "; Received header is " + location + "; Received path variable " + id;
    }

    @RequestMapping(path = "/user/{id}/post/{postId}", method = RequestMethod.GET)
    public String getPost(@PathVariable(name = "id") String id,
                          @PathVariable(name = "postId") String postId){
        return "Received path variable id = " + id + "; postId = " + postId;
    }

    @RequestMapping(path = "/user/{id}/post/{postId}", method = RequestMethod.POST)
    public String createPost(@RequestBody String requestBody,
                             @RequestParam(name = "name") String name,
                             @RequestHeader(name = "location") String location,
                             @PathVariable(name = "id") String id,
                             @PathVariable(name = "postId") String postId,
                             HttpServletRequest request,
                             HttpServletResponse response){
        return String.format("""
              Request Body: %s
              Request Param: name=%s
              Header: location=%s
              Received path variable id = %s; postId = %s
              Request.getPathInfo(): %s
              Response.getClass(): %s
              """,
                requestBody, name, location, id, postId, request.getPathInfo(), response.getClass());
    }
}
