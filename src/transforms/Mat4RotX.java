package transforms;

/** Right-handed rotation about X (row vectors): (0,1,0) maps to (0, cos a, sin a). */
public class Mat4RotX extends Mat4Identity {
    public Mat4RotX(double a) {
        double c = Math.cos(a), s = Math.sin(a);
        mat[1][1] = c;
        mat[1][2] = s;
        mat[2][1] = -s;
        mat[2][2] = c;
    }
}
