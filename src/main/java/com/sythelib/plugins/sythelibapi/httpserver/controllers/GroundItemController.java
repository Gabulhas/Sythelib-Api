package com.sythelib.plugins.sythelibapi.httpserver.controllers;

import com.google.gson.Gson;
import com.sythelib.plugins.sythelibapi.beans.ErrorBean;
import com.sythelib.plugins.sythelibapi.beans.GroundObjectBean;
import com.sythelib.plugins.sythelibapi.httpserver.ClientThreadWrapper;
import com.sythelib.plugins.sythelibapi.httpserver.Controller;
import com.sythelib.plugins.sythelibapi.httpserver.Route;
import net.runelite.api.Client;
import net.runelite.api.Scene;
import net.runelite.api.Tile;
import net.runelite.api.TileItem;
import net.runelite.api.coords.WorldPoint;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import static com.sythelib.plugins.sythelibapi.utils.distanceUtils.nearestToPoint;

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

        int id;
        String name;
        try
        {
            id = Integer.parseInt(params.getOrDefault("id", "-1"));
            name = params.getOrDefault("name", "").replace("%20", " ");

        } catch (NumberFormatException ex)
        {
            return gson.toJson(ErrorBean.from("number format exception parsing " + params.get("id")));
        }

        wrapper.run(() ->
        {
            Set<TileItem> objects = getTileItems(id, name, client);
            groundItemsToBeansList(client, objects, list);
        });

        return gson.toJson(list);
    }

    @Route("/grounditems/nearest")
    public String grounditens_nearest(Map<String, String> params)
    {

        int id, x, y, z;
        String name;
        try
        {
            id = Integer.parseInt(params.getOrDefault("id", "-1"));
            name = params.getOrDefault("name", "").replace("%20", " ");
            x = Integer.parseInt(params.getOrDefault("x", "-1"));
            y = Integer.parseInt(params.getOrDefault("y", "-1"));
            z = Integer.parseInt(params.getOrDefault("z", "0"));

        } catch (NumberFormatException ex)
        {
            return gson.toJson(ErrorBean.from("number format exception parsing " + params.get("id")));
        }

        if (x == -1 || y == -1)
        {
            return grounditems_nearest_aux(id, name, client.getLocalPlayer().getWorldLocation());
        }
        return grounditems_nearest_aux(id, name, new WorldPoint(x, y, z));


    }

    public String grounditems_nearest_aux(int id, String name, WorldPoint point)
    {
        AtomicReference<GroundObjectBean> bean = new AtomicReference<>();

        wrapper.run(() ->
        {
            Set<TileItem> objects = getTileItems(id, name, client);
            TileItem nearestTileItem = nearestToPoint(objects, point);
            if (nearestTileItem == null)
            {
                bean.set(null);
            } else
            {
                bean.set(GroundObjectBean.fromGroundObject(nearestTileItem, client));
            }
        });

        if (bean.get() == null)
        {
            return gson.toJson(ErrorBean.from("not found"));
        }
        return gson.toJson(bean.get());
    }


    private void groundItemsToBeansList(Client client, Set<TileItem> objects, List<GroundObjectBean> list)
    {
        for (TileItem obj : objects)
        {
            GroundObjectBean bean = GroundObjectBean.fromGroundObject(obj, client);
            if (bean != null)
            {
                list.add(bean);
            }
        }

    }

    private Set<TileItem> getTileItems(int id, String name, Client client)
    {

        Set<TileItem> objects = new LinkedHashSet<>();
        forEveryTile(tile ->
        {
            if (tile.getGroundItems() == null)
            {
                return;
            }

            for (TileItem tileItem : tile.getGroundItems())
            {

                if (tileItem == null)
                {
                    continue;
                }

                if ((id == -1 || tileItem.getId() == id) && (name.equals("") || client.getItemDefinition(tileItem.getId()).getName().equals(name)))
                {
                    objects.add(tileItem);
                }
            }
        }, client);

        return objects;
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
