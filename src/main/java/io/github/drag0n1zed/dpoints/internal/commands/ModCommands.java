package io.github.drag0n1zed.dpoints.internal.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.drag0n1zed.dpoints.api.PointsApi;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

/**
 * Handles registration and execution of "/dpoints" subcommands for managing player points across multiple currencies.
 */
public class ModCommands {

    /**
     * Registers the dpoints command and its subcommands with support for currency parameters.
     */
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("dpoints")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("add")
                        .then(Commands.argument("player", EntityArgument.player())
                                .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                                        .then(Commands.argument("currency", StringArgumentType.string())
                                                .executes(ModCommands::addPoints)))))
                .then(Commands.literal("remove")
                        .then(Commands.argument("player", EntityArgument.player())
                                .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                                        .then(Commands.argument("currency", StringArgumentType.string())
                                                .executes(ModCommands::removePoints)))))
                .then(Commands.literal("set")
                        .then(Commands.argument("player", EntityArgument.player())
                                .then(Commands.argument("amount", IntegerArgumentType.integer(0))
                                        .then(Commands.argument("currency", StringArgumentType.string())
                                                .executes(ModCommands::setPoints)))))
                .then(Commands.literal("get")
                        .then(Commands.argument("player", EntityArgument.player())
                                .then(Commands.argument("currency", StringArgumentType.string())
                                        .executes(ModCommands::getPoints))))
        );
    }

    private static int addPoints(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        String currency = StringArgumentType.getString(context, "currency");
        ServerPlayer player = EntityArgument.getPlayer(context, "player");
        int amount = IntegerArgumentType.getInteger(context, "amount");
        PointsApi.addPlayerPoints(player, currency, amount);
        long newTotal = PointsApi.getPlayerPoints(player, currency);
        context.getSource().sendSuccess(() -> Component.translatable("info.dpoints.update.success",
            player.getDisplayName(), newTotal, currency), true);
        return 1;
    }

    private static int removePoints(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        String currency = StringArgumentType.getString(context, "currency");
        ServerPlayer player = EntityArgument.getPlayer(context, "player");
        int amount = IntegerArgumentType.getInteger(context, "amount");
        PointsApi.removePlayerPoints(player, currency, amount);
        long newTotal = PointsApi.getPlayerPoints(player, currency);
        context.getSource().sendSuccess(() -> Component.translatable("info.dpoints.update.success",
                player.getDisplayName(), newTotal, currency), true);
        return 1;
    }

    private static int setPoints(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        String currency = StringArgumentType.getString(context, "currency");
        ServerPlayer player = EntityArgument.getPlayer(context, "player");
        int amount = IntegerArgumentType.getInteger(context, "amount");
        PointsApi.setPlayerPoints(player, currency, amount);
        context.getSource().sendSuccess(() -> Component.translatable("info.dpoints.update.success",
                player.getDisplayName(), amount, currency), true);
        return 1;
    }

    private static int getPoints(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        String currency = StringArgumentType.getString(context, "currency");
        ServerPlayer player = EntityArgument.getPlayer(context, "player");
        long points = PointsApi.getPlayerPoints(player, currency);
        context.getSource().sendSuccess(() -> Component.translatable("info.dpoints.get",
            player.getDisplayName(), points, currency), false);
        return 1;
    }
}