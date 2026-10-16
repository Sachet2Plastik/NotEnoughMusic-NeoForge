# Not Enough Music NeoForge Port

**Not Enough Music** (NEM) is a mod that adds a collection of Minecraft fan-made music found on YouTube.
The goal of this project is to gather these tracks into a single mod while giving proper credit to every artist.
More than just adding the tracks themselve, this mod also add more different way to obtain them, diversifying the collecting process.

As a Minecraft discs collector myself I felt like collecting only the vanilla ones started to get repetitive,
that is why I made this mod in the first place.

Have fun and go collect the maximum of discs !

---

## License

The **source code** of this project is licensed under the **MIT License**.

> [!IMPORTANT]
> The MIT License applies <u>**ONLY to the source code**</u> in this repository. It does **not** apply to the music assets.

> [!NOTE]
> The music included in this mod remains the property of its respective creators.
>
> Each track is used in accordance with the usage permissions provided by its original creator. Please refer to the original source or creator's page for the specific terms under which each track may be used.

> [!WARNING]
> Do **not** assume that the music assets are free to redistribute, modify, or reuse outside of this project simply because the source code is MIT licensed.

THIS IS A PORT OF THE FABRIC VERSION [HERE](http://github.com/Sachet2Plastik/NotEnoughMusic)

This version is not planned to be maintained nor publish as for now, but is currently the main focus as i'm actively receiving feedback on this version that is currently running on a server

for more info on the mod itself, please refer to the fabric version linked above

## Resource generator

`AutoGen.py` generates the jukebox-song data, item models, sound definitions, and
English item names from `src/main/resources/data/notenoughmusic/definitions.json`.

Run it from the repository root:

```text
python AutoGen.py
```

Validate definitions and generated names without changing files:

```text
python AutoGen.py --check
```

The generator uses the same resource layout and naming conventions as the Fabric
version. It remains a Python script because it also organizes resources that
reference external audio files; NeoForge Java datagen is not required for this
workflow.

## Server loot-rule datapack

For a step-by-step guide intended for server owners, see
[SERVER_OWNER_LOOT_TESTING.md](SERVER_OWNER_LOOT_TESTING.md). The complete
track/configuration-name list and condition field reference are maintained in
the [Fabric track list](https://github.com/Sachet2Plastik/NotEnoughMusic/blob/main/TRACKS.md)
and [Fabric loot-table reference](https://github.com/Sachet2Plastik/NotEnoughMusic/blob/main/LOOT_TABLES.md).

The disc set itself is fixed by the mod resources. Disc items, songs, sounds,
metadata, models, and textures cannot be changed by this feature. Only the loot
rules used to obtain those existing discs can be overridden.

Create this file inside a server datapack:

```text
data/notenoughmusic/disc_config.json
```

The override uses full-replacement semantics. When the file is valid, only the
discs listed in it have acquisition rules; omitted discs have no configured loot
rules. If the file is absent, the bundled rules from `definitions.json` remain
active.

Example:

```json
{
  "discs": {
    "undead": [
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

The supported condition types are `random_chance`,
`random_chance_enchantments`, `killed_by_player`, `killed_with_item`,
`killed_with_item_tag`, `killed_by`, `biomes`, `sky`, `light`, and
`survive_explosion`. The condition fields follow the corresponding entries in
the bundled `definitions.json`.

Run `/reload` after adding or editing the datapack. A malformed override is
rejected as a whole and the previous valid rules remain active; it is never
partially applied.
