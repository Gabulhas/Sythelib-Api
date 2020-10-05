package com.sythelib.plugins.sythelibapi.httpserver.controllers;

import com.google.gson.Gson;
import com.sythelib.plugins.sythelibapi.beans.ErrorBean;
import com.sythelib.plugins.sythelibapi.beans.VarplayerBean;
import com.sythelib.plugins.sythelibapi.httpserver.ClientThreadWrapper;
import com.sythelib.plugins.sythelibapi.httpserver.Controller;
import com.sythelib.plugins.sythelibapi.httpserver.Route;
import net.runelite.api.Client;
import net.runelite.api.MenuOpcode;

import javax.inject.Inject;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class VarplayerController implements Controller
{

    @Inject
    private Gson gson;
    @Inject
    private Client client;
    @Inject
    private ClientThreadWrapper wrapper;

    @Route("/varplayer")
    public String varplayer(Map<String, String> params)
    {

        int varplayer_query;
        try
        {
            varplayer_query = Integer.parseInt(params.getOrDefault("varplayer", "-1"));


        } catch (NumberFormatException ex)
        {
            return gson.toJson(ErrorBean.from("number format exception parsing "));
        }

        AtomicReference<VarplayerBean> bean = new AtomicReference<>(null);

        wrapper.run(() ->
        {
            bean.set(VarplayerBean.fromClient(client, varplayer_query));

        });

        return gson.toJson(bean.get());
    }
}
