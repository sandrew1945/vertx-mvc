package com.sandrew.mvc.verticles;

import com.sandrew.mvc.log.Logger;
import io.vertx.core.Launcher;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;

import java.util.ArrayList;
import java.util.List;

public class MainLauncher extends Launcher
{
    private List<String> verticleIds = new ArrayList<>();


    @Override
    public void beforeStartingVertx(VertxOptions options)
    {
        Logger.debug("config vert.x options");
        super.beforeStartingVertx(options);
    }

    @Override
    public void afterStartingVertx(Vertx vertx)
    {
        Logger.debug("start vert.x");
        super.afterStartingVertx(vertx);
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
            Logger.info("Management server is started!");
            // 启动配置解析verticle
            startHttpServer(vertx);
        });
    }


    private void startHttpServer(Vertx vertx)
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
