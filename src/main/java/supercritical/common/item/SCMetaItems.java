package supercritical.common.item;

import gregtech.api.items.metaitem.MetaItem;
import gregtech.api.items.metaitem.MetaItem.MetaValueItem;
import gregtech.api.items.metaitem.StandardMetaItem;
import gregtech.api.unification.material.Materials;
import supercritical.common.item.behaviors.*;

public class SCMetaItems {

    // Nuclear
    public static MetaValueItem ANODE_BASKET;
    public static MetaValueItem FUEL_CLADDING;

    public static MetaItem<?>.MetaValueItem FUEL_ROD_URANIUM;
    public static MetaItem<?>.MetaValueItem FUEL_ROD_MOX;
    public static MetaItem<?>.MetaValueItem FUEL_ROD_DUAL_URANIUM;
    public static MetaItem<?>.MetaValueItem FUEL_ROD_QUAD_URANIUM;

    public static MetaItem<?>.MetaValueItem HEAT_VENT_BASIC;
    public static MetaItem<?>.MetaValueItem HEAT_VENT_ADVANCED;
    public static MetaItem<?>.MetaValueItem HEAT_VENT_REACTOR;

    public static MetaItem<?>.MetaValueItem COMPONENT_HEAT_VENT_BASIC;
    public static MetaItem<?>.MetaValueItem COMPONENT_HEAT_VENT_ADVANCED;

    public static MetaItem<?>.MetaValueItem COOLANT_CELL_10K;
    public static MetaItem<?>.MetaValueItem COOLANT_CELL_30K;
    public static MetaItem<?>.MetaValueItem COOLANT_CELL_60K;

    public static MetaItem<?>.MetaValueItem NEUTRON_REFLECTOR_BASIC;
    public static MetaItem<?>.MetaValueItem NEUTRON_REFLECTOR_THICK;
    public static MetaItem<?>.MetaValueItem NEUTRON_REFLECTOR_IRIDIUM;

    public static MetaItem<?>.MetaValueItem HEAT_EXCHANGER_BASIC;
    public static MetaItem<?>.MetaValueItem HEAT_EXCHANGER_REACTOR;
    public static MetaItem<?>.MetaValueItem COMPONENT_HEAT_EXCHANGER_BASIC;

    public static MetaItem<?>.MetaValueItem REACTOR_PLATING_BASIC;
    public static MetaItem<?>.MetaValueItem REACTOR_PLATING_ADVANCED;
    public static MetaItem<?>.MetaValueItem REACTOR_PLATING_HEAT_RESISTANT;
    public static MetaItem<?>.MetaValueItem REACTOR_PLATING_BLAST_RESISTANT;

    private static StandardMetaItem metaItem;

    public static void initMetaItems() {
        metaItem = new StandardMetaItem();
        metaItem.setRegistryName("supercritical_meta_item");
    }

