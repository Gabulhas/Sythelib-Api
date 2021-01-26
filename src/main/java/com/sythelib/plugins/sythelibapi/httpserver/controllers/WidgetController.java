package com.sythelib.plugins.sythelibapi.httpserver.controllers;

import com.google.gson.Gson;
import com.sythelib.plugins.sythelibapi.beans.ErrorBean;
import com.sythelib.plugins.sythelibapi.beans.NPCBean;
import com.sythelib.plugins.sythelibapi.beans.WidgetBean;
import com.sythelib.plugins.sythelibapi.httpserver.ClientThreadWrapper;
import com.sythelib.plugins.sythelibapi.httpserver.Controller;
import com.sythelib.plugins.sythelibapi.httpserver.Route;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.widgets.Widget;

@Slf4j
public class WidgetController implements Controller {
    @Inject
    private Gson gson;
    @Inject
    private Client client;
    @Inject
    private ClientThreadWrapper wrapper;

    @Route("/widget")
    public String getWidget(Map<String, String> params) {
        int x, y;
        try {
            x = Integer.parseInt(params.getOrDefault("i", "-1"));
            y = Integer.parseInt(params.getOrDefault("ii", "-1"));
        } catch (NumberFormatException ex) {
            return gson.toJson(ErrorBean.from("number format exception parsing "));
        }

        log.info("{} {}", x, y);
        AtomicReference<WidgetBean> bean = new AtomicReference<>();
        Widget widget = client.getWidget(x, y);
        if (widget == null) {
            return gson.toJson(ErrorBean.from("widget not found"));
        }
        wrapper.run(() ->
        {
            bean.set(WidgetBean.fromWidget(widget));
        });

        return gson.toJson(bean.get());
    }
}
