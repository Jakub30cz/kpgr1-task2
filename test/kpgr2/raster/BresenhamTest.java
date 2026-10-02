package kpgr2.raster;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class BresenhamTest {
    private static final int WHITE = 0xFFFFFF;
    private static final int W = 41;
    private static final int H = 41;

    private static Set<Long> lit(RasterBufferedImage r) {
        Set<Long> pixels = new HashSet<>();
        for (int y = 0; y < r.getHeight(); y++) {
            for (int x = 0; x < r.getWidth(); x++) {
                if ((r.getImage().getRGB(x, y) & 0xFFFFFF) == WHITE) {
                    pixels.add(((long) x << 32) | y);
                }
            }
        }
        return pixels;
    }

    private static Set<Long> draw(int x1, int y1, int x2, int y2) {
        RasterBufferedImage r = new RasterBufferedImage(W, H);
        r.clear(0);
        new BresenhamLineRasterizer(r).rasterize(x1, y1, x2, y2, WHITE);
        return lit(r);
    }

    private static void check(int x1, int y1, int x2, int y2) {
        Set<Long> p = draw(x1, y1, x2, y2);
        int expected = Math.max(Math.abs(x2 - x1), Math.abs(y2 - y1)) + 1;
        assertEquals(expected, p.size(), "pixel count for " + x1 + "," + y1 + "->" + x2 + "," + y2);
        assertTrue(p.contains(((long) x1 << 32) | y1), "start endpoint");
        assertTrue(p.contains(((long) x2 << 32) | y2), "end endpoint");
    }

    @Test
    void allEightOctants() {
        int c = 20;
        int[][] ends = {{35, 25}, {25, 35}, {15, 35}, {5, 25}, {5, 15}, {15, 5}, {25, 5}, {35, 15}};
        for (int[] e : ends) {
            check(c, c, e[0], e[1]);
            check(e[0], e[1], c, c);
        }
    }

    @Test
    void axisAlignedAndDiagonals() {
        check(5, 10, 30, 10);
        check(30, 10, 5, 10);
        check(10, 5, 10, 30);
        check(10, 30, 10, 5);
        check(5, 5, 30, 30);
        check(30, 30, 5, 5);
        check(5, 30, 30, 5);
        check(30, 5, 5, 30);
    }

    @Test
    void singlePoint() {
        Set<Long> p = draw(7, 9, 7, 9);
        assertEquals(1, p.size());
        assertTrue(p.contains((7L << 32) | 9));
    }

    @Test
    void outOfRangeIgnored() {
        assertEquals(W, draw(-10, 20, 50, 20).size());
        assertEquals(0, draw(-20, -20, -5, -5).size());
    }
}
