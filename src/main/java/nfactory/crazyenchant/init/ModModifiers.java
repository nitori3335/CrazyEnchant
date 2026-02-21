package nfactory.crazyenchant.init;

import com.mojang.serialization.Codec;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import nfactory.crazyenchant.CrazyEnchant;
import nfactory.crazyenchant.data.loot.modifier.WitchHunterLootModifier;

@SuppressWarnings("unused")
public class ModModifiers {
    public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> MODIFIERS =
            DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, CrazyEnchant.MODID);

    public static final RegistryObject<Codec<? extends IGlobalLootModifier>> WITCHHUNTER =
            MODIFIERS.register("witch_hunter_loot", () -> WitchHunterLootModifier.CODEC);
}
