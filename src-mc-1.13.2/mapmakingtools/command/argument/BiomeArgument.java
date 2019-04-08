package mapmakingtools.command.argument;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import net.minecraft.command.CommandSource;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.init.Biomes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.registries.ForgeRegistries;

public class BiomeArgument implements ArgumentType<Biome> {
	
	private static final Collection<String> EXAMPLES = Stream.of(Biomes.PLAINS, Biomes.FOREST).map((biome) -> {
	      return ForgeRegistries.BIOMES.getKey(biome).toString();
	   }).collect(Collectors.toList());
	public static final DynamicCommandExceptionType INVALID_ARG = new DynamicCommandExceptionType((arg) -> {
		return new TextComponentTranslation("argument.mapmakingtools.biome.invalid", arg);
	});

	public static BiomeArgument biome() {
		return new BiomeArgument();
	}

	public static Biome getBiome(CommandContext<CommandSource> context, String name) {
		return context.getArgument(name, Biome.class);
	}

	@Override
	public Biome parse(StringReader parse) throws CommandSyntaxException {
		ResourceLocation resourcelocation = ResourceLocation.read(parse);
		Biome biome = ForgeRegistries.BIOMES.getValue(resourcelocation);
		if (biome == null) {
			throw INVALID_ARG.create(resourcelocation);
		} else {
			return biome;
		}
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> ctx, SuggestionsBuilder suggestionBuilder) {
		return ISuggestionProvider.suggestIterable(ForgeRegistries.BIOMES.getKeys(), suggestionBuilder);
	}

	@Override
	public Collection<String> getExamples() {
		return EXAMPLES;
	}
}