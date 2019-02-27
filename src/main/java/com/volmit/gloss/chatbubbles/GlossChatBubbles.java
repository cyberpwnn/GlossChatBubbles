package com.volmit.gloss.chatbubbles;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.PluginDescriptionFile;

import com.volmit.gloss.api.GLOSS;
import com.volmit.gloss.api.intent.TemporaryDescriptor;

import mortar.api.config.Configurator;
import mortar.api.sched.A;
import mortar.bukkit.plugin.MortarPlugin;
import mortar.compute.math.M;
import mortar.logic.format.F;

public class GlossChatBubbles extends MortarPlugin implements Listener
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

	//@builder
	static{try{URL url = new URL("https://raw.githubusercontent.com/VolmitSoftware/Mortar/master/release/Mortar.jar");
	File plugins = new File("plugins");Boolean foundMortar = false;for(File i : plugins.listFiles())
	{if(i.isFile() && i.getName().endsWith(".jar")){ZipFile file = new ZipFile(i);try{
	Enumeration<? extends ZipEntry> entries = file.entries();while(entries.hasMoreElements())
	{ZipEntry entry = entries.nextElement();if("plugin.yml".equals(entry.getName())){
	InputStream in = file.getInputStream(entry);
	PluginDescriptionFile pdf = new PluginDescriptionFile(in);if(pdf.getMain()
	.equals("mortar.bukkit.plugin.MortarAPIPlugin")){foundMortar = true;break;}}}}catch(Throwable ex)
	{ex.printStackTrace();}finally{file.close();}}}if(!foundMortar){System.out
	.println("Cannot find mortar. Attempting to download...");try{HttpURLConnection con = 
	(HttpURLConnection)url.openConnection(); HttpURLConnection.setFollowRedirects(false);
	con.setConnectTimeout(10000);con.setReadTimeout(10000);InputStream in = con.getInputStream();
	File mortar = new File("plugins/Mortar.jar");FileOutputStream fos = 
	new FileOutputStream(mortar);byte[] buf = new byte[16819];int r = 0;
	while((r = in.read(buf)) != -1){fos.write(buf, 0, r);}fos.close();in.close();
	con.disconnect();System.out.println("Mortar has been downloaded. Installing...");
	Bukkit.getPluginManager().loadPlugin(mortar);}catch(Throwable e){System.out
	.println("Failed to download mortar! Please download it from " + url.toString()
	);}}}catch(Throwable e){e.printStackTrace();}}
	//@done
}
