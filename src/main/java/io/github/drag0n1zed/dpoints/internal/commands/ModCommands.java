package io.github.drag0n1zed.dpoints.internal.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.drag0n1zed.dpoints.api.PointsApi;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

/**
 * Handles registration and execution of "/dpoints" subcommands for managing player points.
 */
public class ModCommands {

    /**
     * Registers the dpoints command and its subcommands (add, remove, set, get).
     * @param dispatcher Command dispatcher to register commands with.
     */
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("dpoints")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("add").then(Commands.argument("player", EntityArgument.player())
                        .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                                .executes(ModCommands::addPoints))))
                .then(Commands.literal("remove").then(Commands.argument("player", EntityArgument.player())
                        .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                                .executes(ModCommands::removePoints))))
                .then(Commands.literal("set").then(Commands.argument("player", EntityArgument.player())
                        .then(Commands.argument("amount", IntegerArgumentType.integer(0))
                                .executes(ModCommands::setPoints))))
                .then(Commands.literal("get").then(Commands.argument("player", EntityArgument.player())
                        .executes(ModCommands::getPoints)))
        );
    }

    /**
     * Adds specified points to the target player's balance and notifies command source.
     * @param context Command execution context.
     * @return Success status code.
     */
    private static int addPoints(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = EntityArgument.getPlayer(context, "player");
        int amount = IntegerArgumentType.getInteger(context, "amount");
        PointsApi.addPlayerPoints(player, amount);
        long newTotal = PointsApi.getPlayerPoints(player);
        context.getSource().sendSuccess(() -> Component.translatable("info.dpoints.update.success", player.getDisplayName(), newTotal), true);
        return 1;
    }

    /**
     * Removes specified points from the target player's balance and notifies command source.
     * @param context Command execution context.
     * @return Success status code.
     */
    private static int removePoints(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = EntityArgument.getPlayer(context, "player");
        int amount = IntegerArgumentType.getInteger(context, "amount");
        PointsApi.removePlayerPoints(player, amount);
        long newTotal = PointsApi.getPlayerPoints(player);
        context.getSource().sendSuccess(() -> Component.translatable("info.dpoints.update.success", player.getDisplayName(), newTotal), true);
        return 1;
    }

    /**
     * Sets the target player's points to the given value and notifies command source.
     * @param context Command execution context.
     * @return Success status code.
     */
    private static int setPoints(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = EntityArgument.getPlayer(context, "player");
        int amount = IntegerArgumentType.getInteger(context, "amount");
        PointsApi.setPlayerPoints(player, amount);
        context.getSource().sendSuccess(() -> Component.translatable("info.dpoints.update.success", player.getDisplayName(), amount), true);
        return 1;
    }

    /**
     * Retrieves and displays the target player's current point balance.
     * @param context Command execution context.
     * @return Success status code.
     */
    private static int getPoints(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = EntityArgument.getPlayer(context, "player");
        long points = PointsApi.getPlayerPoints(player);
        context.getSource().sendSuccess(() -> Component.translatable("info.dpoints.get", player.getDisplayName(), points), false);
        return 1;
    }
}