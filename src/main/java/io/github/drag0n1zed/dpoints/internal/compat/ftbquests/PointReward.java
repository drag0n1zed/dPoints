package io.github.drag0n1zed.dpoints.internal.compat.ftbquests;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.icon.Color4I;
import dev.ftb.mods.ftblibrary.icon.CombinedIcon;
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
import net.minecraft.util.Mth;

import java.util.List;

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
        return Component.literal(String.valueOf(currency));
    }

    @Override
    public String getButtonText() {
        return String.valueOf(value);
    }

    private static final int QUEST_ICON_HUE_SALT = 350234;
    private static final float GOLDEN_RATIO_CONJUGATE = 0.61803398875F;
    private static final Icon BASE_POINTS_ICON = Icon.getIcon(
            ResourceLocation.fromNamespaceAndPath(Points.MODID, "textures/item/points.png"));
    private static final Icon OVERLAY_PLUS_ICON = Icon.getIcon(
            ResourceLocation.fromNamespaceAndPath(Points.MODID, "textures/plus.png"));

    @Override
    public Icon getAltIcon() {
        // For the default currency, just use the original icon registered with the type.
        if ("dPoints".equalsIgnoreCase(this.currency)) {
            return getType().getIconSupplier();
        }

        long seededHash = (long)this.currency.hashCode() + QUEST_ICON_HUE_SALT;
        float hue = (Math.abs(seededHash) % 1000) / 1000f;
        hue = (hue + GOLDEN_RATIO_CONJUGATE) % 1.0f;
        float saturation = 0.85f;
        float brightness = 0.95f;
        int rgb = Mth.hsvToRgb(hue, saturation, brightness);
        Color4I tint = Color4I.rgb(rgb);

        // Create the layered icon using CombinedIcon
        return CombinedIcon.getCombined(List.of(
                // Bottom Layer: The points icon, tinted with our generated color.
                BASE_POINTS_ICON.withColor(tint),

                // Top Layer: The plus icon, drawn on top with no tint.
                OVERLAY_PLUS_ICON
        ));
    }
}