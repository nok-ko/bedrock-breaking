package me.nokko.bedrockbreaking.mixin;

import com.google.common.collect.ImmutableSet;
import me.nokko.bedrockbreaking.config.BedrockBreakingConfig;
import me.nokko.bedrockbreaking.config.BedrockBreakingDerivedConfig;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.BlockView;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(AbstractBlock.class)

// Mixins HAVE to be written in java due to constraints in the mixin system.
// Boo-hoo.

public class AbstractBlockMixin {
    final float effectiveHardness = BedrockBreakingConfig.INSTANCE.getBedrockEffectiveHardness();
    final boolean useEffectiveModifier = BedrockBreakingConfig.INSTANCE.getUseEffectiveModifier();

    // Change this to use a Block Tag instead! Ugh.
    final ImmutableSet<Identifier> bedrockBreakerIDs = BedrockBreakingDerivedConfig.INSTANCE.getBedrockBreakerIDs();

    // Explanation of this @Inject:
    // Inject in the `true` branch of the "is the player breaking bedrock?" check
    // The JUMP injection point will put us just BEFORE the jump instruction, but
    // we want to be just after it – thus we use `shift`.
    // The injected code will run when any player is breaking bedrock.
    @Inject(at = @At(value = "JUMP", opcode = Opcodes.IFNE, shift = At.Shift.AFTER),
            method = "Lnet/minecraft/block/AbstractBlock;calcBlockBreakingDelta(" +
                    "Lnet/minecraft/block/BlockState;" +
                    "Lnet/minecraft/entity/player/PlayerEntity;" +
                    "Lnet/minecraft/world/BlockView;" +
                    "Lnet/minecraft/util/math/BlockPos;)F",

            cancellable = true, // This is an early-return Mixin.
            locals = LocalCapture.CAPTURE_FAILSOFT
    )
    public void allowBedrockBreaking(BlockState state, PlayerEntity player, BlockView world, BlockPos pos,
                                     CallbackInfoReturnable<Float> cir, float hardness) { // hardness is now a local!

        ItemStack stackInHand = player.getStackInHand(Hand.MAIN_HAND);

        // TODO: Factor out this expensive set lookup, if it's an issue.
        if (hardness == -1.0F && bedrockBreakerIDs.contains(Registry.ITEM.getId(stackInHand.getItem()))) {
            // Pretend hardness is the value in the config (50F by default, like obsidian),
            // and calculate the effective tool multiplier, same as Mojang code.

            // Note that by default this just means that everything that can break bedrock will break it at 30% regular
            // speed, since only the Bedrock Pickaxe overrides the effective tool code.

            // We don't override player.getBlockBreakingSpeed, that's also handled by the pickaxe item itself,
            // plus it's affected by potion effects and enchantments.

            int effectiveToolMod = player.canHarvest(state) || !useEffectiveModifier ? 30 : 100;
            cir.setReturnValue(
                    player.getBlockBreakingSpeed(state)
                            / effectiveHardness
                            / effectiveToolMod);
        }
    }
}