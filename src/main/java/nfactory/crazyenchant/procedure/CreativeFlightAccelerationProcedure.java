package nfactory.crazyenchant.procedure;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import nfactory.crazyenchant.init.ModEnchantments;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class CreativeFlightAccelerationProcedure {

    private static final float DEFAULT_SPEED = 0.05F;
    private static boolean boosted = false;

    @SubscribeEvent
    public static void onPlayerTickEvent(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        Player player = event.player;

        if (!(player instanceof LocalPlayer)) return;
        if (!player.getAbilities().mayfly) return;
        if (!player.getAbilities().flying) return;

        ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
        int level = EnchantmentHelper.getTagEnchantmentLevel(ModEnchantments.SWIFTFLIGHT.get(), chest);

        if (level > 0 && player.getAbilities().flying) {
            float t = (float)level / ModEnchantments.SWIFTFLIGHT.get().getMaxLevel();
            float multiplier = 1.0F + (9.0F * t * t);
            float target = DEFAULT_SPEED * multiplier;
            float current = player.getAbilities().getFlyingSpeed();

            if (current < target) {
                player.getAbilities().setFlyingSpeed(target);
            }
            boosted = true;
        } else if (boosted) {
            player.getAbilities().setFlyingSpeed(DEFAULT_SPEED);
            boosted = false;
        }
    }
}
