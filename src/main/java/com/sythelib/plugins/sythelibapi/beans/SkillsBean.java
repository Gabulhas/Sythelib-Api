package com.sythelib.plugins.sythelibapi.beans;

import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Skill;

@Slf4j
@Value
public class SkillsBean
{
	SkillBean Attack;
	SkillBean Defense;
	SkillBean Strength;
	SkillBean Hitpoints;
	SkillBean Ranged;
	SkillBean Prayer;
	SkillBean Magic;
	SkillBean Cooking;
	SkillBean Woodcutting;
	SkillBean Fletching;
	SkillBean Fishing;
	SkillBean Firemaking;
	SkillBean Crafting;
	SkillBean Smithing;
	SkillBean Mining;
	SkillBean Herblore;
	SkillBean Agility;
	SkillBean Thieving;
	SkillBean Slayer;
	SkillBean Farming;
	SkillBean Runecrafting;
	SkillBean Hunter;
	SkillBean Construction;

	public static SkillsBean fromClient(Client client)
	{
		SkillBean NULL = SkillBean.NULL;
		SkillBean[] skill_beans = new SkillBean[]{NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL};
		int idx = 0;
		for (Skill skill : Skill.values())
		{
			if (skill.equals(Skill.OVERALL))
			{
				continue;
			}
			String name = skill.getName();
			int level = client.getRealSkillLevel(skill);
			int boostedLevel = client.getBoostedSkillLevel(skill);
			float xp = client.getSkillExperience(skill);
			skill_beans[idx++] = SkillBean.from(level, boostedLevel, xp);

		}
		return new SkillsBean(skill_beans[0], skill_beans[1], skill_beans[2], skill_beans[3], skill_beans[4], skill_beans[5], skill_beans[6], skill_beans[7], skill_beans[8], skill_beans[9], skill_beans[10], skill_beans[11], skill_beans[12], skill_beans[13], skill_beans[14], skill_beans[15], skill_beans[16], skill_beans[17], skill_beans[18], skill_beans[19], skill_beans[20], skill_beans[21], skill_beans[22]);
	}

}

@Value
class SkillBean
{
	public static final SkillBean NULL = new SkillBean(-1, -1, -1);

	int level;
	int boostedLevel;
	float xp;

	public static SkillBean from(int level, int boostedLevel, float xp)
	{
		return new SkillBean(level, boostedLevel, xp);
	}
}