package net.blumasc.excitingenchants.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.blumasc.excitingenchants.ExcitingEnchantsMod;
import net.blumasc.excitingenchants.enchantment.ModEnchantments;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderHighlightEvent;
import org.joml.Matrix4f;

import java.util.*;

@EventBusSubscriber(modid = ExcitingEnchantsMod.MODID, value = Dist.CLIENT)
public class BlockHighlightRenderer {

    private static BlockPos lastOrigin = null;
    private static List<long[]> cachedEdges = List.of();

    @SubscribeEvent
    public static void onRenderBlockHighlight(RenderHighlightEvent.Block event) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        Level level = mc.level;
        if (player == null || level == null) return;

        int ench_level = hasVeinMinerEnchantment(player.getMainHandItem(), level);
        if (ench_level<=0) return;

        BlockPos origin = event.getTarget().getBlockPos();
        if (level.getBlockState(origin).isAir()) return;

        if (!player.getMainHandItem().isCorrectToolForDrops(level.getBlockState(origin))) return;
        if (!origin.equals(lastOrigin)) {
            Set<BlockPos> blocks = new HashSet<>(getAffectedBlocks(origin, level, ench_level));
            cachedEdges = computeOutlineEdges(blocks);
            lastOrigin = origin;
        }

        if (cachedEdges.isEmpty()) return;

        PoseStack poseStack = event.getPoseStack();
        Vec3 cam = event.getCamera().getPosition();
        double time = System.currentTimeMillis() / 1000.0;

        VertexConsumer consumer = mc.renderBuffers().bufferSource().getBuffer(RenderType.lines());
        Matrix4f mat = poseStack.last().pose();
        PoseStack.Pose pose = poseStack.last();

        for (long[] e : cachedEdges) {
            double x1 = e[0], y1 = e[1], z1 = e[2];
            double x2 = e[3], y2 = e[4], z2 = e[5];
            double[] s1 = shake(x1, y1, z1, time);
            double[] s2 = shake(x2, y2, z2, time);
            float ax = (float)(s1[0] - cam.x), ay = (float)(s1[1] - cam.y), az = (float)(s1[2] - cam.z);
            float bx = (float)(s2[0] - cam.x), by = (float)(s2[1] - cam.y), bz = (float)(s2[2] - cam.z);
            float ndx = bx - ax, ndy = by - ay, ndz = bz - az;
            float len = (float) Math.sqrt(ndx*ndx + ndy*ndy + ndz*ndz);
            if (len == 0) continue;
            ndx /= len; ndy /= len; ndz /= len;

            consumer.addVertex(mat, ax, ay, az)
                    .setColor(0.85f, 0.85f, 1.0f, 0.85f)
                    .setNormal(pose, ndx, ndy, ndz);
            consumer.addVertex(mat, bx, by, bz)
                    .setColor(0.85f, 0.85f, 1.0f, 0.85f)
                    .setNormal(pose, ndx, ndy, ndz);
        }

