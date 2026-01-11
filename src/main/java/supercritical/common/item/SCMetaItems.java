package supercritical.common.item;

import gregtech.api.GTValues;
import gregtech.api.creativetab.BaseCreativeTab;
import gregtech.api.items.metaitem.MetaItem;
import gregtech.api.items.metaitem.MetaItem.MetaValueItem;
import gregtech.api.items.metaitem.StandardMetaItem;
import gregtech.api.unification.material.Materials;
import gregtech.common.items.MetaItems;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import supercritical.api.unification.material.SCMaterials;
import supercritical.common.item.behaviors.*;

public class SCMetaItems {

    // Nuclear
    public static MetaValueItem ANODE_BASKET;
    public static MetaValueItem FUEL_CLADDING;

    public static MetaItem<?>.MetaValueItem FUEL_ROD_1X;
    public static MetaItem<?>.MetaValueItem FUEL_ROD_2X;
    public static MetaItem<?>.MetaValueItem FUEL_ROD_4X;

    public static MetaItem<?>.MetaValueItem FUEL_ROD_URANIUM_1X_DEPLETED;
    public static MetaItem<?>.MetaValueItem FUEL_ROD_URANIUM_2X_DEPLETED;
    public static MetaItem<?>.MetaValueItem FUEL_ROD_URANIUM_4X_DEPLETED;

    public static MetaItem<?>.MetaValueItem FUEL_ROD_THORIUM_1X_DEPLETED;
    public static MetaItem<?>.MetaValueItem FUEL_ROD_THORIUM_2X_DEPLETED;
    public static MetaItem<?>.MetaValueItem FUEL_ROD_THORIUM_4X_DEPLETED;

    public static MetaItem<?>.MetaValueItem FUEL_ROD_PLUTONIUM_1X_DEPLETED;
    public static MetaItem<?>.MetaValueItem FUEL_ROD_PLUTONIUM_2X_DEPLETED;
    public static MetaItem<?>.MetaValueItem FUEL_ROD_PLUTONIUM_4X_DEPLETED;

    public static MetaItem<?>.MetaValueItem FUEL_ROD_NAQUADAH_1X_DEPLETED;
    public static MetaItem<?>.MetaValueItem FUEL_ROD_NAQUADAH_2X_DEPLETED;
    public static MetaItem<?>.MetaValueItem FUEL_ROD_NAQUADAH_4X_DEPLETED;

    public static MetaItem<?>.MetaValueItem FUEL_ROD_URANIUM_1X;
    public static MetaItem<?>.MetaValueItem FUEL_ROD_URANIUM_2X;
    public static MetaItem<?>.MetaValueItem FUEL_ROD_URANIUM_4X;

    public static MetaItem<?>.MetaValueItem FUEL_ROD_THORIUM_1X;
    public static MetaItem<?>.MetaValueItem FUEL_ROD_THORIUM_2X;
    public static MetaItem<?>.MetaValueItem FUEL_ROD_THORIUM_4X;

    public static MetaItem<?>.MetaValueItem FUEL_ROD_PLUTONIUM_1X;
    public static MetaItem<?>.MetaValueItem FUEL_ROD_PLUTONIUM_2X;
    public static MetaItem<?>.MetaValueItem FUEL_ROD_PLUTONIUM_4X;

    public static MetaItem<?>.MetaValueItem FUEL_ROD_NAQUADAH_1X;
    public static MetaItem<?>.MetaValueItem FUEL_ROD_NAQUADAH_2X;
    public static MetaItem<?>.MetaValueItem FUEL_ROD_NAQUADAH_4X;

    public static MetaItem<?>.MetaValueItem HEAT_VENT_BASIC;
    public static MetaItem<?>.MetaValueItem HEAT_VENT_ADVANCED;
    public static MetaItem<?>.MetaValueItem HEAT_VENT_ELITE;
    public static MetaItem<?>.MetaValueItem HEAT_VENT_ULTIMATE;

