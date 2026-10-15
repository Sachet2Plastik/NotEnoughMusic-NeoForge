package sachet2plastik.notenoughmusic.loottable.conditiondata;

import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.List;

public class LightConditionData implements IConditionData {
    Integer Min = -1;
    Integer Max = -1;
    Integer Exactly = -1;


    public LightConditionData(Integer min, Integer max, Integer exactly) {
        Min = min;
        Max = max;
        Exactly = exactly;
    }


    @Override
    public LootItemCondition.Builder convert(ConditionDataConverter dataConverter, ConditionContext ctx) {
        return dataConverter.buildFromData(this);
    }
}
