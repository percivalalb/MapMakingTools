package mapmakingtools.core.asm;

import java.io.File;
import java.util.Map;

import cpw.mods.fml.relauncher.IFMLCallHook;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.TransformerExclusions;

@TransformerExclusions(value={"mods.mapmakingtools.core.asm"})
public class MapMakingToolsPlugin implements IFMLLoadingPlugin, IFMLCallHook {
   
	@Override
    public String[] getLibraryRequestClass() {
        return null;
    }

    @Override
    public String[] getASMTransformerClass() {
        return new String[]{"mods.mapmakingtools.core.asm.MMTTransformer"};
    }

    @Override
    public String getModContainerClass() {
        return "mods.mapmakingtools.core.asm.MMTModContainer";
    }

    @Override
    public String getSetupClass() {
        return "mods.mapmakingtools.core.asm.MapMakingToolsPlugin";
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
