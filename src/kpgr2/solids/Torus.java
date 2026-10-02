package kpgr2.solids;

import kpgr2.model.Solid;
import transforms.Point3D;

public class Torus extends Solid {
    /** m segments around the main ring, k segments around the tube. */
    public Torus(int m, int k) {
        super("Torus", 0x20B2AA);
        if (m < 3 || k < 3) throw new IllegalArgumentException("m and k must be >= 3");
        double major = 0.35, minor = 0.15;
        for (int i = 0; i < m; i++) {
            double u = 2 * Math.PI * i / m;
            for (int j = 0; j < k; j++) {
                double v = 2 * Math.PI * j / k;
                double r = major + minor * Math.cos(v);
                vertices.add(new Point3D(r * Math.cos(u), r * Math.sin(u), minor * Math.sin(v)));
            }
        }
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < k; j++) {
                int idx = i * k + j;
                indices.add(idx);
                indices.add(i * k + (j + 1) % k);
                indices.add(idx);
                indices.add(((i + 1) % m) * k + j);
            }
        }
    }
}
