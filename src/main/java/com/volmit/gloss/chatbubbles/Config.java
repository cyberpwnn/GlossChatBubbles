package com.volmit.gloss.chatbubbles;

import mortar.api.config.Key;

public class Config
{
	@Key("chat-bubbles.follow-players")
	public static boolean followPlayers = true;

	@Key("chat-bubbles.word-wrap-break-chars")
	public static int wordWrapThreshold = 32;

	@Key("chat-bubbles.display-ticks")
	public static int messageDisplayTicks = 100;

	@Key("chat-bubbles.max-time-alive")
	public static int messageBubbleMaxTimeAlive = 5000;
}
