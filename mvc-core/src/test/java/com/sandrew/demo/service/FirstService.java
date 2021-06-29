package com.sandrew.demo.service;

import io.vertx.core.Promise;
import io.vertx.ext.web.FileUpload;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class FirstService
{
    public Promise<String> doHandler1(String param)
    {
        Promise<String> promise = Promise.promise();
        try
        {
            CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
                try
                {
                    Thread.sleep(1000);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                return "f_handler1";
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

    public Promise<String> doHandler2(Set<FileUpload> uploads)
    {
        Promise<String> promise = Promise.promise();
        try
        {
            CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
                try
                {
                    Thread.sleep(3000);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                System.out.println(uploads);
                uploads.stream().forEach(fileUpload -> {
                    System.out.println("file name ------> " + fileUpload.fileName());
                    System.out.println("file size ------> " + fileUpload.size());
                    System.out.println("name ------> " + fileUpload.name());
                });
                return "f_handler2";
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
