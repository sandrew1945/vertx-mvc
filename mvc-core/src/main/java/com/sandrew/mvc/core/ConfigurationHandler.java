package com.sandrew.mvc.core;

import com.sandrew.mvc.exceptions.ParseException;
import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.concurrent.CompletableFuture;

public class ConfigurationHandler
{

    public void handleConfig(Vertx vertx, Handler<Void> startServerHandler)
    {
        CompletableFuture<Void> future = CompletableFuture.supplyAsync(() -> {
            // 处理yaml格式store
            ConfigStoreOptions yamlStore = new ConfigStoreOptions()
                    .setType("file")
                    .setFormat("yaml")
                    .setConfig(new JsonObject().put("path", "config.yaml").put("hierarchical", true));

            // 处理properties格式store
            ConfigStoreOptions propStroe = new ConfigStoreOptions()
                    .setType("file")
                    .setFormat("properties")
                    .setConfig(new JsonObject().put("path", "config.properties").put("hierarchical", true));
            // 处理jvm变量store
            ConfigStoreOptions jvmStore = new ConfigStoreOptions()
                    .setType("sys")
                    .setConfig(new JsonObject().put("cache", false));

            ConfigRetrieverOptions options = new ConfigRetrieverOptions()
                    .addStore(yamlStore)
                    .addStore(propStroe)
                    .addStore(jvmStore);

            ConfigRetriever retriever = ConfigRetriever.create(vertx, options);

            retriever.configStream().handler(jsonObject -> {
                try
                {
                    // 解析配置文件
                    parseConfigration(jsonObject, vertx);
                    startServerHandler.handle(null);
//                    vertx.executeBlocking(parseMapping, startServer);
                }
                catch (ParseException e)
                {
                    e.printStackTrace();
                    throw new RuntimeException(e.getMessage(), e);
                }
            });
            return null;
        });
    }

    /**
     *  解析配置文件
     * @param config
     * @param vertx
     * @throws ParseException
     */
    private void parseConfigration(JsonObject config, Vertx vertx) throws ParseException
    {
        try
        {
            // 解析扫描包名
            parseBasepackages(config, vertx);
            // 解析服务器配置信息
            parseServerConfig(config, vertx);
        }
        catch (ParseException e)
        {
            throw new ParseException("configration parse error!", e);
        }
    }

    /**
     *  解析扫描的包名
     * @param config
     * @param vertx
     * @throws ParseException
     */
    private void parseBasepackages(JsonObject config, Vertx vertx) throws ParseException
    {
        try
        {
            // 解析扫描的包名
            JsonArray scanBasePackages = config.getJsonObject("mvc").getJsonObject("component").getJsonArray("scan-basepackages");
//            List<String> basePackages = new ArrayList<>();
//            scanBasePackages.getList().stream().forEach(pkg -> {
//                System.out.println("pkg ---->" + pkg);
//                basePackages.add(String.valueOf(pkg));
//            });
//            System.out.println("basePackages -=====》" + basePackages);
            vertx.getOrCreateContext().put("scan-basepackages", scanBasePackages);
        }
        catch (Exception e)
        {
            throw new ParseException("basepackage parse error!", e);
        }
    }

    /**
     *  解析服务器配置信息
     * @param config
     * @param vertx
     * @throws ParseException
     */
    private void parseServerConfig(JsonObject config, Vertx vertx) throws ParseException
    {
        try
        {
//            System.out.println("------<>" + config.getJsonObject("mvc").encodePrettily());
            // 解析服务器配置信息
            Integer port = config.getJsonObject("mvc").getJsonObject("server").getInteger("port");
            if (null == port)
            {
                throw new ParseException("server port is must");
            }
            vertx.getOrCreateContext().put("server-port", port);
            Integer instancesCount = config.getJsonObject("mvc").getJsonObject("server").getInteger("instances-count");
            if (null == instancesCount)
            {
                instancesCount = 1;
            }
            vertx.getOrCreateContext().put("server-instances", instancesCount);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new ParseException("server parse error!", e);
        }
    }
}
