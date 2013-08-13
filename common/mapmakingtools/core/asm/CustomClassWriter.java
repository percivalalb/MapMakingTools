package mapmakingtools.core.asm;

import org.objectweb.asm.ClassWriter;

public class CustomClassWriter extends ClassWriter
{
    public CustomClassWriter(int flags)
    {
        super(flags);
    }
    
    @Override
    protected String getCommonSuperClass(String type1, String type2)
    {
        String c = type1.replace('/', '.');
        String d = type2.replace('/', '.');
        if(ClassHeirachyManager.classExtends(d, c))
            return type1;
        if(ClassHeirachyManager.classExtends(c, d))
            return type2;
        do
        {
            c = ClassHeirachyManager.getSuperClass(c);
        }
        while(!ClassHeirachyManager.classExtends(d, c));
        return c.replace('.', '/');
    }
}
