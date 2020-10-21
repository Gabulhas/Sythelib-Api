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
import com.sythelib.plugins.sythelibapi.beans.NPCBean;
import com.sythelib.plugins.sythelibapi.httpserver.ClientThreadWrapper;
import com.sythelib.plugins.sythelibapi.httpserver.Controller;
import com.sythelib.plugins.sythelibapi.httpserver.Route;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.coords.WorldPoint;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static com.sythelib.plugins.sythelibapi.utils.LocatableUtils.nearestToPoint;

public class NPCController implements Controller
{
    @Inject
    private Gson gson;
    @Inject
    private Client client;
    @Inject
    private ClientThreadWrapper wrapper;

    @Route("/npcs")
    public String npcs(Map<String, String> params)
    {
        int id;
        String name;
        try
        {
            id = Integer.parseInt(params.getOrDefault("id", "-1"));
            name = params.getOrDefault("name", "");

        } catch (NumberFormatException ex)
        {
            return gson.toJson(ErrorBean.from("number format exception parsing " + params.get("id")));
        }
        AtomicReference<List<NPCBean>> beans = new AtomicReference<>();

        // this is running on client thread so npcs dont mutate state while we're looking at them
        wrapper.run(() -> {
            List<NPC> npcs = client.getNpcs();
            List<NPC> filtered = filter(npcs, id, name);
            beans.set(filtered.stream().map(npc -> NPCBean.fromNPC(npc, client)).collect(Collectors.toList()));
        });

        return gson.toJson(beans.get());
    }

    @Route("/npcs/neareast_to/player")
    public String npcs_nearest_to_player(Map<String, String> params)
    {

        AtomicReference<NPCBean> bean = new AtomicReference<>();

        int id;
        String name;
        try
        {
            id = Integer.parseInt(params.getOrDefault("id", "-1"));
            name = params.getOrDefault("name", "");

        } catch (NumberFormatException ex)
        {
            return gson.toJson(ErrorBean.from("number format exception parsing " + params.get("id")));
        }

        wrapper.run(() -> {

            List<NPC> npcs = client.getNpcs();
            List<NPC> filtered = filter(npcs, id, name);
            NPC nearest = nearestToPoint(filtered, client.getLocalPlayer().getWorldLocation());
            bean.set(NPCBean.fromNPC(nearest, client));
        });


        return gson.toJson(bean.get());
    }

    @Route("/npcs/neareast_to/point")
    public String npcs_nearest(Map<String, String> params)
    {
        AtomicReference<NPCBean> bean = new AtomicReference<>();

        int id, x, y, z;
        String name;
        try
        {
            id = Integer.parseInt(params.getOrDefault("id", "-1"));
            name = params.getOrDefault("name", "");
            x = Integer.parseInt(params.getOrDefault("x", "-1"));
            y = Integer.parseInt(params.getOrDefault("y", "-1"));
            z = Integer.parseInt(params.getOrDefault("z", "-1"));

        } catch (NumberFormatException ex)
        {
            return gson.toJson(ErrorBean.from("number format exception parsing " + params.get("id")));
        }

        // this is running on client thread so npcs dont mutate state while we're looking at them
        wrapper.run(() -> {
            List<NPC> npcs = client.getNpcs();
            List<NPC> filtered = filter(npcs, id, name);
            NPC nearest = nearestToPoint(filtered, new WorldPoint(x, y, z));
            bean.set(NPCBean.fromNPC(nearest, client));
        });

        return gson.toJson(bean.get());
    }

    public List<NPC> filter(List<NPC> npcs, int id, String name)
    {

        List<NPC> filtered = new ArrayList<>();

        for (NPC npc : npcs)
        {
            if ((id == -1 || npc.getId() == id) && (name.equals("") || npc.getName().equals(name)))
            {
                filtered.add(npc);
            }
        }
        return filtered;
    }
}



