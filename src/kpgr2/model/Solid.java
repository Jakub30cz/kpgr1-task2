package kpgr2.model;

import java.util.ArrayList;
import java.util.List;
import transforms.Mat4;
import transforms.Mat4Identity;
import transforms.Point3D;
import transforms.Vec3D;

public abstract class Solid {
    protected final List<Point3D> vertices = new ArrayList<>();
    /** Pairs of vertex indices, one pair per edge. */
    protected final List<Integer> indices = new ArrayList<>();
    private final String name;
    private final int color;
    private Mat4 model = new Mat4Identity();

    protected Solid(String name, int color) {
        this.name = name;
        this.color = color;
    }

    public String getName() { return name; }

    public int getColor() { return color; }

    public List<Point3D> getVertices() { return vertices; }

    public List<Integer> getIndices() { return indices; }

    public Mat4 getModel() { return model; }

    public void setModel(Mat4 model) { this.model = model; }

    public int getEdgeColor(int edge) { return getColor(); }

    public Vec3D getWorldCenter() {
        if (vertices.isEmpty()) return new Vec3D(0, 0, 0);
        double sx = 0, sy = 0, sz = 0;
        for (Point3D v : vertices) {
            Point3D p = v.mul(model);
            sx += p.x();
            sy += p.y();
            sz += p.z();
        }
        int n = vertices.size();
        return new Vec3D(sx / n, sy / n, sz / n);
    }
}
