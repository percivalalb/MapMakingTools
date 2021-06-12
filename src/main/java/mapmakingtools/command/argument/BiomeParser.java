package mapmakingtools.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class BiomeParser {

    public static final DynamicCommandExceptionType BIOME_BAD_ID = new DynamicCommandExceptionType((invalidRL) -> {
        return new TranslationTextComponent("argument.biome.id.invalid", invalidRL);
    });
    private static final Function<SuggestionsBuilder, CompletableFuture<Suggestions>> DEFAULT_SUGGESTIONS_BUILDER = (builder) -> {
        return builder.buildFuture();
    };
    private final StringReader reader;
    private Biome biome;
    private int readerCursor;

    private Function<SuggestionsBuilder, CompletableFuture<Suggestions>> suggestionsBuilder = DEFAULT_SUGGESTIONS_BUILDER;

    public BiomeParser(StringReader readerIn) {
        this.reader = readerIn;
    }

    public Biome getBiome() {
        return this.biome;
    }

    public void readBiome() throws CommandSyntaxException {
        int i = this.reader.getCursor();
        ResourceLocation rl = ResourceLocation.read(this.reader);
        Biome tmp = ForgeRegistries.BIOMES.getValue(rl);
        if (tmp == null) {
            this.reader.setCursor(i);
            throw BIOME_BAD_ID.createWithContext(this.reader, rl.toString());
        }
        this.biome = tmp;
    }

    public BiomeParser parse() throws CommandSyntaxException {
        this.readBiome();
        this.suggestionsBuilder = this::suggestItem;

        return this;
    }

    /**
     * Builds a list of suggestions based on item registry names.
     */
    private CompletableFuture<Suggestions> suggestItem(SuggestionsBuilder builder) {
        return builder.buildFuture();
    }

    public CompletableFuture<Suggestions> fillSuggestions(SuggestionsBuilder builder) {
        return this.suggestionsBuilder.apply(builder.createOffset(this.reader.getCursor()));
    }
}
