package org.n_factory.crazyenchant.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.n_factory.crazyenchant.ritual.SummoningGate;

public class SummoningGateRenderer extends EntityRenderer<SummoningGate> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("minecraft", "textures/entity/end_crystal/end_crystal.png");  // End Crystalテクスチャ借用

    public SummoningGateRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull SummoningGate entity) {
        return TEXTURE;
    }

    @Override
    public void render(@NotNull SummoningGate entity, float entityYaw, float partialTicks, @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight) {
        // End Crystalの描画をそのまま再現（浮遊感 + 底表示）
        // 実際はEndCrystalRendererを参考にコピー
        // 簡易版としてデフォルトの透明立方体を紫粒子で演出
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }
}