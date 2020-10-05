package com.sythelib.plugins.sythelibapi.httpserver.controllers;

import com.google.gson.Gson;
import com.sythelib.plugins.sythelibapi.beans.ErrorBean;
import com.sythelib.plugins.sythelibapi.beans.TileSceneBean;
import com.sythelib.plugins.sythelibapi.beans.VarbitBean;
import com.sythelib.plugins.sythelibapi.httpserver.ClientThreadWrapper;
import com.sythelib.plugins.sythelibapi.httpserver.Controller;
import com.sythelib.plugins.sythelibapi.httpserver.Route;
import net.runelite.api.Client;

import javax.inject.Inject;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class VarbitController implements Controller
{

    @Inject
    private Gson gson;
    @Inject
    private Client client;
    @Inject
    private ClientThreadWrapper wrapper;

    @Route("/varbit")
    public String varbit(Map<String, String> params)
    {

        int varbit_query;
        try
        {
            varbit_query = Integer.parseInt(params.getOrDefault("varbit", "-1"));


        }
        catch (NumberFormatException ex)
        {
            return gson.toJson(ErrorBean.from("number format exception parsing "));
        }

        AtomicReference<VarbitBean> bean = new AtomicReference<>(null);

        wrapper.run(() ->
        {
            bean.set(VarbitBean.fromClient(client, varbit_query));

        });

        return gson.toJson(bean.get());
    }
}
