package kpgr2.solids;

import kpgr2.model.Solid;
import kpgr2.model.Tessellated;
import transforms.Bicubic;
import transforms.Cubic;
import transforms.Point3D;

/**
 * Bezier patch drawn as iso-lines: n + 1 lines in u and n + 1 in v, each sampled at n segments.
 * Vertices form an (n + 1) x (n + 1) grid (index = i * (n + 1) + j, i along u); edges 2 * n * (n + 1).
 */
public class BicubicSurface extends Solid implements Tessellated {
    private final Bicubic patch;

    public BicubicSurface(String name, Point3D[] p16, int color) {
        super(name, color);
        this.patch = new Bicubic(Cubic.BEZIER, p16);
        setSegments(10);
    }

    /** Default 4x4 control net: a gently bent surface over [-1, 1]^2. */
    public static Point3D[] defaultControlPoints() {
        Point3D[] p = new Point3D[16];
        for (int i = 0; i < 4; i++)
            for (int j = 0; j < 4; j++) {
                boolean inner = (i == 1 || i == 2) && (j == 1 || j == 2);
                p[i * 4 + j] = new Point3D(-1 + 2 * i / 3.0, -1 + 2 * j / 3.0, inner ? 1.5 : 0);
            }
        return p;
    }

    @Override
    public void setSegments(int n) {
        if (n < 1) throw new IllegalArgumentException("segments must be >= 1");
        vertices.clear();
        indices.clear();
        int m = n + 1;
        for (int i = 0; i < m; i++)
            for (int j = 0; j < m; j++) vertices.add(patch.compute((double) i / n, (double) j / n));
        for (int i = 0; i < m; i++)
            for (int j = 0; j < n; j++) {
                indices.add(i * m + j);
                indices.add(i * m + j + 1);
                indices.add(j * m + i);
                indices.add((j + 1) * m + i);
            }
    }
}
