package net.dynamicdev.anticheat.config.providers;

public class Lang {
	
	public static final String[] ALERT = new String[]{
		"&player has just entered the &group hack group.",
		"&player''s last failed check was: &check.",
		"Type ''/anticheat report &player'' for more information."
	},
	WARNING = new String[]{
		"[AntiCheat+] Hacking is not permitted.",
		"[AntiCheat+] If you continue to hack, action will be taken."
	};

	public static final String
		SPAM_WARNING = "Stop spamming the server or you will be kicked!",
		SPAM_KICK_REASON = "Kicked for spamming",
		SPAM_BAN_REASON = "Banned for spamming",
		SPAM_KICK_BROADCAST = "&player was kicked for spamming.",
		SPAM_BAN_BROADCAST = "&player was banned for spamming.",
		BAN_REASON = "Banned by AntiCheat+",
		BAN_BROADCAST = "&player was banned for hacking.",
		KICK_REASON = "Kicked by AntiCheat+",
		KICK_BROADCAST = "&player was kicked for hacking.";
}
