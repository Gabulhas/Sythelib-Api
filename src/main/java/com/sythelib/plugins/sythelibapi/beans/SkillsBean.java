package com.sythelib.plugins.sythelibapi.beans;

import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Skill;

@Slf4j
@Value
public class SkillsBean
{
    Skill_aux Attack;
    Skill_aux Defense;
    Skill_aux Strength;
    Skill_aux Hitpoints;
    Skill_aux Ranged;
    Skill_aux Prayer;
    Skill_aux Magic;
    Skill_aux Cooking;
    Skill_aux Woodcutting;
    Skill_aux Fletching;
    Skill_aux Fishing;
    Skill_aux Firemaking;
    Skill_aux Crafting;
    Skill_aux Smithing;
    Skill_aux Mining;
    Skill_aux Herblore;
    Skill_aux Agility;
    Skill_aux Thieving;
    Skill_aux Slayer;
    Skill_aux Farming;
    Skill_aux Runecrafting;
    Skill_aux Hunter;
    Skill_aux Construction;



    public static SkillsBean fromClient(Client client)
    {
        Skill_aux NULL = Skill_aux.NULL;
        Skill_aux[] skill_auxes = new Skill_aux[] {NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL};
        int idx = 0;
        for (Skill skill : Skill.values())
        {
            if (skill.equals(Skill.OVERALL)) continue;
            String name = skill.getName();
            int level = client.getRealSkillLevel(skill);
            int boostedLevel = client.getBoostedSkillLevel(skill);
            float xp = client.getSkillExperience(skill);
            skill_auxes[idx++] = Skill_aux.from(level, boostedLevel ,xp);

        }
        return new SkillsBean(skill_auxes[0], skill_auxes[1], skill_auxes[2], skill_auxes[3], skill_auxes[4], skill_auxes[5], skill_auxes[6], skill_auxes[7], skill_auxes[8], skill_auxes[9], skill_auxes[10], skill_auxes[11], skill_auxes[12], skill_auxes[13], skill_auxes[14], skill_auxes[15], skill_auxes[16], skill_auxes[17], skill_auxes[18], skill_auxes[19], skill_auxes[20], skill_auxes[21], skill_auxes[22]);
    }

}

@Value
class Skill_aux
{
    public static final Skill_aux NULL = new Skill_aux(-1, -1,  -1);

    int level;
    int boostedLevel;
    float xp;


    public static Skill_aux from(int level, int boostedLevel, float xp)
    {
        return new Skill_aux(level, boostedLevel, xp);
    }
}