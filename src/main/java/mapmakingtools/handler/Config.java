package mapmakingtools.handler;

import mapmakingtools.MapMakingTools;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;

public class Config {

    public static CommonConfig SERVER;
    private static ForgeConfigSpec CONFIG_COMMON_SPEC;

    public static void init(IEventBus modEventBus) {
        Pair<CommonConfig, ForgeConfigSpec> commonPair = new ForgeConfigSpec.Builder().configure(CommonConfig::new);
        CONFIG_COMMON_SPEC = commonPair.getRight();
        SERVER = commonPair.getLeft();

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CONFIG_COMMON_SPEC);
    }

    public static class CommonConfig {

        public ForgeConfigSpec.ConfigValue<String> CMD_QUICK_BUILD_FORMAT;

        public CommonConfig(ForgeConfigSpec.Builder builder) {
            builder.push("General");

            builder.pop();
            builder.push("Commands");
            builder.push("Quick Build");

            CMD_QUICK_BUILD_FORMAT = builder
                    .comment("Quick build command format. Use <cmd> to represent the command name, for example 'qb-<cmd>' makes the commands /qb-set, /qb-undo etc")
                    .translation("mapmakingtools.config.commands.quick_build.format")
                    .define("format", "/<cmd>");

            builder.pop();
            builder.pop();
        }

        public String generateQuickBuildCommand(String cmd) {
            String format = CMD_QUICK_BUILD_FORMAT.get();

            String placeholder = "<cmd>";
            int fIdx = format.indexOf(placeholder);
            int lIdx = format.lastIndexOf(placeholder);

            if (fIdx == -1 || fIdx != lIdx) {
                MapMakingTools.LOGGER.warn("Command format is not valid: {}. Defaulting to /{}", format, cmd);
                return "/" + cmd;
            }

            // We have already checked there is only one occurrence
            return format.replaceFirst("<cmd>", cmd);
        }
    }
}
