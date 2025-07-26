package supercritical.mixins.gregtech;

import gregtech.api.pattern.MultiblockShapeInfo;
import gregtech.api.util.BlockInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
import java.util.Map;

@Mixin(value = MultiblockShapeInfo.Builder.class, remap = false)
public interface MultiblockShapeInfoBuilderAccessor {

    @Accessor("shape")
    List<String[]> getShape();

    @Accessor("shape")
    void setShape(List<String[]> shape);

    @Accessor("symbolMap")
    Map<Character, BlockInfo> getSymbolMap();

    @Accessor("symbolMap")
    void setSymbolMap(Map<Character, BlockInfo> symbolMap);
}
