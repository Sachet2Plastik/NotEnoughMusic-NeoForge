package sachet2plastik.notenoughmusic.loottable.conditiondata;

import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class RandomChancesEnchantmentConditionData implements IConditionData {
    String Enchant;
    Float Base;
    Float PerLevel;

    public RandomChancesEnchantmentConditionData(String enchant, Float base, Float perLevel) {
        Enchant = enchant;
        Base = base;
        PerLevel = perLevel;
    }

    @Override
    public LootItemCondition.Builder convert(ConditionDataConverter dataConverter, ConditionContext ctx) {
        return dataConverter.buildFromData(this, ctx);
    }
}
