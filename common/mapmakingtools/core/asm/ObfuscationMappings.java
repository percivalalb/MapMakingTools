package mapmakingtools.core.asm;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import com.google.common.base.Objects;

import cpw.mods.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;

public class ObfuscationMappings
{
    public static class ClassMapping
    {
        public String s_class;
        
        public ClassMapping(String name)
        {
            this.s_class = name;
            if(name.contains("."))
                throw new IllegalArgumentException(name);
        }
        
        public boolean matches(ClassNode node)
        {
            return s_class.equals(node.name);
        }

        public boolean isClass(String name)
        {
            return name.replace('.', '/').equals(s_class);
        }

        public String javaClass()
        {
            return s_class.replace('/', '.');
        }
        
        @Override
        public boolean equals(Object obj)
        {
            if(!(obj instanceof ClassMapping))
                return false;
            
            return s_class.equals(((ClassMapping)obj).s_class);
        }
        
        @Override
        public int hashCode()
        {
            return s_class.hashCode();
        }
        
        @Override
        public String toString()
        {
            return "["+s_class+"]";
        }
    }
    
    public static class DescriptorMapping
    {
        public String s_owner;
        public String s_name;
        public String s_desc;
        
        public boolean searge;
        
        public DescriptorMapping(String owner, String name, String desc)
        {
            this.s_owner = owner;
            this.s_name = name;
            this.s_desc = desc;

            if(s_owner.contains("."))
                throw new IllegalArgumentException(s_owner);
        }
        
        public DescriptorMapping(DescriptorMapping descmap, String subclass)
        {
            this(subclass, descmap.s_name, descmap.s_desc);
        }
                
        public DescriptorMapping subclass(String subclass)
        {
            return new DescriptorMapping(this, subclass);
        }

        public boolean matches(MethodNode node)
        {
            return s_name.equals(node.name) && s_desc.equals(node.desc);
        }
        
        public boolean matches(MethodInsnNode node)
        {
            return s_owner.equals(node.owner) && s_name.equals(node.name) && s_desc.equals(node.desc);
        }

        public MethodInsnNode toInsn(int opcode)
        {
            return new MethodInsnNode(opcode, s_owner, s_name, s_desc);
        }
        
        public void visitMethodInsn(MethodVisitor mv, int opcode)
        {
            mv.visitMethodInsn(opcode, s_owner, s_name, s_desc);
        }

        public boolean isClass(String name)
        {
            return name.replace('.', '/').equals(s_owner);
        }

        public boolean matches(String name, String desc)
        {
            return s_name.equals(name) && s_desc.equals(desc);
        }

        public boolean matches(FieldNode node)
        {
            return s_name.equals(node.name) && s_desc.equals(node.desc);
        }
        
        public boolean matches(FieldInsnNode node)
        {
            return s_owner.equals(node.owner) && s_name.equals(node.name) && s_desc.equals(node.desc);
        }

        public FieldInsnNode toFieldInsn(int opcode)
        {
            return new FieldInsnNode(opcode, s_owner, s_name, s_desc);
        }
        
        public void visitFieldInsn(MethodVisitor mv, int opcode)
        {
            mv.visitFieldInsn(opcode, s_owner, s_name, s_desc);
        }

        public String javaClass()
        {
            return s_owner.replace('/', '.');
        }
        
        @Override
        public boolean equals(Object obj)
        {
            if(!(obj instanceof DescriptorMapping))
                return false;
            
            DescriptorMapping desc = (DescriptorMapping)obj;
            return s_owner.equals(desc.s_owner) && s_name.equals(desc.s_name) && s_desc.equals(desc.s_desc);
        }
        
        @Override
        public int hashCode()
        {
            return Objects.hashCode(s_desc, s_name, s_owner);
        }
        
        @Override
        public String toString()
        {
            if(s_name.length() == 0)
                return "["+s_owner+"]";
            if(s_desc.length() == 0)
                return "["+s_owner+"."+s_name+"]";
            return "["+s_owner+"."+s_name+s_desc+"]";
        }

        public void toRuntime()
        {
            if(!obfuscated || searge)
                return;
            
            if(s_desc.contains("("))
                s_name = FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(s_owner, s_name, s_desc);
            else
                s_name = FMLDeobfuscatingRemapper.INSTANCE.mapFieldName(s_owner, s_name, s_desc);
            s_owner = FMLDeobfuscatingRemapper.INSTANCE.mapType(s_owner);
            s_desc = FMLDeobfuscatingRemapper.INSTANCE.mapDesc(s_desc);
            
            //s_name = MCPDeobfuscationTransformer.map(s_name);
        }
    }
    
    public static boolean obfuscated = false;
}
