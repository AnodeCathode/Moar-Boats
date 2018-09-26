package org.jglrxavpok.moarboats.common.network

import io.netty.buffer.ByteBuf
import net.minecraft.client.Minecraft
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.common.network.ByteBufUtils
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext
import net.minecraftforge.fml.relauncher.Side
import org.jglrxavpok.moarboats.api.BoatModuleRegistry
import org.jglrxavpok.moarboats.client.gui.GuiFluid
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity

class S19UpdateFluidGui(): IMessage {

    var fluidAmount = 0
    var fluidCapacity = 0
    var fluidName = ""

    constructor(fluidName: String, fluidAmount: Int, fluidCapacity: Int): this() {
        this.fluidName = fluidName
        this.fluidAmount = fluidAmount
        this.fluidCapacity = fluidCapacity
    }

    override fun fromBytes(buf: ByteBuf) {
        fluidAmount = buf.readInt()
        fluidCapacity = buf.readInt()
        fluidName = ByteBufUtils.readUTF8String(buf)
    }

    override fun toBytes(buf: ByteBuf) {
        buf.writeInt(fluidAmount)
        buf.writeInt(fluidCapacity)
        ByteBufUtils.writeUTF8String(buf, fluidName)
    }

    object Handler: MBMessageHandler<S19UpdateFluidGui, IMessage> {
        override val packetClass = S19UpdateFluidGui::class
        override val receiverSide = Side.CLIENT

        override fun onMessage(message: S19UpdateFluidGui, ctx: MessageContext): IMessage? {
            val screen = Minecraft.getMinecraft().currentScreen
            if(screen is GuiFluid) {
                screen.updateFluid(message.fluidName, message.fluidAmount, message.fluidCapacity)
            }
            return null
        }
    }
}