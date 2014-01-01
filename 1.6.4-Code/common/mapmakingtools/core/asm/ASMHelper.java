package mapmakingtools.core.asm;

import java.util.HashMap;
import java.util.List;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import mapmakingtools.core.asm.ObfuscationMappings.DescriptorMapping;

public class ASMHelper {    

    public static ClassNode createClassNode(byte[] bytes) {
        ClassNode cnode = new ClassNode();
        ClassReader reader = new ClassReader(bytes);
        reader.accept(cnode, 0);
        return cnode;
    }

    public static byte[] createBytes(ClassNode cnode, int i)
    {
        ClassWriter cw = new CustomClassWriter(i);
        cnode.accept(cw);
        return cw.toByteArray();
    }
 
}
