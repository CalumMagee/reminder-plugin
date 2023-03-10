package com.itemreminders;

import com.google.inject.Provides;
import com.sun.jna.platform.win32.OaIdl;
import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.client.Notifier;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import java.util.*;


@Slf4j
@PluginDescriptor(
	name = "ItemReminders"
)
public class ReminderPlugin extends Plugin
{

	private HashMap<Integer, List<String>> pairsMap = new HashMap<>();

	int tickDelay;

	@Inject
	private Notifier notifier;

	@Inject
	private Client client;

	@Inject
	private ReminderConfig config;

	@Override
	protected void startUp() throws Exception
	{
		loadPairs();
		tickDelay = 0;
	}

	public boolean isInListedZone()
	{
		return client.getLocalPlayer() != null
			&& pairsMap.containsKey(WorldPoint.fromLocalInstance(client, client.getLocalPlayer().getLocalLocation()).getRegionID());
	}

	private void loadPairs()
	{
		pairsMap.clear();

		if (!config.pairsString().trim().equals(""))
		{
			String[] pairsArray = (config.pairsString().split(";"));
			List<String> pairs = new ArrayList<>();
			for (int i = 0; i < pairsArray.length; i++)
			{
				pairs.add(pairsArray[i]);
			}

			for (int i = 0; i < pairs.size(); i++)
			{
				String unbracketedPair = pairs.get(i).substring(1, pairs.get(i).length() - 1);
				String[] pair = unbracketedPair.split(",");
				int key = Integer.parseInt(pair[0]);
				if (pairsMap.containsKey(key))
				{
					List<String> temp = new ArrayList<>();
					temp.add(pair[1]);
					temp.addAll(pairsMap.get(key));
					pairsMap.remove(key);
					pairsMap.put(key, temp);
				}
				else
				{
					List<String> temp = new ArrayList<>();
					temp.add(pair[1]);
					pairsMap.put(key, temp);
				}
			}
		}

	}

	private void incrementCounter()
	{
		if (tickDelay == config.ticksNum())
		{
			tickDelay = 0;
		}
		else
		{
			tickDelay++;
		}
	}

	@Subscribe
	public void onGameTick(GameTick tick)
	{
		int debug = WorldPoint.fromLocalInstance(client, client.getLocalPlayer().getLocalLocation()).getRegionID();
		System.out.println(debug);
		boolean anyItem = false;
		loadPairs();

		if (isInListedZone())
		{
			int currentRegion = WorldPoint.fromLocalInstance(client, client.getLocalPlayer().getLocalLocation()).getRegionID();
			if (null != pairsMap.get(currentRegion))
			{

				ItemContainer inventory = client.getItemContainer(InventoryID.INVENTORY);
				ItemContainer equipment = client.getItemContainer(InventoryID.EQUIPMENT);

				for (int i = 0; i < pairsMap.get(currentRegion).size(); i++)
				{
					if (!inventory.contains(Integer.parseInt(pairsMap.get(currentRegion).get(i))) &&
						!equipment.contains(Integer.parseInt(pairsMap.get(currentRegion).get(i))))
					{
						anyItem = true;
					}
				}
				if (anyItem)
				{
					if (tickDelay == 0)
					{
						notifier.notify("You are missing an item!");
						incrementCounter();
					}
					else
					{
						incrementCounter();
					}
				}
			}
		}
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		tickDelay = 0;
	}


	@Provides
	ReminderConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(ReminderConfig.class);
	}
}
