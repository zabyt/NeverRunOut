# NeverRunOut

Mark hotbar slots with Caps Lock; when the item runs out it is instantly refilled from your inventory.

A client-side Fabric mod for Minecraft 26.2 made for hotbar items you burn through constantly - food, blocks, arrows, anything.

## Features

- Toggling Caps Lock marks the currently selected hotbar slot
- When the marked item runs out, it is instantly refilled from your inventory
- Toggle Caps Lock on a marked slot again to unmark it
- Marks are stored per slot and survive going back into the menu

## Configuration

Open the config in-game with [ModMenu](https://modrinth.com/mod/modmenu), or through the icon added by [ZabytLib](https://github.com/zabyt/ZabytLib) on the title/pause screen.

## Building

The clone is self-contained: every jar needed for compilation (ZabytLib, ModMenu) is in `libs/`.

```
git clone https://github.com/zabyt/NeverRunOut
cd NeverRunOut
gradlew.bat build
```

The built jar ends up in `build/libs/`.

## Dependencies

- Minecraft `~26.2`, Fabric Loader `>=0.19.3`, Java `>=25`, Fabric API
- [ZabytLib](https://github.com/zabyt/ZabytLib) at runtime
- [ModMenu](https://modrinth.com/mod/modmenu) at runtime

## License

MIT
