package mapmakingtools.asm;

import java.io.File;
import java.util.Map;

import cpw.mods.fml.relauncher.IFMLCallHook;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.TransformerExclusions;

@TransformerExclusions(value={"mapmakingtools.asm"})
public class MapMakingToolsPlugin implements IFMLLoadingPlugin, IFMLCallHook {

	@Override
	public String getAccessTransformerClass() {
		return MMTAccessTransformer.class.getName();
	}
	
    @Override
    public String[] getASMTransformerClass() {
        return new String[] { ASMTransformer.class.getName() };
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Override
    public String getSetupClass() {
        return MapMakingToolsPlugin.class.getName();
    }

    @Override
    public void injectData(Map<String, Object> data)
    {
        if(data.containsKey("coremodLocation"))
            location = (File) data.get("coremodLocation");
    }
    
	@Override
	public Void call() throws Exception {
		return null;
	}

    public static File location;
}
