package net.dynamicdev.anticheat.event;

import java.util.*;

import org.bukkit.command.*;

import net.dynamicdev.anticheat.*;
import net.dynamicdev.anticheat.check.*;

import org.bukkit.event.*;
import org.bukkit.event.entity.*;
import org.bukkit.entity.*;
import org.bukkit.event.block.*;
import org.bukkit.*;

import net.dynamicdev.anticheat.util.*;

import org.bukkit.inventory.*;
import org.bukkit.block.*;
import org.bukkit.event.player.*;

public class PlayerListener extends EventListener {
	
    private Map<String, Location> setbackLocation;
    
    public PlayerListener() {
        this.setbackLocation = new HashMap<String, Location>();
    }
    
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerCommandPreprocess(final PlayerCommandPreprocessEvent event) {
        final Player player = event.getPlayer();
        if (EventListener.getCheckManager().willCheck(player, CheckType.COMMAND_SPAM) && !Permission.getCommandExempt((CommandSender)player, event.getMessage().split(" ")[0])) {
            final CheckResult result = EventListener.getBackend().getChatCheck().checkCommandSpam(player, event.getMessage());
            if (result.failed()) {
                event.setCancelled(!EventListener.silentMode());
                if (!EventListener.silentMode()) {
                    player.sendMessage(ChatColor.RED + result.getMessage());
                }
                EventListener.getBackend().getChatCheck().processCommandSpammer(player);
                EventListener.log(null, player, CheckType.COMMAND_SPAM);
            }
        }
        AntiCheat.getManager().addEvent(event.getEventName(), event.getHandlers().getRegisteredListeners());
    }
    
    @EventHandler
    public void onPlayerToggleFlight(final PlayerToggleFlightEvent event) {
        if (!event.isFlying()) {
            EventListener.getBackend().logEnterExit(event.getPlayer());
        }
        AntiCheat.getManager().addEvent(event.getEventName(), event.getHandlers().getRegisteredListeners());
    }
    
    @EventHandler
    public void onPlayerGameModeChange(final PlayerGameModeChangeEvent event) {
        if (event.getNewGameMode() != GameMode.CREATIVE) {
            EventListener.getBackend().logEnterExit(event.getPlayer());
        }
        AntiCheat.getManager().addEvent(event.getEventName(), event.getHandlers().getRegisteredListeners());
    }
    
    @SuppressWarnings("deprecation")
	@EventHandler
    public void onProjectileLaunch(final ProjectileLaunchEvent event) {
        if (event.getEntity().getShooter() instanceof Player) {
            final Player player = (Player)event.getEntity().getShooter();
            if (event.getEntity() instanceof Arrow) {
                return;
            }
            if (EventListener.getCheckManager().willCheck(player, CheckType.FAST_PROJECTILE)) {
                final CheckResult result = EventListener.getBackend().getEntityCheck().checkProjectile(player);
                if (result.failed()) {
                    event.setCancelled(!EventListener.silentMode());
                    EventListener.log(result.getMessage(), player, CheckType.FAST_PROJECTILE);
                }
            }
        }
        AntiCheat.getManager().addEvent(event.getEventName(), event.getHandlers().getRegisteredListeners());
    }
    
