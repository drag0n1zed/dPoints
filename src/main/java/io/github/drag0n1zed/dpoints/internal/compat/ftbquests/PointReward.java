package io.github.drag0n1zed.dpoints.internal.compat.ftbquests;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftbquests.net.DisplayRewardToastMessage;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.reward.Reward;
import dev.ftb.mods.ftbquests.quest.reward.RewardType;
import io.github.drag0n1zed.dpoints.api.PointsApi;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

/**
 * Quest reward that grants a defined number of dPoints, persists NBT data, and notifies player.
 */
public class PointReward extends Reward {

    public static RewardType TYPE;
    public long value = 1L;

    public PointReward(long id, Quest q) {
        super(id, q);
    }

    @Override
    public RewardType getType() {
        return TYPE;
    }

    @Override
    public void writeData(CompoundTag nbt) {
        super.writeData(nbt);
        nbt.putLong("value", value);
    }

    @Override
    public void readData(CompoundTag nbt) {
        super.readData(nbt);
        value = nbt.getLong("value");
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
    public void fillConfigGroup(ConfigGroup config) {
        super.fillConfigGroup(config);
        config.addLong("value", value, v -> value = v, 1L, 1L, Long.MAX_VALUE);
    }

    /**
     * Applies reward by adding configured points to the player and showing toast if enabled.
     * @param player Server player to credit.
     * @param notify Whether to display reward toast.
     */
    @Override
    public void claim(ServerPlayer player, boolean notify) {
        PointsApi.addPlayerPoints(player, value);
        if (notify) {
            new DisplayRewardToastMessage(id, getAltTitle(), Icon.getIcon("minecraft:diamond")).sendTo(player);
        }
    }

    @Override
    public Component getAltTitle() {
        return Component.literal(String.valueOf(value)).append(" Points");
    }

    @Override
    public String getButtonText() {
        return String.valueOf(value);
    }
}