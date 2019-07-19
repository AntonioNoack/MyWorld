package net.dynamicdev.anticheat.util;

import org.bukkit.command.*;

import me.corperateraider.generator.MathHelper;
import net.dynamicdev.anticheat.*;

import org.bukkit.entity.*;
import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.enchantments.*;
import org.bukkit.inventory.*;
import org.bukkit.util.*;
import org.bukkit.util.Vector;

import java.util.regex.*;
import java.util.*;

public final class Utilities extends MathHelper {
    private static final List<Material> INSTANT_BREAK;
    private static final List<Material> FOOD;
    private static final List<Material> INTERACTABLE;
    private static final Map<Material, Material> COMBO;
    public static final String SPY_METADATA = "ac-spydata";
    
    public static void alert(final List<String> message) {
        for (final Player player : Bukkit.getOnlinePlayers()) {
            if (Permission.SYSTEM_ALERT.get((CommandSender)player)) {
                for (final String msg : message) {
                    player.sendMessage(msg);
                }
            }
        }
        for (final String msg2 : message) {
            AntiCheat.getManager().log(msg2);
        }
    }
    
    public static boolean isSafeSetbackLocation(final Player player) {
        return (isInWeb(player) || isInWater(player) || !cantStandAtSingle(player.getLocation().getBlock())) && !player.getEyeLocation().getBlock().getType().isSolid();
    }
    
    public static double getXDelta(final Location one, final Location two) {return Math.abs(one.getX() - two.getX());}
    public static double getZDelta(final Location one, final Location two) {return Math.abs(one.getZ() - two.getZ());}
    public static double getDistance3D(final Location one, final Location two) {return one.distance(two);}
    public static double getDistance3D(final SimpleLocation one, final SimpleLocation two) {return Math.sqrt(sq(one.getX()-two.getX())+sq(one.getY()-two.getY())+sq(one.getZ()-two.getZ()));}
    public static double getHorizontalDistance(final Location one, final Location two) {return Math.sqrt(sq(two.getX() - one.getX()) + sq(two.getZ() - one.getZ()));}
    public static double getHorizontalDistance(final SimpleLocation one, final SimpleLocation two) {return Math.sqrt(sq(two.getX() - one.getX()) + sq(two.getZ() - one.getZ()));}
    
    public static boolean cantStandAtBetter(final Block block) {
        final Block otherBlock = block.getRelative(BlockFace.DOWN);
        final boolean center1 = otherBlock.getType() == Material.AIR;
        final boolean north1 = otherBlock.getRelative(BlockFace.NORTH).getType() == Material.AIR;
        final boolean east1 = otherBlock.getRelative(BlockFace.EAST).getType() == Material.AIR;
        final boolean south1 = otherBlock.getRelative(BlockFace.SOUTH).getType() == Material.AIR;
        final boolean west1 = otherBlock.getRelative(BlockFace.WEST).getType() == Material.AIR;
        final boolean northeast1 = otherBlock.getRelative(BlockFace.NORTH_EAST).getType() == Material.AIR;
        final boolean northwest1 = otherBlock.getRelative(BlockFace.NORTH_WEST).getType() == Material.AIR;
        final boolean southeast1 = otherBlock.getRelative(BlockFace.SOUTH_EAST).getType() == Material.AIR;
        final boolean southwest1 = otherBlock.getRelative(BlockFace.SOUTH_WEST).getType() == Material.AIR;
        final boolean overAir1 = otherBlock.getRelative(BlockFace.DOWN).getType() == Material.AIR || otherBlock.getRelative(BlockFace.DOWN).getType() == Material.WATER || otherBlock.getRelative(BlockFace.DOWN).getType() == Material.LAVA;
        return center1 && north1 && east1 && south1 && west1 && northeast1 && southeast1 && northwest1 && southwest1 && overAir1;
    }
    
