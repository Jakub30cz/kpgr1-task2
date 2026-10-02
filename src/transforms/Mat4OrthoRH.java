package transforms;

/** Right-handed orthographic projection, camera looks down -Z, clip z in [0, 1]. */
public class Mat4OrthoRH extends Mat4 {
    public Mat4OrthoRH(double width, double height, double zNear, double zFar) {
        super();
        mat[0][0] = 2.0 / width;
        mat[1][1] = 2.0 / height;
        mat[2][2] = 1.0 / (zNear - zFar);
        mat[3][2] = zNear / (zNear - zFar);
        mat[3][3] = 1;
    }
}