        mc.renderBuffers().bufferSource().endBatch(RenderType.lines());
    }
    private static List<long[]> computeOutlineEdges(Set<BlockPos> blocks) {
        if (blocks.isEmpty()) return List.of();

        int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE, minZ = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE, maxZ = Integer.MIN_VALUE;
        for (BlockPos p : blocks) {
            minX = Math.min(minX, p.getX()); maxX = Math.max(maxX, p.getX());
            minY = Math.min(minY, p.getY()); maxY = Math.max(maxY, p.getY());
            minZ = Math.min(minZ, p.getZ()); maxZ = Math.max(maxZ, p.getZ());
        }

        List<long[]> edges = new ArrayList<>();
        BlockPos.MutableBlockPos m = new BlockPos.MutableBlockPos();

        for (int gx = minX; gx <= maxX + 1; gx++) {
            for (int gz = minZ; gz <= maxZ + 1; gz++) {
                for (int y = minY; y <= maxY; y++) {
                    int n = (has(blocks, m, gx-1, y, gz-1) ? 1 : 0)
                            + (has(blocks, m, gx,   y, gz-1) ? 1 : 0)
                            + (has(blocks, m, gx-1, y, gz  ) ? 1 : 0)
                            + (has(blocks, m, gx,   y, gz  ) ? 1 : 0);
                    if ((n & 1) == 1)
                        edges.add(new long[]{gx, y, gz,  gx, y+1, gz});
                }
            }
        }
        for (int gy = minY; gy <= maxY + 1; gy++) {
            for (int gz = minZ; gz <= maxZ + 1; gz++) {
                for (int x = minX; x <= maxX; x++) {
                    int n = (has(blocks, m, x, gy-1, gz-1) ? 1 : 0)
                            + (has(blocks, m, x, gy,   gz-1) ? 1 : 0)
                            + (has(blocks, m, x, gy-1, gz  ) ? 1 : 0)
                            + (has(blocks, m, x, gy,   gz  ) ? 1 : 0);
                    if ((n & 1) == 1)
                        edges.add(new long[]{x, gy, gz,  x+1, gy, gz});
                }
            }
        }
        for (int gx = minX; gx <= maxX + 1; gx++) {
            for (int gy = minY; gy <= maxY + 1; gy++) {
                for (int z = minZ; z <= maxZ; z++) {
                    int n = (has(blocks, m, gx-1, gy-1, z) ? 1 : 0)
                            + (has(blocks, m, gx,   gy-1, z) ? 1 : 0)
                            + (has(blocks, m, gx-1, gy,   z) ? 1 : 0)
                            + (has(blocks, m, gx,   gy,   z) ? 1 : 0);
                    if ((n & 1) == 1)
                        edges.add(new long[]{gx, gy, z,  gx, gy, z+1});
                }
            }
        }

        return edges;
    }

    private static boolean has(Set<BlockPos> set, BlockPos.MutableBlockPos m, int x, int y, int z) {
        return set.contains(m.set(x, y, z));
    }
    private static double[] shake(double x, double y, double z, double time) {
        double amp   = 0.005;
        double speed = 4.5;
        double phase = x * 1.7 + y * 2.3 + z * 1.1;

        double ox = Math.sin(time * speed        + phase)        * amp;
        double oy = Math.sin(time * speed * 1.2  + phase + 2.09) * amp;
        double oz = Math.sin(time * speed * 0.85 + phase + 4.19) * amp;
        return new double[]{x + ox, y + oy, z + oz};
    }
    private static int hasVeinMinerEnchantment(ItemStack stack, Level level) {
        if (stack.isEmpty()) return 0;
        try {
            Holder<Enchantment> holder = level.registryAccess()
                    .registryOrThrow(Registries.ENCHANTMENT)
                    .getHolderOrThrow(ModEnchantments.REVERBERATING);
            return stack.getEnchantmentLevel(holder);
        } catch (Exception e) {
            return 0;
        }
    }
    private static List<BlockPos> getAffectedBlocks(BlockPos originalBlock, Level world, int enchantLevel) {
        List<BlockPos> out = new ArrayList<BlockPos>();
        out.add(originalBlock);
        Queue<BlockPos> queue = new ArrayDeque<>();
        Set<BlockPos> visited = new HashSet<>();

        BlockState origin =world.getBlockState(originalBlock);

        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -1; dz <= 1; dz++) {
                    if (dx == 0 && dy == 0 && dz == 0) continue;
                    BlockPos neighbor = originalBlock.offset(dx, dy, dz);
                    queue.add(neighbor);
                    visited.add(neighbor);
                }
            }
        }

        int maxExtraBlocks = (int) Math.pow(2, enchantLevel + 2);
        int brokenCount = 0;

        while (!queue.isEmpty() && brokenCount < maxExtraBlocks) {
            BlockPos pos = queue.poll();
            BlockState state = world.getBlockState(pos);

            if (state.getBlock() != origin.getBlock()) continue;

            if (world.getBlockEntity(pos) != null) continue;

            out.add(pos);

            brokenCount++;

            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    for (int dz = -1; dz <= 1; dz++) {
                        BlockPos neighbor = pos.offset(dx, dy, dz);
                        if (!visited.contains(neighbor)) {
                            queue.add(neighbor);
                            visited.add(neighbor);
                        }
                    }
                }
            }
        }
        return out;
    }
}