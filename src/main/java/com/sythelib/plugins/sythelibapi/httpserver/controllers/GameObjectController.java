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
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.Scene;
import net.runelite.api.Tile;

public class GameObjectController implements Controller
{
	@Inject
	private Gson gson;
	@Inject
	private Client client;
	@Inject
	private ClientThreadWrapper wrapper;

	@Route("/gameobjects")
	public String gameobjects(Map<String, String> params)
	{
		final List<GameObjectBean> list = new ArrayList<>();

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

					if (filter == -1 || gameObject.getId() == filter)
					{
						objects.add(gameObject);
					}
				}
			}, client);

			for (GameObject obj : objects)
			{
				GameObjectBean bean = GameObjectBean.fromGameObject(obj, client);
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
