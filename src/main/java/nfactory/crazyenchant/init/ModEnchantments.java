package nfactory.crazyenchant.init;

import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import nfactory.crazyenchant.CrazyEnchant;
import nfactory.crazyenchant.enchantment.FishHaterEnchantment;

public class ModEnchantments {
    public static final DeferredRegister<Enchantment> ENCHANTMENT = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, CrazyEnchant.MODID);

    public static final RegistryObject<Enchantment> FISHHATER = ENCHANTMENT.register("fish_hater", FishHaterEnchantment::new);
}
