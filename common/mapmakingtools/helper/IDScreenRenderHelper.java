package mapmakingtools.helper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

/**
 * @author ProPercivalalb
 */
public class IDScreenRenderHelper {
	
	public static List<ItemStack> stacksDisplay = new ArrayList<ItemStack>();
	
	public static Iterator<ItemStack> getIterator() {
		return stacksDisplay.iterator();
	}
	
	public static int getListSize() {
		return stacksDisplay.size();
	}
	
	public static void addStack(int blockId, int blockMeta) {
		//ItemStack res = new ItemStack(Block.func_149729_e(blockId), 1, blockMeta);
		//stacksDisplay.add(res);
	}
	
	static {
		addStack(1, 0); //Stone
		addStack(2, 0); //Grass Block
		addStack(3, 0); //Dirt
		addStack(4, 0); //Cobblestone
		addStack(5, 0); //Oak Wood Planks
		addStack(5, 1); //Pine Wood Planks
		addStack(5, 2); //Birch Wood Planks
		addStack(5, 3); //Jungle Wood Planks
		addStack(6, 0); //Oak Sapling
		addStack(6, 1); //Pine Sapling
		addStack(6, 2); //Birch Sapling
		addStack(6, 3); //Jungle Sapling
		addStack(7, 0); //Bedrock
		addStack(8, 0); //Water
		addStack(9, 0); //Water
		addStack(10, 0); //Lava
		addStack(11, 0); //Lava
		addStack(12, 0); //Sand
		addStack(13, 0); //Gravel
		addStack(14, 0); //Gold Ore
		addStack(15, 0); //Iron Ore
		addStack(16, 0); //Coal Ore
		addStack(17, 0); //Oak Wood
		addStack(17, 1); //Pine Wood
		addStack(17, 2); //Birch Wood
		addStack(17, 3); //Jungle Wood
		addStack(18, 0); //Oak Leaves
		addStack(18, 1); //Pine Leaves
		addStack(18, 2); //Birch Leaves
		addStack(18, 3); //Jungle Leaves
		addStack(19, 0); //Sponge
		addStack(20, 0); //Glass
		addStack(21, 0); //Lapis Lazuli Ore
		addStack(22, 0); //Lapis Lazuli Block
		addStack(23, 0); //Dispenser
		addStack(24, 0); //Sandstone
		addStack(25, 0); //Note Block
		addStack(26, 0); //Bed
		addStack(27, 0); //Powered Rail
		addStack(28, 0); //Detector Rail
		addStack(29, 0); //Sticky Piston
		addStack(30, 0); //Cobweb
		addStack(31, 0); //Shrub
		addStack(31, 1); //Shrub
		addStack(31, 2); //Shrub
		addStack(31, 3); //Shrub
		addStack(32, 0); //Dead Bush
		addStack(33, 0); //Piston
		addStack(34, 0); //Piston Head
		addStack(35, 0); //Wool
		addStack(35, 1); //Wool
		addStack(35, 2); //Wool
		addStack(35, 3); //Wool
		addStack(35, 4); //Wool
		addStack(35, 5); //Wool
		addStack(35, 6); //Wool
		addStack(35, 7); //Wool
		addStack(35, 8); //Wool
		addStack(35, 9); //Wool
		addStack(35, 10); //Wool
		addStack(35, 11); //Wool
		addStack(35, 12); //Wool
		addStack(35, 13); //Wool
		addStack(35, 14); //Wool
		addStack(35, 15); //Wool
		addStack(36, 0); //Piston Head
		addStack(37, 0); //Flower
		addStack(38, 0); //Rose
		addStack(39, 0); //Mushroom
		addStack(40, 0); //Mushroom
		addStack(41, 0); //Block of Gold
		addStack(42, 0); //Block of Iron
		addStack(44, 0); //Stone Slab
		addStack(44, 1); //Stone Slab
		addStack(44, 2); //Stone Slab
		addStack(44, 3); //Stone Slab
		addStack(44, 3); //Stone Slab
		addStack(44, 4); //Stone Slab
		addStack(44, 5); //Stone Slab
		addStack(44, 6); //Stone Slab
		addStack(44, 7); //Stone Slab
		addStack(45, 0); //Bricks
		addStack(46, 0); //TNT
		addStack(47, 0); //Bookshelf
		addStack(48, 0); //Moss Stone
		addStack(49, 0); //Obsidian
		addStack(50, 0); //Torch
		addStack(51, 0); //Fire
		addStack(52, 0); //Monster Spawner
		addStack(53, 0); //Oak Wood Stairs
		addStack(54, 0); //Chest
		addStack(55, 0); //Redstone Dust
		addStack(56, 0); //Diamond Ore
		addStack(57, 0); //Block of Diamond
		addStack(58, 0); //Crafting Table
		addStack(59, 0); //Crops
		addStack(60, 0); //Farmland
		addStack(61, 0); //Furnace
		addStack(62, 0); //Furnace
		addStack(63, 0); //Sign
		addStack(64, 0); //Wooden Door
		addStack(65, 0); //Ladder
		addStack(66, 0); //Rail
		addStack(67, 0); //Stone Stairs
		addStack(68, 0); //Sign
		addStack(69, 0); //Lever
		addStack(70, 0); //Pressure Plate
		addStack(71, 0); //Iron Door
		addStack(72, 0); //Pressure Plate
		addStack(73, 0); //Redstone Ore
		addStack(74, 0); //Redstone Ore
		addStack(75, 0); //Redstone Torch
		addStack(76, 0); //Redstone Torch
		addStack(77, 0); //Button
		addStack(78, 0); //Snow
		addStack(79, 0); //Ice
		addStack(80, 0); //Snow
		addStack(81, 0); //Cactus
		addStack(82, 0); //Clay
		addStack(83, 0); //Sugar cane
		addStack(84, 0); //Jukebox
		addStack(85, 0); //Fence
		addStack(86, 0); //Pumpkin
		addStack(87, 0); //Netherrack
		addStack(88, 0); //Soul Sand
		addStack(89, 0); //Glowstone
		addStack(90, 0); //Portal
		addStack(91, 0); //Jack 'o' Lantern
		addStack(92, 0); //Cake
		addStack(93, 0); //
		addStack(94, 0); //
		addStack(95, 0); //Locked chest
		addStack(96, 0); //Trapdoor
		addStack(97, 0); //Stone Monster Egg
		addStack(97, 1); //Stone Monster Egg
		addStack(97, 2); //Stone Monster Egg
		addStack(98, 0); //Stone Bricks
		addStack(98, 1); //Stone Bricks
		addStack(98, 2); //Stone Bricks
		addStack(98, 3); //Stone Bricks
		addStack(99, 0); //Mushroom
		addStack(100, 0); //Mushroom
		addStack(101, 0); //Iron Bars
		addStack(102, 0); //Glass Pane
		addStack(103, 0); //Melon
		addStack(104, 0); //Block Redstone Repeater (Off)
		addStack(105, 0); //Block Redstone Repeater (On)
		addStack(106, 0); //Vines
		addStack(107, 0); //Fence Gate
		addStack(108, 0); //Brick Stairs
		addStack(109, 0); //Stone Brick Stairs
		addStack(110, 0); //Mycelium
		addStack(111, 0); //Lily Pad
		addStack(112, 0); //Nether Brick
		addStack(113, 0); //Nether Brick Fence
		addStack(114, 0); //Nether Brick Stairs
		addStack(115, 0); //Nether Wart
		addStack(116, 0); //Enchantment Table
		addStack(117, 0); //Block Brewing Stand
		addStack(118, 0); //Cauldron
		addStack(119, 0); //End Portal
		addStack(120, 0); //End Portal
		addStack(121, 0); //End Stone
		addStack(122, 0); //Dragon Egg
		addStack(123, 0); //Redstone Lamp
		addStack(124, 0); //Redstone Lamp
		addStack(125, 0); //Oak Wood Slab
		addStack(126, 0); //Oak Wood Slab
		addStack(127, 0); //Cocoa
		addStack(128, 0); //Sandstone Stairs
		addStack(129, 0); //Emerald Ore
		addStack(130, 0); //Ender Chest
		addStack(131, 0); //Tripwire Hook
		addStack(132, 0); //Tripwire
		addStack(133, 0); //Block of Emerald
		addStack(134, 0); //Spruce Wood Stairs
		addStack(135, 0); //Birch Wood Stairs
		addStack(136, 0); //Jungle Wood Stairs
		addStack(137, 0); //Command Block
		addStack(138, 0); //Beacon
		addStack(139, 0); //Cobblestone Wall
		addStack(139, 1); //Mossy Cobblestone Wall
		addStack(140, 0); //Block Flower pot
		addStack(141, 0); //Carrots
		addStack(142, 0); //Potatoes
		addStack(143, 0); //Button
		addStack(144, 0); //Block Skull
		addStack(145, 0); //Anvil
		addStack(146, 0); //Trapped Chest
		addStack(147, 0); //Weighted Pressure Plate (Light)
		addStack(148, 0); //Weighted Pressure Plate (Heavy)
		addStack(149, 0); //Block Redstone Comparator (Off)
		addStack(150, 0); //Block Redstone Comparator (On)
		addStack(151, 0); //Daylight Sensor
		addStack(152, 0); //Block of Redstone
		addStack(153, 0); //Nether Quartz Ore
		addStack(154, 0); //Hopper
		addStack(155, 0); //Block of Quartz
		addStack(155, 1); //Block of Quartz
		addStack(155, 2); //Block of Quartz
		addStack(156, 0); //Quartz Stairs
		addStack(157, 0); //Activator Rail
		addStack(158, 0); //Dropper
		addStack(159, 0); //Coloured Clay
		addStack(159, 1); //Coloured Clay
		addStack(159, 2); //Coloured Clay
		addStack(159, 3); //Coloured Clay
		addStack(159, 4); //Coloured Clay
		addStack(159, 5); //Coloured Clay
		addStack(159, 6); //Coloured Clay
		addStack(159, 7); //Coloured Clay
		addStack(159, 8); //Coloured Clay
		addStack(159, 9); //Coloured Clay
		addStack(159, 10); //Coloured Clay
		addStack(159, 11); //Coloured Clay
		addStack(159, 12); //Coloured Clay
		addStack(159, 13); //Coloured Clay
		addStack(159, 14); //Coloured Clay
		addStack(159, 15); //Coloured Clay
		addStack(170, 0); //Block of Hay
		addStack(171, 0); //Carpet
		addStack(171, 1); //Carpet
		addStack(171, 2); //Carpet
		addStack(171, 3); //Carpet
		addStack(171, 4); //Carpet
		addStack(171, 5); //Carpet
		addStack(171, 6); //Carpet
		addStack(171, 7); //Carpet
		addStack(171, 8); //Carpet
		addStack(171, 9); //Carpet
		addStack(171, 10); //Carpet
		addStack(171, 11); //Carpet
		addStack(171, 12); //Carpet
		addStack(171, 13); //Carpet
		addStack(171, 14); //Carpet
		addStack(171, 15); //Carpet
		addStack(172, 0); //Hardened Clay
		addStack(173, 0); //Block of Coal
	}
}
