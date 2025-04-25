package rbasamoyai.escalated.walkways;

import com.tterrag.registrate.util.nullness.NonNullSupplier;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public interface WalkwaySet {

    BlockState getNarrowBlock(Level level, BlockState state, BlockPos pos);
    BlockState getWideSideBlock(Level level, BlockState state, BlockPos pos);
    BlockState getWideCenterBlock(Level level, BlockState state, BlockPos pos);
    BlockState getTerminalBlock(Level level, BlockState state, BlockPos pos);
    BlockState getHandrailBlock(Level level, BlockState state, BlockPos pos);

    boolean blockInSet(BlockState state);

    record Impl(NonNullSupplier<Block> narrow, NonNullSupplier<Block> wideSide, NonNullSupplier<Block> wideCenter,
                NonNullSupplier<Block> terminal, NonNullSupplier<Block> handrail) implements WalkwaySet {
        @Override
        public BlockState getNarrowBlock(Level level, BlockState state, BlockPos pos) {
            return this.narrow.get().defaultBlockState();
        }

        @Override
        public BlockState getWideSideBlock(Level level, BlockState state, BlockPos pos) {
            return this.wideSide.get().defaultBlockState();
        }

        @Override
        public BlockState getWideCenterBlock(Level level, BlockState state, BlockPos pos) {
            return this.wideCenter.get().defaultBlockState();
        }

        @Override
        public BlockState getTerminalBlock(Level level, BlockState state, BlockPos pos) {
            return this.terminal.get().defaultBlockState();
        }

        @Override
        public BlockState getHandrailBlock(Level level, BlockState state, BlockPos pos) {
            return this.handrail.get().defaultBlockState();
        }

        @Override
        public boolean blockInSet(BlockState state) {
            Block block = state.getBlock();
            return block == this.narrow.get() || block == this.wideSide.get() || block == this.wideCenter.get() || block == this.terminal.get();
        }
    }

}
