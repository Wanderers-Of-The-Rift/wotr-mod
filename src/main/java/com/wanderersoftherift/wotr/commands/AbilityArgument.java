package com.wanderersoftherift.wotr.commands;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.abilities.AbstractAbility;
import com.wanderersoftherift.wotr.init.RegistryEvents;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Argument for specifying an ability
 */
public class AbilityArgument implements ArgumentType<ResourceLocation> {
    private static final Collection<String> EXAMPLES = Stream
            .of(WanderersOfTheRift.id("fireball"), WanderersOfTheRift.id("mega_boost"))
            .map(ResourceLocation::toString)
            .collect(Collectors.toList());
    private static final DynamicCommandExceptionType ERROR_INVALID_VALUE = new DynamicCommandExceptionType(
            argument -> Component.translatableEscape("argument.wotr.ability.invalid", argument));

    public ResourceLocation parse(StringReader reader) throws CommandSyntaxException {
        return ResourceLocation.read(reader);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        if (context.getSource() instanceof SharedSuggestionProvider) {
            return SharedSuggestionProvider
                    .suggestResource(((SharedSuggestionProvider) context.getSource()).registryAccess()
                            .lookupOrThrow(RegistryEvents.ABILITY_REGISTRY)
                            .keySet(), builder);
        } else {
            return Suggestions.empty();
        }
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    public static AbilityArgument ability() {
        return new AbilityArgument();
    }

    public static Holder<AbstractAbility> getAbility(CommandContext<CommandSourceStack> context, String name)
            throws CommandSyntaxException {
        ResourceLocation resourcelocation = context.getArgument(name, ResourceLocation.class);
        Optional<Holder<AbstractAbility>> result = context.getSource()
                .getLevel()
                .registryAccess()
                .lookup(RegistryEvents.ABILITY_REGISTRY)
                .flatMap(registry -> registry.get(resourcelocation));
        if (result.isPresent()) {
            return result.get();
        } else {
            throw ERROR_INVALID_VALUE.create(resourcelocation);
        }
    }
}