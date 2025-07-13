package io.github.drag0n1zed.dpoints.network;

import io.github.drag0n1zed.dpoints.service.IPointManager;
import io.github.drag0n1zed.dpoints.service.PointServiceProvider;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Packet for transmitting updated player point balance for a specific currency from server to client.
 * Ensures client-side cache reflects server state for each currency.
 */
public class SyncPointsPacket {
    private final String currency;
    private final long points;

    public SyncPointsPacket(String currency, long points) {
        this.currency = currency;
        this.points = points;
    }

        /**
         * Serializes packet data into the buffer.
         * @param message Packet instance to serialize.
         * @param buffer ByteBuf to write to.
         */
    public static void encode(SyncPointsPacket message, FriendlyByteBuf buffer) {
        buffer.writeUtf(message.currency);
        buffer.writeLong(message.points);
    }

        /**
         * Deserializes packet data from the buffer.
         * @param buffer ByteBuf to read from.
         * @return New SyncPointsPacket instance.
         */
    public static SyncPointsPacket decode(FriendlyByteBuf buffer) {
        return new SyncPointsPacket(buffer.readUtf(), buffer.readLong());
    }

        /**
         * Handles incoming packet by updating client-side cache on the main thread.
         * @param message Packet containing updated points for a currency.
         * @param context Supplier of network event context.
         */
    public static void handle(SyncPointsPacket message, Supplier<NetworkEvent.Context> context) {
        NetworkEvent.Context ctx = context.get();
        // Ensure this logic runs on the main game thread
        ctx.enqueueWork(() -> {
            // We are on the client side, so we update the client-side cache.
            if (ctx.getDirection().getReceptionSide() == LogicalSide.CLIENT) {
                IPointManager pointManager = PointServiceProvider.get(true);
                pointManager.setPoints(null, message.currency, message.points);
            }
        });
        // Mark the message as handled
        ctx.setPacketHandled(true);
    }
}