package transforms;

/** Right-handed rotation about Y (row vectors): (0,0,1) maps to (sin a, 0, cos a). */
public class Mat4RotY extends Mat4Identity {
    public Mat4RotY(double a) {
        double c = Math.cos(a), s = Math.sin(a);
        mat[0][0] = c;
        mat[0][2] = -s;
        mat[2][0] = s;
        mat[2][2] = c;
    }
}
