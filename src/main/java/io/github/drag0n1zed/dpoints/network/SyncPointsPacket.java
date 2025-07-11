package io.github.drag0n1zed.dpoints.network;

import io.github.drag0n1zed.dpoints.service.IPointManager;
import io.github.drag0n1zed.dpoints.service.PointServiceProvider;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Packet for transmitting updated player point balance from server to client.
 * Ensures client-side cache reflects server state.
 */
public class SyncPointsPacket {
    private final long points;

    public SyncPointsPacket(long points) {
        this.points = points;
    }

        /**
         * Serializes packet data into the buffer.
         * @param message Packet instance to serialize.
         * @param buffer ByteBuf to write to.
         */
    public static void encode(SyncPointsPacket message, FriendlyByteBuf buffer) {
        buffer.writeLong(message.points);
    }

        /**
         * Deserializes packet data from the buffer.
         * @param buffer ByteBuf to read from.
         * @return New SyncPointsPacket instance.
         */
    public static SyncPointsPacket decode(FriendlyByteBuf buffer) {
        return new SyncPointsPacket(buffer.readLong());
    }

        /**
         * Handles incoming packet by updating client-side cache on the main thread.
         * @param message Packet containing updated points.
         * @param context Supplier of network event context.
         */
    public static void handle(SyncPointsPacket message, Supplier<NetworkEvent.Context> context) {
        NetworkEvent.Context ctx = context.get();
        // Ensure this logic runs on the main game thread
        ctx.enqueueWork(() -> {
            // We are on the client side, so we update the client-side cache.
            if (ctx.getDirection().getReceptionSide() == LogicalSide.CLIENT) {
                IPointManager pointManager = PointServiceProvider.get(true);
                pointManager.setPoints(null, message.points);
            }
        });
        // Mark the message as handled
        ctx.setPacketHandled(true);
    }
}