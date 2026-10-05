package lehjr.powersuits.common.config.module;

import lehjr.numina.common.constants.NuminaConstants;
import lehjr.powersuits.common.constants.MPSConstants;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * Energy generation modules, 4 tiers each. Every generator family is its own section under Energy_Generation.
 * Index 0 of the arrays is unused so the tier number can be used directly.
 */
public class EnergyGenerationModuleConfig {
    private static final int TIERS = 4;
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder().push("Energy_Generation");

    // Combustion Generators ----------------------------------------------------------------------
    private static final ModConfigSpec.BooleanValue[] COMBUSTION_IS_ALLOWED = new ModConfigSpec.BooleanValue[TIERS + 1];
    private static final ModConfigSpec.DoubleValue[] COMBUSTION_ENERGY_PER_TICK = new ModConfigSpec.DoubleValue[TIERS + 1];
    private static final ModConfigSpec.DoubleValue[] COMBUSTION_HEAT_PER_TICK = new ModConfigSpec.DoubleValue[TIERS + 1];
    private static final ModConfigSpec.IntValue[] COMBUSTION_FUEL_TICKS_PER_TICK = new ModConfigSpec.IntValue[TIERS + 1];
    static {
        BUILDER.push("Combustion_Generators");
        for (int tier = 1; tier <= TIERS; tier++) {
            BUILDER.push("Combustion_Energy_Generator_Module_" + tier);
            COMBUSTION_IS_ALLOWED[tier] = BUILDER.define(NuminaConstants.CONFIG_IS_ALLOWED, true);
            COMBUSTION_ENERGY_PER_TICK[tier] = BUILDER
                .comment("FE generated per tick while burning fuel taken from the player's inventory")
                .defineInRange(MPSConstants.ENERGY_PER_TICK, 40D * (1 << (tier - 1)), 0, 100000D);
            COMBUSTION_HEAT_PER_TICK[tier] = BUILDER
                .comment("Heat added to the suit per tick while burning")
                .defineInRange(MPSConstants.HEAT_GENERATION, 0.5D * (1 << (tier - 1)), 0, 10000D);
            COMBUSTION_FUEL_TICKS_PER_TICK[tier] = BUILDER
                .comment("Furnace burn ticks consumed per game tick (1 = same speed as a furnace)")
                .defineInRange(MPSConstants.FUEL_BURN_RATE, 1, 1, 100);
            BUILDER.pop();
        }
        BUILDER.pop();
    }

    // Thermal Generators -------------------------------------------------------------------------
    private static final ModConfigSpec.BooleanValue[] THERMAL_IS_ALLOWED = new ModConfigSpec.BooleanValue[TIERS + 1];
    private static final ModConfigSpec.DoubleValue[] THERMAL_THERMOELECTRIC_ENERGY_GENERATION = new ModConfigSpec.DoubleValue[TIERS + 1];
    private static final ModConfigSpec.DoubleValue[] THERMAL_STEAM_ELECTRIC_WATER_CONSUMPTION_BASE = new ModConfigSpec.DoubleValue[TIERS + 1];
    private static final ModConfigSpec.DoubleValue[] THERMAL_STEAM_ELECTRIC_WATER_CONSUMPTION_MULTIPLIER = new ModConfigSpec.DoubleValue[TIERS + 1];
    private static final ModConfigSpec.DoubleValue[] THERMAL_STEAM_ELECTRIC_ENERGY_GENERATION_BASE = new ModConfigSpec.DoubleValue[TIERS + 1];
    private static final ModConfigSpec.DoubleValue[] THERMAL_STEAM_ELECTRIC_ENERGY_GENERATION_MULTIPLIER = new ModConfigSpec.DoubleValue[TIERS + 1];
    static {
        BUILDER.push("Thermal_Generators");
        for (int tier = 1; tier <= TIERS; tier++) {
            BUILDER.push("Thermal_Energy_Generator_Module_" + tier);
            THERMAL_IS_ALLOWED[tier] = BUILDER.define(NuminaConstants.CONFIG_IS_ALLOWED, true);
            THERMAL_THERMOELECTRIC_ENERGY_GENERATION[tier] = BUILDER.defineInRange(MPSConstants.THERMOELECTRIC_ENERGY_GENERATION, 250D, 0, 100000D);
            THERMAL_STEAM_ELECTRIC_WATER_CONSUMPTION_BASE[tier] = BUILDER.defineInRange(MPSConstants.STEAM_ELECTRIC_WATER_CONSUMPTION + MPSConstants.BASE, 150D, 10D, 100000D);
            THERMAL_STEAM_ELECTRIC_WATER_CONSUMPTION_MULTIPLIER[tier] = BUILDER.defineInRange(MPSConstants.STEAM_ELECTRIC_WATER_CONSUMPTION + MPSConstants.MULTIPLIER, 150D, 10D, 100000D);
            THERMAL_STEAM_ELECTRIC_ENERGY_GENERATION_BASE[tier] = BUILDER.defineInRange(MPSConstants.STEAM_ELECTRIC_ENERGY_GENERATION + MPSConstants.BASE, 150D, 10D, 100000D);
            THERMAL_STEAM_ELECTRIC_ENERGY_GENERATION_MULTIPLIER[tier] = BUILDER.defineInRange(MPSConstants.STEAM_ELECTRIC_ENERGY_GENERATION + MPSConstants.MULTIPLIER, 500D, 0, 100000D);
            BUILDER.pop();
        }
        BUILDER.pop();
    }

