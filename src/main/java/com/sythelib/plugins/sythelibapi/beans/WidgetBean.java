package com.sythelib.plugins.sythelibapi.beans;

import lombok.Value;
import net.runelite.api.Client;
import net.runelite.api.widgets.Widget;

@Value
public class WidgetBean
{
    int index;
    CanvasBean canvas;
    String text;
    String[] actions;
    Boolean isHidden;
    Boolean isSelfHidden;

    int itemId;
    int itemQuantity;
    int spriteId;

    int type;
    int id;
    String name;
    int fontId;

    public static WidgetBean fromId(Client client, int i, int ii)
    {
        Widget widget = client.getWidget(i, ii);
        CanvasBean canvas = CanvasBean.fromClickbox(widget.getBounds());


        return new WidgetBean(widget.getIndex(), canvas, widget.getText(), widget.getActions(), widget.isHidden(),
                widget.isSelfHidden(), widget.getItemId(), widget.getItemQuantity(), widget.getSpriteId(),
                widget.getType(), widget.getId(), widget.getName(), widget.getFontId());


    }
}
