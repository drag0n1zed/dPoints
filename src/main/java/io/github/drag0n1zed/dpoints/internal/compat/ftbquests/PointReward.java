package io.github.drag0n1zed.dpoints.internal.compat.ftbquests;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftbquests.net.DisplayRewardToastMessage;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.reward.Reward;
import dev.ftb.mods.ftbquests.quest.reward.RewardType;
import io.github.drag0n1zed.dpoints.api.PointsApi;
import io.github.drag0n1zed.dpoints.internal.Points;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

/**
 * Quest reward that grants a defined number of dPoints for a specific currency, persists NBT data, and notifies player.
 */
public class PointReward extends Reward {

    public static RewardType TYPE;
    public long value = 1L;
    private String currency = "dPoints";

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
        nbt.putString("currency", currency);
    }

    @Override
    public void readData(CompoundTag nbt) {
        super.readData(nbt);
        value = nbt.getLong("value");
        if (nbt.contains("currency")) {
            currency = nbt.getString("currency");
        }
    }

    @Override
    public void writeNetData(FriendlyByteBuf buf) {
        super.writeNetData(buf);
        buf.writeVarLong(value);
        buf.writeUtf(currency, 100);
    }

    @Override
    public void readNetData(FriendlyByteBuf buf) {
        super.readNetData(buf);
        value = buf.readVarLong();
        currency = buf.readUtf(100);
    }

    @Override
    public void fillConfigGroup(ConfigGroup config) {
        super.fillConfigGroup(config);
        config.addLong("value", value, v -> value = v, 1L, 1L, Long.MAX_VALUE);
        config.addString("currency", currency, v -> currency = v, "dPoints");
    }

    /**
     * Applies reward by adding configured points to the player and showing toast if enabled.
     * @param player Server player to credit.
     * @param notify Whether to display reward toast.
     */
    @Override
    public void claim(ServerPlayer player, boolean notify) {
        PointsApi.addPlayerPoints(player, currency, value);
        if (notify) {
            new DisplayRewardToastMessage(id, getAltTitle(), Icon.getIcon(ResourceLocation.fromNamespaceAndPath(Points.MODID, "textures/point-add.png"))).sendTo(player);
        }
    }

    @Override
    public Component getAltTitle() {
        return Component.literal(String.valueOf(value)).append(" ").append(String.valueOf(currency));
    }

    @Override
    public String getButtonText() {
        return value + " " + currency;
    }
}