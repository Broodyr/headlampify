<img src="src/main/resources/assets/headlampify/icon.png" alt="Headlampify icon" width="96" align="right">

# Headlampify

Headlampify any light block! A new slot by your helmet turns torches, lanterns, glowstone and more into a dynamic headlamp.

A Fabric mod for Minecraft 26.3.

## Features

- A **headlamp slot** beside the player model, level with the helmet slot (in creative, it's in the inventory tab opposite the offhand slot).
- **Any light-emitting block works**, and shines at the light level that block gives off: torch 14, lantern/glowstone/sea lantern/jack o'lantern 15, soul torch 10, redstone torch 7. The Light block uses whatever level is set on the item.
- **Smooth dynamic lighting** that follows your head and fades with distance, lighting terrain, mobs, items and particles.
- **Compatible with Sodium and Iris**, including shaders.
- Shift-click moves light blocks into an empty headlamp slot; the headlamp is saved with your player and drops on death unless `keepInventory` is on.

## Installation

Requires Fabric Loader and [Fabric API](https://modrinth.com/mod/fabric-api).

- **Singleplayer:** install on your client.
- **Servers:** install on the server and on each client that wants to use or see headlamps. Vanilla clients can still join a Headlampify server; they just won't see the slot or any headlamp light.

## Building

Requires JDK 25.

```sh
./gradlew build
```

The mod jar is written to `build/libs/`.

## License

[MIT](LICENSE)
