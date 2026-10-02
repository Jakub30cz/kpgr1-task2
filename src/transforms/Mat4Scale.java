package transforms;

public class Mat4Scale extends Mat4Identity {
    public Mat4Scale(double x, double y, double z) {
        mat[0][0] = x;
        mat[1][1] = y;
        mat[2][2] = z;
    }

    public Mat4Scale(double s) {
        this(s, s, s);
    }
}
