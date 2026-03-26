package com.brachy84.mechtech.api;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import gregtech.api.pattern.PatternMatchContext;
import gregtech.api.pattern.TraceabilityPredicate;
import gregtech.api.unification.material.Material;
import gregtech.api.util.BlockInfo;
import gregtech.api.util.GTLog;
import gregtech.common.blocks.BlockCompressed;
import gregtech.common.blocks.MetaBlocks;

public class ToroidBlock {

    private static final ToroidBlock NULL = new ToroidBlock("NULL", Blocks.AIR.getDefaultState());

    private static final Map<String, ToroidBlock> TORUS_BLOCK_MAP = new HashMap<>();

    @Nullable
    public static ToroidBlock get(IBlockState state) {
        for (ToroidBlock block : TORUS_BLOCK_MAP.values()) {
            if (block.state.getBlock() == state.getBlock() &&
                    block.state.getBlock().getMetaFromState(block.state) == state.getBlock().getMetaFromState(state))
                return block;
        }
        return null;
    }

    @Nullable
    public static ToroidBlock get(String name) {
        return TORUS_BLOCK_MAP.get(name);
    }

    public static void remove(String name) {
        if (!TORUS_BLOCK_MAP.containsKey(name)) {
            GTLog.logger.error("Could not find ToroidBlock with name {}", name);
            return;
        }
        TORUS_BLOCK_MAP.remove(name);
    }

    public static Collection<ToroidBlock> getAll() {
        return Collections.unmodifiableCollection(TORUS_BLOCK_MAP.values());
    }

    public static Map<String, ToroidBlock> getRegistryMap() {
        return Collections.unmodifiableMap(TORUS_BLOCK_MAP);
    }

    private final IBlockState state;
    public float dmgModifier = 0;
    public float rangeModifier = 0;
    private final String name;

    public ToroidBlock(String name, IBlockState state) {
        this.state = state;
        this.name = name;
    }

    public static ToroidBlock create(String name, IBlockState state) {
        return new ToroidBlock(name, state);
    }

    public static ToroidBlock create(String name, Block block) {
        return new ToroidBlock(name, block.getDefaultState());
    }

    public static ToroidBlock create(Material material) {
        if (material == null) {
            GTLog.logger.error("Can't create ToroidBlock of null material");
            return NULL;
        }
        return create(material.toString(), material);
    }

    public static ToroidBlock create(String name, Material material) {
        if (material == null) {
            GTLog.logger.error("Can't create ToroidBlock of null material");
            return NULL;
        }
        BlockCompressed block = MetaBlocks.COMPRESSED.get(material);
        if (block == null) {
            GTLog.logger.error("Can't find block for ToroidBlock for material " + material.toString());
            return NULL;
        }
        return new ToroidBlock(name, block.getBlock(material));
    }

    @SuppressWarnings("all")
    public ToroidBlock register() {
        if (state.getBlock() == Blocks.AIR) {
            GTLog.logger.error("Can not register TorusBlock with AIR block state");
            return this;
        }
        if (TORUS_BLOCK_MAP.containsKey(name)) {
            GTLog.logger.error("Can't register ToroidBlock '{}' as it already exists", name);
        } else {
            TORUS_BLOCK_MAP.put(name, this);
        }
        return this;
    }

    public ToroidBlock setDmgModifier(float dmgModifier) {
        this.dmgModifier = dmgModifier;
        return this;
    }

    public ToroidBlock setRangeModifier(float rangeModifier) {
        this.rangeModifier = rangeModifier;
        return this;
    }

    public float getDmgModifier() {
        return dmgModifier;
    }

    public float getRangeModifier() {
        return rangeModifier;
    }

    public String getName() {
        return name;
    }

    public ItemStack getItemForm() {
        return new ItemStack(Item.getItemFromBlock(state.getBlock()), 1, state.getBlock().damageDropped(state));
    }

    public static TraceabilityPredicate traceabilityPredicate() {
        Supplier<BlockInfo[]> candidates = () -> {
            BlockInfo[] info = new BlockInfo[TORUS_BLOCK_MAP.size()];
            int i = 0;
            for (ToroidBlock block : TORUS_BLOCK_MAP.values()) {
                info[i] = new BlockInfo(block.state);
                i++;
            }
            return info;
        };
        return new TraceabilityPredicate(blockWorldState -> {
            ToroidBlock toroidBlock = get(blockWorldState.getBlockState());
            if (toroidBlock == null) {
                return false;
            }
            PatternMatchContext matchContext = blockWorldState.getMatchContext();
            matchContext.increment(toroidBlock.name, 1);
            return true;
        }, candidates).addTooltips();
    }
}
