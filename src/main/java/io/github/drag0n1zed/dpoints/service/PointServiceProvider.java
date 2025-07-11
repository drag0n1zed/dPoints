package io.github.drag0n1zed.dpoints.service;

import org.jetbrains.annotations.ApiStatus;

/**
 * Maintains server and client IPointManager instances and supplies the correct one for each side.
 */
public final class PointServiceProvider {

    private static IPointManager serverPointManager;
    private static IPointManager clientPointManager;

    private PointServiceProvider() {}

    /**
     * Returns server or client IPointManager based on execution side.
     * @param isClientSide True for client side, false for server side.
     * @return Active IPointManager instance.
     */
    public static IPointManager get(boolean isClientSide) {
        return isClientSide ? clientPointManager : serverPointManager;
    }

    /**
     * Registers the server-side point manager; prevents multiple initializations.
     * @param manager Server IPointManager implementation.
     */
    @ApiStatus.Internal
    public static void setServer(IPointManager manager) {
        if (serverPointManager != null) {
            throw new IllegalStateException("Server point manager has already been set.");
        }
        serverPointManager = manager;
    }

    /**
     * Registers the client-side point manager; prevents multiple initializations.
     * @param manager Client IPointManager implementation.
     */
    @ApiStatus.Internal
    public static void setClient(IPointManager manager) {
        if (clientPointManager != null) {
            throw new IllegalStateException("Client point manager has already been set.");
        }
        clientPointManager = manager;
    }
}