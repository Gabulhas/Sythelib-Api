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
package com.sythelib.plugins.sythelibapi.beans;

import java.awt.Shape;
import lombok.Value;
import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.Player;

@Value
public class PlayerBean
{
	boolean localPlayer;
	String name;
	int id;
	PositionBean pos;
	int healthRatio;
	int healthScale;
	int level;
	String interacting;
	int interactingID;
	int overhead;
	boolean skulled;
	EquipmentBean equipment;
	CanvasBean canvas;

	public static PlayerBean fromPlayer(Player player, Client client)
	{
		boolean isLocalPlayer = player.getPlayerId() == client.getLocalPlayerIndex();

		Shape clickbox = Perspective.getClickbox(client, player.getModel(), player.getOrientation(), player.getLocalLocation());
		return new PlayerBean(
			isLocalPlayer,
			player.getName() == null ? "" : player.getName(),
			player.getPlayerId(),
			PositionBean.fromWorldPoint(player.getWorldLocation()),
			player.getHealthRatio(),
			player.getHealthScale(),
			player.getCombatLevel(),
			player.getInteracting() != null ? player.getInteracting().getName() : "",
			player.getRSInteracting(),
			player.getOverheadIcon() == null ? 0 : player.getOverheadIcon().ordinal(),
			player.getSkullIcon() != null,
			isLocalPlayer ?
				EquipmentBean.fromClient(client) :
				EquipmentBean.fromPlayerAppearance(player.getPlayerAppearance(), client),
			CanvasBean.fromClickbox(clickbox)
		);
	}
}
