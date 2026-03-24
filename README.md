# Energetic Environments

Energetic Environments is a NeoForge technology mod for modern Minecraft focused on large-scale, multiblock-driven infrastructure. The mod is built around turning the world itself into an industrial input: wind, heat, fluids, terrain, weather, and biome conditions become part of your power and resource network instead of background scenery.

This repository is the initial foundation for the project and now targets a compatibility-first `1.21.x` strategy with primary support on the stable `1.21.1` NeoForge line:

- Minecraft `1.21.1`
- NeoForge `21.1.219`
- Java `21`
- License: MIT

The codebase is being kept conservative so it is easier to validate on other `1.21.x` environments later, but `1.21.1` is the primary supported baseline and the version we will target first when testing and debugging.

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

- A clean NeoForge ModDevGradle setup centered on `1.21.1` with a broad-compatibility posture for `1.21.x`
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

The project is being set up for compatibility-first development on NeoForge rather than invasive loader hacks. Early architecture decisions will favor:

- Standard NeoForge registration and data-driven content
- Clean separation between power systems, machines, and world interaction logic
- Future-friendly integration points for recipes, automation, and addon support
- Conservative baseline choices that make collaboration and maintenance easier

In practice, that means:

- Primary support and first-line testing on Minecraft `1.21.1`
- Avoiding unnecessary dependencies on patch-specific APIs where possible
- Expanding validated support across `1.21.x` only after explicit test coverage on those lines

## Publishing and Contribution

Source: [github.com/DeepDaddyTTV/energeticenvironments](https://github.com/DeepDaddyTTV/energeticenvironments)
Issues: [github.com/DeepDaddyTTV/energeticenvironments/issues](https://github.com/DeepDaddyTTV/energeticenvironments/issues)

All project branding in this repository is currently attributed to `DeepDaddyTTV`.
