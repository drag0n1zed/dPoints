package io.github.drag0n1zed.dpoints.internal;

import io.github.drag0n1zed.dpoints.internal.commands.ModCommands;
import io.github.drag0n1zed.dpoints.internal.compat.ftbquests.FTBQuestsIntegration;
import io.github.drag0n1zed.dpoints.internal.items.ModItems;
import io.github.drag0n1zed.dpoints.manager.ClientPointManager;
import io.github.drag0n1zed.dpoints.manager.ServerPointManager;
import io.github.drag0n1zed.dpoints.network.Networking;
import io.github.drag0n1zed.dpoints.service.PointServiceProvider;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Mod entry point: registers networking, FTBQuests integration, and lifecycle event handlers.
 */
@Mod(Points.MODID)
public class Points {
    public static final String MODID = "dpoints";

    public static final Logger LOGGER = LogManager.getLogger();
    private static ServerPointManager serverPointManager;

    /**
     * Initializes mod: sets up integrations and registers event listeners.
     */
    public Points(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();
        modEventBus.addListener(this::onClientSetup);

        ModItems.ITEMS.register(modEventBus);

        FTBQuestsIntegration.init();
        Networking.registerMessages();

        MinecraftForge.EVENT_BUS.register(this);
    }

    /**
     * Configures client-side point manager after client setup event.
     *
     * @param event Client setup event context.
     */
    private void onClientSetup(FMLClientSetupEvent event) {
        PointServiceProvider.setClient(new ClientPointManager());
    }

    /**
     * Initializes server-side point manager on server start.
     * @param event Server starting event context.
     */
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        serverPointManager = new ServerPointManager();
        PointServiceProvider.setServer(serverPointManager);
    }

    /**
     * Registers in-game commands for managing player points.
     * @param event Command registration event.
     */
    @SubscribeEvent
    public void onCommandsRegister(RegisterCommandsEvent event) {
        ModCommands.register(event.getDispatcher());
    }


    /**
     * Loads player point data and syncs to client on login.
     * @param event Player login event context.
     */
    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        serverPointManager.onPlayerJoin(event.getEntity());
    }

    /**
     * Clears cached point data when a player logs out.
     * @param event Player logout event context.
     */
    @SubscribeEvent
    public void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        if (serverPointManager != null) {
            serverPointManager.onPlayerLeave(event.getEntity());
        }
    }

    /**
     * Cleans up server point manager reference on server stop.
     * @param event Server stopping event context.
     */
    @SubscribeEvent
    public void onServerStopping(ServerStoppingEvent event) {
        serverPointManager = null;
    }
}