    public static boolean cantStandAtSingle(final Block block) {
        return block.getRelative(BlockFace.DOWN).getType() == Material.AIR;
    }
    
    public static boolean cantStandAtWater(final Block block) {
        final Block otherBlock = block.getRelative(BlockFace.DOWN);
        final boolean isHover = block.getType() == Material.AIR;
        final boolean n = otherBlock.getRelative(BlockFace.NORTH).getType() == Material.WATER;
        final boolean s = otherBlock.getRelative(BlockFace.SOUTH).getType() == Material.WATER;
        final boolean e = otherBlock.getRelative(BlockFace.EAST).getType() == Material.WATER;
        final boolean w = otherBlock.getRelative(BlockFace.WEST).getType() == Material.WATER;
        final boolean ne = otherBlock.getRelative(BlockFace.NORTH_EAST).getType() == Material.WATER;
        final boolean nw = otherBlock.getRelative(BlockFace.NORTH_WEST).getType() == Material.WATER;
        final boolean se = otherBlock.getRelative(BlockFace.SOUTH_EAST).getType() == Material.WATER;
        final boolean sw = otherBlock.getRelative(BlockFace.SOUTH_WEST).getType() == Material.WATER;
        return n && s && e && w && ne && nw && se && sw && isHover;
    }
    
    public static boolean canStandWithin(final Block block) {
        final boolean isSand = block.getType() == Material.SAND;
        final boolean isGravel = block.getType() == Material.GRAVEL;
        final boolean solid = block.getType().isSolid() && !block.getType().name().toLowerCase().contains("door") && !block.getType().name().toLowerCase().contains("fence") && !block.getType().name().toLowerCase().contains("bars") && !block.getType().name().toLowerCase().contains("sign");
        return !isSand && !isGravel && !solid;
    }
    
    public static Vector getRotation(final Location one, final Location two) {
        final double dx = two.getX() - one.getX();
        final double dy = two.getY() - one.getY();
        final double dz = two.getZ() - one.getZ();
        final double distanceXZ = Math.sqrt(dx * dx + dz * dz);
        final float yaw = (float)(Math.atan2(dz, dx) * 180.0 / 3.141592653589793) - 90.0f;
        final float pitch = (float)(-(Math.atan2(dy, distanceXZ) * 180.0 / 3.141592653589793));
        return new Vector(yaw, pitch, 0.0f);
    }
    
    public static double clamp180(double theta) {
        theta %= 360.0;
        if (theta >= 180.0) {
            theta -= 360.0;
        }
        if (theta < -180.0) {
            theta += 360.0;
        }
        return theta;
    }
    
    public static int getLevelForEnchantment(final Player player, final String enchantment) {
        try {
            final Enchantment theEnchantment = Enchantment.getByName(enchantment);
            for (final ItemStack item : player.getInventory().getArmorContents()) {
                if (item.containsEnchantment(theEnchantment)) {
                    return item.getEnchantmentLevel(theEnchantment);
                }
            }
        }
        catch (Exception e) {
            return -1;
        }
        return -1;
    }
    
    public static boolean cantStandAt(final Block block) {
        return !canStand(block) && cantStandClose(block) && cantStandFar(block);
    }
    
    public static boolean cantStandAtExp(final Location location) {
        return cantStandAt(new Location(location.getWorld(), fixXAxis(location.getX()), location.getY() - 0.01, (double)location.getBlockZ()).getBlock());
    }
    
    public static boolean cantStandClose(final Block block) {
        return !canStand(block.getRelative(BlockFace.NORTH)) && !canStand(block.getRelative(BlockFace.EAST)) && !canStand(block.getRelative(BlockFace.SOUTH)) && !canStand(block.getRelative(BlockFace.WEST));
    }
    
    public static boolean cantStandFar(final Block block) {
        return !canStand(block.getRelative(BlockFace.NORTH_WEST)) && !canStand(block.getRelative(BlockFace.NORTH_EAST)) && !canStand(block.getRelative(BlockFace.SOUTH_WEST)) && !canStand(block.getRelative(BlockFace.SOUTH_EAST));
    }
    
