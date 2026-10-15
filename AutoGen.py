"""Generate Not Enough Music resources from definitions.json."""

from __future__ import annotations

import argparse
import json
from pathlib import Path
from typing import Any


MOD_ID = "notenoughmusic"
ARTISTS = {
    "tenm": {"name": "tenm", "prefix": "t_en_m/", "raw": "t_en_m"},
    "laudividni": {"name": "laudividni", "prefix": "laudividni/", "raw": "laudividni"},
    "stereonyx": {"name": "stereonyx", "prefix": "stereonyx/", "raw": "stereonyx"},
}


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--root", type=Path, default=Path(__file__).parent)
    parser.add_argument("--definitions", type=Path)
    parser.add_argument(
        "--check",
        action="store_true",
        help="validate definitions and generated names without writing files",
    )
    return parser.parse_args()


def resource_paths(root: Path) -> dict[str, Path]:
    resources = root / "src" / "main" / "resources"
    assets = resources / "assets" / MOD_ID
    data = resources / "data" / MOD_ID
    return {
        "definitions": data / "definitions.json",
        "jukebox": data / "jukebox_song",
        "models": assets / "models" / "item" / "discs",
        "sounds_json": assets / "sounds.json",
        "lang_json": assets / "lang" / "en_us.json",
    }


def require(condition: bool, message: str) -> None:
    if not condition:
        raise ValueError(message)


def validate_definition(data: dict[str, Any], index: int) -> dict[str, str]:
    label = f"definition {index}"
    require(isinstance(data, dict), f"{label} must be an object")
    for field in ("name", "artist", "description", "itemName"):
        require(isinstance(data.get(field), str) and data[field], f"{label}: '{field}' is required")

    artist = ARTISTS.get(data["artist"])
    require(artist is not None, f"{label}: unknown artist '{data['artist']}'")
    for field in ("maxCount", "comparatorOutput"):
        require(isinstance(data.get(field), int),f"{label}: '{field}' must be an integer")

    require(isinstance(data.get("lengthInSeconds"), (int, float)), f"{label}: 'lengthInSeconds' must be numeric",)
    require(data["maxCount"] > 0, f"{label}: maxCount must be positive")
    return artist


def write_json(path: Path, value: Any, check: bool) -> None:
    if check:
        return

    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(
        json.dumps(value, indent=2, ensure_ascii=False) + "\n",
        encoding="utf-8",
    )


def generate(root: Path, definitions_path: Path, check: bool) -> None:
    paths = resource_paths(root)
    definitions = json.loads(definitions_path.read_text(encoding="utf-8"))
    require(isinstance(definitions, list), "definitions.json must contain an array")

    sounds: dict[str, Any] = {
        "nem_test": {
            "sounds": [{"name": f"{MOD_ID}:musics/test/multimilionaire", "stream": True}]
        }
    }
    lang = {"item.notenoughmusic.discs.test.nem_test_music_disc": "Mod Maker"}
    generated_ids: set[str] = set()

    for index, data in enumerate(definitions, 1):
        artist = validate_definition(data, index)
        name = data["name"]
        generated_id = f"{artist['name']}_{name}"
        require(generated_id not in generated_ids, f"duplicate generated disc id: {generated_id}")
        generated_ids.add(generated_id)

        sound_id = f"nem_{artist['name']}_{name}"
        write_json(
            paths["jukebox"] / artist["prefix"] / f"{name}.json",
            {
                "comparator_output": data["comparatorOutput"],
                "description": {"translate": data["description"]},
                "length_in_seconds": data["lengthInSeconds"],
                "sound_event": f"{MOD_ID}:{sound_id}",
            },
            check,
        )
        write_json(
            paths["models"] / artist["prefix"] / f"{sound_id}_music_disc.json",
            {
                "parent": "minecraft:item/generated",
                "textures": {
                    "layer0": f"{MOD_ID}:item/discs/{artist['prefix']}{name}"
                },
            },
            check,
        )
        sounds[sound_id] = {
            "sounds": [
                {
                    "name": f"{MOD_ID}:musics/{artist['prefix']}{name}",
                    "stream": True,
                }
            ]
        }
        lang_key = (
            f"item.{MOD_ID}.discs.{artist['raw']}."
            f"{sound_id}_music_disc"
        )
        lang[lang_key] = data["itemName"]

    write_json(paths["sounds_json"], sounds, check)
    write_json(paths["lang_json"], lang, check)


def main() -> None:
    args = parse_args()
    root = args.root.resolve()
    paths = resource_paths(root)
    definitions = (args.definitions or paths["definitions"]).resolve()
    require(definitions.is_file(), f"definitions file does not exist: {definitions}")
    generate(root, definitions, args.check)
    action = "Validated" if args.check else "Generated"
    print(f"{action} resources from {definitions}")


if __name__ == "__main__":
    try:
        main()
    except (OSError, ValueError, json.JSONDecodeError) as error:
        raise SystemExit(f"AutoGen error: {error}") from error
