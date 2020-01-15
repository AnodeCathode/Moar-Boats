package org.jglrxavpok.moarboats.datagen

import net.minecraft.block.Blocks
import net.minecraft.data.DataGenerator
import net.minecraft.data.IFinishedRecipe
import net.minecraft.data.RecipeProvider
import net.minecraft.data.ShapelessRecipeBuilder
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.Items
import org.jglrxavpok.moarboats.common.data.BoatType
import org.jglrxavpok.moarboats.common.items.UtilityBoatItem
import java.util.function.Consumer

class UtilityBoatRecipes(generator: DataGenerator): RecipeProvider(generator) {

    override fun registerRecipes(consumer: Consumer<IFinishedRecipe>) {
        for(item in Items.list) {
            if(item is UtilityBoatItem) {
                registerRecipe(consumer, item)
            }
        }
    }

    private fun registerRecipe(consumer: Consumer<IFinishedRecipe>, item: UtilityBoatItem) {
        MoarBoats.logger.info("Generating recipe for item ${item.registryName}")
        ShapelessRecipeBuilder.shapelessRecipe(item)
                .setGroup("moarboats:utility_boat_${item.containerType}")
                .addIngredient(BoatType.getBoatItemFromType(item.boatType)!!)
                .addIngredient(UtilityBoatType2Block(item.containerType))
                .addCriterion("in_water", this.enteredBlock(Blocks.WATER))
                .build(consumer)
    }
}