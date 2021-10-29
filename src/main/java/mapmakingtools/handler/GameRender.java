package mapmakingtools.handler;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import mapmakingtools.MapMakingTools;
import mapmakingtools.api.util.FeatureAvailability;
import mapmakingtools.client.ClientSelection;
import mapmakingtools.item.WrenchItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ResourceLoadProgressGui;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import org.lwjgl.opengl.GL11;

public class GameRender {

    public static void initListeners() {
        IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;
        forgeEventBus.addListener(GameRender::onPreRenderGameOverlay);
        forgeEventBus.addListener(GameRender::onWorldRenderLast);
    }

    public static void onPreRenderGameOverlay(RenderGameOverlayEvent.Pre e) {
        Minecraft mc = Minecraft.getInstance();
        ClientPlayerEntity player = mc.player;
        ItemStack stack = player.getMainHandItem();
        if (e.getType() == ElementType.TEXT && FeatureAvailability.canEdit(mc.player) && stack.getItem() == MapMakingTools.WRENCH && WrenchItem.getMode(stack) == WrenchItem.Mode.QUICK_BUILD) {

            if (mc.overlay instanceof ResourceLoadProgressGui) {
                return;
            }

            FontRenderer font = Minecraft.getInstance().font;
            GlStateManager._pushMatrix();
//            GlStateManager.disableRescaleNormal();
//            RenderHelper.disableStandardItemLighting();
//            GlStateManager.disableDepthTest();

            if (ClientSelection.SELECTION.isSet()) {
                int[] dimensions = ClientSelection.SELECTION.getDimensions();
                font.drawShadow(e.getMatrixStack(), new TranslationTextComponent("world_editor.mapmakingtools.selection.describe", dimensions[0], dimensions[1], dimensions[2], dimensions[0] * dimensions[1] * dimensions[2]), 4, 4, -1);
            }
            else {
                font.drawShadow(e.getMatrixStack(), new TranslationTextComponent("world_editor.mapmakingtools.selection.none"), 4, 4, -1);
            }

            if (ClientSelection.LAST_COMMAND != null) {
                font.drawShadow(e.getMatrixStack(), new StringTextComponent(ClientSelection.LAST_COMMAND), 4, 15, -1);
            }

            RenderSystem.popMatrix();

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

    public static void onWorldRenderLast(final RenderWorldLastEvent event) {
        ClientPlayerEntity player = Minecraft.getInstance().player;
        ItemStack stack = player.getMainHandItem();
        if (ClientSelection.SELECTION.anySet() && stack.getItem() == MapMakingTools.WRENCH && WrenchItem.getMode(stack) == WrenchItem.Mode.QUICK_BUILD) {
            drawSelectionBox(event.getMatrixStack(), ClientSelection.SELECTION.getPrimaryBB(), ClientSelection.SELECTION.getSecondaryBB());
        }
    }

    public static void drawSelectionBox(MatrixStack stack, AxisAlignedBB boundingBox1, AxisAlignedBB boundingBox2) {
        RenderSystem.disableAlphaTest();
        RenderSystem.disableLighting(); // Full bright
        RenderSystem.depthMask(false);
        RenderSystem.disableDepthTest(); // Visible through blocks
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

        RenderSystem.lineWidth(2.0F);

        RenderSystem.disableTexture();
        Vector3d vec3d = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        double d0 = vec3d.x();
        double d1 = vec3d.y();
        double d2 = vec3d.z();

        BufferBuilder buf = Tessellator.getInstance().getBuilder();
        buf.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
        if (boundingBox1 != null && boundingBox2 != null) {
            WorldRenderer.renderLineBox(stack, buf, boundingBox1.minmax(boundingBox2).move(-d0, -d1, -d2), 1F, 1F, 1F, 1F);
        }
        if (boundingBox1 != null) {
            WorldRenderer.renderLineBox(stack, buf, boundingBox1.move(-d0, -d1, -d2), 1F, 1F, 0F, 0.8F);
        }
        if (boundingBox2 != null) {
            WorldRenderer.renderLineBox(stack, buf, boundingBox2.move(-d0, -d1, -d2), 0F, 1F, 1F, 0.8F);
        }
        Tessellator.getInstance().end();
        RenderSystem.color4f(0.0F, 0.0F, 0.0F, 0.3F);
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(true);
        RenderSystem.enableTexture();
        RenderSystem.enableTexture();
        RenderSystem.enableLighting();
        RenderSystem.disableBlend();
        RenderSystem.enableAlphaTest();
    }
}
