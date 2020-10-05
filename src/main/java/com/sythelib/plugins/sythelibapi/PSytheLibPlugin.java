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

package com.sythelib.plugins.sythelibapi;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.Binder;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.sythelib.plugins.sythelibapi.httpserver.PSytheLibServer;
import java.io.IOException;
import javax.inject.Inject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import org.pf4j.Extension;

@Extension
@PluginDescriptor(
	name = "SytheLib Server",
	tags = {"sythe", "server", "api"},
	enabledByDefault = false
)
@Slf4j
public class PSytheLibPlugin extends Plugin
{
	private static final Gson GSON = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();

	@Inject
	private PSytheLibConfig config;
	@Inject
	@Getter
	private Client client;
	@Inject
	private ClientThread clientThread;

	private PSytheLibServer server;

	@Provides
	public PSytheLibConfig provideConfig(ConfigManager manager)
	{
		return manager.getConfig(PSytheLibConfig.class);
	}

	@Override
	public void configure(Binder binder)
	{
		binder.bind(Gson.class).toInstance(GSON);
	}

	@Override
	protected void startUp()
	{
		startServer();
	}

	@Override
	protected void shutDown()
	{
		server.stop();
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		if (event.getGroup().equals("psythelib"))
		{
			if (parsePort(event.getNewValue()))
			{
				server.stop();
				startServer();
			}
		}
	}

	private void startServer()
	{
		try
		{
			server = new PSytheLibServer(this, GSON, config.port());
			server.start();
			log.info("PSytheLib server started on port " + config.port());
		}
		catch (IOException ex)
		{
			log.error("Failed starting PSytheLib server", ex);
		}
	}

	public static boolean parsePort(String value)
	{
		if (value == null)
		{
			return false;
		}
		if (value.length() < 6 && value.matches("[0-9]*"))
		{
			int port = Integer.parseInt(value);
			return port > 1024 && port < 65536;
		}
		return false;
	}

	@Override
	public Injector getInjector()
	{
		return super.getInjector();
	}
}
