package me.nokko.bedrockbreaking

import me.nokko.bedrockbreaking.tools.BedrockPickaxeItem
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.registry.Registry
import net.minecraft.util.registry.Registry.ITEM
import net.minecraft.world.World
import me.nokko.bedrockbreaking.config.BedrockBreakingConfig as cfg

const val modID = "bedrockbreaking"
val BEDROCK_PICKAXE = BedrockPickaxeItem()

class BedrockBreaking {
    @Suppress("unused") // Pay the IDE Tax, peasant.
    fun init() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.

        // Add the bedrock-breaking pickaxe, if the config option is set.
        if (cfg.bedrockPickaxeEnabled) {
            Registry.register(ITEM, Identifier(modID, "${BedrockPickaxeItem.itemID}"), BEDROCK_PICKAXE)
        }

        // Bedrock usually doesn't drop anything when broken, this feature adds drops.
        // Not affected by Fortune, because duh.
        if (cfg.bedrockDrops) {
            PlayerBlockBreakEvents.AFTER.register {
                world: World, player: PlayerEntity, pos: BlockPos, state: BlockState, _: BlockEntity? ->
                if (state.block == Blocks.BEDROCK && !player.isCreative) {
                    Block.dropStack(world, pos, ItemStack(Items.BEDROCK))
                }
            }
        }
    }
}

