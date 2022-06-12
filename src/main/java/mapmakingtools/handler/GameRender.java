package mapmakingtools.handler;

import com.mojang.blaze3d.vertex.*;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import mapmakingtools.MapMakingTools;
import mapmakingtools.api.util.FeatureAvailability;
import mapmakingtools.client.ClientSelection;
import mapmakingtools.item.WrenchItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.LoadingOverlay;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderLevelLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;

public class GameRender {

    public static void initListeners() {
        IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;
        forgeEventBus.addListener(GameRender::onPreRenderGameOverlay);
        forgeEventBus.addListener(GameRender::onWorldRenderLast);
    }

    public static void onPreRenderGameOverlay(RenderGameOverlayEvent.Pre e) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        ItemStack stack = player.getMainHandItem();
        if (e.getType() == ElementType.TEXT && FeatureAvailability.canEdit(mc.player) && stack.is(MapMakingTools.WRENCH.get()) && WrenchItem.getMode(stack) == WrenchItem.Mode.QUICK_BUILD) {

            if (mc.getOverlay() instanceof LoadingOverlay) {
                return;
            }

            PoseStack poseStack = e.getPoseStack();

            Font font = Minecraft.getInstance().font;
            poseStack.pushPose();
            // TODO
//            GlStateManager.disableRescaleNormal();
//            RenderHelper.disableStandardItemLighting();
//            GlStateManager.disableDepthTest();

            if (ClientSelection.SELECTION.isSet()) {
                int[] dimensions = ClientSelection.SELECTION.getDimensions();
                font.drawShadow(poseStack, Component.translatable("world_editor.mapmakingtools.selection.describe", dimensions[0], dimensions[1], dimensions[2], dimensions[0] * dimensions[1] * dimensions[2]), 4, 4, -1);
            }
            else {
                font.drawShadow(poseStack, Component.translatable("world_editor.mapmakingtools.selection.none"), 4, 4, -1);
            }

            if (ClientSelection.LAST_COMMAND != null) {
                font.drawShadow(poseStack, Component.literal(ClientSelection.LAST_COMMAND), 4, 15, -1);
            }

            poseStack.popPose();

//            ItemStack fillerBlock = new ItemStack(Blocks.PUMPKIN);
////            GlStateManager.enableDepthTest();
////            RenderHelper.enableStandardItemLighting();
////            GlStateManager.enableRescaleNormal();
//            GlStateManager.popMatrix();
//            //MapMakingTools.LOGGER.info(Arrays.toString(ClientSelection.SELECTION));
//
//            RenderSystem.pushMatrix();
//            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
//            RenderSystem.enableRescaleNormal();
//            RenderSystem.glMultiTexCoord2f(33986, 240.0F, 240.0F);
//            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
//            RenderSystem.scalef(3F, 3F, 3F);
//            ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
//            itemRenderer.zLevel = 100.0F;
//
//            RenderSystem.enableDepthTest();
//            itemRenderer.renderItemAndEffectIntoGUI(player, fillerBlock, 2, 6);
//            itemRenderer.renderItemOverlayIntoGUI(font, fillerBlock, 2, 6, null);
//
//            itemRenderer.zLevel = 0.0F;
//
//            RenderSystem.popMatrix();
//            RenderSystem.enableDepthTest();
        }
    }

    public static void onWorldRenderLast(final RenderLevelLastEvent event) {
        LocalPlayer player = Minecraft.getInstance().player;
        ItemStack stack = player.getMainHandItem();
        if (ClientSelection.SELECTION.anySet() && stack.is(MapMakingTools.WRENCH.get()) && WrenchItem.getMode(stack) == WrenchItem.Mode.QUICK_BUILD) {
            drawSelectionBox(event.getPoseStack(), ClientSelection.SELECTION.getPrimaryBB(), ClientSelection.SELECTION.getSecondaryBB());
        }
    }

    public static void drawSelectionBox(PoseStack stack, AABB boundingBox1, AABB boundingBox2) {
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        // TODO
//        RenderSystem.disableAlphaTest();
//        RenderSystem.disableLighting(); // Full bright
        RenderSystem.depthMask(false);
        RenderSystem.disableDepthTest(); // Visible through blocks
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

        RenderSystem.disableTexture();
        Vec3 vec3d = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        double d0 = vec3d.x();
        double d1 = vec3d.y();
        double d2 = vec3d.z();
        stack.pushPose();
        stack.translate(-d0, -d1, -d2);

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buf = tesselator.getBuilder();
        // TODO Has no effect RenderSystem.lineWidth(2.0F);
        buf.begin(VertexFormat.Mode.DEBUG_LINES, DefaultVertexFormat.POSITION_COLOR);

        if (boundingBox1 != null && boundingBox2 != null) {
            LevelRenderer.renderLineBox(stack, buf, boundingBox1.minmax(boundingBox2), 1F, 1F, 1F, 1F);
        }
        if (boundingBox1 != null) {
            LevelRenderer.renderLineBox(stack, buf, boundingBox1, 1F, 1F, 0F, 0.8F);
        }
        if (boundingBox2 != null) {
            LevelRenderer.renderLineBox(stack, buf, boundingBox2, 0F, 1F, 1F, 0.8F);
        }
        tesselator.end();
        RenderSystem.lineWidth(1.0F);
        RenderSystem.setShaderColor(0.0F, 0.0F, 0.0F, 0.3F);
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(true);
        RenderSystem.enableTexture();
        RenderSystem.enableTexture();
//        RenderSystem.enableLighting();
        RenderSystem.disableBlend();
//        RenderSystem.enableAlphaTest();
        stack.popPose();
    }
}
