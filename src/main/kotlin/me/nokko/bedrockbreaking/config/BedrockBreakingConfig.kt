package me.nokko.bedrockbreaking.config

import com.google.common.collect.ImmutableSet
import io.github.redstoneparadox.paradoxconfig.config.ConfigCategory
import net.minecraft.util.Identifier

object BedrockBreakingConfig: ConfigCategory("bedrockbreaking.json") {
        var bedrockEffectiveHardness: Float by option(50F, "bedrock_effective_hardness",
                "How hard bedrock is to break with the appropriate tool, " +
                        "in terms of MC hardness. Default value: same hardness as obsidian.")
        var bedrockDrops: Boolean by option(false, "bedrock_drops",
                "Does bedrock drop a bedrock block when broken?")
        var bedrockPickaxeEnabled: Boolean by option(true, "bedrock_pickaxe_enabled",
                "Is the bedrock pickaxe item in the game?")
        var useEffectiveModifier: Boolean by option(false, "use_effective_modifier",
                "Should tool effectiveness be considered? " +
                        "Tools other than the bedrock pickaxe that work on bedrock will be " +
                        "30% as speedy at breaking bedrock with this setting turned on.")
        var bedrockBreakers: MutableList<String> by option(mutableListOf("bedrockbreaking:bedrock_pickaxe"),
                "bedrock_breakers","A list of items that can break bedrock.")

}

object BedrockBreakingDerivedConfig {
        val bedrockBreakerIDs: ImmutableSet<Identifier> =
                ImmutableSet.copyOf(BedrockBreakingConfig.bedrockBreakers.map { Identifier(it) })
}

