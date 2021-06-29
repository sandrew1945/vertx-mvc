package com.sandrew.mvc;

import com.sandrew.mvc.core.Config;
import com.sandrew.mvc.core.ConfigurationHandler;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

import java.util.concurrent.CountDownLatch;

public class ServerStarter
{
    public static void start() throws InterruptedException
    {
        CountDownLatch latch = new CountDownLatch(1);
        Vertx vertx = Vertx.vertx();
        // 解析配置文件
        ConfigurationHandler handler = new ConfigurationHandler();
        handler.handleConfig(vertx, aVoid -> {
            System.out.println("port" + vertx.getOrCreateContext().get("server-port"));
            System.out.println("instances" + vertx.getOrCreateContext().get("server-instances"));
            System.out.println("scan-basepackages" + vertx.getOrCreateContext().get("scan-basepackages"));
            DeploymentOptions options = new DeploymentOptions();
            Integer instancesCount = vertx.getOrCreateContext().get("server-instances");
            options.setInstances(null == instancesCount ? 1 : instancesCount);
            Config config = new Config();
            config.setBasepackages(vertx.getOrCreateContext().get("scan-basepackages"));
            config.setInstancesCount(instancesCount);
            config.setPort(vertx.getOrCreateContext().get("server-port"));
            options.setConfig(JsonObject.mapFrom(config));
            vertx.deployVerticle("com.sandrew.mvc.MainVerticle", options).onSuccess(s -> {
                latch.countDown();
            });

        });
        latch.await();
    }
}
