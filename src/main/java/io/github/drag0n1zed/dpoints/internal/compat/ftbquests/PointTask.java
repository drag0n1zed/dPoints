package io.github.drag0n1zed.dpoints.internal.compat.ftbquests;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.icon.Color4I;
import dev.ftb.mods.ftblibrary.icon.CombinedIcon;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.TeamData;
import dev.ftb.mods.ftbquests.quest.task.Task;
import dev.ftb.mods.ftbquests.quest.task.TaskType;
import dev.ftb.mods.ftblibrary.util.TooltipList;
import io.github.drag0n1zed.dpoints.api.PointsApi;
import io.github.drag0n1zed.dpoints.internal.Points;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

/**
 * Quest task that requires spending a specified number of dPoints for a specific currency to complete.
 */
public class PointTask extends Task{
    public static TaskType TYPE;
    private long value = 0L;
    private String currency = "dPoints";

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
        nbt.putString("currency", currency);
    }

    @Override
    public void readData(CompoundTag nbt) {
        super.readData(nbt);
        if (nbt.contains("value")) {
            value = nbt.getLong("value");
        } else {
            value = 0L;
        }
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
    public boolean consumesResources() {
        return true;
    }

    @Override
    public void fillConfigGroup(ConfigGroup config) {
        super.fillConfigGroup(config);
        config.addLong("value", value, v -> value = v, 0L, 0L, Long.MAX_VALUE);
        config.addString("currency", currency, v -> currency = v, "dPoints");
    }

    @Override
    public Component getAltTitle() {
        return Component.literal(String.valueOf(value)).append(" ").append(String.valueOf(currency));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void addMouseOverText(TooltipList list, TeamData teamData) {
        super.addMouseOverText(list, teamData);
        // Display player's current cached point balance for this currency
        String balance = String.valueOf(PointsApi.getPlayerPoints(null, currency));
        Component balanceText = Component.translatable("dpoints.balance", currency)
                .append(": ")
                .append(Component.literal(balance).withStyle(ChatFormatting.GRAY));
        list.add(balanceText);
    }

    @Override
    public void submitTask(TeamData teamData, ServerPlayer player, ItemStack craftedItem) {
        if (teamData.isCompleted(this)) {
            return;
        }

        long playerPoints = PointsApi.getPlayerPoints(player, currency);
        long progress = teamData.getProgress(this);
        long needed = value - progress;

        if (needed <= 0) {
            return;
        }

        if (playerPoints >= needed) {
            int amountToTake = (int) needed;

            PointsApi.removePlayerPoints(player, currency, amountToTake);
            teamData.addProgress(this, amountToTake);
        }
    }


    private static final int QUEST_ICON_HUE_SALT = 350234;
    private static final float GOLDEN_RATIO_CONJUGATE = 0.61803398875F;
    private static final Icon BASE_POINTS_ICON = Icon.getIcon(
            ResourceLocation.fromNamespaceAndPath(Points.MODID, "textures/item/points.png"));
    private static final Icon OVERLAY_PLUS_ICON = Icon.getIcon(
            ResourceLocation.fromNamespaceAndPath(Points.MODID, "textures/minus.png"));

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