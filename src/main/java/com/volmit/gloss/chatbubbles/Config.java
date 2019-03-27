package com.volmit.gloss.chatbubbles;

import mortar.api.config.Key;

public class Config
{
	@Key("chat-bubbles.follow-players")
	public static boolean followPlayers = true;

	@Key("chat-bubbles.hide-own-messages")
	public static boolean hideOwn = true;

	@Key("chat-bubbles.word-wrap-break-chars")
	public static int wordWrapThreshold = 32;

	@Key("chat-bubbles.display-ticks")
	public static int messageDisplayTicks = 100;

	@Key("chat-bubbles.max-time-alive")
	public static int messageBubbleMaxTimeAlive = 5000;

	@Key("chat-bubbles.message.prefix")
	public static String prefix = "&7";

	@Key("chat-bubbles.message.suffix")
	public static String suffix = "";

	@Key("chat-bubbles.message.offset.x")
	public static double offsetX = 0;

	@Key("chat-bubbles.message.offset.y")
	public static double offsetY = 1;

	@Key("chat-bubbles.message.offset.z")
	public static double offsetZ = 0;
}
