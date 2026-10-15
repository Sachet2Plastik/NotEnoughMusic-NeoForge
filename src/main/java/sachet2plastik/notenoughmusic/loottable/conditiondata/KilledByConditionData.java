package sachet2plastik.notenoughmusic.loottable.conditiondata;

import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class KilledByConditionData implements IConditionData {
    String Killer;
    Boolean Direct;

    public KilledByConditionData(String killer, Boolean direct) {
        Killer = killer;
        Direct = direct;
    }

    @Override
    public LootItemCondition.Builder convert(ConditionDataConverter dataConverter, ConditionContext ctx) {
        return dataConverter.buildFromData(this);
    }
}
