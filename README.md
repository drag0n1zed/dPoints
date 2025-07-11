# dPoints

dPoints is a Minecraft Forge mod for tracking and managing player point balances in a server-authoritative manner, with client-side caching and optional integration with FTBQuests.

## Features

- Server-authoritative point storage and synchronization
- Client-side cache for displaying point balances
- Commands: `/dpoints add`, `/dpoints remove`, `/dpoints set`, `/dpoints get`
- API for modders: `PointsApi`
- FTBQuests integration: `PointTask` and `PointReward`
- Ideal for modpack developers to implement shop unlock systems, custom economies, and similar features

## Usage

- Use the in-game commands to manage points.
- Modders can call `PointsApi` methods in code.

## Configuration

No additional configuration required. Localization files available in `src/main/resources/assets/dpoints/lang`.

## License

This project is licensed under the GNU Lesser General Public License v3.0. See `LICENSE` for details.