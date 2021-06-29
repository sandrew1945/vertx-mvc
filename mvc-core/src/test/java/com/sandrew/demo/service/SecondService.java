package com.sandrew.demo.service;

import io.vertx.core.Promise;

import java.util.concurrent.CompletableFuture;

public class SecondService
{
    public Promise<String> doHandler2(String param)
    {
        Promise<String> promise = Promise.promise();
        try
        {
            CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
                try
                {
                    System.out.println("参数：" + param);
                    System.out.println("do sth in s_handler2");
                    Thread.sleep(4000);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                return "s_handler2";
            }).whenCompleteAsync((ret, throwable) -> {
                if (null == throwable)
                {
                    promise.complete(ret);
                }
                else
                {
                    promise.fail(throwable.getMessage());
                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
            promise.fail(e.getMessage());
        }
        return promise;
    }
}
