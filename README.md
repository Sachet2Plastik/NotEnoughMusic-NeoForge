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
