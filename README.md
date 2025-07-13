# dPoints

dPoints is a Minecraft Forge mod for tracking and managing player point balances in a server-authoritative manner, with client-side caching and optional integration with FTBQuests.

## Features

- Support for multiple currencies
- Commands: `/dpoints add`, `/dpoints remove`, `/dpoints set`, `/dpoints get`
- API for modders: `PointsApi`
- FTBQuests integration: `PointTask` and `PointReward`
- Ideal for modpack developers to implement shop unlock systems, custom economies, and similar features

## Usage

- Use the in-game commands to manage points.
- Modders can call `PointsApi` methods in code.

## Configuration

No additional configuration required.

## License

This project is licensed under the GNU Lesser General Public License v3.0.