    public static boolean canStand(final Block block) {
        return !block.isLiquid() && block.getType() != Material.AIR;
    }
    
    public static boolean isFullyInWater(final Location player) {
        final double touchedX = fixXAxis(player.getX());
        return (!new Location(player.getWorld(), touchedX, player.getY(), (double)player.getBlockZ()).getBlock().isLiquid() && !new Location(player.getWorld(), touchedX, (double)Math.round(player.getY()), (double)player.getBlockZ()).getBlock().isLiquid()) || (new Location(player.getWorld(), touchedX, player.getY(), (double)player.getBlockZ()).getBlock().isLiquid() && new Location(player.getWorld(), touchedX, (double)Math.round(player.getY()), (double)player.getBlockZ()).getBlock().isLiquid());
    }
    
    public static double fixXAxis(final double x) {
        double touchedX = x;
        final double rem = touchedX - Math.round(touchedX) + 0.01;
        if (rem < 0.3) {
            touchedX = NumberConversions.floor(x) - 1;
        }
        return touchedX;
    }
    
    public static boolean isHoveringOverWater(final Location player, final int blocks) {
        for (int i = player.getBlockY(); i > player.getBlockY() - blocks; --i) {
            final Block newloc = new Location(player.getWorld(), (double)player.getBlockX(), (double)i, (double)player.getBlockZ()).getBlock();
            if (newloc.getType() != Material.AIR) {
                return newloc.isLiquid();
            }
        }
        return false;
    }
    
    public static boolean isHoveringOverWater(final Location player) {
        return isHoveringOverWater(player, 25);
    }
    
    public static boolean isInstantBreak(final Material m) {
        return Utilities.INSTANT_BREAK.contains(m);
    }
    
    public static boolean isFood(final Material m) {
        return Utilities.FOOD.contains(m);
    }
    
    public static boolean isSlab(final Block block) {
        final Material type = block.getType();
        switch (type) {
            case STEP:
            case DOUBLE_STEP:
            case WOOD_STEP:
            case WOOD_DOUBLE_STEP:
                return true;
            default:
                return false;
        }
    }
    
    public static boolean isStair(final Block block) {
        final Material type = block.getType();
        switch (type) {
            case WOOD_STAIRS:
            case SPRUCE_WOOD_STAIRS:
            case SMOOTH_STAIRS:
            case SANDSTONE_STAIRS:
            case QUARTZ_STAIRS:
            case JUNGLE_WOOD_STAIRS:
            case NETHER_BRICK_STAIRS:
            case BIRCH_WOOD_STAIRS:
            case COBBLESTONE_STAIRS:
                return true;
            default:
                return false;
        }
    }
    
    public static boolean isInteractable(final Material m) {
        return Utilities.INTERACTABLE.contains(m);
    }
    
    public static boolean sprintFly(final Player player) {
        return player.isSprinting() || player.isFlying();
    }
    
    public static boolean isOnLilyPad(final Player player) {
        final Block block = player.getLocation().getBlock();
        final Material lily = Material.WATER_LILY;
        return block.getType() == lily || block.getRelative(BlockFace.NORTH).getType() == lily || block.getRelative(BlockFace.SOUTH).getType() == lily || block.getRelative(BlockFace.EAST).getType() == lily || block.getRelative(BlockFace.WEST).getType() == lily;
    }
    
    public static boolean isSubmersed(final Player player) {
        return player.getLocation().getBlock().isLiquid() && player.getLocation().getBlock().getRelative(BlockFace.UP).isLiquid();
    }
    
    public static boolean isInWater(final Player player) {
        return player.getLocation().getBlock().isLiquid() || player.getLocation().getBlock().getRelative(BlockFace.DOWN).isLiquid() || player.getLocation().getBlock().getRelative(BlockFace.UP).isLiquid();
    }
    