    public static MetaItem<?>.MetaValueItem COMPONENT_HEAT_VENT_BASIC;
    public static MetaItem<?>.MetaValueItem COMPONENT_HEAT_VENT_ADVANCED;
    public static MetaItem<?>.MetaValueItem COMPONENT_HEAT_VENT_ELITE;
    public static MetaItem<?>.MetaValueItem COMPONENT_HEAT_VENT_ULTIMATE;

    public static MetaItem<?>.MetaValueItem COOLANT_CELL_10K;
    public static MetaItem<?>.MetaValueItem COOLANT_CELL_30K;
    public static MetaItem<?>.MetaValueItem COOLANT_CELL_60K;

    public static MetaItem<?>.MetaValueItem COOLANT_CELL_COOLANT_10K;
    public static MetaItem<?>.MetaValueItem COOLANT_CELL_COOLANT_30K;
    public static MetaItem<?>.MetaValueItem COOLANT_CELL_COOLANT_60K;

    public static MetaItem<?>.MetaValueItem COOLANT_CELL_SODIUM_POTASSIUM_10K;
    public static MetaItem<?>.MetaValueItem COOLANT_CELL_SODIUM_POTASSIUM_30K;
    public static MetaItem<?>.MetaValueItem COOLANT_CELL_SODIUM_POTASSIUM_60K;

    public static MetaItem<?>.MetaValueItem NEUTRON_REFLECTOR_BASIC;
    public static MetaItem<?>.MetaValueItem NEUTRON_REFLECTOR_THICK;
    public static MetaItem<?>.MetaValueItem NEUTRON_REFLECTOR_IRIDIUM;

    public static MetaItem<?>.MetaValueItem HEAT_EXCHANGER_BASIC;
    public static MetaItem<?>.MetaValueItem HEAT_EXCHANGER_ADVANCED;

    public static MetaItem<?>.MetaValueItem HEAT_EXCHANGER_REACTOR;
    public static MetaItem<?>.MetaValueItem COMPONENT_HEAT_EXCHANGER;

    public static MetaItem<?>.MetaValueItem REACTOR_PLATING_BASIC;
    public static MetaItem<?>.MetaValueItem REACTOR_PLATING_ADVANCED;
    public static MetaItem<?>.MetaValueItem REACTOR_PLATING_HEAT_RESISTANT;
    public static MetaItem<?>.MetaValueItem REACTOR_PLATING_BLAST_RESISTANT;

    private static StandardMetaItem metaItem;

    public static void initMetaItems() {
        metaItem = new StandardMetaItem();
        metaItem.setRegistryName("supercritical_meta_item");
        metaItem.setCreativeTab(SUPERCRITICAL_TAB);
    }

    public static final BaseCreativeTab SUPERCRITICAL_TAB = new BaseCreativeTab("supercritical_tab",
            () -> SCMetaItems.FUEL_ROD_URANIUM_1X.getStackForm(), false);

