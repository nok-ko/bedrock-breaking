package me.nokko.bedrockpickaxe.mixin;

import com.google.common.collect.ImmutableSet;
import me.nokko.bedrockpickaxe.config.BedrockPickaxeConfig;
import me.nokko.bedrockpickaxe.config.BedrockPickaxeDerivedConfig;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(AbstractBlock.class)

// Mixins HAVE to be written in java due to constraints in the mixin system.
// Boo-hoo.

public class AbstractBlockMixin {
    float effectiveHardness = BedrockPickaxeConfig.INSTANCE.getBedrockEffectiveHardness();
    boolean useEffectiveModifier = BedrockPickaxeConfig.INSTANCE.getUseEffectiveModifier();
    ImmutableSet<Identifier> bedrockBreakerIDs = BedrockPickaxeDerivedConfig.INSTANCE.getBedrockBreakerIDs();

    @Inject(at = @At(value = "INVOKE_ASSIGN", shift = At.Shift.AFTER),
            method = "calcBlockBreakingDelta(" +
                    "Lnet/minecraft/block/BlockState;" +
                    "Lnet/minecraft/entity/player/PlayerEntity;" +
                    "Lnet/minecraft/world/BlockView;" +
                    "Lnet/minecraft/util/math/BlockPos;)F",
            cancellable = true, // This is an early-return Mixin.
            locals = LocalCapture.CAPTURE_FAILSOFT
    )
    public void allowBedrockBreaking(BlockState state, PlayerEntity player, BlockView world, BlockPos pos, CallbackInfoReturnable<Float> info, float hardness) {
        ItemStack stackInHand = player.getStackInHand(Hand.MAIN_HAND);

        if (hardness == -1.0F && bedrockBreakerIDs.contains(Registry.ITEM.getId(stackInHand.getItem()))) {
            // Pretend hardness is the value in the config (50F by default, like obsidian),
            // and calculate the effective tool multiplier, same as Mojang code.

            // Note that by default this just means that everything that can break bedrock will break it at 30% regular
            // speed, since only the Bedrock Pickaxe overrides the effective tool code.

            // We don't override player.getBlockBreakingSpeed, that's also handled by the pickaxe item itself,
            // plus it's affected by potion effects and enchantments.

            info.setReturnValue(player.getBlockBreakingSpeed(state) / effectiveHardness / ((player.isUsingEffectiveTool(state) || !useEffectiveModifier) ? 30 : 100));
        }
    }
}