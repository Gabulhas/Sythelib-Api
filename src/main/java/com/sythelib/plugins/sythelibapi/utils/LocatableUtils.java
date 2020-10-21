package com.sythelib.plugins.sythelibapi.utils;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Locatable;
import net.runelite.api.coords.WorldPoint;

import java.util.Collection;


@Slf4j
public class LocatableUtils
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
}
