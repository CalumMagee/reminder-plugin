package com.reminder;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.GameTick;
import net.runelite.client.Notifier;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.util.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;


@Slf4j
@PluginDescriptor(
	name = "Example"
)
public class ReminderPlugin extends Plugin
{

	private HashMap<Integer,List<String>> pairsMap = new HashMap<>();

	private static final int OLM_ROOM_REGION = 12889;

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
	}

	public boolean isInListedZone()
	{
		Set<Integer> zones = pairsMap.keySet();
		return client.getLocalPlayer() != null
				&& pairsMap.containsKey(client.getLocalPlayer().getWorldLocation().getRegionID());
	}

	private void loadPairs() {
		pairsMap.clear();

		if (!config.pairsString().trim().equals(""))
		{
			List<String> pairs = Text.fromCSV(config.pairsString());
			for (int i = 0; i < pairs.size(); i++) {
				String unbracketedPair = pairs.get(i).substring(1,pairs.get(i).length()-1);
				List<String> pair = Text.fromCSV(unbracketedPair);
				int key = Integer.parseInt(pair.get(0));
				if (pairsMap.containsKey(key)) {
					List<String> temp = new ArrayList<>();
					temp.add(pair.get(1));
					temp.addAll(pairsMap.get(key));
					pairsMap.remove(key);
					pairsMap.put(key, temp);
				}
				else {
					List<String> temp = new ArrayList<>();
					temp.add(pair.get(1));
					pairsMap.put(key, temp);
				}
			}
		}

	}

	@Subscribe
	public void onGameTick(GameTick tick)
	{

		if (isInListedZone()) {
			int currentRegion = client.getLocalPlayer().getWorldLocation().getRegionID();
			if (null!=pairsMap.get(currentRegion)) {

				ItemContainer inventory = client.getItemContainer(InventoryID.INVENTORY);
				ItemContainer equipment = client.getItemContainer(InventoryID.EQUIPMENT);
				boolean anyItem = false;

				for (int i = 0; i < pairsMap.get(currentRegion).size(); i++) {
					if (!inventory.contains(Integer.parseInt(pairsMap.get(currentRegion).get(i))) ||
							!equipment.contains(Integer.parseInt(pairsMap.get(currentRegion).get(i)))) {
						anyItem = true;
					}
				}

				if (anyItem) {
					notifier.notify("You are missing an item!");
				}
			}
		}

	}

	@Provides
	ReminderConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(ReminderConfig.class);
	}
}
