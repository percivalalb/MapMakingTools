package mapmakingtools.asm;

import cpw.mods.fml.common.FMLLog;
import net.minecraft.launchwrapper.IClassTransformer;

/**
 * @author ProPercivalalb
 */
public class ASMTransformer implements IClassTransformer {

	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass) { 
		return basicClass;
	}

}
