package com.reminder;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.client.Notifier;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;


@Slf4j
@PluginDescriptor(
	name = "Example"
)
public class ReminderPlugin extends Plugin
{

	private static final int OLM_ROOM_REGION = 12593;

	@Inject
	private Notifier notifier;

	@Inject
	private Client client;

	@Inject
	private ReminderConfig config;

	@Override
	protected void startUp() throws Exception
	{
		log.info("Example started!");
	}

	@Override
	protected void shutDown() throws Exception
	{
		log.info("Example stopped!");
	}

	public boolean isInCoX()
	{
		return client.getLocalPlayer() != null
				&& OLM_ROOM_REGION == client.getLocalPlayer().getWorldLocation().getRegionID();
	}

	@Subscribe
	public void onGameTick(GameTick tick)
	{
		if (isInCoX()) {
			ItemContainer itemContainer = client.getItemContainer(InventoryID.INVENTORY);
			//DHL
			if (!itemContainer.contains(22978)) {
				notifier.notify("You are missing an item!");
			}
		}

	}

	@Provides
	ReminderConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(ReminderConfig.class);
	}
}
