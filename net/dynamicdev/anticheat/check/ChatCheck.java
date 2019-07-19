package net.dynamicdev.anticheat.check;

import java.util.HashMap;
import java.util.Map;

import net.dynamicdev.anticheat.AntiCheat;
import net.dynamicdev.anticheat.manage.*;
import net.dynamicdev.anticheat.util.User;

import org.bukkit.entity.Player;

import net.dynamicdev.anticheat.config.providers.Lang;
import net.dynamicdev.anticheat.config.providers.Magic;

public class ChatCheck extends AntiCheatCheck {
	
    private Map<String, Integer> chatLevel;
    private Map<String, Integer> commandLevel;
    
    public ChatCheck(final AntiCheatManager instance) {
        super(instance);
        this.chatLevel = new HashMap<String, Integer>();
        this.commandLevel = new HashMap<String, Integer>();
    }
    
    public void resetChatLevel(final User user) {
        this.chatLevel.put(user.getName(), 0);
    }
    
    public void processChatSpammer(final Player player) {
        final User user = this.manager.getUserManager().getUser(player.getName());
        final int level = this.chatLevel.containsKey(user.getName()) ? this.chatLevel.get(user.getName()) : 0;
        if (player != null && player.isOnline() && level >= Magic.CHAT_ACTION_ONE_LEVEL) {
            final String event = (level >= Magic.CHAT_ACTION_TWO_LEVEL) ? AntiCheat.reasonTwo : AntiCheat.reasonOne;
            this.manager.getUserManager().execute(this.manager.getUserManager().getUser(player.getName()), new String[]{event}, CheckType.CHAT_SPAM, Lang.SPAM_KICK_REASON, new String[]{Lang.SPAM_WARNING}, Lang.SPAM_BAN_REASON);
        }
        this.chatLevel.put(user.getName(), level + 1);
    }
    
    public void processCommandSpammer(final Player player) {
        final User user = this.manager.getUserManager().getUser(player.getName());
        final int level = this.commandLevel.containsKey(user.getName()) ? this.commandLevel.get(user.getName()) : 0;
        if (player != null && player.isOnline() && level >= Magic.COMMAND_ACTION_ONE_LEVEL) {
            final String event = (level >= Magic.COMMAND_ACTION_TWO_LEVEL) ? AntiCheat.reasonTwo : AntiCheat.reasonOne;
            this.manager.getUserManager().execute(this.manager.getUserManager().getUser(player.getName()), new String[]{event}, CheckType.COMMAND_SPAM, Lang.SPAM_KICK_REASON, new String[]{Lang.SPAM_WARNING}, Lang.SPAM_BAN_REASON);
        }
        this.commandLevel.put(user.getName(), level + 1);
    }
    
    public CheckResult checkChatSpam(final Player player, final String msg) {
        final String name = player.getName();
        final User user = this.manager.getUserManager().getUser(name);
        if (user.getLastMessageTime() != -1L) {
            for (int i = 0; i < 2; ++i) {
                final String m = user.getMessage(i);
                if (m == null) {
                    break;
                }
                final Long l = user.getMessageTime(i);
                if (System.currentTimeMillis() - l > Magic.CHAT_REPEAT_MIN * 100) {
                    user.clearMessages();
                    break;
                }
                if (m.equalsIgnoreCase(msg) && i == 1) {
                    this.manager.getLoggingManager().logFineInfo(player.getName() + " spam-repeated \"" + msg + "\"");
                    return new CheckResult(CheckResult.Result.FAILED, Lang.SPAM_WARNING);
                }
                if (System.currentTimeMillis() - user.getLastCommandTime() < Magic.COMMAND_MIN * 2) {
                    this.manager.getLoggingManager().logFineInfo(player.getName() + " spammed quickly \"" + msg + "\"");
                    return new CheckResult(CheckResult.Result.FAILED, Lang.SPAM_WARNING);
                }
            }
        }
        user.addMessage(msg);
        return ChatCheck.PASS;
    }
    
    public CheckResult checkCommandSpam(final Player player, final String cmd) {
        final String name = player.getName();
        final User user = this.manager.getUserManager().getUser(name);
        if (user.getLastCommandTime() != -1L) {
            for (int i = 0; i < 2; ++i) {
                final String m = user.getCommand(i);
                if (m == null) {
                    break;
                }
                final Long l = user.getCommandTime(i);
                if (System.currentTimeMillis() - l > Magic.COMMAND_REPEAT_MIN * 100) {
                    user.clearCommands();
                    break;
                }
                if (m.equalsIgnoreCase(cmd) && i == 1) {
                    return new CheckResult(CheckResult.Result.FAILED, Lang.SPAM_WARNING);
                }
                if (System.currentTimeMillis() - user.getLastCommandTime() < Magic.COMMAND_MIN * 2) {
                    return new CheckResult(CheckResult.Result.FAILED, Lang.SPAM_WARNING);
                }
            }
        }
        user.addCommand(cmd);
        return ChatCheck.PASS;
    }
}