    @EventHandler
    public void onPlayerTeleport(final PlayerTeleportEvent event) {
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL || event.getCause() == PlayerTeleportEvent.TeleportCause.PLUGIN) {
            EventListener.getBackend().logTeleport(event.getPlayer());
        }
        AntiCheat.getManager().addEvent(event.getEventName(), event.getHandlers().getRegisteredListeners());
    }
    
    @EventHandler
    public void onPlayerChangeWorlds(final PlayerChangedWorldEvent event) {
        EventListener.getBackend().logTeleport(event.getPlayer());
        AntiCheat.getManager().addEvent(event.getEventName(), event.getHandlers().getRegisteredListeners());
    }
    
    @EventHandler
    public void onPlayerToggleSneak(final PlayerToggleSneakEvent event) {
        if (event.isSneaking()) {
            EventListener.getBackend().logToggleSneak(event.getPlayer());
        }
        AntiCheat.getManager().addEvent(event.getEventName(), event.getHandlers().getRegisteredListeners());
    }
    
    @EventHandler
    public void onPlayerVelocity(final PlayerVelocityEvent event) {
        final Player player = event.getPlayer();
        if (EventListener.getCheckManager().willCheck(player, CheckType.FLY)) {
            if (EventListener.getBackend().getMovementCheck().justVelocity(player) && EventListener.getBackend().getMovementCheck().extendVelocityTime(player)) {
                event.setCancelled(!EventListener.silentMode());
                return;
            }
            EventListener.getBackend().getMovementCheck().logVelocity(player);
        }
        AntiCheat.getManager().addEvent(event.getEventName(), event.getHandlers().getRegisteredListeners());
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onPlayerChat(final AsyncPlayerChatEvent event) {
        final Player player = event.getPlayer();
        if (EventListener.getCheckManager().willCheck(player, CheckType.CHAT_SPAM)) {
            final CheckResult result = EventListener.getBackend().getChatCheck().checkChatSpam(player, event.getMessage());
            if (result.failed()) {
                event.setCancelled(!EventListener.silentMode());
                if (!result.getMessage().equals("") && !EventListener.silentMode()) {
                    player.sendMessage(ChatColor.RED + result.getMessage());
                }
                EventListener.getBackend().getChatCheck().processChatSpammer(player);
                EventListener.log(null, player, CheckType.CHAT_SPAM);
            }
        }
        AntiCheat.getManager().addEvent(event.getEventName(), event.getHandlers().getRegisteredListeners());
    }
    
    @EventHandler
    public void onPlayerKick(final PlayerKickEvent event) {
        AntiCheat.getManager().addEvent(event.getEventName(), event.getHandlers().getRegisteredListeners());
    }
    
    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent event) {
        final User user = EventListener.getUserManager().getUser(event.getPlayer().getName());
        AntiCheat.saveLevelFromUser(user);
        AntiCheat.getManager().addEvent(event.getEventName(), event.getHandlers().getRegisteredListeners());
    }
    
    @EventHandler
    public void onPlayerToggleSprint(final PlayerToggleSprintEvent event) {
        final Player player = event.getPlayer();
        if (!event.isSprinting()) {
            EventListener.getBackend().logEnterExit(player);
        }
        if (EventListener.getCheckManager().willCheck(player, CheckType.SPRINT)) {
            final CheckResult result = EventListener.getBackend().getMovementCheck().checkSprintHungry(event);
            if (result.failed()) {
                event.setCancelled(!EventListener.silentMode());
                EventListener.log(result.getMessage(), player, CheckType.SPRINT);
            }
            else {
                EventListener.decrease(player);
            }
        }
        AntiCheat.getManager().addEvent(event.getEventName(), event.getHandlers().getRegisteredListeners());
    }
    
    @EventHandler
    public void onPlayerInteract(final PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        final PlayerInventory inv = player.getInventory();
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            final Material m = inv.getItemInHand().getType();
            if (m == Material.BOW) {
                EventListener.getBackend().getEntityCheck().logBowWindUp(player);
            }
            else if (Utilities.isFood(m)) {
                EventListener.getBackend().getEntityCheck().logEatingStart(player);
            }
        }
        final Block block = event.getClickedBlock();
        if (block != null) {
            final Distance distance = new Distance(player.getLocation(), block.getLocation());
            EventListener.getBackend().getBlockCheck().checkLongReachBlock(player, distance.getXDifference(), distance.getYDifference(), distance.getZDifference());
        }
        AntiCheat.getManager().addEvent(event.getEventName(), event.getHandlers().getRegisteredListeners());
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onPlayerDropItem(final PlayerDropItemEvent event) {
        final Player player = event.getPlayer();
        if (EventListener.getCheckManager().willCheck(player, CheckType.ITEM_SPAM)) {
            final CheckResult result = EventListener.getBackend().getInventoryCheck().checkFastDrop(player);
            if (result.failed()) {
                event.setCancelled(!EventListener.silentMode());
                EventListener.log(result.getMessage(), player, CheckType.ITEM_SPAM);
            }
        }
        AntiCheat.getManager().addEvent(event.getEventName(), event.getHandlers().getRegisteredListeners());
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onPlayerEnterBed(final PlayerBedEnterEvent event) {
        if (event.getBed().getType() != Material.BED) {
            return;
        }
        EventListener.getBackend().logEnterExit(event.getPlayer());
        AntiCheat.getManager().addEvent(event.getEventName(), event.getHandlers().getRegisteredListeners());
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onPlayerExitBed(final PlayerBedLeaveEvent event) {
        if (event.getBed().getType() != Material.BED) {
            return;
        }
        EventListener.getBackend().logEnterExit(event.getPlayer());
        AntiCheat.getManager().addEvent(event.getEventName(), event.getHandlers().getRegisteredListeners());
    }
    
    @EventHandler
    public void onPlayerAnimation(final PlayerAnimationEvent event) {
        EventListener.getBackend().getBlockCheck().logAnimation(event.getPlayer());
        AntiCheat.getManager().addEvent(event.getEventName(), event.getHandlers().getRegisteredListeners());
    }
    
    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        final String section = "§";
        if (EventListener.getCheckManager().willCheck(player, CheckType.ZOMBE_FLY)) {
            player.sendMessage(section + "f " + section + "f " + section + "1 " + section + "0 " + section + "2 " + section + "4");
        }
        if (EventListener.getCheckManager().willCheck(player, CheckType.ZOMBE_CHEAT)) {
            player.sendMessage(section + "f " + section + "f " + section + "2 " + section + "0 " + section + "4 " + section + "8");
        }
        if (EventListener.getCheckManager().willCheck(player, CheckType.ZOMBE_NOCLIP)) {
            player.sendMessage(section + "f " + section + "f " + section + "4 " + section + "0 " + section + "9 " + section + "6");
        }
        EventListener.getBackend().logJoin(player);
        final User user = new User(player.getName());
        user.setIsWaitingOnLevelSync(true);
        AntiCheat.loadLevelToUser(user);
        EventListener.getUserManager().addUser(user);
        if (player.hasMetadata("ac-spydata")) {
            for (final Player p : player.getServer().getOnlinePlayers()) {
                if (!Permission.SYSTEM_SPY.get((CommandSender)p)) {
                    p.hidePlayer(player);
                }
            }
        }
        AntiCheat.getManager().addEvent(event.getEventName(), event.getHandlers().getRegisteredListeners());
    }
    
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerMove(final PlayerMoveEvent event) {
        final Player player = event.getPlayer();
        boolean setBack = false;
        if (!this.setbackLocation.containsKey(player.getName())) {
            this.setbackLocation.put(player.getName(), player.getLocation());
        }
        if (player == null || player.getName() == null) {
            return;
        }
        if (EventListener.getCheckManager().checkInWorld(player) && !EventListener.getCheckManager().isOpExempt(player)) {
            final Location from = event.getFrom();
            final Location to = event.getTo();
            final Distance distance = new Distance(from, to);
            final double y = distance.getYDifference();
            EventListener.getBackend().getMovementCheck().logAscension(player, from.getY(), to.getY());
            final User user = EventListener.getUserManager().getUser(player.getName());
            user.setTo(to.getX(), to.getY(), to.getZ());
            if (EventListener.getCheckManager().willCheckQuick(player, CheckType.SPEED)) {
                final CheckResult result = EventListener.getBackend().getMovementCheck().checkFreeze(player, from.getY(), to.getY());
                if (result.failed()) {
                    EventListener.log(result.getMessage(), player, CheckType.SPEED);
                    if (!EventListener.silentMode() && !AntiCheat.developerMode()) {
                        player.kickPlayer("Freezing client");
                    }
                }
            }
            if (EventListener.getCheckManager().willCheckQuick(player, CheckType.SPRINT)) {
                final CheckResult result = EventListener.getBackend().getMovementCheck().checkSprintStill(player, from, to);
                if (result.failed()) {
                    event.setCancelled(!EventListener.silentMode());
                    EventListener.log(result.getMessage(), player, CheckType.SPRINT);
                }
            }
            if (EventListener.getCheckManager().willCheckQuick(player, CheckType.FLY) && !player.isFlying()) {
                final CheckResult result = EventListener.getBackend().getMovementCheck().checkFlight(player, distance);
                if (result.failed()) {
                    if (!EventListener.silentMode()) {
                        setBack = true;
                    }
                    EventListener.log(result.getMessage(), player, CheckType.FLY);
                }
            }
            if (EventListener.getCheckManager().willCheckQuick(player, CheckType.FLY) && !player.isFlying()) {
                final CheckResult result = EventListener.getBackend().getMovementCheck().checkGlide(player);
                if (result.failed()) {
                    if (!EventListener.silentMode()) {
                        setBack = true;
                    }
                    EventListener.log(result.getMessage(), player, CheckType.FLY);
                }
            }
            if (EventListener.getCheckManager().willCheckQuick(player, CheckType.VCLIP) && event.getFrom().getY() > event.getTo().getY()) {
                final CheckResult result = EventListener.getBackend().getMovementCheck().checkVClip(player, new Distance(event.getFrom(), event.getTo()));
                if (result.failed()) {
                    if (!EventListener.silentMode()) {
                        setBack = true;
                    }
                    EventListener.log(result.getMessage(), player, CheckType.VCLIP);
                }
            }
            if (EventListener.getCheckManager().willCheckQuick(player, CheckType.VCLIP)) {
                final CheckResult result = EventListener.getBackend().getMovementCheck().checkNoclip(player);
                if (result.failed()) {
                    if (!EventListener.silentMode()) {
                        setBack = true;
                    }
                    EventListener.log(result.getMessage(), player, CheckType.VCLIP);
                }
            }
            if (EventListener.getCheckManager().willCheckQuick(player, CheckType.NOFALL) && EventListener.getCheckManager().willCheck(player, CheckType.FLY) && !Utilities.isClimbableBlock(player.getLocation().getBlock()) && event.getFrom().getY() > event.getTo().getY()) {
                final CheckResult result = EventListener.getBackend().getMovementCheck().checkNoFall(player, y);
                if (result.failed()) {
                    if (!EventListener.silentMode()) {
                        setBack = true;
                        player.damage(1.0);
                    }
                    EventListener.log(result.getMessage(), player, CheckType.NOFALL);
                }
            }
            boolean changed = false;
            if (event.getTo() != event.getFrom()) {
                final double x = distance.getXDifference();
                final double z = distance.getZDifference();
                if (EventListener.getCheckManager().willCheckQuick(player, CheckType.SPEED) && EventListener.getCheckManager().willCheck(player, CheckType.FLY)) {
                    if (event.getFrom().getY() < event.getTo().getY()) {
                        final CheckResult result2 = EventListener.getBackend().getMovementCheck().checkYSpeed(player, y);
                        if (result2.failed()) {
                            if (!EventListener.silentMode()) {
                                setBack = true;
                            }
                            EventListener.log(result2.getMessage(), player, CheckType.SPEED);
                            changed = true;
                        }
                    }
                    final CheckResult result2 = EventListener.getBackend().getMovementCheck().checkXZSpeed(player, x, z);
                    if (result2.failed()) {
                        if (!EventListener.silentMode()) {
                            setBack = true;
                        }
                        EventListener.log(result2.getMessage(), player, CheckType.SPEED);
                        changed = true;
                    }
                    if ((event.getFrom().getX() != event.getTo().getX() || event.getFrom().getZ() != event.getTo().getZ()) && EventListener.getCheckManager().willCheckQuick(player, CheckType.MOREPACKETS)) {
                        final CheckResult result3 = EventListener.getBackend().getMovementCheck().checkTimer(player);
                        if (result3.failed()) {
                            if (!EventListener.silentMode()) {
                                setBack = true;
                            }
                            EventListener.log(result3.getMessage(), player, CheckType.MOREPACKETS);
                            changed = true;
                        }
                    }
                }
                if (EventListener.getCheckManager().willCheckQuick(player, CheckType.WATER_WALK)) {
                    final CheckResult result2 = EventListener.getBackend().getMovementCheck().checkWaterWalk(player, x, y, z);
                    if (result2.failed()) {
                        if (!EventListener.silentMode()) {
                            setBack = true;
                        }
                        EventListener.log(result2.getMessage(), player, CheckType.WATER_WALK);
                        changed = true;
                    }
                }
                if (EventListener.getCheckManager().willCheckQuick(player, CheckType.SNEAK)) {
                    final CheckResult result2 = EventListener.getBackend().getMovementCheck().checkSneak(player, x, z);
                    if (result2.failed()) {
                        if (!EventListener.silentMode()) {
                            setBack = true;
                            player.setSneaking(false);
                        }
                        EventListener.log(result2.getMessage(), player, CheckType.SNEAK);
                        changed = true;
                    }
                }
                if (EventListener.getCheckManager().willCheckQuick(player, CheckType.SPIDER)) {
                    final CheckResult result2 = EventListener.getBackend().getMovementCheck().checkSpider(player, y);
                    if (result2.failed()) {
                        if (!EventListener.silentMode()) {
                            setBack = true;
                        }
                        EventListener.log(result2.getMessage(), player, CheckType.SPIDER);
                        changed = true;
                    }
                }
                if (EventListener.getCheckManager().willCheckQuick(player, CheckType.VELOCITY)) {
                    final CheckResult result2 = EventListener.getBackend().getMovementCheck().checkVelocitized(player, distance);
                    if (result2.failed()) {
                        EventListener.log(result2.getMessage(), player, CheckType.VELOCITY);
                    }
                }
                if (!changed) {
                    user.setGoodLocation(event.getFrom());
                }
            }
            if (setBack) {
                player.teleport((Location)this.setbackLocation.get(player.getName()));
            }
            else if (Utilities.isSafeSetbackLocation(player)) {
                this.setbackLocation.put(player.getName(), player.getLocation());
            }
        }
        AntiCheat.getManager().addEvent(event.getEventName(), event.getHandlers().getRegisteredListeners());
    }
    
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void checkFly(final PlayerMoveEvent event) {
        final Player player = event.getPlayer();
        final User user = EventListener.getUserManager().getUser(player.getName());
        final Location from = event.getFrom();
        final Location to = event.getTo();
        if (!user.checkTo(to.getX(), to.getY(), to.getZ())) {
            return;
        }
        if (EventListener.getCheckManager().willCheck(player, CheckType.FLY) && !player.isFlying()) {
            final CheckResult result1 = EventListener.getBackend().getMovementCheck().checkYAxis(player, new Distance(from, to));
            final CheckResult result2 = EventListener.getBackend().getMovementCheck().checkAscension(player, from.getY(), to.getY());
            final String log = result1.failed() ? result1.getMessage() : (result2.failed() ? result2.getMessage() : "");
            if (!log.equals("")) {
                if (!EventListener.silentMode()) {
                    event.setTo(user.getGoodLocation(from.clone()));
                }
                EventListener.log(log, player, CheckType.FLY);
            }
        }
    }
}