    // Kinetic Generators -------------------------------------------------------------------------
    private static final ModConfigSpec.BooleanValue[] KINETIC_IS_ALLOWED = new ModConfigSpec.BooleanValue[TIERS + 1];
    private static final ModConfigSpec.DoubleValue[] KINETIC_ENERGY_GENERATION_BASE = new ModConfigSpec.DoubleValue[TIERS + 1];
    private static final ModConfigSpec.DoubleValue[] KINETIC_ENERGY_GENERATION_MULTIPLIER = new ModConfigSpec.DoubleValue[TIERS + 1];
    private static final ModConfigSpec.DoubleValue[] KINETIC_MOVEMENT_RESISTANCE_BASE = new ModConfigSpec.DoubleValue[TIERS + 1];
    private static final ModConfigSpec.DoubleValue[] KINETIC_MOVEMENT_RESISTANCE_MULTIPLIER = new ModConfigSpec.DoubleValue[TIERS + 1];
    static {
        BUILDER.push("Kinetic_Generators");
        for (int tier = 1; tier <= TIERS; tier++) {
            BUILDER.push("Kinetic_Energy_Generator_Module_" + tier);
            KINETIC_IS_ALLOWED[tier] = BUILDER.define(NuminaConstants.CONFIG_IS_ALLOWED, true);
            KINETIC_ENERGY_GENERATION_BASE[tier] = BUILDER
                .comment("FE generated per block walked on the ground")
                .defineInRange(MPSConstants.ENERGY_GENERATION + MPSConstants.BASE, 1000D + 1000D * tier, 0, 1000000D);
            KINETIC_ENERGY_GENERATION_MULTIPLIER[tier] = BUILDER
                .comment("Extra FE per block at the maximum \"energy generated\" tinker setting")
                .defineInRange(MPSConstants.ENERGY_GENERATION + MPSConstants.MULTIPLIER, 3000D + 3000D * tier, 0, 1000000D);
            KINETIC_MOVEMENT_RESISTANCE_BASE[tier] = BUILDER.defineInRange(NuminaConstants.MOVEMENT_RESISTANCE + MPSConstants.BASE, 0.01D, 0, 1D);
            KINETIC_MOVEMENT_RESISTANCE_MULTIPLIER[tier] = BUILDER
                .comment("Extra movement resistance at the maximum \"energy generated\" tinker setting")
                .defineInRange(NuminaConstants.MOVEMENT_RESISTANCE + MPSConstants.MULTIPLIER, 0.59D - 0.1D * tier, 0, 1D);
            BUILDER.pop();
        }
        BUILDER.pop();
    }

    // Solar Generators ---------------------------------------------------------------------------
    private static final ModConfigSpec.BooleanValue[] SOLAR_IS_ALLOWED = new ModConfigSpec.BooleanValue[TIERS + 1];
    private static final ModConfigSpec.DoubleValue[] SOLAR_ENERGY_GENERATION_DAY_BASE = new ModConfigSpec.DoubleValue[TIERS + 1];
    private static final ModConfigSpec.DoubleValue[] SOLAR_ENERGY_GENERATION_NIGHT_BASE = new ModConfigSpec.DoubleValue[TIERS + 1];
    private static final ModConfigSpec.DoubleValue[] SOLAR_HEAT_GENERATION_DAY_BASE = new ModConfigSpec.DoubleValue[TIERS + 1];
    private static final ModConfigSpec.DoubleValue[] SOLAR_HEAT_GENERATION_NIGHT_BASE = new ModConfigSpec.DoubleValue[TIERS + 1];
    static {
        BUILDER.push("Solar_Generators");
        for (int tier = 1; tier <= TIERS; tier++) {
            BUILDER.push("Solar_Energy_Generator_Module_" + tier);
            SOLAR_IS_ALLOWED[tier] = BUILDER.define(NuminaConstants.CONFIG_IS_ALLOWED, true);
            SOLAR_ENERGY_GENERATION_DAY_BASE[tier] = BUILDER.defineInRange(MPSConstants.ENERGY_GENERATION_DAY_BASE, 15000, 0, 100000.0D);
            SOLAR_ENERGY_GENERATION_NIGHT_BASE[tier] = BUILDER.defineInRange(MPSConstants.ENERGY_GENERATION_NIGHT_BASE, 1500, 0, 100000.0D);
            SOLAR_HEAT_GENERATION_DAY_BASE[tier] = BUILDER.defineInRange(MPSConstants.HEAT_GENERATION_DAY_BASE, 15, 0, 100000.0D);
            SOLAR_HEAT_GENERATION_NIGHT_BASE[tier] = BUILDER.defineInRange(MPSConstants.HEAT_GENERATION_NIGHT_BASE, 5, 0, 100000.0D);
            BUILDER.pop();
        }
        BUILDER.pop();
    }

