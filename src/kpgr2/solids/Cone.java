package kpgr2.solids;

import kpgr2.model.Solid;
import transforms.Point3D;

public class Cone extends Solid {
    public Cone(int n) {
        super("Cone", 0x8B4513);
        if (n < 3) throw new IllegalArgumentException("n must be >= 3");
        for (int i = 0; i < n; i++) {
            double t = 2 * Math.PI * i / n;
            vertices.add(new Point3D(0.5 * Math.cos(t), 0.5 * Math.sin(t), -0.5));
        }
        vertices.add(new Point3D(0, 0, 0.5));
        for (int i = 0; i < n; i++) {
            indices.add(i);
            indices.add((i + 1) % n);
            indices.add(i);
            indices.add(n);
        }
    }
}