    public static boolean isInWeb(final Player player) {
        return player.getLocation().getBlock().getType() == Material.WEB || player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.WEB || player.getLocation().getBlock().getRelative(BlockFace.UP).getType() == Material.WEB;
    }
    
    public static boolean isClimbableBlock(final Block block) {
        return block.getType() == Material.VINE || block.getType() == Material.LADDER || block.getType() == Material.WATER || block.getType() == Material.STATIONARY_WATER;
    }
    
    public static boolean isOnVine(final Player player) {
        return player.getLocation().getBlock().getType() == Material.VINE;
    }
    
    public static boolean isInt(final String string) {
    	try {
            Integer.parseInt(string);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
    
    public static boolean isDouble(final String string) {
        try {
            Double.parseDouble(string);
            return true;
        }
        catch (Exception ex) {
            return false;
        }
    }
    
    public static long calcSurvivalFastBreak(final ItemStack tool, final Material block) {
        if (isInstantBreak(block) || (tool.getType() == Material.SHEARS && block == Material.LEAVES)) {
            return 0L;
        }
        final double bhardness = BlockHardness.getBlockHardness(block);
        final double thardness = ToolHardness.getToolHardness(tool.getType());
        final long enchantlvl = tool.getEnchantmentLevel(Enchantment.DIG_SPEED);
        long result = Math.round(bhardness * thardness * 0.1 * 10000.0);
        if (enchantlvl > 0L) {
            result /= enchantlvl * enchantlvl + 1L;
        }
        result = ((result > 25000L) ? 25000L : ((result < 0L) ? 0L : result));
        if (isQuickCombo(tool, block)) {
            result /= 2L;
        }
        return result;
    }
    
    private static boolean isQuickCombo(final ItemStack tool, final Material block) {
        for (final Material t : Utilities.COMBO.keySet()) {
            if (tool.getType() == t && Utilities.COMBO.get(t) == block) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean blockIsnt(final Block block, final Material[] materials) {
        final Material type = block.getType();
        for (final Material m : materials) {
            if (m == type) {
                return false;
            }
        }
        return false;
    }
    
    public static String[] getCommands(final String command) {
        return command.replaceAll("COMMAND\\[", "").replaceAll("]", "").split(";");
    }
    
    public static String removeWhitespace(final String string) {
        return string.replaceAll(" ", "");
    }
    
    public static boolean hasArmorEnchantment(final Player player, final Enchantment e) {
        for (final ItemStack is : player.getInventory().getArmorContents()) {
            if (is != null && is.containsEnchantment(e)) {
                return true;
            }
        }
        return false;
    }
    
    public static ArrayList<String> stringToList(final String string) {
    	ArrayList<String> ret = new ArrayList<>();
    	ret.add(string);
    	return ret;
    }
    

	public static String arrayToCommaString(String[] actions) {
		if(actions.length==0) return "";
		String ret = actions[0];
		for(int i=1;i<actions.length;i++){
			ret+=","+actions[i];
		}
		return ret;
	}
    
    public static String listToCommaString(final List<String> list) {
        final StringBuilder b = new StringBuilder();
        for (int i = 0; i < list.size(); ++i) {
            b.append(list.get(i));
            if (i < list.size() - 1) {
                b.append(",");
            }
        }
        return b.toString();
    }
    
    public static long lifeToSeconds(final String string) {
        if (string.equals("0") || string.equals("")) {
            return 0L;
        }
        final String[] lifeMatch = { "d", "h", "m", "s" };
        final int[] lifeInterval = { 86400, 3600, 60, 1 };
        long seconds = 0L;
        for (int i = 0; i < lifeMatch.length; ++i) {
            final Matcher matcher = Pattern.compile("([0-9]*)" + lifeMatch[i]).matcher(string);
            while (matcher.find()) {
                seconds += Integer.parseInt(matcher.group(1)) * lifeInterval[i];
            }
        }
        return seconds;
    }
    
    static {
        INSTANT_BREAK = new ArrayList<Material>();
        FOOD = new ArrayList<Material>();
        INTERACTABLE = new ArrayList<Material>();
        COMBO = new HashMap<Material, Material>();
        Utilities.INSTANT_BREAK.add(Material.RED_MUSHROOM);
        Utilities.INSTANT_BREAK.add(Material.RED_ROSE);
        Utilities.INSTANT_BREAK.add(Material.BROWN_MUSHROOM);
        Utilities.INSTANT_BREAK.add(Material.YELLOW_FLOWER);
        Utilities.INSTANT_BREAK.add(Material.REDSTONE);
        Utilities.INSTANT_BREAK.add(Material.REDSTONE_TORCH_OFF);
        Utilities.INSTANT_BREAK.add(Material.REDSTONE_TORCH_ON);
        Utilities.INSTANT_BREAK.add(Material.REDSTONE_WIRE);
        Utilities.INSTANT_BREAK.add(Material.LONG_GRASS);
        Utilities.INSTANT_BREAK.add(Material.PAINTING);
        Utilities.INSTANT_BREAK.add(Material.WHEAT);
        Utilities.INSTANT_BREAK.add(Material.SUGAR_CANE);
        Utilities.INSTANT_BREAK.add(Material.SUGAR_CANE_BLOCK);
        Utilities.INSTANT_BREAK.add(Material.DIODE);
        Utilities.INSTANT_BREAK.add(Material.DIODE_BLOCK_OFF);
        Utilities.INSTANT_BREAK.add(Material.DIODE_BLOCK_ON);
        Utilities.INSTANT_BREAK.add(Material.SAPLING);
        Utilities.INSTANT_BREAK.add(Material.TORCH);
        Utilities.INSTANT_BREAK.add(Material.CROPS);
        Utilities.INSTANT_BREAK.add(Material.SNOW);
        Utilities.INSTANT_BREAK.add(Material.TNT);
        Utilities.INSTANT_BREAK.add(Material.POTATO);
        Utilities.INSTANT_BREAK.add(Material.CARROT);
        Utilities.INTERACTABLE.add(Material.STONE_BUTTON);
        Utilities.INTERACTABLE.add(Material.LEVER);
        Utilities.INTERACTABLE.add(Material.CHEST);
        Utilities.FOOD.add(Material.COOKED_BEEF);
        Utilities.FOOD.add(Material.COOKED_CHICKEN);
        Utilities.FOOD.add(Material.COOKED_FISH);
        Utilities.FOOD.add(Material.GRILLED_PORK);
        Utilities.FOOD.add(Material.PORK);
        Utilities.FOOD.add(Material.MUSHROOM_SOUP);
        Utilities.FOOD.add(Material.RAW_BEEF);
        Utilities.FOOD.add(Material.RAW_CHICKEN);
        Utilities.FOOD.add(Material.RAW_FISH);
        Utilities.FOOD.add(Material.APPLE);
        Utilities.FOOD.add(Material.GOLDEN_APPLE);
        Utilities.FOOD.add(Material.MELON);
        Utilities.FOOD.add(Material.COOKIE);
        Utilities.FOOD.add(Material.BREAD);
        Utilities.FOOD.add(Material.SPIDER_EYE);
        Utilities.FOOD.add(Material.ROTTEN_FLESH);
        Utilities.FOOD.add(Material.POTATO_ITEM);
        Utilities.COMBO.put(Material.SHEARS, Material.WOOL);
        Utilities.COMBO.put(Material.IRON_SWORD, Material.WEB);
        Utilities.COMBO.put(Material.DIAMOND_SWORD, Material.WEB);
        Utilities.COMBO.put(Material.STONE_SWORD, Material.WEB);
        Utilities.COMBO.put(Material.WOOD_SWORD, Material.WEB);
    }
}
