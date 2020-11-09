package com.sythelib.plugins.sythelibapi.beans;

import lombok.Value;
import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;

@Value
public class TileSceneBean
{

	public static final TileSceneBean NULL = new TileSceneBean(-1, -1);

	int x;
	int y;

	public static TileSceneBean fromClient(Client client, int x, int y)
	{
		LocalPoint lp = LocalPoint.fromWorld(client, x, y);

		//FOR World
        /*
        Polygon tilePoly = Perspective.getCanvasTileAreaPoly(client,lp,1);
        Rectangle rt = tilePoly.getBounds();
        return new TileSceneBean((int) rt.getCenterX(),(int) rt.getCenterY());
         */
		if (lp == null)
		{
			return NULL;
		}
		Point minimapPoint = Perspective.localToMinimap(client, lp);

		if (minimapPoint == null)
		{
			return NULL;
		}

		return new TileSceneBean(minimapPoint.getX(), minimapPoint.getY());
	}
}
