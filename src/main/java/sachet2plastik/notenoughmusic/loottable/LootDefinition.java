package sachet2plastik.notenoughmusic.loottable;

import sachet2plastik.notenoughmusic.loottable.conditiondata.IConditionData;

import java.util.List;

public class LootDefinition {
    public Integer Rolls;
    public String Target;
    public Integer CountMin;
    public Integer CountMax;
    public List<IConditionData> Conditions;
}
