# Only Zombies Spawn

**Only Zombies Spawn** is a lightweight mod that restricts hostile mob spawns exclusively to **zombies**.
When a non-zombie monster attempts to spawn, it is automatically replaced with one or more zombies.

## Features
- **Spawn Replacement**: Replaces all hostile mobs (monsters) with zombies.
- **Wide Coverage**: Works for natural spawns, mob spawners, chunk generation, structures, and patrols.
- **Boss Exclusion**: Ender Dragons and Withers are excluded from replacement and will spawn normally.
- **Spawn Multiplier**: Configure between 1 to 40 zombies to spawn in place of a single replaced mob.
- **Persistence Inheritance**: If the original mob was set to not despawn (e.g., via a Name Tag), the replacement zombies will also inherit this persistence.

## Configuration
You can open the configuration GUI in-game using the `/ozsconfig` command to adjust the following settings:

- **Allowed Mobs (`allowedMobs`)**: Specify a list of mobs that should be allowed to spawn normally without being replaced.
- **Disabled Dimensions (`disabledDimensions`)**: Specify dimensions (e.g., The Nether, The End) where the mod's effects should be disabled.
- **Zombie Spawn Multiplier (`zombieSpawnMultiplier`)**: Set the number of zombies that appear per replacement (Default: 1).

*The configuration file is saved at `config/onlyzombiesspawn.properties`.*

## Supported Versions
- **Forge**: 1.18.2, 1.19.2, 1.20.1
- **NeoForge**: 1.21.1, 26.1, 26.1.2
- **Fabric**: 1.20.1, 1.21.1, 26.1, 26.1.2

---
*Perfect for zombie-themed worlds, custom modpacks, and survival challenges.*
