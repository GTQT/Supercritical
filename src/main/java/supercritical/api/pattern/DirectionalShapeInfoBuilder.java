package supercritical.api.pattern;

import java.util.*;
import java.util.function.Supplier;

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.MetaTileEntityHolder;
import gregtech.api.pattern.MultiblockShapeInfo;
import gregtech.api.util.BlockInfo;
import gregtech.api.util.RelativeDirection;

/**
 * Extends {@link MultiblockShapeInfo.Builder} to incorporate directional awareness.
 */
public class DirectionalShapeInfoBuilder {
    // 内聚父类 Builder 的字段和方法
    private final RelativeDirection[] structureDir;
    private final List<String[]> shape;
    private final Map<Character, BlockInfo> symbolMap;

    // 构造函数直接处理参数校验
    public DirectionalShapeInfoBuilder(RelativeDirection... structureDir) {
        if (structureDir.length != 3) {
            throw new IllegalArgumentException("必须提供 3 个 RelativeDirection 参数");
        }
        this.structureDir = Arrays.copyOf(structureDir, 3);
        for (int i = 0; i < 3; i++) {
            if (this.structureDir[i] == null) {
                throw new IllegalArgumentException("RelativeDirection 参数不能为 null");
            }
        }
        this.shape = new ArrayList<>();
        this.symbolMap = new HashMap<>();
        validateAxes();
    }

    // 校验三个方向是否属于不同坐标轴
    private void validateAxes() {
        int flags = 0;
        for (RelativeDirection dir : structureDir) {
            switch (dir) {
                case UP:
                case DOWN:
                    flags |= 1;
                    break;
                case LEFT:
                case RIGHT:
                    flags |= 2;
                    break;
                case FRONT:
                case BACK:
                    flags |= 4;
                    break;
                default:
                    throw new IllegalArgumentException("无效的 RelativeDirection: " + dir);
            }
        }
        if (flags != 7) {
            throw new IllegalArgumentException("三个方向必须属于不同坐标轴");
        }
    }

    // 链式方法返回自身类型
    public DirectionalShapeInfoBuilder aisle(String... data) {
        if (data == null || data.length == 0) {
            throw new IllegalArgumentException("aisle 参数不能为空");
        }
        this.shape.add(data);
        return this;
    }

    public DirectionalShapeInfoBuilder where(char symbol, BlockInfo value) {
        this.symbolMap.put(symbol, value);
        return this;
    }

    public DirectionalShapeInfoBuilder where(char symbol, IBlockState blockState) {
        return where(symbol, new BlockInfo(blockState));
    }

    public DirectionalShapeInfoBuilder where(char symbol, IBlockState blockState, TileEntity tileEntity) {
        return where(symbol, new BlockInfo(blockState, tileEntity));
    }

    public DirectionalShapeInfoBuilder where(char symbol, MetaTileEntity tileEntity, EnumFacing frontSide) {
        MetaTileEntityHolder holder = new MetaTileEntityHolder();
        holder.setMetaTileEntity(tileEntity);
        holder.getMetaTileEntity().onPlacement();
        holder.getMetaTileEntity().setFrontFacing(frontSide);
        return where(symbol, new BlockInfo(tileEntity.getBlock().getDefaultState(), holder));
    }

    public DirectionalShapeInfoBuilder where(char symbol, Supplier<?> partSupplier, EnumFacing frontSideIfTE) {
        Object part = partSupplier.get();
        if (part instanceof IBlockState) {
            return where(symbol, (IBlockState) part);
        } else if (part instanceof MetaTileEntity) {
            return where(symbol, (MetaTileEntity) part, frontSideIfTE);
        } else {
            throw new IllegalArgumentException("Supplier 必须返回 IBlockState 或 MetaTileEntity");
        }
    }

    // 核心坐标转换逻辑（修复空指针）
    private BlockPos setActualRelativeOffset(int x, int y, int z, EnumFacing facing, EnumFacing upwardsFacing, boolean isFlipped) {
        Objects.requireNonNull(facing, "facing 方向不能为 null");
        Objects.requireNonNull(upwardsFacing, "upwardsFacing 方向不能为 null");

        int[] c0 = {x, y, z};
        int[] c1 = new int[3];

        for (int i = 0; i < 3; i++) {
            RelativeDirection dir = structureDir[i];
            Objects.requireNonNull(dir, "structureDir 中存在 null 值");

            EnumFacing actualFacing = dir.getActualFacing(facing);
            Objects.requireNonNull(actualFacing, "无法解析实际方向");

            switch (actualFacing) {
                case UP:    c1[1] = c0[i]; break;
                case DOWN:  c1[1] = -c0[i]; break;
                case WEST:  c1[0] = -c0[i]; break;
                case EAST:  c1[0] = c0[i]; break;
                case NORTH: c1[2] = -c0[i]; break;
                case SOUTH: c1[2] = c0[i]; break;
                default: throw new IllegalStateException("意外的方向: " + actualFacing);
            }
        }

        // 处理坐标系翻转和偏移
        if (isFlipped) {
            c1[0] = -c1[0];
            c1[2] = -c1[2];
        }

        return new BlockPos(c1[0], c1[1], c1[2]);
    }

    // 生成最终三维结构
    public MultiblockShapeInfo build() {
        BlockInfo[][][] blockInfos = bakeArray();
        return new MultiblockShapeInfo(blockInfos);
    }

    private BlockInfo[][][] bakeArray() {
        int maxZ = shape.size();
        int maxY = shape.isEmpty() ? 0 : shape.get(0).length;
        int maxX = (maxY == 0) ? 0 : shape.get(0)[0].length();

        BlockPos end = setActualRelativeOffset(maxX, maxY, maxZ, EnumFacing.SOUTH, EnumFacing.UP, true);
        BlockPos addition = new BlockPos(
                end.getX() < 0 ? -end.getX() - 1 : 0,
                end.getY() < 0 ? -end.getY() - 1 : 0,
                end.getZ() < 0 ? -end.getZ() - 1 : 0
        );
        BlockPos bound = new BlockPos(
                Math.abs(end.getX()),
                Math.abs(end.getY()),
                Math.abs(end.getZ())
        );

        BlockInfo[][][] blockInfos = new BlockInfo[bound.getX()][bound.getY()][bound.getZ()];
        for (int z = 0; z < maxZ; z++) {
            String[] aisleEntry = shape.get(z);
            for (int y = 0; y < maxY; y++) {
                String row = aisleEntry[y];
                for (int x = 0; x < maxX; x++) {
                    BlockInfo info = symbolMap.getOrDefault(row.charAt(x), BlockInfo.EMPTY);
                    BlockPos pos = setActualRelativeOffset(x, y, z, EnumFacing.SOUTH, EnumFacing.UP, true).add(addition);
                    blockInfos[pos.getX()][pos.getY()][pos.getZ()] = info;
                }
            }
        }
        return blockInfos;
    }
}