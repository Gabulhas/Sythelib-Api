package com.sythelib.plugins.sythelibapi.httpserver.controllers;

import com.google.gson.Gson;
import com.sythelib.plugins.sythelibapi.beans.CanvasBean;
import com.sythelib.plugins.sythelibapi.beans.ErrorBean;
import com.sythelib.plugins.sythelibapi.beans.PolygonBean;
import com.sythelib.plugins.sythelibapi.httpserver.ClientThreadWrapper;
import com.sythelib.plugins.sythelibapi.httpserver.Controller;
import com.sythelib.plugins.sythelibapi.httpserver.Route;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;

@Slf4j
public class CanvasController implements Controller
{
	@Inject
	private Gson gson;
	@Inject
	private Client client;
	@Inject
	private ClientThreadWrapper wrapper;

	@Route("/canvas")
	public String canvas(Map<String, String> params, String data)
	{
		return canvas_center(params, data);
	}

	@Route("/canvas/center")
	public String canvas_center(Map<String, String> params, String data)
	{
		CanvasBean bean = gson.fromJson(data, CanvasBean.class);
		if (bean == null)
		{
			return gson.toJson(ErrorBean.from("Invalid canvas"));
		}
		else if (bean.getPolys().size() == 0)
		{
			return gson.toJson(ErrorBean.from("Invalid canvas"));
		}
		PolygonBean polygonBean = bean.getPolys().get(0);

		return gson.toJson(centroid(polygonBean.getVerts()));
	}

	@Route("/canvas/random")
	public String canvas_random(Map<String, String> params, String data)
	{
		CanvasBean bean = gson.fromJson(data, CanvasBean.class);
		if (bean == null)
		{
			return gson.toJson(ErrorBean.from("Invalid canvas"));
		}
		else if (bean.getPolys().size() == 0)
		{
			return gson.toJson(ErrorBean.from("Invalid canvas"));
		}
		PolygonBean polygonBean = bean.getPolys().get(0);

		return gson.toJson(random(polygonBean.getVerts()));
	}

	public Point centroid(List<int[]> verts)
	{
		double centroidX = 0, centroidY = 0;
		for (int[] knot : verts)
		{
			centroidX += knot[0];
			centroidY += knot[1];
		}
		return new Point((int) centroidX / (verts.size()), (int) centroidY / verts.size());
	}

	public Point random(List<int[]> verts)
	{
		Polygon region = new Polygon();
		for (int[] vert : verts)
		{
			region.addPoint(vert[0], vert[1]);
		}

		Rectangle r = region.getBounds();
		double x, y;
		do
		{
			x = r.getX() + r.getWidth() * Math.random();
			y = r.getY() + r.getHeight() * Math.random();
		}
		while (!region.contains(x, y));

		return new Point((int) x, (int) y);
	}
}