    public static final ModConfigSpec MPS_GENERATOR_MODULE_SPEC = BUILDER.pop().build();

    // Combustion Generators
    public static final boolean[] combustionGenerator_isAllowed = new boolean[TIERS + 1];
    public static final double[] combustionGenerator_energyPerTick = new double[TIERS + 1];
    public static final double[] combustionGenerator_heatPerTick = new double[TIERS + 1];
    public static final int[] combustionGenerator_fuelTicksPerTick = new int[TIERS + 1];

    // Thermal Generators
    public static boolean thermalEnergyGenerator_1_isAllowed;
    public static double thermalEnergyGenerator_1_thermoelectricEnergyGeneration;
    public static double thermalEnergyGenerator_1_steamElectricWaterConsumptionBase;
    public static double thermalEnergyGenerator_1_steamElectricWaterConsumptionMultiplier;
    public static double thermalEnergyGenerator_1_steamElectricEnergyGenerationBase;
    public static double thermalEnergyGenerator_1_steamElectricEnergyGenerationMultiplier;

    public static boolean thermalEnergyGenerator_2_isAllowed;
    public static double thermalEnergyGenerator_2_thermoelectricEnergyGeneration;
    public static double thermalEnergyGenerator_2_steamElectricWaterConsumptionBase;
    public static double thermalEnergyGenerator_2_steamElectricWaterConsumptionMultiplier;
    public static double thermalEnergyGenerator_2_steamElectricEnergyGenerationBase;
    public static double thermalEnergyGenerator_2_steamElectricEnergyGenerationMultiplier;

    public static boolean thermalEnergyGenerator_3_isAllowed;
    public static double thermalEnergyGenerator_3_thermoelectricEnergyGeneration;
    public static double thermalEnergyGenerator_3_steamElectricWaterConsumptionBase;
    public static double thermalEnergyGenerator_3_steamElectricWaterConsumptionMultiplier;
    public static double thermalEnergyGenerator_3_steamElectricEnergyGenerationBase;
    public static double thermalEnergyGenerator_3_steamElectricEnergyGenerationMultiplier;

    public static boolean thermalEnergyGenerator_4_isAllowed;
    public static double thermalEnergyGenerator_4_thermoelectricEnergyGeneration;
    public static double thermalEnergyGenerator_4_steamElectricWaterConsumptionBase;
    public static double thermalEnergyGenerator_4_steamElectricWaterConsumptionMultiplier;
    public static double thermalEnergyGenerator_4_steamElectricEnergyGenerationBase;
    public static double thermalEnergyGenerator_4_steamElectricEnergyGenerationMultiplier;

    // Kinetic Generators
    public static final boolean[] kineticGenerator_isAllowed = new boolean[TIERS + 1];
    public static final double[] kineticGenerator_energyGenerationBase = new double[TIERS + 1];
    public static final double[] kineticGenerator_energyGenerationMultiplier = new double[TIERS + 1];
    public static final double[] kineticGenerator_movementResistanceBase = new double[TIERS + 1];
    public static final double[] kineticGenerator_movementResistanceMultiplier = new double[TIERS + 1];

    // Solar Generators
    public static boolean solarGeneratorModule_1_IsAllowed;
    public static double solarGeneratorModule_1_energyGenerationDay;
    public static double solarGeneratorModule_1_energyGenerationNight;
    public static double solarGeneratorModule_1_heatGenerationDay;
    public static double solarGeneratorModule_1_heatGenerationNight;

