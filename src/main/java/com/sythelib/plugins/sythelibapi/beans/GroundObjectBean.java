package com.sythelib.plugins.sythelibapi.beans;

import lombok.Value;
import net.runelite.api.Client;
import net.runelite.api.ItemComposition;
import net.runelite.api.Perspective;
import net.runelite.api.TileItem;

import java.awt.Shape;


@Value
public class GroundObjectBean
{
    int id;
    int quantity;
    String name;
    PositionBean pos;
    CanvasBean canvas;

    public static GroundObjectBean fromGroundObject(TileItem object, Client client)
    {
        if (object.getModel() == null || object.getTile() == null)
        {
            return null;
        }
        Shape clickbox = Perspective.getClickbox(client, object.getModel(), 0, object.getTile().getLocalLocation());

        ItemComposition def = client.getItemDefinition(object.getId());
        if (def == null)
        {
            return null;
        }
        return new GroundObjectBean(def.getId(), object.getQuantity(), def.getName(), PositionBean.fromWorldPoint(object.getTile().getWorldLocation()), CanvasBean.fromClickbox(clickbox));
    }
}
