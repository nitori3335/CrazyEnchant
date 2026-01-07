package org.n_factory.crazyenchant.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.n_factory.crazyenchant.ritual.SummoningGate;


public class SummoningGateRenderer extends EntityRenderer<SummoningGate> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("minecraft", "textures/entity/end_crystal/end_crystal.png");
    private static final float SIN_45 = (float) Math.sin(Math.PI / 4.0);

    private final ModelPart glass;
    private final ModelPart cube;

    public SummoningGateRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
        // VanillaのEndCrystalレイヤーをbake（モデルgeometry自動取得）
        ModelPart root = ctx.bakeLayer(net.minecraft.client.model.geom.ModelLayers.END_CRYSTAL);
        this.glass = root.getChild("glass");
        this.cube = root.getChild("cube");
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull SummoningGate entity) {
        return TEXTURE;  // Vanillaテクスチャ（アニメフレーム付き）
    }

    @Override
    public void render(SummoningGate entity, float entityYaw, float partialTicks,
                       PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();

        // 浮遊アニメ（VanillaのgetY()相当）
        float time = entity.tickCount + partialTicks;
        float f = getY(entity, partialTicks);  // 別メソッドで定義（下記参照）
        float f1 = time * 3.0F;  // 回転速度係数

        VertexConsumer vc = buffer.getBuffer(RenderType.entityCutoutNoCull(getTextureLocation(entity)));

        poseStack.pushPose();
        poseStack.scale(2.0F, 2.0F, 2.0F);
        poseStack.translate(0.0F, -0.5F, 0.0F);

        // baseは浮遊ゲート風に不要 → スキップ（showsBottom()がfalse扱い）

        // glass + cubeの複合回転（Vanillaそのまま）
        poseStack.mulPose(Axis.YP.rotationDegrees(f1));  // 基本Y回転
        poseStack.translate(0.0F, 1.5F + f / 2.0F, 0.0F);

        // 対角軸回転（これが「頂点上向き」キー！）
        poseStack.mulPose(new Quaternionf().setAngleAxis((float)Math.PI / 3F, SIN_45, 0.0F, SIN_45));

        this.glass.render(poseStack, vc, packedLight, OverlayTexture.NO_OVERLAY);

        // 2回目glass（スケールダウンで重ね）
        float f2 = 0.875F;
        poseStack.scale(f2, f2, f2);
        poseStack.mulPose(new Quaternionf().setAngleAxis((float)Math.PI / 3F, SIN_45, 0.0F, SIN_45));
        poseStack.mulPose(Axis.YP.rotationDegrees(f1));
        this.glass.render(poseStack, vc, packedLight, OverlayTexture.NO_OVERLAY);

        // 3回目cube（さらにスケールダウン + 回転）
        poseStack.scale(f2, f2, f2);
        poseStack.mulPose(new Quaternionf().setAngleAxis((float)Math.PI / 3F, SIN_45, 0.0F, SIN_45));
        poseStack.mulPose(Axis.YP.rotationDegrees(f1));
        this.cube.render(poseStack, vc, packedLight, OverlayTexture.NO_OVERLAY);

        poseStack.popPose();
        poseStack.popPose();

        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    private static float getY(SummoningGate entity, float partialTicks) {
        float f = (float) entity.tickCount + partialTicks;
        float f1 = Mth.sin(f * 0.2F) / 2.0F + 0.5F;
        f1 = (f1 * f1 + f1) * 0.4F;
        return f1 - 1.4F;
    }
}