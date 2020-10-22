package com.sythelib.plugins.sythelibapi.utils;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Locatable;
import net.runelite.api.TileItem;
import net.runelite.api.coords.WorldPoint;

import java.util.Collection;
import java.util.List;
import java.util.Set;


@Slf4j
public class distanceUtils
{
    public static <T extends Locatable, E extends Collection<T>> T nearestToPoint(E objects, WorldPoint wp)
    {

        T nearest = null;
        int nearestDistance = Integer.MAX_VALUE;
        for (T obj : objects)
        {
            int distance = obj.getWorldLocation().distanceTo(wp);
            if (distance < nearestDistance)
            {
                nearest = obj;
                nearestDistance = distance;
            }

        }
        return nearest;

    }

    public static TileItem nearestToPoint(Set<TileItem> objects, WorldPoint wp)
    {

        TileItem nearest = null;
        int nearestDistance = Integer.MAX_VALUE;
        for (TileItem obj : objects)
        {
            int distance = obj.getTile().getWorldLocation().distanceTo(wp);
            if (distance < nearestDistance)
            {
                nearest = obj;
                nearestDistance = distance;
            }

        }
        return nearest;

    }
}
