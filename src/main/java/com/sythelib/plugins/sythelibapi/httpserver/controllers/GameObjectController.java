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

package com.sythelib.plugins.sythelibapi.httpserver.controllers;

import com.google.gson.Gson;
import com.sythelib.plugins.sythelibapi.beans.ErrorBean;
import com.sythelib.plugins.sythelibapi.beans.GameObjectBean;
import com.sythelib.plugins.sythelibapi.httpserver.ClientThreadWrapper;
import com.sythelib.plugins.sythelibapi.httpserver.Controller;
import com.sythelib.plugins.sythelibapi.httpserver.Route;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.Scene;
import net.runelite.api.Tile;
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


@Slf4j
public class GameObjectController implements Controller
{
    @Inject
    private Gson gson;
    @Inject
    private Client client;
    @Inject
    private ClientThreadWrapper wrapper;


    /*
    There should be a better way to filter before turning GameObjects into a Bean
     */

    @Route("/gameobjects")
    public String gameobjects(Map<String, String> params)
    {
        final List<GameObjectBean> list = new ArrayList<>();

        int id;
        String name;
        try
        {
            id = Integer.parseInt(params.getOrDefault("id", "-1"));
            name = params.getOrDefault("name", "").replace("%20", " ").strip();

        } catch (NumberFormatException ex)
        {
            return gson.toJson(ErrorBean.from("number format exception parsing " + params.get("id")));
        }
        log.info(name, id);

        wrapper.run(() ->
        {
            Set<GameObject> objects = getGameObjects(id, name, client);
            gameObjectsToBeansList(client, objects, list);

        });

        return gson.toJson(list);
    }

    @Route("/gameobjects/nearest")
    public String gameobjects_nearest(Map<String, String> params)
    {

        int id, x, y, z;
        String name;
        try
        {
            id = Integer.parseInt(params.getOrDefault("id", "-1"));
            name = params.getOrDefault("name", "").replace("%20", " ").strip();
            x = Integer.parseInt(params.getOrDefault("x", "-1"));
            y = Integer.parseInt(params.getOrDefault("y", "-1"));
            z = Integer.parseInt(params.getOrDefault("z", "0"));

        } catch (NumberFormatException ex)
        {
            return gson.toJson(ErrorBean.from("number format exception parsing " + params.get("id")));
        }

        if (x == -1 || y == -1)
        {
            return gameobjects_nearst_aux(id, name, client.getLocalPlayer().getWorldLocation());
        }
        return gameobjects_nearst_aux(id, name, new WorldPoint(x, y, z));

    }

    public String gameobjects_nearst_aux(int id, String name, WorldPoint point)
    {
        AtomicReference<GameObjectBean> bean = new AtomicReference<>();
        wrapper.run(() ->
        {
            Set<GameObject> objects = getGameObjects(id, name, client);
            GameObject nearestGameObject = nearestToPoint(objects, point);
            if (nearestGameObject == null)
            {
                bean.set(null);
            } else
            {
                bean.set(GameObjectBean.fromGameObject(nearestGameObject, client));
            }

        });
        GameObjectBean finalBean = bean.get();
        if (finalBean == null)
        {
            return gson.toJson(ErrorBean.from("not found"));
        }
        return gson.toJson(finalBean);
    }


    private void gameObjectsToBeansList(Client client, Set<GameObject> objects, List<GameObjectBean> list)
    {

        for (GameObject obj : objects)
        {
            GameObjectBean bean = GameObjectBean.fromGameObject(obj, client);
            if (bean != null)
            {
                list.add(bean);

            }
        }


    }

    private Set<GameObject> getGameObjects(int id, String name, Client client)
    {

        Set<GameObject> objects = new LinkedHashSet<>();
        forEveryTile(tile ->
        {
            if (tile.getGameObjects() == null)
            {
                return;
            }

            for (GameObject gameObject : tile.getGameObjects())
            {
                if (gameObject == null)
                {
                    continue;
                }

                if ((id == -1 || gameObject.getId() == id) && (name.equals("") || client.getObjectDefinition(gameObject.getId()).getName().equals(name)))
                    objects.add(gameObject);
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
