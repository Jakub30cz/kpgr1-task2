package kpgr2.solids;

import transforms.Point3D;

/** Spiral on a cone: radius and height grow linearly over the given number of turns. */
public class ConicalSpiral extends CurvePolyline {
    private final double radius;
    private final double height;
    private final double turns;

    public ConicalSpiral(String name, double radius, double height, double turns, int color) {
        super(name, color, 60);
        this.radius = radius;
        this.height = height;
        this.turns = turns;
        rebuild();
    }

    @Override
    protected Point3D at(double u) {
        double a = u * turns * 2 * Math.PI;
        return new Point3D(radius * u * Math.cos(a), radius * u * Math.sin(a), height * u);
    }
}
