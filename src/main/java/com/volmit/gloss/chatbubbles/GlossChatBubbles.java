package com.volmit.gloss.chatbubbles;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.volmit.gloss.api.GLOSS;
import com.volmit.gloss.api.intent.TemporaryDescriptor;

import primal.bukkit.config.Configurator;
import primal.bukkit.plugin.PrimalPlugin;
import primal.bukkit.sched.A;
import primal.compute.math.M;
import primal.logic.format.F;

public class GlossChatBubbles extends PrimalPlugin implements Listener
{
	@Override
	public void start()
	{
		registerListener(this);

		try
		{
			Configurator.BUKKIT.load(Config.class, GLOSS.getConfigLocation(this));
		}

		catch(Exception e)
		{
			System.out.println("Failed to read gloss chat bubbles config.");
			e.printStackTrace();
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void on(AsyncPlayerChatEvent e)
	{
		int m = 0;

		for(String i : F.wrapWords(e.getMessage(), Config.wordWrapThreshold).split("\n"))
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
		Location l = p.getEyeLocation().clone().add(0, 1, 0);
		TemporaryDescriptor d = GLOSS.getSourceLibrary().createTemporaryDescriptor("chat-" + p.getUniqueId() + "-" + M.ms() + UUID.randomUUID().toString(), l, Config.messageBubbleMaxTimeAlive);
		d.addLine("&s&7" + msg);
		d.setEmissiveLevel(0);
		int trk = GLOSS.getContextLibrary().getView(p).getTrackedBubbles();

		d.bindPosition(() ->
		{
			int size = GLOSS.getContextLibrary().getView(p).getTrackedBubbles();
			int index = trk;
			double h = GLOSS.getIntentLibrary().getStackSpread() * ((size + index));
			double g = GLOSS.getIntentLibrary().getStackSpread() * (index);
			double m = size * GLOSS.getIntentLibrary().getStackSpread();
			double f = Math.min(index, size) * GLOSS.getIntentLibrary().getStackSpread();
			double j = Math.max((m) - (((m / 2D) - ((h + g) / 2D)) + (f * 2D)), 0);
			double v = d.getHealth() < 2000 ? (Math.pow(1D - ((double) d.getHealth() / 2000D), 16) * 10D) : 0;

			return (Config.followPlayers ? p.getEyeLocation() : l).clone().add(0, 0.86 + j + v, 0);
		});

		d.setLocation(p.getEyeLocation());
		GLOSS.getContextLibrary().getView(p).setTrackedBubbles(GLOSS.getContextLibrary().getView(p).getTrackedBubbles() + 1);

		new A(Config.messageDisplayTicks)
		{
			@Override
			public void run()
			{
				GLOSS.getContextLibrary().getView(p).setTrackedBubbles(GLOSS.getContextLibrary().getView(p).getTrackedBubbles() - 1);
			}
		};

		GLOSS.getSourceLibrary().register(d);
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
