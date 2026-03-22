# Energetic Environments

Energetic Environments is a NeoForge technology mod for modern Minecraft focused on large-scale, multiblock-driven infrastructure. The mod is built around turning the world itself into an industrial input: wind, heat, fluids, terrain, weather, and biome conditions become part of your power and resource network instead of background scenery.

This repository is the initial foundation for the project and targets the latest official `1.21.x` NeoForge MDK baseline available at scaffold time:

- Minecraft `1.21.11`
- NeoForge `21.11.38-beta`
- Java `21`
- License: MIT

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

Source: [github.com/MMFQDEATH/energeticenvironments](https://github.com/MMFQDEATH/energeticenvironments)
Issues: [github.com/MMFQDEATH/energeticenvironments/issues](https://github.com/MMFQDEATH/energeticenvironments/issues)

All project branding in this repository is currently attributed to `DeepDaddyTTV`.
