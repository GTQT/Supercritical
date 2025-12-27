package supercritical.common.item.behaviors;

import gregtech.api.items.metaitem.MetaItem;
import gregtech.api.items.metaitem.stats.IItemDurabilityManager;
import gregtech.api.unification.material.Material;
import lombok.Getter;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FuelRodBehavior extends NuclearComponentBehavior {

    @Getter
    private final Material material;               // 燃料材料
    @Getter
    private final int heatOutput;                 // 热量产出（HU/t）
    @Getter
    private final int energyOutput;               // 能量产出（EU/t）
    @Getter
    private final float neutronEmission;          // 中子发射率

    public FuelRodBehavior(int maxDurability,
                           Material material,
                           int heatOutput,
                           int energyOutput,
                           float neutronEmission) {
        super(maxDurability);
        this.material = material;
        this.heatOutput = Math.max(0, heatOutput);
        this.energyOutput = Math.max(0, energyOutput);
        this.neutronEmission = Math.max(0.0f, neutronEmission);
    }

    @Nullable
    public static FuelRodBehavior getInstanceFor(ItemStack itemStack) {
        if (!(itemStack.getItem() instanceof MetaItem)) return null;

        MetaItem<?>.MetaValueItem valueItem = ((MetaItem<?>) itemStack.getItem()).getItem(itemStack);
        if (valueItem == null) return null;

        IItemDurabilityManager durabilityManager = valueItem.getDurabilityManager();
        if (!(durabilityManager instanceof FuelRodBehavior)) return null;

        return (FuelRodBehavior) durabilityManager;
    }

    // 获取耐久消耗（每tick固定消耗1耐久）
    public int getDurabilityCost() {
        return 1;
    }

    @Override
    public void addInformation(ItemStack stack, List<String> lines) {
        super.addInformation(stack, lines);

        // 基础信息
        lines.add(I18n.format("燃料材料: " + material.getLocalizedName()));

        // 性能参数
        lines.add(I18n.format("热量产出: " + heatOutput + " HU/t"));
        lines.add(I18n.format("能量产出: " + energyOutput + " EU/t"));
        lines.add(I18n.format("中子发射率: " + String.format("%.2f", neutronEmission)));

        // 每tick耐久消耗
        lines.add(I18n.format("耐久消耗: " + getDurabilityCost() + "/tick"));
    }
}