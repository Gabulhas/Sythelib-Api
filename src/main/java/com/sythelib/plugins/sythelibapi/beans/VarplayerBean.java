package com.sythelib.plugins.sythelibapi.beans;

import lombok.Value;
import net.runelite.api.Client;
import net.runelite.api.VarPlayer;

@Value
public class VarplayerBean
{

    public static final VarplayerBean NULL = new VarplayerBean(-1);

    int value;

    public static VarplayerBean fromClient(Client client, int varplayer_value)
    {

        int varplayer = client.getVarpValue(varplayer_value);

        return new VarplayerBean(varplayer);

    }
}
