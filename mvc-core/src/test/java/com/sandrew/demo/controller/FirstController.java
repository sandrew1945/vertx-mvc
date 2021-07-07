package com.sandrew.demo.controller;

import com.sandrew.demo.service.FirstService;
import com.sandrew.mvc.annotation.Controller;
import com.sandrew.mvc.annotation.RequestMapping;
import com.sandrew.mvc.core.RequestMethod;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpHeaders;
import io.vertx.ext.web.FileUpload;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.Session;

import java.util.List;
import java.util.Set;

@Controller("/f")
public class FirstController
{
    @RequestMapping(value = "/f_handler1", method = { RequestMethod.POST, RequestMethod.GET })
    public void f_handler1(RoutingContext context) throws Exception
    {
        try
        {
            List<String> param1 = context.queryParam("name");
            System.out.println(this.toString() + " do sth in f_handler1, param:" + param1);
            Session session = context.session();
            System.out.println("session id =======> " + session.id());
            session.put("sid", "1234567890");

            FirstService firstService = new FirstService();
            Promise<String> promise = firstService.doHandler1("p1");
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
            context.fail(500, e);
            throw new Exception(e.getMessage(), e);
        }
    }

//    private void preHandler1(Promise<Boolean> promise)
//    {
//        try
//        {
//            Thread.sleep(5000);
//        }
//        catch (Exception e)
//        {
//            promise.fail(e);
//            e.printStackTrace();
//        }
//    }
//    private void endHandler1(AsyncResult<Boolean> ar)
//    {
//        if (ar.succeeded())
//        {
//            System.out.println("start Http Server!");
//            httpServer.requestHandler(router).listen(8080);
//        }
//        else
//        {
//            System.err.println(ar.cause());
//        }
//    }

    @RequestMapping("/f_handler2")
    public void f_handler2(RoutingContext context) throws Exception
    {
        try
        {
            System.out.println("do sth in f_handler2, sid ====>" + context.session().get("sid"));
            FirstService firstService = new FirstService();

//            Thread.sleep(3000);
            System.out.println(context.getBodyAsString());
            Set<FileUpload> uploads = context.fileUploads();
            firstService.doHandler2(uploads);
//            System.out.println(uploads);
//            uploads.stream().forEach(fileUpload -> {
//                System.out.println("file name ------> " + fileUpload.fileName());
//                System.out.println("file size ------> " + fileUpload.size());
//                System.out.println("name ------> " + fileUpload.name());
//
//            });
            System.out.println(context.request().getParam("name"));
            System.out.println(context.request().getParam("age"));
            context.response().putHeader("content-type", "text/plain");
            context.response().end("f_handler2");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new Exception(e.getMessage(), e);
        }
    }

    /**
     *  测试获取jwt数据
     * @param context
     * @throws Exception
     */
    @RequestMapping("/f_handler3")
    public void f_handler3(RoutingContext context) throws Exception
    {
        try
        {
            System.out.println("do sth in f_handler3, uid ====>" + context.user().get("id"));
            System.out.println("do sth in f_handler3, sub ====>" + context.user().get("sub"));

            context.response().putHeader("content-type", "text/plain");
            context.response().end("f_handler3");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new Exception(e.getMessage(), e);
        }
    }

    /**
     *  测试返回文件
     * @param context
     * @throws Exception
     */
    @RequestMapping("/f_handler4")
    public void f_handler4(RoutingContext context) throws Exception
    {
        try
        {
            System.out.println("do sth in f_handler4");

            context.response().putHeader(HttpHeaders.CONTENT_ENCODING, HttpHeaders.IDENTITY);
            context.response().sendFile("/Users/summer/Documents/LocalFilePath/science/guest/2018110515475538281.xml");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new Exception(e.getMessage(), e);
        }
    }
}
