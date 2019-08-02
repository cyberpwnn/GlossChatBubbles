package com.volmit.gloss.chatbubbles;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.volmit.gloss.api.GLOSS;
import com.volmit.gloss.api.intent.ListMode;
import com.volmit.gloss.api.intent.TemporaryDescriptor;

import mortar.api.config.Configurator;
import mortar.api.sched.A;
import mortar.api.sched.J;
import mortar.bukkit.plugin.MortarPlugin;
import mortar.compute.math.M;
import mortar.lang.collection.GList;
import mortar.lang.collection.GMap;
import mortar.lang.collection.GSet;
import mortar.logic.format.F;

public class GlossChatBubbles extends MortarPlugin implements Listener
{
	private GMap<Player, GList<String>> messages;
	private GMap<Player, String> justSent;

	@Override
	public void start()
	{
		registerListener(this);
		messages = new GMap<>();
		justSent = new GMap<>();

		try
		{
			Configurator.BUKKIT.load(Config.class, getDataFile("config.yml"));
		}

		catch(Exception e)
		{
			System.out.println("Failed to read gloss chat bubbles config.");
			e.printStackTrace();
		}
	}

	@EventHandler
	public void on(PlayerQuitEvent e)
	{
		messages.remove(e.getPlayer());
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void on(AsyncPlayerChatEvent e)
	{
		if(e.isCancelled() || Config.blacklist.contains(e.getPlayer().getLocation().getWorld().getName()) ||
					!e.getPlayer().hasPermission("gloss.chatbubbles.use"))
		{
			return;
		}

		if(justSent.containsKey(e.getPlayer()) && justSent.get(e.getPlayer()).equals(e.getMessage()))
		{
			return;
		}

		justSent.put(e.getPlayer(), e.getMessage());
		J.a(() -> justSent.remove(e.getPlayer()));

		int m = 0;

		for(String i : new GSet<>(new GList<>(F.wrapWords(e.getMessage(), Config.wordWrapThreshold).split("\n"))))
		{
			new A(5 * m)
			{
				@Override
				public void run()
				{
					bubble(i, e.getPlayer());
				}
			};
			m++;
		}
	}

	private void bubble(String msg, Player p)
	{
		if(getTracked(p).contains(msg))
		{
			untrack(p, msg);
			track(p, msg);
			return;
		}

		Location l = p.getEyeLocation().clone().add(Config.offsetX, Config.offsetY, Config.offsetZ);
		TemporaryDescriptor d = GLOSS.getSourceLibrary().createTemporaryDescriptor("chat-" + p.getUniqueId() + "-" + M.ms() + UUID.randomUUID().toString(), l, Config.messageBubbleMaxTimeAlive);
		d.getPlayerList().clear();
		d.getPlayerList().setMode(ListMode.BLACKLIST);

		if(Config.hideOwn)
		{
			d.getPlayerList().add(p.getUniqueId());
		}

		d.clearLines();
		d.addLine(Config.prefix + msg);

		track(p, msg);

		d.bindPosition(() ->
		{
			int size = getTrackedIndex(p, msg);
			int index = getTracked(p).size();
			double h = GLOSS.getIntentLibrary().getStackSpread() * ((size + index));
			double g = GLOSS.getIntentLibrary().getStackSpread() * (index);
			double m = size * GLOSS.getIntentLibrary().getStackSpread();
			double f = Math.min(index, size) * GLOSS.getIntentLibrary().getStackSpread();
			double j = Math.max((m) - (((m / 2D) - ((h + g) / 2D)) + (f * 2D)), 0);
			double v = d.getHealth() < 2000 ? (Math.pow(1D - ((double) d.getHealth() / 2000D), 16) * 10D) : 0;

			return (Config.followPlayers ? p.getEyeLocation() : l).clone().add(0, 0.86 + j + v, 0);
		});

		d.setLocation(p.getEyeLocation());

		new A(Config.messageDisplayTicks)
		{
			@Override
			public void run()
			{
				untrack(p, msg);
			}
		};

		GLOSS.getSourceLibrary().register(d);
	}

	private GList<String> getTracked(Player p)
	{
		return messages.containsKey(p) ? messages.get(p) : new GList<>();
	}

	private int getTrackedIndex(Player p, String f)
	{
		return getTracked(p).indexOf(f);
	}

	private void track(Player p, String message)
	{
		if(!messages.containsKey(p))
		{
			messages.put(p, new GList<>());
		}

		try
		{
			messages.get(p).add(message);
		}

		catch(Throwable e)
		{

		}
	}

	private void untrack(Player p, String message)
	{
		if(!messages.containsKey(p))
		{
			return;
		}

		messages.get(p).remove(message);
	}

	@Override
	public void stop()
	{

	}

	@Override
	public String getTag(String subTag)
	{
		return "";
	}
}
