package com.sandrew.mvc.verticles;

import com.sandrew.mvc.core.Config;
import com.sandrew.mvc.core.ConfigurationHandler;
import com.sandrew.mvc.log.Logger;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by summer on 2020/4/23.
 */
public class ConfigurationVerticle extends AbstractVerticle
{
    private List<String> verticleIds = new ArrayList<>();

    @Override
    public void start() throws Exception
    {
        // 解析配置文件，支持yaml,properties,jvm变量，优先级: jvm变量 > properties文件 > yaml文件
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
            vertx.deployVerticle("com.sandrew.mvc.verticles.HttpServerVerticle", options, ar -> {
                verticleIds.add(ar.result());
            });
        });
    }

    @Override
    public void stop(Promise<Void> stopPromise) throws Exception
    {
        super.stop(stopPromise);
        verticleIds.stream().forEach(vid -> {
            Logger.debug("Undeploy verticle:" + vid);
            vertx.undeploy(vid);
        });
        verticleIds.clear();
    }
}
