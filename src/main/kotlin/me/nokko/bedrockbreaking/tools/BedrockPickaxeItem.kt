package me.nokko.bedrockbreaking.tools

import me.nokko.bedrockbreaking.BedrockBreaking
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.block.Material
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemStack
import net.minecraft.item.MiningToolItem
import net.minecraft.item.ToolMaterials
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

const val itemID: String = "bedrock_pickaxe"

class BedrockPickaxeItem :
    MiningToolItem(3F, -2.8f, ToolMaterials.DIAMOND,
        BedrockBreaking.MINEABLE_AND_BEDROCK, Settings().group(ItemGroup.TOOLS)) {

//    override fun canMine(state: BlockState?, world: World?, pos: BlockPos?, miner: PlayerEntity?): Boolean {
//        return true
//    }

//    override fun isEffectiveOn(state: BlockState?): Boolean {
//        if (state?.isOf(Blocks.BEDROCK) == true) {
//            return true
//        }
//        return super.isEffectiveOn(state)
//    }

    override fun getMiningSpeedMultiplier(stack: ItemStack?, state: BlockState): Float {
//      Special case for bedrock
        if (state.isOf(Blocks.BEDROCK)) {
            return miningSpeed
        }

        val material = state.material

        return if (material != Material.METAL && material != Material.REPAIR_STATION && material != Material.STONE) {
            super.getMiningSpeedMultiplier(stack, state)
        } else miningSpeed
    }

    override fun postMine(
        stack: ItemStack,
        world: World,
        state: BlockState,
        pos: BlockPos?,
        miner: LivingEntity
    ): Boolean {

        val miningCallback: (LivingEntity) -> Unit = { e: LivingEntity ->
            e.sendEquipmentBreakStatus(
                EquipmentSlot.MAINHAND
            )
        }
//      Breaking bedrock takes a toll on the tool!
        if (!world.isClient && state.getHardness(world, pos) == -1f) {
            stack.damage(100, miner, miningCallback)
        }

        if (!world.isClient && state.getHardness(world, pos) != 0.0f) {
            stack.damage(1, miner, miningCallback)
        }

//      Blocks with < 0.0 hardness don't damage the pickaxe, as usual.
        return true
    }


}
