package sachet2plastik.notenoughmusic.item;

import java.util.Comparator;
import java.util.Map;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredItem;
import sachet2plastik.notenoughmusic.DiscDefinition;
import sachet2plastik.notenoughmusic.DiscsRegistry;
import sachet2plastik.notenoughmusic.NotEnoughMusic;
import sachet2plastik.notenoughmusic.sound.ModSound;

public final class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(NotEnoughMusic.MOD_ID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, NotEnoughMusic.MOD_ID);

    public static final DeferredItem<Item> TEST_MUSIC_DISC = ITEMS.register("discs/test/nem_test_music_disc",
            () -> new Item(new Item.Properties().jukeboxPlayable(ModSound.NEM_TEST_KEY)
                    .stacksTo(1).rarity(Rarity.COMMON)));

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> CUSTOM_ITEM_GROUP =
            CREATIVE_MODE_TABS.register("item_group", () -> CreativeModeTab.builder()
                    .title(Component.literal("Not Enough Music"))
                    .icon(() -> new ItemStack(TEST_MUSIC_DISC.get()))
                    .displayItems((parameters, output) -> {
                        output.accept(TEST_MUSIC_DISC.get());
                        DiscsRegistry.DISCS.values().stream()
                                .sorted(Comparator.comparing(def -> def.Artist.Name()))
                                .forEach(def -> output.accept(def.Item));
                    }).build());

    private ModItems() {
    }

    public static void registerModItems() {
        NotEnoughMusic.LOGGER.info("Registering {} items", NotEnoughMusic.MOD_ID);
        DiscsRegistry.DISCS.forEach((name, definition) -> {
            String itemName = "discs/" + definition.Artist.prefix() + NotEnoughMusic.MOD_FORMAT_NAME + "_"
                    + definition.Artist.Name() + "_" + name + "_music_disc";
            definition.Item = ITEMS.register(itemName,
                    () -> new Item(new Item.Properties().jukeboxPlayable(definition.Key)
                            .stacksTo(definition.MaxCount).rarity(definition.Rarity)));
        });
    }
}
