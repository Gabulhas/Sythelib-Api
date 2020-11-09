package com.sythelib.plugins.sythelibapi.beans;

import lombok.Value;
import net.runelite.api.Client;

@Value
public class VarbitBean
{
	public static final VarbitBean NULL = new VarbitBean(-1);

	int value;

	public static VarbitBean fromClient(Client client, int varbit_value)
	{
		int varbit = client.getVarbitValue(varbit_value);

		return new VarbitBean(varbit);
	}
}
