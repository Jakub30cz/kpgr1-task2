package transforms;

/**
 * Right-handed perspective projection, camera looks down -Z, clip z in [0, w].
 * k = height / width.
 */
public class Mat4PerspRH extends Mat4 {
    public Mat4PerspRH(double fovY, double k, double zNear, double zFar) {
        super();
        double f = 1.0 / Math.tan(fovY / 2);
        mat[0][0] = f * k;
        mat[1][1] = f;
        mat[2][2] = zFar / (zNear - zFar);
        mat[2][3] = -1;
        mat[3][2] = zNear * zFar / (zNear - zFar);
    }
}