    public static void initSubitems() {
        SCMetaItems.ANODE_BASKET = metaItem.addItem(0, "basket.anode");
        SCMetaItems.FUEL_CLADDING = metaItem.addItem(1, "cladding.fuel");

        // 1. 燃料棒
        FUEL_ROD_URANIUM = metaItem.addItem(1000, "fuel_rod.uranium")
                .addComponents(new FuelRodBehavior(
                        20000,      // 最大耐久
                        Materials.Uranium,
                        4,          // 热量产出 HU/t
                        5,          // 能量产出 EU/t
                        1.0f        // 中子发射率
                ));

        FUEL_ROD_MOX = metaItem.addItem(1001, "fuel_rod.mox")
                .addComponents(new FuelRodBehavior(
                        15000,      // 最大耐久
                        Materials.Plutonium,
                        6,          // 热量产出 HU/t
                        8,          // 能量产出 EU/t
                        1.2f        // 中子发射率
                ));

        FUEL_ROD_DUAL_URANIUM = metaItem.addItem(1002, "fuel_rod.dual_uranium")
                .addComponents(new FuelRodBehavior(
                        40000,      // 最大耐久
                        Materials.Uranium,
                        8,          // 热量产出 HU/t
                        10,         // 能量产出 EU/t
                        2.0f        // 中子发射率
                ));

        FUEL_ROD_QUAD_URANIUM = metaItem.addItem(1003, "fuel_rod.quad_uranium")
                .addComponents(new FuelRodBehavior(
                        80000,      // 最大耐久
                        Materials.Uranium,
                        16,         // 热量产出 HU/t
                        20,         // 能量产出 EU/t
                        4.0f        // 中子发射率
                ));

        // 2. 散热片
        HEAT_VENT_BASIC = metaItem.addItem(1010, "heat_vent.basic")
                .addComponents(new HeatVentBehavior(
                        100000,     // 最大耐久
                        Materials.Copper,
                        6           // 散热能力 HU/t
                ));

        HEAT_VENT_ADVANCED = metaItem.addItem(1011, "heat_vent.advanced")
                .addComponents(new HeatVentBehavior(
                        150000,     // 最大耐久
                        Materials.Aluminium,
                        12          // 散热能力 HU/t
                ));

        HEAT_VENT_REACTOR = metaItem.addItem(1012, "heat_vent.reactor")
                .addComponents(new HeatVentBehavior(
                        200000,     // 最大耐久
                        Materials.Electrum,
                        20          // 散热能力 HU/t
                ));

        // 3. 元件散热片
        COMPONENT_HEAT_VENT_BASIC = metaItem.addItem(1020, "component_heat_vent.basic")
                .addComponents(new ComponentHeatVentBehavior(
                        120000,     // 最大耐久
                        Materials.Bronze,
                        8           // 冷却速率 HU/t
                ));

        COMPONENT_HEAT_VENT_ADVANCED = metaItem.addItem(1021, "component_heat_vent.advanced")
                .addComponents(new ComponentHeatVentBehavior(
                        180000,     // 最大耐久
                        Materials.Aluminium,
                        15          // 冷却速率 HU/t
                ));

        // 4. 冷却单元
        COOLANT_CELL_10K = metaItem.addItem(1030, "coolant_cell.10k")
                .addComponents(new CoolantCellBehavior(
                        10000,      // 最大耐久（等于热容量）
                        Materials.Water,
                        10000,      // 热容量 HU
                        20          // 冷却速率 HU/t
                ));

        COOLANT_CELL_30K = metaItem.addItem(1031, "coolant_cell.30k")
                .addComponents(new CoolantCellBehavior(
                        30000,      // 最大耐久（等于热容量）
                        Materials.Water,
                        30000,      // 热容量 HU
                        40          // 冷却速率 HU/t
                ));

        COOLANT_CELL_60K = metaItem.addItem(1032, "coolant_cell.60k")
                .addComponents(new CoolantCellBehavior(
                        60000,      // 最大耐久（等于热容量）
                        Materials.Water,
                        60000,      // 热容量 HU
                        60          // 冷却速率 HU/t
                ));

        // 5. 中子反射板
        NEUTRON_REFLECTOR_BASIC = metaItem.addItem(1040, "neutron_reflector.basic")
                .addComponents(new NeutronReflectorBehavior(
                        50000,      // 最大耐久
                        Materials.Graphite,
                        0.5f        // 50%反射效率
                ));

        NEUTRON_REFLECTOR_THICK = metaItem.addItem(1041, "neutron_reflector.thick")
                .addComponents(new NeutronReflectorBehavior(
                        100000,     // 最大耐久
                        Materials.Beryllium,
                        0.75f       // 75%反射效率
                ));

        NEUTRON_REFLECTOR_IRIDIUM = metaItem.addItem(1042, "neutron_reflector.iridium")
                .addComponents(new NeutronReflectorBehavior(
                        200000,     // 最大耐久
                        Materials.Iridium,
                        0.95f       // 95%反射效率
                ));

        // 6. 热交换器
        HEAT_EXCHANGER_BASIC = metaItem.addItem(1050, "heat_exchanger.basic")
                .addComponents(new HeatExchangerBehavior(
                        100000,     // 最大耐久
                        Materials.Copper,
                        10          // 热传递速率 HU/t
                ));

        HEAT_EXCHANGER_REACTOR = metaItem.addItem(1051, "heat_exchanger.reactor")
                .addComponents(new ReactorHeatExchangerBehavior(
                        150000,     // 最大耐久
                        Materials.Aluminium,
                        10000,      // 热存储容量 HU
                        20          // 热传递速率 HU/t
                ));

        COMPONENT_HEAT_EXCHANGER_BASIC = metaItem.addItem(1052, "component_heat_exchanger.basic")
                .addComponents(new ComponentHeatExchangerBehavior(
                        120000,     // 最大耐久
                        Materials.Bronze,
                        15          // 热传递速率 HU/t
                ));

        // 7. 反应堆隔板
        REACTOR_PLATING_BASIC = metaItem.addItem(1060, "reactor_plating.basic")
                .addComponents(new ReactorPlatingBehavior(
                        50000,      // 最大耐久
                        Materials.Iron,
                        1000,       // 热容量提升 HU
                        0.1f        // 10%爆炸抗性
                ));

        REACTOR_PLATING_ADVANCED = metaItem.addItem(1061, "reactor_plating.advanced")
                .addComponents(new ReactorPlatingBehavior(
                        100000,     // 最大耐久
                        Materials.Steel,
                        2500,       // 热容量提升 HU
                        0.25f       // 25%爆炸抗性
                ));

        REACTOR_PLATING_HEAT_RESISTANT = metaItem.addItem(1062, "reactor_plating.heat_resistant")
                .addComponents(new ReactorPlatingBehavior(
                        150000,     // 最大耐久
                        Materials.Titanium,
                        5000,       // 热容量提升 HU
                        0.15f       // 15%爆炸抗性
                ));

        REACTOR_PLATING_BLAST_RESISTANT = metaItem.addItem(1063, "reactor_plating.blast_resistant")
                .addComponents(new ReactorPlatingBehavior(
                        120000,     // 最大耐久
                        Materials.TungstenSteel,
                        2000,       // 热容量提升 HU
                        0.5f        // 50%爆炸抗性
                ));
    }
}
