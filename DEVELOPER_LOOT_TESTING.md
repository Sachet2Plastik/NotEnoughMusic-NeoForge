# Developer loot-rule testing

This guide is for testing the datapack override from the IntelliJ NeoForge
development client. For normal server administration, use
[SERVER_OWNER_LOOT_TESTING.md](SERVER_OWNER_LOOT_TESTING.md).

## Development datapack location

For the Gradle `runClient` configuration, the integrated server world is stored
under:

```text
NotEnoughMusic-NeoForge/run/client/saves/<world-name>/
```

Create the datapack at:

```text
run/client/saves/<world-name>/datapacks/notenoughmusic/
```

The required layout is:

```text
notenoughmusic/
├── pack.mcmeta
└── data/
    └── notenoughmusic/
        └── disc_config.json
```

Use `pack_format` 48 for Minecraft 1.21.1:

```json
{
  "pack": {
    "pack_format": 48,
    "description": "Not Enough Music developer loot test"
  }
}
```

## Deterministic smoke test

Use this configuration first:

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

In-game:

```text
/datapack list
/reload
/summon minecraft:creeper ~ ~ ~ (or just use a spawn egg bru)
```

Kill the creeper as the player. Exactly one `frosted` disc should be produced.

## Condition test matrix

Test one rule at a time and use `chance: 1.0` or fixed counts where possible.
After each edit, save the JSON and run `/reload`.

| Condition | Example fields | Suggested test |
|---|---|---|
| `random_chance` | `chance: 1.0` | Confirm it always passes; repeat with `0.0`. |
| `random_chance_enchantments` | `enchant: "minecraft:sharpness"` | Test an attacking item with and without Sharpness. |
| `killed_by_player` | none | Compare a player kill with a mob-caused kill. |
| `killed_with_item` | `item: "minecraft:diamond_sword"` | Kill with a diamond sword, then another item. |
| `killed_with_item_tag` | `tag: "minecraft:swords"` | Compare a sword with a non-sword. |
| `killed_by` | `killer: "minecraft:zombie"` | Compare a zombie attacker with another attacker. |
| `biomes` | `biomes: ["minecraft:plains"]` | Test in plains and another biome. |
| `sky` | `canSee: true` | Test outdoors and underground. |
| `light` | `exactly`, or `min`/`max` | Test at known light levels. |
| `survive_explosion` | none | Use a block target such as `blocks/gravel`. |

Example condition snippets:

```json
{ "type": "random_chance", "chance": 1.0 }
```

```json
{
  "type": "random_chance_enchantments",
  "enchant": "minecraft:sharpness",
  "base": 1.0,
  "perLevel": 0.0
}
```

```json
{ "type": "killed_with_item", "item": "minecraft:diamond_sword" }
```

```json
{ "type": "killed_with_item_tag", "tag": "minecraft:swords" }
```

```json
{
  "type": "killed_by",
  "killer": "minecraft:zombie",
  "direct": false
}
```

```json
{
  "type": "biomes",
  "biomes": ["minecraft:plains"]
}
```

```json
{ "type": "sky", "canSee": true }
```

```json
{ "type": "light", "exactly": 15 }
```

```json
{
  "target": "blocks/gravel",
  "rolls": 1,
  "conditions": [
    { "type": "survive_explosion" }
  ],
  "count": { "min": 1, "max": 1 }
}
```

## Reload and validation tests

### Valid reload

Edit a rule, save it, run `/reload`, and verify that the server log reports the
number of loaded discs.

### Full replacement

Put only one disc in the `discs` object. Verify that the listed disc has the
override and omitted discs no longer have configured acquisition rules.

### Unknown disc

Test this invalid file:

```json
{
  "discs": {
    "not_a_loaded_disc": []
  }
}
```

Run `/reload`. The complete override must be rejected and the previous valid
snapshot must remain active.

### Restore defaults

Remove the datapack or remove `disc_config.json`, then run `/reload`. The
bundled rules from `src/main/resources/data/notenoughmusic/definitions.json`
should become active.

## Required regression checks

After changing reload or loot code, run:

```text
gradlew.bat compileJava --no-daemon --no-configuration-cache
gradlew.bat runClient --no-daemon --no-configuration-cache --dry-run
gradlew.bat runServer --no-daemon --no-configuration-cache --dry-run
python AutoGen.py --check
```

Also create/open a new integrated-server world before testing loot. A datapack
reload implementation must not change item registration, sound registration,
jukebox data, or world creation behavior.
