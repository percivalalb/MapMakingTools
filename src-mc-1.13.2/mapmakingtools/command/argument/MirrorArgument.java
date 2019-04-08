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
import net.minecraft.util.Mirror;
import net.minecraft.util.text.TextComponentTranslation;

public class MirrorArgument implements ArgumentType<Mirror> {
	
	private static final Collection<String> EXAMPLES = Stream.of(Mirror.LEFT_RIGHT, Mirror.FRONT_BACK).map(m -> m.name()).collect(Collectors.toList());
	public static final DynamicCommandExceptionType INVALID_ARG = new DynamicCommandExceptionType((arg) -> {
		return new TextComponentTranslation("argument.mapmakingtools.mirror.invalid", arg);
	});

	public static MirrorArgument mirror() {
		return new MirrorArgument();
	}

	public static Mirror getMirror(CommandContext<CommandSource> context, String name) {
		return context.getArgument(name, Mirror.class);
	}

	@Override
	public Mirror parse(StringReader parse) throws CommandSyntaxException {
		String s = parse.readUnquotedString();
		Mirror mirror = Stream.of(Mirror.values()).filter(m -> m.name().equals(s)).findFirst().orElseGet(null);
		if (mirror == null) {
			throw INVALID_ARG.create(s);
		} else {
			return mirror;
		}
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> ctx, SuggestionsBuilder suggestionBuilder) {
		return ISuggestionProvider.suggest(Stream.of(Mirror.values()).map(m -> m.name()), suggestionBuilder);
	}

	@Override
	public Collection<String> getExamples() {
		return EXAMPLES;
	}
}