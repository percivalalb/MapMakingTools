package mapmakingtools.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.world.level.biome.Biome;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class BiomeArgument implements ArgumentType<Biome> {

    private static final Collection<String> EXAMPLES = Arrays.asList("plains", "minecraft:plains");

    public static BiomeArgument biome() {
        return new BiomeArgument();
    }

    @Override
    public Biome parse(StringReader reader) throws CommandSyntaxException {
        BiomeParser biomeParser = new BiomeParser(reader).parse();

        return biomeParser.getBiome();
    }

    public static <S> Biome getBiome(CommandContext<S> context, String name) {
        return context.getArgument(name, Biome.class);
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> p_listSuggestions_1_, SuggestionsBuilder p_listSuggestions_2_) {
        StringReader stringreader = new StringReader(p_listSuggestions_2_.getInput());
        stringreader.setCursor(p_listSuggestions_2_.getStart());
        BiomeParser itemparser = new BiomeParser(stringreader);

        try {
            itemparser.parse();
        } catch (CommandSyntaxException commandsyntaxexception) {
        }

        return itemparser.fillSuggestions(p_listSuggestions_2_);
    }

    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}
