# Not Enough Music server-owner loot configuration

This is a quick-start guide for configuring disc acquisition on a Minecraft
server. It does not require Java, IntelliJ, or access to the mod source code.

The mod's disc set is fixed. This configuration changes only how existing discs
can be obtained through loot tables.

## Quick start

1. Create a datapack in the world folder.
2. Create `pack.mcmeta`.
3. Create `data/notenoughmusic/disc_config.json`.
4. Choose a disc identifier from the
   [track and configuration-name table](https://github.com/Sachet2Plastik/NotEnoughMusic/blob/main/TRACKS.md).
5. Add one or more loot rules.
6. Enable the datapack and run `/reload`.

The shortest useful configuration is:

```json
{
  "discs": {
    "frosted": [
      {
        "target": "entities/creeper",
        "rolls": 1,
        "conditions": [
          { "type": "killed_by_player" }
        ],
        "count": { "min": 1, "max": 1 }
      }
    ]
  }
}
```

This makes the `frosted` disc drop from a creeper killed by a player, with a
guaranteed count of one.

## Requirements

- Not Enough Music NeoForge installed on the server.
- Permission to edit the server files.
- Operator permission in-game to run `/reload`.

## Create the datapack

Stop the server before creating the datapack, then open the world folder:

```text
<server-folder>/<world-name>/
```

Create this structure:

```text
datapacks/
└── notenoughmusic/
    ├── pack.mcmeta
    └── data/
        └── notenoughmusic/
            └── disc_config.json
```

For Minecraft 1.21.1, create `pack.mcmeta` with:

```json
{
  "pack": {
    "pack_format": 48,
    "description": "Not Enough Music loot configuration"
  }
}
```

## Configure loot rules

Create `data/notenoughmusic/disc_config.json`.

The top-level `discs` object maps a disc's **configuration name** to an array of
loot rules. Use the exact identifier from the
[track list](https://github.com/Sachet2Plastik/NotEnoughMusic/blob/main/TRACKS.md),
not the display name shown in-game.

```json
{
  "discs": {
    "frosted": [
      {
        "target": "entities/creeper",
        "rolls": 1,
        "conditions": [
          {
            "type": "killed_by_player"
          }
        ],
        "count": {
          "min": 1,
          "max": 1
        }
      }
    ]
  }
}
```

The `discs` keys must be existing configuration names, such as:

- `undead`
- `frosted`
- `nightfall`
- `dragonfight`

For the complete list of discs and their exact configuration names, see the
[track list](https://github.com/Sachet2Plastik/NotEnoughMusic/blob/main/TRACKS.md).

The configuration uses **full replacement** behavior. When the file is valid,
only the discs listed in the file have configured acquisition rules. Discs
omitted from the file will not be added to loot tables.

The disc item, music, texture, name, and other metadata are not changed.

## Apply changes

Start the server and run:

```text
/datapack list
/reload
```

Run `/reload` after every edit to `disc_config.json`. Players do not normally
need to reconnect.

## Available conditions

The supported condition types are listed below. For the complete field
reference and JSON examples, see the Fabric repository's
[loot-table definition reference](https://github.com/Sachet2Plastik/NotEnoughMusic/blob/main/LOOT_TABLES.md).

- `random_chance`
- `random_chance_enchantments`
- `killed_by_player`
- `killed_with_item`
- `killed_with_item_tag`
- `killed_by`
- `biomes`
- `sky`
- `light`
- `survive_explosion`

Conditions in the same rule must all pass.

Each rule has this structure:

| Field | Meaning |
|---|---|
| `target` | Vanilla loot-table path, without the `minecraft:` prefix. |
| `rolls` | Number of rolls for the added loot pool. |
| `conditions` | Conditions that must all pass. |
| `count.min` | Minimum number of discs produced by one successful roll. |
| `count.max` | Maximum number of discs produced by one successful roll. |

For example, use `entities/creeper`, not `minecraft:entities/creeper`.

Examples:

```json
{ "type": "random_chance", "chance": 0.25 }
```

```json
{ "type": "killed_with_item", "item": "minecraft:diamond_sword" }
```

```json
{ "type": "killed_with_item_tag", "tag": "minecraft:swords" }
```

```json
{
  "type": "biomes",
  "biomes": ["minecraft:plains", "minecraft:forest"]
}
```

```json
{ "type": "sky", "canSee": true }
```

```json
{ "type": "light", "exactly": 15 }
```

```json
{ "type": "light", "min": 0, "max": 7 }
```

```json
{
  "type": "killed_by",
  "killer": "minecraft:zombie",
  "direct": false
}
```

For `random_chance`, use a value between `0.0` and `1.0`. A value of `1.0`
always passes and `0.0` always fails.

## Multiple rules

A disc may have more than one rule. Each rule creates a separate loot pool:

```json
{
  "discs": {
    "frosted": [
      {
        "target": "entities/creeper",
        "rolls": 1,
        "conditions": [
          { "type": "killed_by_player" }
        ],
        "count": { "min": 1, "max": 1 }
      },
      {
        "target": "blocks/gravel",
        "rolls": 1,
        "conditions": [
          { "type": "survive_explosion" }
        ],
        "count": { "min": 1, "max": 1 }
      }
    ]
  }
}
```

Each rule creates a separate loot pool.

You can also configure multiple discs in the same file. Every disc must be
listed under the same top-level `discs` object.

## Restore the mod defaults

To stop overriding the mod's rules:

1. Remove `disc_config.json`, or disable/remove the datapack.
2. Run:

   ```text
   /reload
   ```

When no override is present, the mod's bundled acquisition rules are used.

## Troubleshooting

### The datapack is not listed

- Check that `pack.mcmeta` is directly inside the datapack folder.
- Check that the datapack is inside the correct world folder.
- Run `/datapack list`.
- Check that the server is running Minecraft 1.21.1.

### The override does not apply

- Check the path is exactly:

  ```text
  data/notenoughmusic/disc_config.json
  ```

- Check the file is valid JSON.
- Check every disc name exists.
- Run `/reload`.
- Check the server log for an error mentioning `disc_config`.

An invalid override is rejected as a whole. It is never partially applied.
