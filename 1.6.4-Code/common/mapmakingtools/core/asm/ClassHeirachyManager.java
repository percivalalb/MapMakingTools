package mapmakingtools.core.asm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import org.objectweb.asm.tree.ClassNode;

public class ClassHeirachyManager {

    
    public static boolean classExtends(String clazz, String superclass) {        
        if(clazz.equals(superclass))
            return true;
        
        if(clazz.equals("java.lang.Object"))
            return false;
        
        return false;
    }

    public static String getSuperClass(String c)  {
        return "java.lang.Object";
    }
}
