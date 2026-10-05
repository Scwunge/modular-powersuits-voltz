package lehjr.powersuits.client.compat.jei;

import lehjr.numina.common.registration.NuminaItems;
import lehjr.powersuits.common.constants.MPSConstants;
import lehjr.powersuits.common.registration.MPSItems;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * JEI integration. JEI already lists every Numina/Powersuits crafting recipe, since they all extend vanilla shaped or
 * shapeless recipes, so this only adds information pages for the items that need an explanation.
 * <p>
 * JEI only loads this class when JEI is installed, so the API dependency stays compile-only.
 */
@JeiPlugin
public class MPSJeiPlugin implements IModPlugin {
    private static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(MPSConstants.MOD_ID, "jei");

    @Override
    public ResourceLocation getPluginUid() {
        return UID;
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addItemStackInfo(new ItemStack(MPSItems.TINKER_TABLE_ITEM.get()),
            Component.translatable("jei." + MPSConstants.MOD_ID + ".info.tinker_table"));

        registration.addItemStackInfo(new ItemStack(NuminaItems.CHARGING_BASE_ITEM.get()),
            Component.translatable("jei." + MPSConstants.MOD_ID + ".info.charging_base"));

        registration.addItemStackInfo(List.of(
                new ItemStack(MPSItems.POWER_ARMOR_HELMET_1.get()),
                new ItemStack(MPSItems.POWER_ARMOR_CHESTPLATE_1.get()),
                new ItemStack(MPSItems.POWER_ARMOR_LEGGINGS_1.get()),
                new ItemStack(MPSItems.POWER_ARMOR_BOOTS_1.get())),
            Component.translatable("jei." + MPSConstants.MOD_ID + ".info.power_armor"));

        registration.addItemStackInfo(new ItemStack(MPSItems.POWER_FIST_1.get()),
            Component.translatable("jei." + MPSConstants.MOD_ID + ".info.power_fist"));
    }
}
