package mapmakingtools.command;

import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

import net.minecraft.util.text.TextComponentTranslation;

public class CommandUtil {

	public static final DynamicCommandExceptionType NO_POINTS_SELECTED2 = new DynamicCommandExceptionType((p_208659_0_) -> {
	      return new TextComponentTranslation("argument.color.invalid", p_208659_0_);
	   });
	
	public static final SimpleCommandExceptionType NO_POINTS_SELECTED = new SimpleCommandExceptionType(new TextComponentTranslation("commands.mapmakingtools.build.postionsnotselected"));
	public static final SimpleCommandExceptionType HAS_NOTHING_TO_UNDO = new SimpleCommandExceptionType(new TextComponentTranslation("commands.mapmakingtools.build.hasnotingtoundo"));
	public static final SimpleCommandExceptionType HAS_NOTHING_TO_REDO = new SimpleCommandExceptionType(new TextComponentTranslation("commands.mapmakingtools.build.hasnotingtoredo"));
	public static final SimpleCommandExceptionType NOTHING_TO_PASTE = new SimpleCommandExceptionType(new TextComponentTranslation("commands.mapmakingtools.build.nothingtorotate"));
}
