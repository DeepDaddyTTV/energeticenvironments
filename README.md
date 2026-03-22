# Energetic Environments

Energetic Environments is a NeoForge technology mod for modern Minecraft focused on large-scale, multiblock-driven infrastructure. The mod is built around turning the world itself into an industrial input: wind, heat, fluids, terrain, weather, and biome conditions become part of your power and resource network instead of background scenery.

This repository is the initial foundation for the project and targets the latest stable NeoForge `1.21.x` baseline available at update time:

- Minecraft `1.21.10`
- NeoForge `21.10.64`
- Java `21`
- License: MIT

`1.21.11` was available only on a beta NeoForge branch when this was updated, so the project is intentionally pinned to the newest non-beta `1.21.x` release line instead.

## Design Direction

The long-term goal is a tech mod with heavy emphasis on:

- Multiblock machines that feel structural, not decorative
- Sustainable high-output energy systems sourced from the environment
- Powerful downstream processing chains that reward planning and scale
- Broad ecosystem compatibility on the modern NeoForge toolchain
- A grounded industrial fantasy centered on infrastructure, efficiency, and world interaction

Planned examples include environmental harvesters, large rotating or fluid-driven power systems, industrial refining networks, and massive machine assemblies that convert natural conditions into reliable energy and materials.

## Project Structure

This starter commit includes:

- A clean NeoForge ModDevGradle setup for the current `1.21.x` line
- GitHub Actions build automation
- Initial mod metadata for Energetic Environments
- A minimal Java entrypoint ready for block, item, menu, recipe, and datagen expansion
- MIT licensing under the `DeepDaddyTTV` name

## Development

Requirements:

- JDK `21`
- A Gradle-capable shell environment

Common tasks:

```bash
./gradlew runClient
./gradlew runServer
./gradlew build
```

## Compatibility Approach

The project is being set up for compatibility-first development on current NeoForge rather than invasive loader hacks. Early architecture decisions will favor:

- Standard NeoForge registration and data-driven content
- Clean separation between power systems, machines, and world interaction logic
- Future-friendly integration points for recipes, automation, and addon support
- Conservative baseline choices that make collaboration and maintenance easier

## Publishing and Contribution

Source: [github.com/DeepDaddyTTV/energeticenvironments](https://github.com/DeepDaddyTTV/energeticenvironments)
Issues: [github.com/DeepDaddyTTV/energeticenvironments/issues](https://github.com/DeepDaddyTTV/energeticenvironments/issues)

All project branding in this repository is currently attributed to `DeepDaddyTTV`.
