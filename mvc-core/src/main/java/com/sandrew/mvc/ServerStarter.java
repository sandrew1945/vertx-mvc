package com.sandrew.mvc;

import com.sandrew.mvc.verticles.MainVerticle;
import io.vertx.core.Vertx;

public class ServerStarter
{
    public static void start() throws InterruptedException
    {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new MainVerticle());
//        CountDownLatch latch = new CountDownLatch(1);
//        Vertx vertx = Vertx.vertx();
//        // 解析配置文件
//        ConfigurationHandler handler = new ConfigurationHandler();
//        handler.handleConfig(vertx, aVoid -> {
//            System.out.println("port" + vertx.getOrCreateContext().get("server-port"));
//            System.out.println("instances" + vertx.getOrCreateContext().get("server-instances"));
//            System.out.println("scan-basepackages" + vertx.getOrCreateContext().get("scan-basepackages"));
//            DeploymentOptions options = new DeploymentOptions();
//            Integer instancesCount = vertx.getOrCreateContext().get("server-instances");
//            options.setInstances(null == instancesCount ? 1 : instancesCount);
//            Config config = new Config();
//            config.setBasepackages(vertx.getOrCreateContext().get("scan-basepackages"));
//            config.setInstancesCount(instancesCount);
//            config.setPort(vertx.getOrCreateContext().get("server-port"));
//            options.setConfig(JsonObject.mapFrom(config));
//            vertx.deployVerticle("com.sandrew.mvc.verticles.MainVerticle", options).onSuccess(s -> {
//                latch.countDown();
//            });
//
//        });
//        latch.await();
    }
}