    public static void initSubitems() {
        SCMetaItems.ANODE_BASKET = metaItem.addItem(0, "basket.anode");
        SCMetaItems.FUEL_CLADDING = metaItem.addItem(1, "cladding.fuel");

        //核电-燃料棒
        SCMetaItems.FUEL_ROD_1X = metaItem.addItem(100, "fuel_rod.1x");
        SCMetaItems.FUEL_ROD_2X = metaItem.addItem(101, "fuel_rod.2x");
        SCMetaItems.FUEL_ROD_4X = metaItem.addItem(102, "fuel_rod.4x");

        //枯竭燃料棒(纯物品)
        //铀 1x 2x 4x
        SCMetaItems.FUEL_ROD_URANIUM_1X_DEPLETED = metaItem.addItem(110, "fuel_rod.uranium.1x.depleted");
        SCMetaItems.FUEL_ROD_URANIUM_2X_DEPLETED = metaItem.addItem(111, "fuel_rod.uranium.2x.depleted");
        SCMetaItems.FUEL_ROD_URANIUM_4X_DEPLETED = metaItem.addItem(112, "fuel_rod.uranium.4x.depleted");

        //钍 1x 2x 4x
        SCMetaItems.FUEL_ROD_THORIUM_1X_DEPLETED = metaItem.addItem(113, "fuel_rod.thorium.1x.depleted");
        SCMetaItems.FUEL_ROD_THORIUM_2X_DEPLETED = metaItem.addItem(114, "fuel_rod.thorium.2x.depleted");
        SCMetaItems.FUEL_ROD_THORIUM_4X_DEPLETED = metaItem.addItem(115, "fuel_rod.thorium.4x.depleted");

        //钚 1x 2x 4x
        SCMetaItems.FUEL_ROD_PLUTONIUM_1X_DEPLETED = metaItem.addItem(116, "fuel_rod.plutonium.1x.depleted");
        SCMetaItems.FUEL_ROD_PLUTONIUM_2X_DEPLETED = metaItem.addItem(117, "fuel_rod.plutonium.2x.depleted");
        SCMetaItems.FUEL_ROD_PLUTONIUM_4X_DEPLETED = metaItem.addItem(118, "fuel_rod.plutonium.4x.depleted");

        //硅岩 1x 2x 4x Naquadah
        SCMetaItems.FUEL_ROD_NAQUADAH_1X_DEPLETED = metaItem.addItem(119, "fuel_rod.naquadah.1x.depleted");
        SCMetaItems.FUEL_ROD_NAQUADAH_2X_DEPLETED = metaItem.addItem(120, "fuel_rod.naquadah.2x.depleted");
        SCMetaItems.FUEL_ROD_NAQUADAH_4X_DEPLETED = metaItem.addItem(121, "fuel_rod.naquadah.4x.depleted");


        //燃料棒(纯物品)

        //铀 1x 2x 4x
        FUEL_ROD_URANIUM_1X = metaItem.addItem(150, "fuel_rod.uranium.1x")
                .addComponents(new FuelRodBehavior(20000, Materials.Uranium, 5, 512, 1.0f,
                        SCMetaItems.FUEL_ROD_URANIUM_1X_DEPLETED.getStackForm()
                ));

        FUEL_ROD_URANIUM_2X = metaItem.addItem(151, "fuel_rod.uranium.2x")
                .addComponents(new FuelRodBehavior(20000, Materials.Uranium, 10, 1024, 2.0f,
                        SCMetaItems.FUEL_ROD_URANIUM_2X_DEPLETED.getStackForm()
                ));

        FUEL_ROD_URANIUM_4X = metaItem.addItem(152, "fuel_rod.uranium.4x")
                .addComponents(new FuelRodBehavior(20000, Materials.Uranium, 20, 2048, 4.0f,
                        SCMetaItems.FUEL_ROD_URANIUM_4X_DEPLETED.getStackForm()
                ));

        //钍 1x 2x 4x
        FUEL_ROD_THORIUM_1X = metaItem.addItem(153, "fuel_rod.thorium.1x")
                .addComponents(new FuelRodBehavior(24000, Materials.Thorium, 3, 384, 1.5f,
                        SCMetaItems.FUEL_ROD_THORIUM_1X_DEPLETED.getStackForm()
                ));

        FUEL_ROD_THORIUM_2X = metaItem.addItem(154, "fuel_rod.thorium.2x")
                .addComponents(new FuelRodBehavior(24000, Materials.Thorium, 6, 768, 3.0f,
                        SCMetaItems.FUEL_ROD_THORIUM_2X_DEPLETED.getStackForm()
                ));

        FUEL_ROD_THORIUM_4X = metaItem.addItem(155, "fuel_rod.thorium.4x")
                .addComponents(new FuelRodBehavior(24000, Materials.Thorium, 12, 1536, 6.0f,
                        SCMetaItems.FUEL_ROD_THORIUM_4X_DEPLETED.getStackForm()
                ));

        //钚 1x 2x 4x
        FUEL_ROD_PLUTONIUM_1X = metaItem.addItem(156, "fuel_rod.plutonium.1x")
                .addComponents(new FuelRodBehavior(15000, Materials.Plutonium, 12, 640, 2.5f,
                        SCMetaItems.FUEL_ROD_PLUTONIUM_1X_DEPLETED.getStackForm()
                ));

        FUEL_ROD_PLUTONIUM_2X = metaItem.addItem(157, "fuel_rod.plutonium.2x")
                .addComponents(new FuelRodBehavior(15000, Materials.Plutonium, 24, 1280, 5.0f,
                        SCMetaItems.FUEL_ROD_PLUTONIUM_2X_DEPLETED.getStackForm()
                ));

        FUEL_ROD_PLUTONIUM_4X = metaItem.addItem(158, "fuel_rod.plutonium.4x")
                .addComponents(new FuelRodBehavior(15000, Materials.Plutonium, 48, 2560, 10.0f,
                        SCMetaItems.FUEL_ROD_PLUTONIUM_4X_DEPLETED.getStackForm()
                ));

        //硅岩 1x 2x 4x
        FUEL_ROD_NAQUADAH_1X = metaItem.addItem(159, "fuel_rod.naquadah.1x")
                .addComponents(new FuelRodBehavior(36000, Materials.Naquadah, 20, 1024, 3.0f,
                        SCMetaItems.FUEL_ROD_NAQUADAH_1X_DEPLETED.getStackForm()
                ));

        FUEL_ROD_NAQUADAH_2X = metaItem.addItem(160, "fuel_rod.naquadah.2x")
                .addComponents(new FuelRodBehavior(36000, Materials.Naquadah, 40, 2048, 6.0f,
                        SCMetaItems.FUEL_ROD_NAQUADAH_2X_DEPLETED.getStackForm()
                ));

        FUEL_ROD_NAQUADAH_4X = metaItem.addItem(161, "fuel_rod.naquadah.4x")
                .addComponents(new FuelRodBehavior(36000, Materials.Naquadah, 80, 4096, 12.0f,
                        SCMetaItems.FUEL_ROD_NAQUADAH_4X_DEPLETED.getStackForm()
                ));

        //散热片
        HEAT_VENT_BASIC = metaItem.addItem(200, "heat_vent.basic")
                .addComponents(new HeatVentBehavior(24000, Materials.Steel, 6
                ));

        HEAT_VENT_ADVANCED = metaItem.addItem(201, "heat_vent.advanced")
                .addComponents(new HeatVentBehavior(28000, Materials.StainlessSteel, 12
                ));

        HEAT_VENT_ELITE = metaItem.addItem(202, "heat_vent.elite")
                .addComponents(new HeatVentBehavior(32000, Materials.Titanium, 18
                ));

        HEAT_VENT_ULTIMATE = metaItem.addItem(203, "heat_vent.ultimate")
                .addComponents(new HeatVentBehavior(36000, Materials.TungstenSteel, 24
                ));

        //元件散热片
        COMPONENT_HEAT_VENT_BASIC = metaItem.addItem(205, "component_heat_vent.basic")
                .addComponents(new ComponentHeatVentBehavior(120000, Materials.Aluminium, 4
                ));

        COMPONENT_HEAT_VENT_ADVANCED = metaItem.addItem(206, "component_heat_vent.advanced")
                .addComponents(new ComponentHeatVentBehavior(180000, Materials.Gold, 8
                ));

        COMPONENT_HEAT_VENT_ELITE = metaItem.addItem(207, "component_heat_vent.elite")
                .addComponents(new ComponentHeatVentBehavior(240000, Materials.Platinum, 12
                ));

        COMPONENT_HEAT_VENT_ULTIMATE = metaItem.addItem(208, "component_heat_vent.ultimate")
                .addComponents(new ComponentHeatVentBehavior(300000, Materials.Neodymium, 16
                ));

        //冷却单元 10k 30k 60k
        COOLANT_CELL_10K = metaItem.addItem(210, "coolant_cell.10k");
        COOLANT_CELL_30K = metaItem.addItem(211, "coolant_cell.30k");
        COOLANT_CELL_60K = metaItem.addItem(212, "coolant_cell.60k");

        //青金石冷却剂
        COOLANT_CELL_COOLANT_10K = metaItem.addItem(213, "coolant_cell.coolant.10k")
                .addComponents(new CoolantCellBehavior(10000, Materials.WaterCoolant, 10000, 10
                ));
        COOLANT_CELL_COOLANT_30K = metaItem.addItem(214, "coolant_cell.coolant.30k")
                .addComponents(new CoolantCellBehavior(10000, Materials.WaterCoolant, 30000, 30
                ));
        COOLANT_CELL_COOLANT_60K = metaItem.addItem(215, "coolant_cell.coolant.60k")
                .addComponents(new CoolantCellBehavior(10000, Materials.WaterCoolant, 60000, 60
                ));

        //钠钾合金
        COOLANT_CELL_SODIUM_POTASSIUM_10K = metaItem.addItem(216, "coolant_cell.sodium_potassium.10k")
                .addComponents(new CoolantCellBehavior(10000, Materials.SodiumPotassium, 10000, 30
                ));

        COOLANT_CELL_SODIUM_POTASSIUM_30K = metaItem.addItem(217, "coolant_cell.sodium_potassium.30k")
                .addComponents(new CoolantCellBehavior(10000, Materials.SodiumPotassium, 30000, 90
                ));

        COOLANT_CELL_SODIUM_POTASSIUM_60K = metaItem.addItem(218, "coolant_cell.sodium_potassium.60k")
                .addComponents(new CoolantCellBehavior(10000, Materials.SodiumPotassium, 60000, 180
                ));


        //中子反射板
        NEUTRON_REFLECTOR_BASIC = metaItem.addItem(230, "neutron_reflector.basic")
                .addComponents(new NeutronReflectorBehavior(
                        50000, Materials.Graphite, 0.5f
                ));

        NEUTRON_REFLECTOR_THICK = metaItem.addItem(231, "neutron_reflector.thick")
                .addComponents(new NeutronReflectorBehavior(100000, Materials.Beryllium, 0.75f
                ));

        NEUTRON_REFLECTOR_IRIDIUM = metaItem.addItem(232, "neutron_reflector.iridium")
                .addComponents(new NeutronReflectorBehavior(200000, Materials.Iridium, 0.95f
                ));


        //热交换器
        HEAT_EXCHANGER_BASIC = metaItem.addItem(235, "heat_exchanger.basic")
                .addComponents(new HeatExchangerBehavior(100000, Materials.Copper, 10
                ));

        HEAT_EXCHANGER_ADVANCED = metaItem.addItem(236, "heat_exchanger.advanced")
                .addComponents(new HeatExchangerBehavior(120000, Materials.Gold, 20
                ));


        HEAT_EXCHANGER_REACTOR = metaItem.addItem(237, "heat_exchanger.reactor")
                .addComponents(new ReactorHeatExchangerBehavior(150000, Materials.Aluminium, 10000, 20
                ));

        COMPONENT_HEAT_EXCHANGER = metaItem.addItem(238, "heat_exchanger.component")
                .addComponents(new ComponentHeatExchangerBehavior(120000, Materials.Bronze, 15
                ));

        //反应堆隔板
        REACTOR_PLATING_BASIC = metaItem.addItem(240, "reactor_plating.basic")
                .addComponents(new ReactorPlatingBehavior(50000, Materials.Bronze, 1000, 0.1f
                ));

        REACTOR_PLATING_ADVANCED = metaItem.addItem(241, "reactor_plating.advanced")
                .addComponents(new ReactorPlatingBehavior(100000, Materials.Lead, 2500, 0.25f
                ));
    }
}
