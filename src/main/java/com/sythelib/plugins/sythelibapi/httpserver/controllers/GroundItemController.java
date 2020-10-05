package com.sythelib.plugins.sythelibapi.httpserver.controllers;

import com.google.gson.Gson;
import com.sythelib.plugins.sythelibapi.beans.ErrorBean;
import com.sythelib.plugins.sythelibapi.beans.GameObjectBean;
import com.sythelib.plugins.sythelibapi.beans.GroundObjectBean;
import com.sythelib.plugins.sythelibapi.httpserver.ClientThreadWrapper;
import com.sythelib.plugins.sythelibapi.httpserver.Controller;
import com.sythelib.plugins.sythelibapi.httpserver.Route;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.Scene;
import net.runelite.api.Tile;
import net.runelite.api.TileItem;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class GroundItemController implements Controller
{
    @Inject
    private Gson gson;
    @Inject
    private Client client;
    @Inject
    private ClientThreadWrapper wrapper;

    @Route("/grounditems")
    public String grounditems(Map<String, String> params)
    {
        final List<GroundObjectBean> list = new ArrayList<>();

        int filter;
        try
        {
            filter = Integer.parseInt(params.getOrDefault("id", "-1"));
        }
        catch (NumberFormatException ex)
        {
            return gson.toJson(ErrorBean.from("number format exception parsing " + params.get("id")));
        }

        wrapper.run(() ->
        {
            Set<TileItem> objects = new LinkedHashSet<>();
            forEveryTile(tile ->
            {
                if (tile.getGroundItems() == null)
                {
                    return;
                }

                for (TileItem tileItem: tile.getGroundItems())
                {

                    if (tileItem == null)
                    {
                        continue;
                    }

                    if (filter == -1 || tileItem.getId() == filter)
                    {
                        objects.add(tileItem);
                    }
                }
            }, client);

            for (TileItem obj : objects)
            {
                GroundObjectBean bean = GroundObjectBean.fromGroundObject(obj, client);
                if (bean != null)
                {
                    list.add(bean);
                }
            }
        });

        return gson.toJson(list);
    }

    private void forEveryTile(Consumer<Tile> r, Client client)
    {
        Scene localScene = client.getScene();
        Tile[][][] zxyAry = localScene.getTiles();
        for (Tile[][] xyAry : zxyAry)
        {
            for (Tile[] yAry : xyAry)
            {
                for (Tile tile : yAry)
                {
                    if (tile == null)
                    {
                        continue;
                    }
                    r.accept(tile);
                }
            }
        }
    }

}
