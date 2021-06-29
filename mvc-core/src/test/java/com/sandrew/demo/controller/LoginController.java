package com.sandrew.demo.controller;


import com.sandrew.mvc.annotation.Controller;
import com.sandrew.mvc.annotation.RequestMapping;
import com.sandrew.mvc.core.RequestMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.web.RoutingContext;

@Controller("/")
public class LoginController
{
    @RequestMapping(value = "/login", method = { RequestMethod.POST })
    public void login(RoutingContext context) throws Exception
    {
        try
        {
            JWTAuth jwtAuth = (JWTAuth) context.vertx().getOrCreateContext().config().getValue("jwt");
            System.out.println("jwt ------>" + jwtAuth);
            String username = context.request().getParam("username");
            String password = context.request().getParam("password");
            System.out.println("======>" + username);
            System.out.println("======>" + password);
            if ("admin".equals(username) && "123456".equals(password))
            {
                JsonObject payload = new JsonObject();
                payload.put("sub", username);
                payload.put("id", "1");
//                context.response().putHeader("content-type", "text/plain");
                context.response().end(jwtAuth.generateToken(payload));
            }
            else
            {
                context.fail(401);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            context.fail(500, e);
            throw new Exception(e.getMessage(), e);
        }
    }

}
