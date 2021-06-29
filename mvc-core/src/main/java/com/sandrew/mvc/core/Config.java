package com.sandrew.mvc.core;

import io.vertx.core.json.JsonArray;

/**
 *  配置属性
 */
public class Config
{
    private JsonArray basepackages;

    private Integer port;

    private Integer instancesCount;

    public JsonArray getBasepackages()
    {
        return basepackages;
    }

    public void setBasepackages(JsonArray basepackages)
    {
        this.basepackages = basepackages;
    }

    public Integer getPort()
    {
        return port;
    }

    public void setPort(Integer port)
    {
        this.port = port;
    }

    public Integer getInstancesCount()
    {
        return instancesCount;
    }

    public void setInstancesCount(Integer instancesCount)
    {
        this.instancesCount = instancesCount;
    }
}
