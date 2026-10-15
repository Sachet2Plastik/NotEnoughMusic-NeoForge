package sachet2plastik.notenoughmusic;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import sachet2plastik.notenoughmusic.item.ModItems;
import sachet2plastik.notenoughmusic.loottable.ModLootTables;
import sachet2plastik.notenoughmusic.sound.ModSound;

@Mod(NotEnoughMusic.MOD_ID)
public class NotEnoughMusic {
    public static final String MOD_ID = "notenoughmusic";
    public static final String MOD_NAME = "Not Enough Music";
    public static final String MOD_FORMAT_NAME = "nem";
    public static final Logger LOGGER = LogUtils.getLogger();

    public NotEnoughMusic(IEventBus modEventBus) {
        DiscsRegistry.registerDiscFromConfig();
        ModSound.registerSounds();
        ModItems.registerModItems();
        ModSound.SOUND_EVENTS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModItems.CREATIVE_MODE_TABS.register(modEventBus);
        ModLootTables.registerLootTables();
    }
}
