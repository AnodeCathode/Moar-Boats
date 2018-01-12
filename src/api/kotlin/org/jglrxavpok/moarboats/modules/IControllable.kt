package org.jglrxavpok.moarboats.modules

import net.minecraft.inventory.IInventory
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.world.World

interface IControllable {

    val entityID: Int
    val modules: List<BoatModule>
    val worldRef: World
    val rngSeed: Long
    val positionX: Double
    val positionY: Double
    val positionZ: Double
    val velocityX: Double
    val velocityY: Double
    val velocityZ: Double
    val yaw: Float

    fun turnRight(multiplier: Float = 1f)

    fun turnLeft(multiplier: Float = 1f)
    fun accelerate(multiplier: Float = 1f)
    fun decelerate(multiplier: Float = 1f)

    fun saveState(module: BoatModule)
    fun getState(module: BoatModule): NBTTagCompound
    fun getInventory(module: BoatModule): IBoatModuleInventory
}