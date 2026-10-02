package transforms;

public class Mat4Identity extends Mat4 {
    public Mat4Identity() {
        super();
        for (int i = 0; i < 4; i++) mat[i][i] = 1;
    }
}
