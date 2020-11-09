package com.sythelib.plugins.sythelibapi.httpserver.controllers;

import com.google.gson.Gson;
import com.sythelib.plugins.sythelibapi.beans.ErrorBean;
import com.sythelib.plugins.sythelibapi.beans.SuccessBean;
import com.sythelib.plugins.sythelibapi.httpserver.ClientThreadWrapper;
import com.sythelib.plugins.sythelibapi.httpserver.Controller;
import com.sythelib.plugins.sythelibapi.httpserver.Route;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.client.menus.AbstractComparableEntry;
import net.runelite.client.menus.MenuManager;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

import static net.runelite.client.menus.ComparableEntries.newBaseComparableEntry;

@Slf4j
public class MenuEntrySwapperController implements Controller
{
    @Inject
    private Gson gson;
    @Inject
    private Client client;
    @Inject
    private ClientThreadWrapper wrapper;
    // yoinked from https://github.com/open-osrs/plugins/blob/42096a15e23c606e1a66b7f7752b1b6776aa1460/menuentryswapper/src/main/java/net/runelite/client/plugins/menuentryswapper/MenuEntrySwapperPlugin.java

    @Inject
    private MenuManager menuManager;
    //entry, priority
    private final Map<AbstractComparableEntry, Integer> customSwaps = new HashMap<>();

    @Route("/addmenuentry")
    public String addmenuentry(Map<String, String> params)
    {
        int priority;
        String option;
        String target;

        try
        {
            priority = Integer.parseInt(params.getOrDefault("priority", "-1"));
            option = params.getOrDefault("option", "").replace("%20", "");
            target = params.getOrDefault("target", "").replace("%20", "");

        } catch (NumberFormatException ex)
        {
            return gson.toJson(ErrorBean.from("number format exception parsing "));
        }
        if (priority == -1 || option.equals("") || target.equals(""))
        {
            return gson.toJson(ErrorBean.from("Get parameter missing. Required: priority, option, target"));
        }
        option = option.replace("%20", " ");
        target = target.replace("%20", " ");
        log.info(priority + " " + option + " " + target);
        final AbstractComparableEntry prioEntry = newBaseComparableEntry(option, target);
        customSwaps.put(prioEntry, priority);
        menuManager.addPriorityEntry(prioEntry).setPriority(priority);
        return gson.toJson(SuccessBean.from("Successfully added Menu Entry"));

    }


    @Route("/removemenuentry")
    public String removemenuentry(Map<String, String> params)
    {

        String option;
        String target;

        option = params.getOrDefault("option", "");
        target = params.getOrDefault("target", "");

        if (option.equals("") || target.equals(""))
        {
            return gson.toJson(ErrorBean.from("Get parameter missing. Required: priority, option, target"));
        }
        option = option.replace("%20", " ");
        target = target.replace("%20", " ");

        final AbstractComparableEntry prioEntry = newBaseComparableEntry(option, target);
        customSwaps.remove(prioEntry);
        menuManager.removePriorityEntry(prioEntry);


        return gson.toJson(SuccessBean.from("Successfully removed Menu Entry"));
    }

    @Route("/togglemenuentry")
    public String togglemenuentry(Map<String, String> params)
    {
        String option;
        String target;

        option = params.getOrDefault("option", "");
        target = params.getOrDefault("target", "");

        if (option.equals("") || target.equals(""))
        {
            return gson.toJson(ErrorBean.from("Get parameter missing. Required: priority, option, target"));
        }
        option = option.replace("%20", " ");
        target = target.replace("%20", " ");

        final AbstractComparableEntry prioEntry = newBaseComparableEntry(option, target);

        if (customSwaps.containsKey(prioEntry))
        {
            return removemenuentry(params);
        } else
        {
            return addmenuentry(params);
        }
    }

}