    public static boolean solarGeneratorModule_2_IsAllowed;
    public static double solarGeneratorModule_2_energyGenerationDay;
    public static double solarGeneratorModule_2_energyGenerationNight;
    public static double solarGeneratorModule_2_heatGenerationDay;
    public static double solarGeneratorModule_2_heatGenerationNight;

    public static boolean solarGeneratorModule_3_IsAllowed;
    public static double solarGeneratorModule_3_energyGenerationDay;
    public static double solarGeneratorModule_3_energyGenerationNight;
    public static double solarGeneratorModule_3_heatGenerationDay;
    public static double solarGeneratorModule_3_heatGenerationNight;

    public static boolean solarGeneratorModule_4_IsAllowed;
    public static double solarGeneratorModule_4_energyGenerationDay;
    public static double solarGeneratorModule_4_energyGenerationNight;
    public static double solarGeneratorModule_4_heatGenerationDay;
    public static double solarGeneratorModule_4_heatGenerationNight;

    public static void onLoad(final ModConfigEvent event) {
        if (event.getConfig().getSpec() == MPS_GENERATOR_MODULE_SPEC) {
            for (int tier = 1; tier <= TIERS; tier++) {
                // Combustion Generators ----------------------------------------------------------------------------
                combustionGenerator_isAllowed[tier] = COMBUSTION_IS_ALLOWED[tier].get();
                combustionGenerator_energyPerTick[tier] = COMBUSTION_ENERGY_PER_TICK[tier].get();
                combustionGenerator_heatPerTick[tier] = COMBUSTION_HEAT_PER_TICK[tier].get();
                combustionGenerator_fuelTicksPerTick[tier] = COMBUSTION_FUEL_TICKS_PER_TICK[tier].get();

                // Kinetic Generators -------------------------------------------------------------------------------
                kineticGenerator_isAllowed[tier] = KINETIC_IS_ALLOWED[tier].get();
                kineticGenerator_energyGenerationBase[tier] = KINETIC_ENERGY_GENERATION_BASE[tier].get();
                kineticGenerator_energyGenerationMultiplier[tier] = KINETIC_ENERGY_GENERATION_MULTIPLIER[tier].get();
                kineticGenerator_movementResistanceBase[tier] = KINETIC_MOVEMENT_RESISTANCE_BASE[tier].get();
                kineticGenerator_movementResistanceMultiplier[tier] = KINETIC_MOVEMENT_RESISTANCE_MULTIPLIER[tier].get();
            }

            // Thermal Generators -----------------------------------------------------------------------------------
            thermalEnergyGenerator_1_isAllowed = THERMAL_IS_ALLOWED[1].get();
            thermalEnergyGenerator_1_thermoelectricEnergyGeneration = THERMAL_THERMOELECTRIC_ENERGY_GENERATION[1].get();
            thermalEnergyGenerator_1_steamElectricWaterConsumptionBase = THERMAL_STEAM_ELECTRIC_WATER_CONSUMPTION_BASE[1].get();
            thermalEnergyGenerator_1_steamElectricWaterConsumptionMultiplier = THERMAL_STEAM_ELECTRIC_WATER_CONSUMPTION_MULTIPLIER[1].get();
            thermalEnergyGenerator_1_steamElectricEnergyGenerationBase = THERMAL_STEAM_ELECTRIC_ENERGY_GENERATION_BASE[1].get();
            thermalEnergyGenerator_1_steamElectricEnergyGenerationMultiplier = THERMAL_STEAM_ELECTRIC_ENERGY_GENERATION_MULTIPLIER[1].get();

            thermalEnergyGenerator_2_isAllowed = THERMAL_IS_ALLOWED[2].get();
            thermalEnergyGenerator_2_thermoelectricEnergyGeneration = THERMAL_THERMOELECTRIC_ENERGY_GENERATION[2].get();
            thermalEnergyGenerator_2_steamElectricWaterConsumptionBase = THERMAL_STEAM_ELECTRIC_WATER_CONSUMPTION_BASE[2].get();
            thermalEnergyGenerator_2_steamElectricWaterConsumptionMultiplier = THERMAL_STEAM_ELECTRIC_WATER_CONSUMPTION_MULTIPLIER[2].get();
            thermalEnergyGenerator_2_steamElectricEnergyGenerationBase = THERMAL_STEAM_ELECTRIC_ENERGY_GENERATION_BASE[2].get();
            thermalEnergyGenerator_2_steamElectricEnergyGenerationMultiplier = THERMAL_STEAM_ELECTRIC_ENERGY_GENERATION_MULTIPLIER[2].get();

            thermalEnergyGenerator_3_isAllowed = THERMAL_IS_ALLOWED[3].get();
            thermalEnergyGenerator_3_thermoelectricEnergyGeneration = THERMAL_THERMOELECTRIC_ENERGY_GENERATION[3].get();
            thermalEnergyGenerator_3_steamElectricWaterConsumptionBase = THERMAL_STEAM_ELECTRIC_WATER_CONSUMPTION_BASE[3].get();
            thermalEnergyGenerator_3_steamElectricWaterConsumptionMultiplier = THERMAL_STEAM_ELECTRIC_WATER_CONSUMPTION_MULTIPLIER[3].get();
            thermalEnergyGenerator_3_steamElectricEnergyGenerationBase = THERMAL_STEAM_ELECTRIC_ENERGY_GENERATION_BASE[3].get();
            thermalEnergyGenerator_3_steamElectricEnergyGenerationMultiplier = THERMAL_STEAM_ELECTRIC_ENERGY_GENERATION_MULTIPLIER[3].get();

            thermalEnergyGenerator_4_isAllowed = THERMAL_IS_ALLOWED[4].get();
            thermalEnergyGenerator_4_thermoelectricEnergyGeneration = THERMAL_THERMOELECTRIC_ENERGY_GENERATION[4].get();
            thermalEnergyGenerator_4_steamElectricWaterConsumptionBase = THERMAL_STEAM_ELECTRIC_WATER_CONSUMPTION_BASE[4].get();
            thermalEnergyGenerator_4_steamElectricWaterConsumptionMultiplier = THERMAL_STEAM_ELECTRIC_WATER_CONSUMPTION_MULTIPLIER[4].get();
            thermalEnergyGenerator_4_steamElectricEnergyGenerationBase = THERMAL_STEAM_ELECTRIC_ENERGY_GENERATION_BASE[4].get();
            thermalEnergyGenerator_4_steamElectricEnergyGenerationMultiplier = THERMAL_STEAM_ELECTRIC_ENERGY_GENERATION_MULTIPLIER[4].get();

            // Solar Generators -------------------------------------------------------------------------------------
            solarGeneratorModule_1_IsAllowed = SOLAR_IS_ALLOWED[1].get();
            solarGeneratorModule_1_energyGenerationDay = SOLAR_ENERGY_GENERATION_DAY_BASE[1].get();
            solarGeneratorModule_1_energyGenerationNight = SOLAR_ENERGY_GENERATION_NIGHT_BASE[1].get();
            solarGeneratorModule_1_heatGenerationDay = SOLAR_HEAT_GENERATION_DAY_BASE[1].get();
            solarGeneratorModule_1_heatGenerationNight = SOLAR_HEAT_GENERATION_NIGHT_BASE[1].get();

            solarGeneratorModule_2_IsAllowed = SOLAR_IS_ALLOWED[2].get();
            solarGeneratorModule_2_energyGenerationDay = SOLAR_ENERGY_GENERATION_DAY_BASE[2].get();
            solarGeneratorModule_2_energyGenerationNight = SOLAR_ENERGY_GENERATION_NIGHT_BASE[2].get();
            solarGeneratorModule_2_heatGenerationDay = SOLAR_HEAT_GENERATION_DAY_BASE[2].get();
            solarGeneratorModule_2_heatGenerationNight = SOLAR_HEAT_GENERATION_NIGHT_BASE[2].get();

            solarGeneratorModule_3_IsAllowed = SOLAR_IS_ALLOWED[3].get();
            solarGeneratorModule_3_energyGenerationDay = SOLAR_ENERGY_GENERATION_DAY_BASE[3].get();
            solarGeneratorModule_3_energyGenerationNight = SOLAR_ENERGY_GENERATION_NIGHT_BASE[3].get();
            solarGeneratorModule_3_heatGenerationDay = SOLAR_HEAT_GENERATION_DAY_BASE[3].get();
            solarGeneratorModule_3_heatGenerationNight = SOLAR_HEAT_GENERATION_NIGHT_BASE[3].get();

            solarGeneratorModule_4_IsAllowed = SOLAR_IS_ALLOWED[4].get();
            solarGeneratorModule_4_energyGenerationDay = SOLAR_ENERGY_GENERATION_DAY_BASE[4].get();
            solarGeneratorModule_4_energyGenerationNight = SOLAR_ENERGY_GENERATION_NIGHT_BASE[4].get();
            solarGeneratorModule_4_heatGenerationDay = SOLAR_HEAT_GENERATION_DAY_BASE[4].get();
            solarGeneratorModule_4_heatGenerationNight = SOLAR_HEAT_GENERATION_NIGHT_BASE[4].get();
        }
    }
}
