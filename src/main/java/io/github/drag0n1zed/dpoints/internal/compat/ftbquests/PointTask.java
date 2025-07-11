package io.github.drag0n1zed.dpoints.internal.compat.ftbquests;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.TeamData;
import dev.ftb.mods.ftbquests.quest.task.ISingleLongValueTask;
import dev.ftb.mods.ftbquests.quest.task.Task;
import dev.ftb.mods.ftbquests.quest.task.TaskType;
import dev.ftb.mods.ftblibrary.util.TooltipList;
import io.github.drag0n1zed.dpoints.api.PointsApi;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Quest task that requires spending a specified number of dPoints to complete.
 */
public class PointTask extends Task implements ISingleLongValueTask {
    public static TaskType TYPE;
    private long value = 0L;

    public PointTask(long id, Quest quest) {
        super(id, quest);
        this.value = 0L;
    }

    @Override
    public TaskType getType() {
        return TYPE;
    }

    @Override
    public long getMaxProgress() {
        return value;
    }

    @Override
    public String formatMaxProgress() {
        return String.valueOf(value);
    }

    @Override
    public String formatProgress(TeamData teamData, long progress) {
        return String.valueOf(progress);
    }

    @Override
    public void writeData(CompoundTag nbt) {
        super.writeData(nbt);
        nbt.putLong("value", value);
    }

    @Override
    public void readData(CompoundTag nbt) {
        super.readData(nbt);
        if (nbt.contains("value")) {
            value = nbt.getLong("value");
        } else {
            value = 0L;
        }
    }

    @Override
    public void writeNetData(FriendlyByteBuf buf) {
        super.writeNetData(buf);
        buf.writeVarLong(value);
    }

    @Override
    public void readNetData(FriendlyByteBuf buf) {
        super.readNetData(buf);
        value = buf.readVarLong();
    }

    @Override
    public boolean consumesResources() {
        return true;
    }

    @Override
    public void setValue(long v) {
        value = v;
    }

    @Override
    public void fillConfigGroup(ConfigGroup config) {
        super.fillConfigGroup(config);
        config.addLong("value", value, v -> value = v, 0L, 0L, Long.MAX_VALUE);
    }

    @Override
    public Component getAltTitle() {
        return Component.literal(String.valueOf(value)).append(Component.translatable("dpoints.points"));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void addMouseOverText(TooltipList list, TeamData teamData) {
        super.addMouseOverText(list, teamData);
        // Display player's current cached point balance
        String balance = String.valueOf(PointsApi.getPlayerPoints(null));
        Component balanceText = Component.translatable("dpoints.balance")
                .append(": ")
                .append(Component.literal(balance).withStyle(ChatFormatting.GRAY));
        list.add(balanceText);
    }

    @Override
    public void submitTask(TeamData teamData, ServerPlayer player, ItemStack craftedItem) {
        if (teamData.isCompleted(this)) {
            return;
        }

        long playerPoints = PointsApi.getPlayerPoints(player);
        long progress = teamData.getProgress(this);
        long needed = value - progress;

        if (needed <= 0) {
            return;
        }

        if (playerPoints >= needed) {
            int amountToTake = (int) needed;

            PointsApi.removePlayerPoints(player, amountToTake);
            teamData.addProgress(this, amountToTake);
        }
    }
}