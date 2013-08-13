package mapmakingtools.core.asm;

import mapmakingtools.core.asm.ObfuscationMappings.ClassMapping;
import mapmakingtools.core.asm.ObfuscationMappings.DescriptorMapping;
import net.minecraft.launchwrapper.IClassTransformer;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import cpw.mods.fml.relauncher.FMLLaunchHandler;

import static org.objectweb.asm.Opcodes.*;
import static org.objectweb.asm.ClassWriter.*;

public class MMTTransformer implements IClassTransformer {
   
    @Override
    public byte[] transform(String name, String tname, byte[] bytes) {
        if (bytes == null) return null;
        try {
            bytes = ClassOverrider.overrideBytes(name, bytes, new ClassMapping("net/minecraft/item/ItemAxe"), MapMakingToolsPlugin.location);
        }
        catch(Exception e) {
            throw new RuntimeException(e);
        }

        return bytes;
    }
}
