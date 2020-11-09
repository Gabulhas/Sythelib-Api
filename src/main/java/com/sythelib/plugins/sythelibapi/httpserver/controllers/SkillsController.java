package com.sythelib.plugins.sythelibapi.httpserver.controllers;

import com.google.gson.Gson;
import com.sythelib.plugins.sythelibapi.beans.SkillsBean;
import com.sythelib.plugins.sythelibapi.httpserver.ClientThreadWrapper;
import com.sythelib.plugins.sythelibapi.httpserver.Controller;
import com.sythelib.plugins.sythelibapi.httpserver.Route;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import javax.inject.Inject;
import net.runelite.api.Client;

public class SkillsController implements Controller
{
	@Inject
	private Gson gson;
	@Inject
	private Client client;
	@Inject
	private ClientThreadWrapper wrapper;

	@Route("/skills")
	public String skills(Map<String, String> params)
	{
		AtomicReference<SkillsBean> bean = new AtomicReference<>(null);

		wrapper.run(() -> bean.set(SkillsBean.fromClient(client)));

		return gson.toJson(bean.get());
	}
}

