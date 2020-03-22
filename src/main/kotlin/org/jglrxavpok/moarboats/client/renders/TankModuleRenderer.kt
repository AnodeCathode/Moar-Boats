package org.jglrxavpok.moarboats.client.renders

import com.mojang.blaze3d.matrix.MatrixStack
import net.minecraft.client.Minecraft
import com.mojang.blaze3d.platform.GlStateManager
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.renderer.Atlases
import net.minecraft.client.renderer.IRenderTypeBuffer
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.entity.EntityRendererManager
import net.minecraft.client.renderer.texture.AtlasTexture
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.client.pos
import org.jglrxavpok.moarboats.common.blocks.BlockBoatTank
import org.jglrxavpok.moarboats.common.modules.FluidTankModule
import org.lwjgl.opengl.GL11

object TankModuleRenderer : BoatModuleRenderer() {

    init {
        registryName = FluidTankModule.id
    }

    override fun renderModule(boat: ModularBoatEntity, module: BoatModule, matrixStack: MatrixStack, buffers: IRenderTypeBuffer, packedLightIn: Int, partialTicks: Float, entityYaw: Float, entityRendererManager: EntityRendererManager) {
        module as FluidTankModule
        matrixStack.push()
        matrixStack.scale(0.75f, 0.75f, 0.75f)
        matrixStack.translate(-0.15, -4f/16.0, 0.5)
        val block = BlockBoatTank
        renderBlockState(matrixStack, buffers, packedLightIn, entityRendererManager, block.defaultState, boat.brightness)
        val fluid = module.getFluidInside(boat)
        if(fluid != null && module.getFluidAmount(boat) > 0) {
            val scale = 1f/16f
            matrixStack.scale(scale, scale, scale)
            val sprite = Minecraft.getInstance().getAtlasSpriteGetter(AtlasTexture.LOCATION_BLOCKS_TEXTURE).apply(fluid.attributes.stillTexture)
            val buffer = buffers.getBuffer(Atlases.getTranslucentBlockType())
            val minU = sprite.minU
            val maxU = sprite.maxU
            val minV = sprite.minV
            val maxV = sprite.maxV
            buffer.pos(matrixStack,1.0, 1.01, 1.0).tex(minU, minV).color(1f, 1f, 1f, 1f).normal(0f, 1f, 0f).endVertex()
            buffer.pos(matrixStack,15.0, 1.01, 1.0).tex(maxU, minV).color(1f, 1f, 1f, 1f).normal(0f, 1f, 0f).endVertex()
            buffer.pos(matrixStack,15.0, 1.01, 15.0).tex(maxU, maxV).color(1f, 1f, 1f, 1f).normal(0f, 1f, 0f).endVertex()
            buffer.pos(matrixStack,1.0, 1.01, 15.0).tex(minU, maxV).color(1f, 1f, 1f, 1f).normal(0f, 1f, 0f).endVertex()

            val fillAmount = module.getFluidAmount(boat) / module.getCapacity(boat).toFloat()
            val height = 15.0* fillAmount
            buffer.pos(matrixStack,1.0, height, 1.0).tex(minU, minV).color(1f, 1f, 1f, 1f).normal(0f, -1f, 0f).endVertex()
            buffer.pos(matrixStack,15.0, height, 1.0).tex(maxU, minV).color(1f, 1f, 1f, 1f).normal(0f, -1f, 0f).endVertex()
            buffer.pos(matrixStack,15.0, height, 15.0).tex(maxU, maxV).color(1f, 1f, 1f, 1f).normal(0f, -1f, 0f).endVertex()
            buffer.pos(matrixStack,1.0, height, 15.0).tex(minU, maxV).color(1f, 1f, 1f, 1f).normal(0f, -1f, 0f).endVertex()

            val bottomV = maxV * fillAmount + (1.0f-fillAmount)*minV
            val topV = minV
            buffer.pos(matrixStack,1.0, height, 1.0).tex(minU, topV).color(1f, 1f, 1f, 1f).normal(0f, 0f, 1f).endVertex()
            buffer.pos(matrixStack,15.0, height, 1.0).tex(maxU, topV).color(1f, 1f, 1f, 1f).normal(0f, 0f, 1f).endVertex()
            buffer.pos(matrixStack,15.0, 1.01, 1.0).tex(maxU, bottomV).color(1f, 1f, 1f, 1f).normal(0f, 0f, 1f).endVertex()
            buffer.pos(matrixStack,1.0, 1.01, 1.0).tex(minU, bottomV).color(1f, 1f, 1f, 1f).normal(0f, 0f, 1f).endVertex()

            buffer.pos(matrixStack,1.0, 1.01, 15.0).tex(minU, bottomV).color(1f, 1f, 1f, 1f).normal(0f, 0f, -1f).endVertex()
            buffer.pos(matrixStack,15.0, 1.01, 15.0).tex(maxU, bottomV).color(1f, 1f, 1f, 1f).normal(0f, 0f, -1f).endVertex()
            buffer.pos(matrixStack,15.0, height, 15.0).tex(maxU, topV).color(1f, 1f, 1f, 1f).normal(0f, 0f, -1f).endVertex()
            buffer.pos(matrixStack,1.0, height, 15.0).tex(minU, topV).color(1f, 1f, 1f, 1f).normal(0f, 0f, -1f).endVertex()

            buffer.pos(matrixStack,1.0, 1.01, 1.0).tex(minU, bottomV).color(1f, 1f, 1f, 1f).normal(1f, 0f, 0f).endVertex()
            buffer.pos(matrixStack,1.0, 1.01, 15.0).tex(maxU, bottomV).color(1f, 1f, 1f, 1f).normal(1f, 0f, 0f).endVertex()
            buffer.pos(matrixStack,1.0, height, 15.0).tex(maxU, topV).color(1f, 1f, 1f, 1f).normal(1f, 0f, 0f).endVertex()
            buffer.pos(matrixStack,1.0, height, 1.0).tex(minU, topV).color(1f, 1f, 1f, 1f).normal(1f, 0f, 0f).endVertex()

            buffer.pos(matrixStack,15.0, 1.01, 1.0).tex(minU, bottomV).color(1f, 1f, 1f, 1f).normal(-1f, 0f, 0f).endVertex()
            buffer.pos(matrixStack,15.0, 1.01, 15.0).tex(maxU, bottomV).color(1f, 1f, 1f, 1f).normal(-1f, 0f, 0f).endVertex()
            buffer.pos(matrixStack,15.0, height, 15.0).tex(maxU, topV).color(1f, 1f, 1f, 1f).normal(-1f, 0f, 0f).endVertex()
            buffer.pos(matrixStack,15.0, height, 1.0).tex(minU, topV).color(1f, 1f, 1f, 1f).normal(-1f, 0f, 0f).endVertex()
        }
        matrixStack.pop()
    }
}