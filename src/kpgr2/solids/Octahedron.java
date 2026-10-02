package kpgr2.solids;

import kpgr2.model.Solid;
import transforms.Point3D;

public class Octahedron extends Solid {
    public Octahedron() {
        super("Octahedron", 0xFF69B4);
        double a = 0.5;
        vertices.add(new Point3D(a, 0, 0));
        vertices.add(new Point3D(-a, 0, 0));
        vertices.add(new Point3D(0, a, 0));
        vertices.add(new Point3D(0, -a, 0));
        vertices.add(new Point3D(0, 0, a));
        vertices.add(new Point3D(0, 0, -a));
        // Every vertex connects to all but its opposite one (pairs 0/1, 2/3, 4/5).
        for (int i = 0; i < 6; i++) {
            for (int j = i + 1; j < 6; j++) {
                if (i / 2 == j / 2) continue;
                indices.add(i);
                indices.add(j);
            }
        }
    }
}
