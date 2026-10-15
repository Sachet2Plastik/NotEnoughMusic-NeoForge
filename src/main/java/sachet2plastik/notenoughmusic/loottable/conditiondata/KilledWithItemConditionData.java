package sachet2plastik.notenoughmusic.loottable.conditiondata;

import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class KilledWithItemConditionData implements IConditionData {
    String Item;

    public KilledWithItemConditionData(String item) {
        Item = item;
    }

    @Override
    public LootItemCondition.Builder convert(ConditionDataConverter dataConverter, ConditionContext ctx) {
        return dataConverter.buildFromData(this);
    }

}
