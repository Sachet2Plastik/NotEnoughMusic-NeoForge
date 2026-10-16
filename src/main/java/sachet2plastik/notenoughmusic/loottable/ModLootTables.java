package sachet2plastik.notenoughmusic.loottable;

import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.LootTableLoadEvent;
import sachet2plastik.notenoughmusic.DiscsRegistry;
import sachet2plastik.notenoughmusic.loottable.conditiondata.ConditionDataConverter;
import sachet2plastik.notenoughmusic.loottable.conditiondata.IConditionData;

public final class ModLootTables {
    private ModLootTables() {
    }

    public static void registerLootTables() {
        NeoForge.EVENT_BUS.addListener(ModLootTables::modifyLootTable);
    }

    private static void modifyLootTable(LootTableLoadEvent event) {
        ResourceLocation tableId = event.getName();
        DiscsRegistry.DISCS.forEach((name, definition) -> LootRulesManager.rulesFor(name).forEach(lootDefinition -> {
            ResourceLocation targetId = ResourceLocation.fromNamespaceAndPath("minecraft", lootDefinition.Target);
            if (!targetId.equals(tableId)) {
                return;
            }

            LootPool.Builder poolBuilder = LootPool.lootPool()
                    .setRolls(ConstantValue.exactly(lootDefinition.Rolls))
                    .add(LootItem.lootTableItem(definition.Item))
                    .apply(SetItemCountFunction.setCount(
                            UniformGenerator.between(lootDefinition.CountMin, lootDefinition.CountMax)));
            lootDefinition.Conditions.forEach(condition -> poolBuilder.when(condition.convert(
                    new ConditionDataConverter(),
                    new IConditionData.ConditionContext(null, null, event, event.getRegistries()))));
            event.getTable().addPool(poolBuilder.build());
        }));
    }
}
