package com.sythelib.plugins.sythelibapi.httpserver.controllers;

import com.google.gson.Gson;
import com.sythelib.plugins.sythelibapi.beans.ErrorBean;
import com.sythelib.plugins.sythelibapi.beans.TileSceneBean;
import com.sythelib.plugins.sythelibapi.httpserver.ClientThreadWrapper;
import com.sythelib.plugins.sythelibapi.httpserver.Controller;
import com.sythelib.plugins.sythelibapi.httpserver.Route;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;

@Slf4j
public class TileSceneController implements Controller
{
	@Inject
	private Gson gson;
	@Inject
	private Client client;
	@Inject
	private ClientThreadWrapper wrapper;

	@Route("/tileminimap")
	public String tilescene(Map<String, String> params)
	{
		int x, y;
		try
		{
			x = Integer.parseInt(params.getOrDefault("x", "-1"));
			y = Integer.parseInt(params.getOrDefault("y", "-1"));
		}
		catch (NumberFormatException ex)
		{
			return gson.toJson(ErrorBean.from("number format exception parsing "));
		}

		AtomicReference<TileSceneBean> bean = new AtomicReference<>(null);

		wrapper.run(() -> bean.set(TileSceneBean.fromClient(client, x, y)));

		return gson.toJson(bean.get());
	}
}
