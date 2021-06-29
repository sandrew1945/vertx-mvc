package com.sandrew.mvc;

import com.sandrew.mvc.annotation.Controller;
import com.sandrew.mvc.annotation.RequestMapping;
import com.sandrew.mvc.core.RequestMethod;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.auth.KeyStoreOptions;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.auth.jwt.JWTAuthOptions;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.JWTAuthHandler;
import io.vertx.ext.web.handler.SessionHandler;
import io.vertx.ext.web.sstore.LocalSessionStore;
import org.apache.commons.lang3.StringUtils;
import org.reflections.ReflectionUtils;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Created by summer on 2020/4/23.
 */
public class MainVerticle extends AbstractVerticle
{
    private HttpServer httpServer;
    private Router router;

    @Override
    public void start() throws Exception
    {
        httpServer = vertx.createHttpServer();
        router = Router.router(vertx);
        vertx.executeBlocking(this::parseMapping, this::resultHandler);
        // 解析配置文件，支持yaml,properties,jvm变量，优先级: jvm变量 > properties文件 > yaml文件
//        ConfigurationHandler handler = new ConfigurationHandler();
//        handler.handleConfig(vertx, this::parseMapping, this::resultHandler);
    }

//    private void parseConfig(Promise<Boolean> promise)
//    {
//        // 解析配置文件，支持yaml,properties,jvm变量，优先级: jvm变量 > properties文件 > yaml文件
//        try
//        {
//            ConfigurationHandler handler = new ConfigurationHandler(promise);
//            handler.handleConfig(vertx);
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace();
//        }
//    }

//    private void startServer(AsyncResult<Boolean> ar)
//    {
//        if (ar.succeeded())
//        {
//            vertx.executeBlocking(this::parseMapping, this::resultHandler);
//        }
//        else
//        {
//            System.err.println(ar.cause());
//        }
//    }

    /**
     *  解析工程中的Controller，并根据注解的值映射到router
     * @param promise
     */
    private void parseMapping(Promise<Boolean> promise)
    {
        try
        {
            // TODO Reflections's bug
            List<String> basePackages = config().getJsonArray("basepackages").getList();    // 暂时用不上
//            Reflections f = new Reflections(new ConfigurationBuilder().setUrls(basePackages).addScanners(new MethodParameterScanner(), new MethodParameterNamesScanner()));
//            Reflections f = new Reflections(new ConfigurationBuilder().forPackages("com.sandrew.demo", "com.sandrew.demo1").filterInputsBy(new FilterBuilder().exclude("/^.*?\\.(jceks)$/")).addScanners(new MethodParameterScanner(), new MethodParameterNamesScanner()));
            Reflections f = new Reflections("");
            Set<Class<?>> set = f.getTypesAnnotatedWith(Controller.class);
            for (Class<?> clz : set)
            {
                System.out.println("class : " + clz.getName());
                Router methodRouter = handleMethodRouter(clz);

                Router classRouter = Router.router(vertx);
                Controller controllerAnn = clz.getAnnotation(Controller.class);
                if (StringUtils.isEmpty(controllerAnn.value()))
                {
                    classRouter.mountSubRouter("/", methodRouter);
                }
                else
                {
                    classRouter.mountSubRouter(controllerAnn.value(), methodRouter);
                }
                // 处理请求body
                router.route().handler(BodyHandler.create());
                // 处理session
                LocalSessionStore store = LocalSessionStore.create(vertx);
                router.route().handler(SessionHandler.create(store));
                // JWT
                JWTAuthOptions authConfig = new JWTAuthOptions()
                        .setKeyStore(new KeyStoreOptions()
                        .setType("jceks")
                        .setPath("keystore.jceks")
                        .setPassword("qweasdzxc"));
                JWTAuth jwt = JWTAuth.create(vertx, authConfig);
                vertx.getOrCreateContext().config().put("jwt", jwt);
                router.mountSubRouter("/", classRouter);
            }
            promise.complete(true);
        }
        catch (Exception e)
        {
            promise.fail(e);
            e.printStackTrace();
        }
    }

