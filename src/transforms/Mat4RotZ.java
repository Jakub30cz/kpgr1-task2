package transforms;

/** Right-handed rotation about Z (row vectors): (1,0,0) maps to (cos a, sin a, 0). */
public class Mat4RotZ extends Mat4Identity {
    public Mat4RotZ(double a) {
        double c = Math.cos(a), s = Math.sin(a);
        mat[0][0] = c;
        mat[0][1] = s;
        mat[1][0] = -s;
        mat[1][1] = c;
    }
}
