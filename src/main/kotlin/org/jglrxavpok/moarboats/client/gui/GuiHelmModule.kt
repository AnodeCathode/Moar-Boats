package org.jglrxavpok.moarboats.client.gui

import com.mojang.blaze3d.matrix.MatrixStack
import com.mojang.blaze3d.platform.GlStateManager
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.widget.button.Button
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.FilledMapItem
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.util.ResourceLocation
import net.minecraft.util.text.TranslationTextComponent
import net.minecraft.world.storage.MapData
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.api.IControllable
import org.jglrxavpok.moarboats.client.RenderInfo
import org.jglrxavpok.moarboats.client.renders.HelmModuleRenderer
import org.jglrxavpok.moarboats.common.containers.ContainerHelmModule
import org.jglrxavpok.moarboats.common.data.LoopingOptions
import org.jglrxavpok.moarboats.common.items.ItemGoldenTicket
import org.jglrxavpok.moarboats.common.items.ItemPath
import org.jglrxavpok.moarboats.common.items.MapItemWithPath
import org.jglrxavpok.moarboats.common.modules.HelmModule
import org.jglrxavpok.moarboats.common.network.CChangeEngineMode
import org.jglrxavpok.moarboats.common.network.CSaveItineraryToMap
import org.jglrxavpok.moarboats.common.state.EmptyMapData
import org.lwjgl.opengl.GL11

class GuiHelmModule(containerID: Int, playerInventory: PlayerInventory, engine: BoatModule, boat: IControllable):
        GuiModuleBase<ContainerHelmModule>(engine, boat, playerInventory, ContainerHelmModule(containerID, playerInventory, engine, boat), isLarge = true) {

    override val moduleBackground = ResourceLocation(MoarBoats.ModID, "textures/gui/modules/helm.png")

    private val RES_MAP_BACKGROUND = ResourceLocation("textures/map/map_background.png")
    private val margins = 7.0
    private val mapSize = 100.0
    private val mapStack = ItemStack(Items.FILLED_MAP)
    private val editButtonText = TranslationTextComponent("gui.helm.path_editor")
    private val saveButtonText = TranslationTextComponent("moarboats.gui.helm.save_on_map")
    private val mapEditButton = Button(0, 0, 150, 20, editButtonText.formattedText) {
        val mapData = getMapData(baseContainer.getSlot(0).stack)
        if(mapData != null && mapData != EmptyMapData) {
            boat.modules.firstOrNull() { it.moduleSpot == BoatModule.Spot.Engine }?.let {
                MoarBoats.network.sendToServer(CChangeEngineMode(boat.entityID, it.id, true))
            }
            mc.displayGuiScreen(HelmModule.createPathEditorGui(playerInventory.player, boat, mapData))
        }
    }
    private val saveButton = Button(0, 0, 150, 20, saveButtonText.formattedText) {
        val mapData = getMapData(baseContainer.getSlot(0).stack)
        if(mapData != null && mapData != EmptyMapData && baseContainer.getSlot(0).stack.item == Items.FILLED_MAP) {
            MoarBoats.network.sendToServer(CSaveItineraryToMap(boat.entityID, HelmModule.id))
        }
    }

    init {
        shouldRenderInventoryName = false
    }

    override fun init() {
        super.init()
        mapEditButton.width = (xSize * .75).toInt() /2
        mapEditButton.x = guiLeft + xSize/2 - mapEditButton.width
        mapEditButton.y = guiTop + (mapSize + 7).toInt()
        addButton(mapEditButton)

        saveButton.width = (xSize*.75).toInt() /2
        saveButton.x = guiLeft + xSize/2
        saveButton.y = guiTop + (mapSize + 7).toInt()
        addButton(saveButton)
    }

    override fun drawModuleBackground(mouseX: Int, mouseY: Int) {
        super.drawModuleBackground(mouseX, mouseY)
        this.mc.textureManager.bindTexture(RES_MAP_BACKGROUND)
        val tessellator = Tessellator.getInstance()
        val bufferbuilder = tessellator.buffer
        val x = guiLeft + xSize/2f - mapSize/2
        val y = guiTop.toDouble() + 5.0
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR_TEX_LIGHTMAP)
        bufferbuilder.pos(x, y+mapSize, 0.0).color(1f, 1f, 1f, 1f).tex(0.0f, 1.0f).lightmap(15728880).endVertex()
        bufferbuilder.pos(x+mapSize, y+mapSize, 0.0).color(1f, 1f, 1f, 1f).tex(1.0f, 1.0f).lightmap(15728880).endVertex()
        bufferbuilder.pos(x+mapSize, y, 0.0).color(1f, 1f, 1f, 1f).tex(1.0f, 0.0f).lightmap(15728880).endVertex()
        bufferbuilder.pos(x, y, 0.0).color(1f, 1f, 1f, 1f).tex(0.0f, 0.0f).lightmap(15728880).endVertex()
        tessellator.draw()
        val stack = baseContainer.getSlot(0).stack
        var hasMap = false
        val buffers = mc.renderTypeBuffers.bufferSource
        getMapData(stack)?.let { mapdata ->
            val item = stack.item
            val (waypoints, loopingOption) = when(item) {
                net.minecraft.item.Items.FILLED_MAP -> Pair(HelmModule.waypointsProperty[boat], HelmModule.loopingProperty[boat])
                is ItemPath -> Pair(item.getWaypointData(stack, MoarBoats.getLocalMapStorage()), item.getLoopingOptions(stack))
                else -> return@let
            }
            val renderInfo = RenderInfo(matrixStack, mc.renderTypeBuffers.bufferSource)
            HelmModuleRenderer.renderMap(boat, renderInfo, mapdata, x, y, mapSize, boat.positionX, boat.positionZ, margins, waypoints, loopingOption == LoopingOptions.Loops)

            if(mouseX >= x+margins && mouseX <= x+mapSize-margins && mouseY >= y+margins && mouseY <= y+mapSize-margins) {
                val waypointBuffer = buffers.getBuffer(HelmModuleRenderer.waypointRenderType)
                HelmModuleRenderer.renderSingleWaypoint(renderInfo, waypointBuffer, mouseX.toDouble(), mouseY.toDouble()-6.0)
            }

            hasMap = true
        }
        buffers.finish() // render to screen

        if(!hasMap) {
            RenderSystem.pushMatrix()
            RenderSystem.translatef(guiLeft.toFloat()+8f, guiTop.toFloat()+8f, 0f)
            Screen.fill(0, 0, 16, 16, 0x30ff0000)
            mc.itemRenderer.renderItemIntoGUI(mapStack, 0, 0)
            RenderSystem.depthFunc(GL11.GL_GREATER)
            Screen.fill(0, 0, 16, 16, 0x30ffffff)
            RenderSystem.depthFunc(GL11.GL_LEQUAL)
            RenderSystem.popMatrix()
        }
    }

    private fun getMapData(stack: ItemStack): MapData? {
        return HelmModule.getMapData(stack, boat)
    }

    override fun tick() {
        super.tick()
        val mapData = getMapData(baseContainer.getSlot(0).stack)
        mapEditButton.active = mapData != null && mapData != EmptyMapData
        saveButton.active = mapData != null && mapData != EmptyMapData && baseContainer.getSlot(0).stack.item == Items.FILLED_MAP
    }

}