    /**
     *  处理Controller方法，解析方法的注解
     * @param controllClz
     * @return
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    private Router handleMethodRouter(Class<?> controllClz) throws IllegalAccessException, InstantiationException
    {
        Object bean = controllClz.newInstance();
        Router methodRouter = Router.router(vertx);
        Set<Method> methodsInController = ReflectionUtils.getAllMethods(controllClz, ReflectionUtils.withAnnotation(RequestMapping.class));
        for (Method m : methodsInController)
        {

            System.out.println("method ----->" + m.getName());
            RequestMapping requestMapping = m.getAnnotation(RequestMapping.class);
            RequestMethod[] httpMethods = requestMapping.method();
            if (requestMapping.value().length == 0)
            {
                throw new RuntimeException("Method " + m.getName() + " in Controller " + controllClz.getName() + "'s value is null");
            }
            else
            {
                Arrays.stream(requestMapping.value()).forEach(path -> {
                    handleRouter(methodRouter, bean, m, path, httpMethods);
                });
            }
        }
        return methodRouter;
    }

    /**
     *  处理Controller方法，将解析的注解映射成route
     * @param router
     * @param originObj
     * @param invokeMethod
     * @param path
     * @param requestMethods
     */
    private void handleRouter(Router router, Object originObj, Method invokeMethod, String path, RequestMethod[] requestMethods)
    {
        Route route = router.route(path);
        if (null != requestMethods && requestMethods.length > 0)
        {
            Arrays.stream(requestMethods).forEach(requestMethod -> route.method(convert(requestMethod)));
        }
//        route.handler(ctx -> {
//            ctx.request().setExpectMultipart(true);
//            ctx.next();
//        });
        // TODO 通过白名单处理
        if (!"/login".equals(path))
        {
            route.handler(JWTAuthHandler.create((JWTAuth) vertx.getOrCreateContext().config().getValue("jwt")));
        }
        route.handler(context -> {
            try
            {
                RoutingContext[] routingContexts = new RoutingContext[]{context};
                invokeMethod.invoke(originObj, routingContexts);
            }
            catch (IllegalAccessException e)
            {
                e.printStackTrace();
            }
            catch (InvocationTargetException e)
            {
                e.printStackTrace();
            }
        });
        // 捕获异常
        route.failureHandler(failureCtx -> {

            int statusCode = failureCtx.statusCode();
            // Status code will be 500 for the RuntimeException
            // or 403 for the other failure
            HttpServerResponse response = failureCtx.response();
            response.setStatusCode(statusCode).end("Sorry! There have been some exceptions");
        });
    }

    /**
     *  服务器启动完成
     * @param ar
     */
    private void resultHandler(AsyncResult<Boolean> ar)
    {
        if (ar.succeeded())
        {
            Integer port = config().getInteger("port");
            System.out.println("Starting Http Server!");
            httpServer.requestHandler(router).listen(null == port ? 8080 : port);
            System.out.println("Http Server stated at port " + (null == port ? 8080 : port));
        }
        else
        {
            System.err.println(ar.cause());
        }
    }

    /**
     *  将RequestMethod转换为HttpMethod
     * @param requestMethod
     * @return
     */
    private HttpMethod convert(RequestMethod requestMethod)
    {
        switch (requestMethod)
        {
            case DELETE:
                return HttpMethod.DELETE;
            case GET:
                return HttpMethod.GET;
            case PUT:
                return HttpMethod.PUT;
            case HEAD:
                return HttpMethod.HEAD;
            case POST:
                return HttpMethod.POST;
            case PATCH:
                return HttpMethod.PATCH;
            case TRACE:
                return HttpMethod.TRACE;
            case OPTIONS:
                return HttpMethod.OPTIONS;
            default:
                return null;
        }

    }
}
