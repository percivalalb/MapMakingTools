package mapmakingtools.asm;

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
