package org.jglrxavpok.moarboats.client.gui

import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.GuiButtonImage
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraft.util.text.TextComponentTranslation
import net.minecraftforge.fml.client.config.GuiSlider
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.api.IControllable
import org.jglrxavpok.moarboats.common.containers.ContainerDispenserModule
import org.jglrxavpok.moarboats.common.modules.DispensingModule
import org.jglrxavpok.moarboats.common.network.CChangeDispenserFacing
import org.jglrxavpok.moarboats.common.network.CChangeDispenserPeriod

class GuiDispenserModule(inventoryPlayer: InventoryPlayer, module: BoatModule, boat: IControllable): GuiModuleBase(module, boat, inventoryPlayer, ContainerDispenserModule(inventoryPlayer, module, boat), isLarge = true) {
    override val moduleBackground = ResourceLocation(MoarBoats.ModID, "textures/gui/modules/dispenser.png")

    private val dispensingModule = module as DispensingModule
    private val sliderPrefix = TextComponentTranslation("gui.dispenser.period.prefix")
    private val sliderSuffix = TextComponentTranslation("gui.dispenser.period.suffix")
    private val topRowText = TextComponentTranslation("gui.dispenser.top_row")
    private val middleRowText = TextComponentTranslation("gui.dispenser.middle_row")
    private val bottomRowText = TextComponentTranslation("gui.dispenser.bottom_row")
    private val periodText = TextComponentTranslation("gui.dispenser.period")
    private val orientationText = TextComponentTranslation("gui.dispenser.orientation")
    private val facings = arrayOf(EnumFacing.NORTH, EnumFacing.SOUTH, EnumFacing.EAST, EnumFacing.WEST, EnumFacing.UP, EnumFacing.DOWN)
    private val facingSelectionTexLocation = ResourceLocation(MoarBoats.ModID, "textures/gui/modules/dispenser_facings.png")

    private inner class GuiFacingButton(val facing: EnumFacing, textureOffsetX: Int, textureOffsetY: Int):
            GuiButtonImage(facing.ordinal, 0, 0, 16, 16, textureOffsetX, textureOffsetY, 32, facingSelectionTexLocation) {
        override fun onClick(mouseX: Double, mouseY: Double) {
            MoarBoats.network.sendToServer(CChangeDispenserFacing(boat.entityID, module.id, facing))
        }
    }

    private val frontFacingButton = GuiFacingButton(EnumFacing.NORTH, 0, 0)
    private val backFacingButton = GuiFacingButton(EnumFacing.SOUTH, 16, 0)
    private val leftFacingButton = GuiFacingButton(EnumFacing.EAST, 48, 0)
    private val rightFacingButton = GuiFacingButton(EnumFacing.WEST, 32, 0)
    private val upFacingButton = GuiFacingButton(EnumFacing.UP, 0, 16)
    private val downFacingButton = GuiFacingButton(EnumFacing.DOWN, 16, 16)
    private val facingButtons = arrayOf(frontFacingButton, backFacingButton, leftFacingButton, rightFacingButton, upFacingButton, downFacingButton)

    private lateinit var periodSlider: GuiSlider
    private val sliderCallback = GuiSlider.ISlider { slider ->
        MoarBoats.network.sendToServer(CChangeDispenserPeriod(boat.entityID, module.id, slider.value))
    }

    override fun initGui() {
        super.initGui()
        val sliderWidth = xSize-10
        periodSlider = GuiSlider(-1, guiLeft+xSize/2-sliderWidth/2, guiTop + 100, sliderWidth, 20, "${sliderPrefix.formattedText} ", sliderSuffix.formattedText, 1.0, 100.0, 0.0, true, true, sliderCallback)
        periodSlider.value = dispensingModule.blockPeriodProperty[boat]
        addButton(periodSlider)

        val yStart = guiTop + 35
        frontFacingButton.x = guiLeft+16+4
        frontFacingButton.y = yStart

        backFacingButton.x = guiLeft+16+4
        backFacingButton.y = yStart+32

        leftFacingButton.x = guiLeft+4
        leftFacingButton.y = yStart+16

        rightFacingButton.x = guiLeft+4+32
        rightFacingButton.y = yStart+16

        upFacingButton.x = guiLeft+4
        upFacingButton.y = yStart

        downFacingButton.x = guiLeft+4+32
        downFacingButton.y = yStart
        facingButtons.forEach {

            addButton(it)
        }
    }

    override fun tick() {
        super.tick()
        periodSlider.updateSlider()
    }

    override fun drawModuleForeground(mouseX: Int, mouseY: Int) {
        val maxX = 78
        val startY = 26
        val topWidth = fontRenderer.getStringWidth(topRowText.formattedText)
        drawString(fontRenderer, topRowText.formattedText, maxX - topWidth, startY, 0xF0F0F0)

        val middleWidth = fontRenderer.getStringWidth(middleRowText.formattedText)
        drawString(fontRenderer, middleRowText.formattedText, maxX - middleWidth, startY + 20, 0xF0F0F0)

        val bottomWidth = fontRenderer.getStringWidth(bottomRowText.formattedText)
        drawString(fontRenderer, bottomRowText.formattedText, maxX - bottomWidth, startY + 40, 0xF0F0F0)

        drawCenteredString(fontRenderer, periodText.formattedText, 88, 90, 0xF0F0F0)

        drawCenteredString(fontRenderer, orientationText.formattedText, 32, 25, 0xF0F0F0)
    }

}