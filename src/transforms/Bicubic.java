package transforms;

/** Bicubic patch: 16 control points, row-major (index = 4 * i + j, i along u, j along v). */
public class Bicubic {
    private final Mat4 basis;
    private final Point3D[] p;

    public Bicubic(Mat4 basis, Point3D[] p16) {
        if (p16.length != 16) throw new IllegalArgumentException("16 control points required");
        this.basis = basis;
        this.p = p16.clone();
    }

    public Point3D compute(double u, double v) {
        double[] wu = Cubic.weights(basis, u);
        double[] wv = Cubic.weights(basis, v);
        double x = 0, y = 0, z = 0;
        for (int i = 0; i < 4; i++)
            for (int j = 0; j < 4; j++) {
                double w = wu[i] * wv[j];
                Point3D q = p[i * 4 + j];
                x += w * q.x();
                y += w * q.y();
                z += w * q.z();
            }
        return new Point3D(x, y, z);
    }
}
