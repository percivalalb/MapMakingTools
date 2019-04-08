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
import net.minecraft.util.Rotation;
import net.minecraft.util.text.TextComponentTranslation;

public class RotationArgument implements ArgumentType<Rotation> {
	
	private static final Collection<String> EXAMPLES = Stream.of(Rotation.CLOCKWISE_180, Rotation.CLOCKWISE_90).map(m -> m.name()).collect(Collectors.toList());
	public static final DynamicCommandExceptionType INVALID_ARG = new DynamicCommandExceptionType((arg) -> {
		return new TextComponentTranslation("argument.mapmakingtools.rotation.invalid", arg);
	});

	public static RotationArgument rotation() {
		return new RotationArgument();
	}

	public static Rotation getRotation(CommandContext<CommandSource> context, String name) {
		return context.getArgument(name, Rotation.class);
	}

	@Override
	public Rotation parse(StringReader parse) throws CommandSyntaxException {
		String s = parse.readUnquotedString();
		Rotation rotation = Stream.of(Rotation.values()).filter(m -> m.name().equals(s)).findFirst().orElseGet(null);
		if (rotation == null) {
			throw INVALID_ARG.create(s);
		} else {
			return rotation;
		}
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> ctx, SuggestionsBuilder suggestionBuilder) {
		return ISuggestionProvider.suggest(Stream.of(Rotation.values()).map(m -> m.name()), suggestionBuilder);
	}

	@Override
	public Collection<String> getExamples() {
		return EXAMPLES;
	}
}