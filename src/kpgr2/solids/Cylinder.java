package kpgr2.solids;

import kpgr2.model.Solid;
import transforms.Point3D;

public class Cylinder extends Solid {
    public Cylinder(int n) {
        super("Cylinder", 0x9370DB);
        if (n < 3) throw new IllegalArgumentException("n must be >= 3");
        for (int i = 0; i < n; i++) {
            double t = 2 * Math.PI * i / n;
            vertices.add(new Point3D(0.5 * Math.cos(t), 0.5 * Math.sin(t), -0.5));
        }
        for (int i = 0; i < n; i++) {
            double t = 2 * Math.PI * i / n;
            vertices.add(new Point3D(0.5 * Math.cos(t), 0.5 * Math.sin(t), 0.5));
        }
        for (int i = 0; i < n; i++) {
            int next = (i + 1) % n;
            indices.add(i);
            indices.add(next);
            indices.add(n + i);
            indices.add(n + next);
            indices.add(i);
            indices.add(n + i);
        }
    }
}
