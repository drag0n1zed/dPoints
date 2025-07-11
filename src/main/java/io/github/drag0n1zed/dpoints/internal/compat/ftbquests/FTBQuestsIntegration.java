package io.github.drag0n1zed.dpoints.internal.compat.ftbquests;

import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftbquests.quest.reward.RewardTypes;
import dev.ftb.mods.ftbquests.quest.task.TaskTypes;
import io.github.drag0n1zed.dpoints.internal.Points;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.ModList;


/**
 * Registers point task and reward types with FTBQuests when the mod is loaded.
 */
public class FTBQuestsIntegration {

        /**
         * Initializes FTBQuests integration by registering point task and reward types with icons.
         */
        public static void init() {
        if (ModList.get().isLoaded("ftbquests")) {
            PointTask.TYPE = TaskTypes.register(
                    ResourceLocation.parse(Points.MODID + ":points"),
                    PointTask::new,
                    () -> Icon.getIcon(ResourceLocation.fromNamespaceAndPath(Points.MODID, "textures/point-remove.png"))
            );
            PointReward.TYPE = RewardTypes.register(
                    ResourceLocation.parse(Points.MODID + ":points"),
                    PointReward::new,
                    () -> Icon.getIcon(ResourceLocation.fromNamespaceAndPath(Points.MODID, "textures/point-add.png"))
            );
        }
    }
}