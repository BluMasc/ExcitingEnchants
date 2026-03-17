package net.blumasc.excitingenchants.entity.client.souls;

import com.mojang.blaze3d.vertex.VertexConsumer;

public class SoulVertexConsumer implements VertexConsumer {
    private final VertexConsumer delegate;

    private static final int TINT_R = 0;
    private static final int TINT_G = 200;
    private static final int TINT_B = 220;
    private static final int TINT_A = 80;

    public SoulVertexConsumer(VertexConsumer delegate) {
        this.delegate = delegate;
    }

    @Override
    public VertexConsumer setColor(int r, int g, int b, int a) {
        int blendedR = (r * TINT_R) / 255;
        int blendedG = (g + TINT_G) / 2;
        int blendedB = (b + TINT_B) / 2;
        int blendedA = Math.min(a, TINT_A);
        return delegate.setColor(blendedR, blendedG, blendedB, blendedA);
    }
    @Override
    public VertexConsumer addVertex(float x, float y, float z) {
        return delegate.addVertex(x, y, z);
    }
    @Override
    public VertexConsumer setUv(float u, float v) {
        return delegate.setUv(u, v);
    }
    @Override
    public VertexConsumer setUv1(int u, int v) {
        return delegate.setUv1(u, v);
    }
    @Override
    public VertexConsumer setUv2(int u, int v) {
        return delegate.setUv2(u, v);
    }
    @Override
    public VertexConsumer setNormal(float nx, float ny, float nz) {
        return delegate.setNormal(nx, ny, nz);
    }
}