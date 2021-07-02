package com.sandrew.mvc.verticles;

import com.sandrew.mvc.log.Logger;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;

import java.util.ArrayList;
import java.util.List;

/**
 *  主控Verticle
 * Created by summer on 2020/4/23.
 */
public class MainVerticle extends AbstractVerticle
{
    private List<String> verticleIds = new ArrayList<>();


    @Override
    public void start() throws Exception
    {
        Vertx vertx = Vertx.vertx();
        // 启动控制服务器
        HttpServer httpServer = vertx.createHttpServer();
        Router router = Router.router(vertx);
        router.route("/stop").handler(context -> {
            Logger.debug("Waiting to stop httpserver.....");
            verticleIds.stream().forEach(vid -> {
                Logger.debug("httpserver(" + vid + ") is undeploy");
                vertx.undeploy(vid);
            });
            verticleIds.clear();
            context.response().end("HttpServer stoped!");
        });
//        router.route("/start").handler(context -> {
//            startHttpServer();
//            context.response().end("HttpServer started!");
//        });
        httpServer.requestHandler(router).listen(8888).onSuccess(server -> {
            Logger.debug("Management server is started!");
            // 启动配置解析verticle
            startHttpServer();
//            vertx.deployVerticle(new ConfigurationVerticle(), ar -> {
//                System.out.println("stringAsyncResult ---------->" + ar);
//                verticleIds.add(ar.result());
//            });
        });
    }

    private void startHttpServer()
    {
        if (verticleIds.size() >= 1)
        {
            Logger.debug("HttpServer has bean started!");
            return;
        }
        // 启动配置解析verticle
        vertx.deployVerticle(new ConfigurationVerticle(), ar -> {
            Logger.debug("HttpServer is started! vid:" + ar.result());
            verticleIds.add(ar.result());
        });
    }
}
