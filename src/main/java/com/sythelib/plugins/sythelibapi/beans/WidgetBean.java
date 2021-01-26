package com.sythelib.plugins.sythelibapi.beans;

import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.widgets.Widget;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Value
@Slf4j
public class WidgetBean {
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
    int parentID;
    String name;
    int fontId;


    List<WidgetBean> children;
    List<WidgetBean> nestedChildren;
    List<WidgetBean> dynamicChildren;
    List<WidgetBean> staticChildren;

    public static WidgetBean fromWidget(Widget widget) {
        return new WidgetBean(widget.getIndex(),
                CanvasBean.fromClickbox(widget.getBounds()),
                widget.getText(),
                widget.getActions(),
                widget.isHidden(),
                widget.isSelfHidden(),
                widget.getItemId(),
                widget.getItemQuantity(),
                widget.getSpriteId(),
                widget.getType(),
                widget.getId(),
                widget.getParentId(),
                widget.getName(),
                widget.getFontId(),
                widgetArrayToWidgetBeanList(widget.getChildren()),
                widgetArrayToWidgetBeanList(widget.getNestedChildren()),
                widgetArrayToWidgetBeanList(widget.getDynamicChildren()),
                widgetArrayToWidgetBeanList(widget.getStaticChildren()));
    }

    private static List<WidgetBean> widgetArrayToWidgetBeanList(Widget[] list) {

        if (list == null) {
            return null;
        }
        return Arrays.stream(list).map(WidgetBean::fromWidget).collect(Collectors.toList());
    }
}
