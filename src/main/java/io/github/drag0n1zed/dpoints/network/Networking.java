package io.github.drag0n1zed.dpoints.network;

import io.github.drag0n1zed.dpoints.internal.Points;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

/**
 * Handles network channel setup and message dispatch for point synchronization.
 */
public class Networking {

    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            ResourceLocation.fromNamespaceAndPath(Points.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

        /**
         * Registers network message types for point synchronization.
         */
        public static void registerMessages() {
        int id = 0;
        INSTANCE.registerMessage(id++,
                SyncPointsPacket.class,
                SyncPointsPacket::encode,
                SyncPointsPacket::decode,
                SyncPointsPacket::handle
        );
    }

        /**
         * Sends a packet to a specific player.
         * @param packet Packet instance to send.
         * @param player Destination server player.
         */
        public static void sendToClient(Object packet, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), packet);
    }
}