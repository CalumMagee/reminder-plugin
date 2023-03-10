package com.itemreminders;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup("ItemReminders")
public interface ReminderConfig extends Config
{

	@ConfigSection(
		name = "ZoneID/ItemID pairs",
		description = "Send notification if the itemID is not in inventory in the corresponding ZoneId",
		position = 0
	)
	String pairs = "pairs";

	@ConfigSection(
		name = "Ticks between notifications",
		description = "There will be this many ticks between notifications. Default 4",
		position = 1
	)
	String ticks = "ticks";

	@ConfigItem(
		position = 1,
		keyName = "pairsString",
		name = "ZoneID/ItemID pairs",
		description = "Send notification if the itemID is not in inventory in the corresponding ZoneId.\n" +
			"Format is [ZoneID1,ItemID1];[ZoneID1,ItemID2];[ZoneID2,ItemID3]",
		section = pairs
	)
	default String pairsString()
	{
		return "";
	}

	@ConfigItem(
		position = 1,
		keyName = "ticks",
		name = "Ticks between notifications",
		description = "There will be this many ticks between notifications. Default 4",
		section = ticks
	)
	default int ticksNum()
	{
		return 4;
	}
}
