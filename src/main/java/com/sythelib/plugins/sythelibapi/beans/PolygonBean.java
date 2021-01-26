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

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import lombok.Value;
import net.runelite.api.geometry.SimplePolygon;

@Value
public class PolygonBean {
    List<int[]> verts;

    public static PolygonBean fromSimplePolygon(SimplePolygon poly) {
        List<int[]> verts = new ArrayList<>();
        for (int i = 0; i < poly.size(); i++) {
            verts.add(new int[]{poly.getX(i), poly.getY(i)});
        }
        return new PolygonBean(verts);
    }

    public static PolygonBean fromRectangle2d(Rectangle2D rectangle2D) {
	    /*
	    Change lol, I bet BlueMan has a better solution, probbaly a oneliner
	     */
        List<int[]> verts = new ArrayList<>();
        int[] top_left = {(int) rectangle2D.getMinX(), (int) rectangle2D.getMaxY()};
        int[] top_right = {(int) rectangle2D.getMaxX(), (int) rectangle2D.getMaxY()};
        int[] bottom_right = {(int) rectangle2D.getMaxX(), (int) rectangle2D.getMinY()};
        int[] bottom_left = {(int) rectangle2D.getMinX(), (int) rectangle2D.getMinY()};
        verts.add(top_left);
        verts.add(top_right);
        verts.add(bottom_left);
        verts.add(bottom_right);
        return new PolygonBean(verts);
    }
}
