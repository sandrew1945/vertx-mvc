package com.sandrew.demo.controller;

import com.sandrew.demo.model.UserBean;
import com.sandrew.demo.service.SecondService;
import com.sandrew.mvc.annotation.Controller;
import com.sandrew.mvc.annotation.RequestMapping;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

@Controller("/s")
public class SecondController
{
    @RequestMapping("/s_handler1")
    public void s_handler1(RoutingContext context) throws Exception
    {
        try
        {
            System.out.println("do sth in s_handler1 for handle json");
            JsonObject json = context.getBodyAsJson();
            UserBean user = json.mapTo(UserBean.class);
            System.out.println(user.getName());
            System.out.println(user.getAge());
            System.out.println("json -----> " + json);
            context.response().putHeader("content-type", "text/plain");
            context.response().end("s_handler1");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new Exception(e.getMessage(), e);
        }
    }

    @RequestMapping("/s_handler2")
    public void s_handler2(RoutingContext context)
    {
        try
        {
//            CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
//                try
//                {
//                    System.out.println("do sth in s_handler2");
//                    Thread.sleep(1000);
//                }
//                catch (InterruptedException e)
//                {
//                    e.printStackTrace();
//                }
//                return "s_handler2";
//            });
//            future.whenCompleteAsync((ret, throwable) -> {
//                context.response().putHeader("content-type", "text/plain");
//                context.response().end(ret);
//            });
            SecondService secondService = new SecondService();
            Promise<String> promise = secondService.doHandler2("v1");
            Future<String> future = promise.future();
            future.onSuccess(ret -> {
                context.response().putHeader("content-type", "text/plain");
                context.response().end(ret);
            })
            .onFailure(throwable -> {
                throw new RuntimeException(throwable);
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw e;
        }
    }

}
