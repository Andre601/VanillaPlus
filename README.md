# VanillaPlus
This repository houses the source of the custom VanillaPlus Plugin and the [VanillaPlus Site and Wiki](https://docs.andre601.ch/VanillaPlus).

## Plugin
The VanillaPlus plugin is a Paper plugin designed for the VanillaPlus Server, providing custom functionality otherwise not avaiable via other plugins or methods.

It currently provides:

- A land-claiming system, allowing a player to claim a 3x3 Chunk Area, using WorldGuard as a method to check for intesecting regions and for general region protection/configuration.
- Custom Items and Furniture functionality:
    - Teleport Crystal's storing of a location and sub-sequent teleportation to it.
    - Scythe's 3x3 harvesting functionality.
    - Trashcan's Inventory and Item desposing functionality.
- Item/Block translations through PlaceholderAPI placeholders to bypass some current limitations in DeluxeMenus regarding translatables in lore.

## Website/Wiki
The Source of the Website and Wiki are found in the [`docs/`](docs) directory.  
The site is made using [MkDocs](https://mkdocs.org) with the [Material for MkDocs theme](https://squidfunk.github.io/mkdocs-material) and published to GitHub Pages using a GitHub Actions Workflow.

## License

The source of the plugin is licensed under MIT. See the [LICENSE](LICENSE) file for details.  
The Website and Wiki are licensed under the [Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International](https://creativecommons.org/licenses/by-nc-sa/4.0/) license.
