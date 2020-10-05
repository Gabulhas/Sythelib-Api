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

import static com.sythelib.plugins.sythelibapi.beans.ItemBean.*;
import java.util.LinkedHashSet;
import java.util.Set;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.InventoryID;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.PlayerAppearance;
import net.runelite.api.kit.KitType;

@Slf4j
@Value
public class EquipmentBean
{
	private static final Set<KitType> EQUIPPABLES = new LinkedHashSet<>() {{
		add(KitType.HEAD);
		add(KitType.CAPE);
		add(KitType.AMULET);
		add(KitType.WEAPON);
		add(KitType.TORSO);
		add(KitType.SHIELD);
		add(KitType.LEGS);
		add(KitType.HANDS);
		add(KitType.BOOTS);
		add(KitType.RING);
		add(KitType.AMMUNITION);
	}};

	ItemBean head;
	ItemBean cape;
	ItemBean amulet;
	ItemBean weapon;
	ItemBean torso;
	ItemBean shield;
	ItemBean legs;
	ItemBean gloves;
	ItemBean boots;
	ItemBean ring;
	ItemBean ammo;

	public static EquipmentBean fromPlayerAppearance(PlayerAppearance appearance, Client client)
	{
		ItemBean[] items = new ItemBean[11];
		int idx = 0;
		for (KitType kt : EQUIPPABLES)
		{
			try
			{
				int id = appearance.getEquipmentId(kt);
				int amount = 1;
				String name = client.getItemDefinition(id).getName();
				items[idx] = ItemBean.from(id, amount, name, idx);
			}
			catch (ArrayIndexOutOfBoundsException ex)
			{
				items[idx] = NULL;
			}
			idx++;
		}

		return new EquipmentBean(items[0], items[1], items[2], items[3], items[4], items[5], items[6], items[7], items[8], items[9], items[10]);
	}

	public static EquipmentBean fromClient(Client client)
	{
		ItemContainer container = client.getItemContainer(InventoryID.EQUIPMENT);
		if (container == null)
		{
			return new EquipmentBean(NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
		}
		ItemBean[] items = new ItemBean[]{NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL};
		int idx = 0;
		for (Item item : container.getItems())
		{
			int id = item.getId();
			int amount = item.getQuantity();
			String name = client.getItemDefinition(id).getName();
			items[idx++] = ItemBean.from(id, amount, name, idx);
		}

		return new EquipmentBean(items[0], items[1], items[2], items[3], items[4], items[5], items[7], items[9], items[10], items[12], items[13]);
	}
}
