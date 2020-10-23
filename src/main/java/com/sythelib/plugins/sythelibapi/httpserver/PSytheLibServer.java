/*
 * Copyright (c) 2020, ThatGamerBlue <thatgamerblue@gmail.com>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.sythelib.plugins.sythelibapi.httpserver;

import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ClassInfo;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.sythelib.plugins.sythelibapi.PSytheLibPlugin;
import com.sythelib.plugins.sythelibapi.beans.ErrorBean;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.GameState;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;

@Slf4j
public class PSytheLibServer implements HttpHandler
{
    private static final String CONTROLLERS_PACKAGE = "com.sythelib.plugins.sythelibapi.httpserver.controllers";


    private final PSytheLibPlugin plugin;
    private final Gson gson;
    private final Map<String, RouteImpl> routes;

    private HttpServer internalServer;

    public PSytheLibServer(PSytheLibPlugin plugin, Gson gson, String port) throws IOException
    {
        this.plugin = plugin;
        this.gson = gson;
        this.routes = new HashMap<>();

        internalServer = HttpServer.create(new InetSocketAddress(Short.parseShort(port)), 0);
        internalServer.createContext("/", this);

        scanAndRegisterRoutes();

        internalServer.setExecutor(Executors.newSingleThreadExecutor());
    }

    private void scanAndRegisterRoutes() throws IOException
    {
        Set<ClassInfo> classes = ClassPath.from(this.getClass().getClassLoader()).getTopLevelClassesRecursive(CONTROLLERS_PACKAGE);

        for (ClassInfo ci : classes)
        {
            Class<?> clazz = ci.load();

            if (clazz == null)
            {
                continue;
            }

            if (!Controller.class.isAssignableFrom(clazz))
            {
                continue;
            }

            registerController((Controller) plugin.getInjector().getInstance(clazz));
        }
    }

    public void start()
    {
        internalServer.start();
    }

    public void stop()
    {
        internalServer.stop(1);
        routes.clear();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException
    {
        log.debug("Received request: {}", exchange.getRequestURI());
        log.info(exchange.getRequestMethod());
        String response = "";
        switch (exchange.getRequestMethod())
        {
            case "GET":
                response = invokeRoute(exchange.getRequestURI().toString());
                break;
            case "POST":
                response = invokeRoute(exchange.getRequestURI().toString(), dataFromInputStream(exchange.getRequestBody()));

        }
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, response.getBytes().length);
        exchange.getResponseBody().write(response.getBytes());
        exchange.getResponseBody().flush();
        exchange.close();
    }

    private void registerController(Controller controller)
    {
        log.debug("Registering {}", controller.getClass());
        for (Method method : controller.getClass().getMethods())
        {
            Route routeAnnotation = method.getDeclaredAnnotation(Route.class);
            if (routeAnnotation == null)
            {
                continue;
            }

            String route = routeAnnotation.value().toLowerCase();

            if (!route.startsWith("/"))
            {
                route = "/" + route;
            }

            if (routes.containsKey(route))
            {
                throw new IllegalStateException("Route " + route + " already registered");
            }


            /*
            This should be refactored for real
             */
            if (method.getParameterTypes().length > 2)
            {
                throw new IllegalStateException("Route methods must have the descriptor `public String name(Map<String, String> params)`");
            }
            else if (method.getParameterTypes().length == 1)
            {
                if(!Map.class.isAssignableFrom(method.getParameterTypes()[0])){
                    throw new IllegalStateException("Route methods must have the descriptor `public String name(Map<String, String> params)`");
                }

            }
            else if (method.getParameterTypes().length == 2)
            {
                if(!Map.class.isAssignableFrom(method.getParameterTypes()[0]) || !String.class.isAssignableFrom(method.getParameterTypes()[1])){
                    throw new IllegalStateException("Route methods must have the descriptor `public String name(Map<String, String> params)`");
                }
            }

            routes.put(route, new RouteImpl(controller, method));
        }
    }

    @SneakyThrows
    private String invokeRoute(String route)
    {
        String[] split = route.split("\\?", 2);
        route = split[0].toLowerCase();
        if (!route.startsWith("/"))
        {
            route = "/" + route;
        }

        if (!routes.containsKey(route))
        {
            return "Route " + route + " not found";
        }

        if (plugin.getClient().getGameState() != GameState.LOGGED_IN)
        {
            // skip invoking a route when they all require being logged in anyway
            return gson.toJson(ErrorBean.from("not logged in"));
        }

        Map<String, String> params = split.length == 2 ? parseParams(split[1]) : new HashMap<>();

        log.debug("invokeRoute > {} params: {}", route, params);

        RouteImpl impl = routes.get(route);
        return (String) impl.getMethod().invoke(impl.getController(), params);
    }

    @SneakyThrows
    private String invokeRoute(String route, String data)
    {
        String[] split = route.split("\\?", 2);
        route = split[0].toLowerCase();
        if (!route.startsWith("/"))
        {
            route = "/" + route;
        }

        if (!routes.containsKey(route))
        {
            return "Route " + route + " not found";
        }

        if (plugin.getClient().getGameState() != GameState.LOGGED_IN)
        {
            // skip invoking a route when they all require being logged in anyway
            return gson.toJson(ErrorBean.from("not logged in"));
        }

        Map<String, String> params = split.length == 2 ? parseParams(split[1]) : new HashMap<>();

        log.debug("invokeRoute > {} params: {}", route, params);

        RouteImpl impl = routes.get(route);
        return (String) impl.getMethod().invoke(impl.getController(), params, data);
    }

    private Map<String, String> parseParams(String queryString)
    {
        Map<String, String> params = new HashMap<>();
        String[] sets = queryString.split("&");
        for (String s : sets)
        {
            String[] param = s.split("=", 2);
            String left = param[0].toLowerCase();
            String right = param[1];
            params.put(left, right);
        }

        return params;
    }

    private String dataFromInputStream(InputStream ios) throws IOException
    {
        StringBuilder sb = new StringBuilder();
        int i;
        while ((i = ios.read()) != -1)
        {
            sb.append((char) i);
        }
        return sb.toString();
    }
}
