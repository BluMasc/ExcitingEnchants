package net.blumasc.excitingenchants.client;

import oshi.util.tuples.Pair;

import java.util.List;

public class ClientInkedData {
    public record InkSplooch(int x, int y, int type, int renderW, int renderH, float rotation, boolean mirrored) {}
    public static List<InkSplooch> inkSploches;
}
