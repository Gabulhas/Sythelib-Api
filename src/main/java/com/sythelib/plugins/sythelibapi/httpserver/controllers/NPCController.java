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
import static com.sythelib.plugins.sythelibapi.utils.DistanceUtils.*;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.NPCDefinition;
import net.runelite.api.coords.WorldPoint;

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
			name = params.get("name");
		}
		catch (NumberFormatException ex)
		{
			return gson.toJson(ErrorBean.from("number format exception parsing " + params.get("id")));
		}
		AtomicReference<List<NPCBean>> beans = new AtomicReference<>();

		// this is running on client thread so npcs dont mutate state while we're looking at them
		wrapper.run(() ->
		{
			List<NPC> filtered = getNpcsFiltered(id, name);
			beans.set(filtered.stream().map(npc -> NPCBean.fromNPC(npc, client)).collect(Collectors.toList()));
		});

		return gson.toJson(beans.get());
	}

	@Route("/npcs/nearest")
	public String npcs_nearest(Map<String, String> params)
	{
		int id, x, y, z;
		String name;
		try
		{
			id = Integer.parseInt(params.getOrDefault("id", "-1"));
			name = params.get("name");
			x = Integer.parseInt(params.getOrDefault("x", "-1"));
			y = Integer.parseInt(params.getOrDefault("y", "-1"));
			z = Integer.parseInt(params.getOrDefault("z", "0"));
		}
		catch (NumberFormatException ex)
		{
			return gson.toJson(ErrorBean.from("number format exception parsing " + params.get("id")));
		}

		if (x == -1 || y == -1)
		{
			return npcs_nearest_aux(id, name, client.getLocalPlayer().getWorldLocation());
		}
		return npcs_nearest_aux(id, name, new WorldPoint(x, y, z));
	}

	public String npcs_nearest_aux(int id, String name, WorldPoint point)
	{
		AtomicReference<NPCBean> bean = new AtomicReference<>();

		wrapper.run(() ->
		{
			List<NPC> filtered = getNpcsFiltered( id, name);
			NPC nearest = nearestToPoint(filtered, point);
			if (nearest == null)
			{
				bean.set(null);
			}
			else
			{
				bean.set(NPCBean.fromNPC(nearest, client));
			}
		});
		if (bean.get() == null)
		{
			return gson.toJson(ErrorBean.from("not found"));
		}

		return gson.toJson(bean.get());
	}

	public List<NPC> getNpcsFiltered(int id, String name)
	{
		return client.getNpcs().stream().filter(npc -> {
			NPCDefinition def = npc.getTransformedDefinition();
			return def != null && !def.getName().equals("null") &&
				(name == null || Objects.equals(def.getName(), name)) &&
				(id == -1 || def.getId() == id);
		}).collect(Collectors.toUnmodifiableList());
	}
}



