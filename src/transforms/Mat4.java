package transforms;

/** 4x4 matrix for row-vector convention (p' = p * M). */
public class Mat4 {
    protected final double[][] mat;

    public Mat4() {
        mat = new double[4][4];
    }

    public Mat4(double[][] m) {
        mat = new double[4][4];
        for (int r = 0; r < 4; r++) System.arraycopy(m[r], 0, mat[r], 0, 4);
    }

    public Mat4 mul(Mat4 o) {
        double[][] res = new double[4][4];
        for (int r = 0; r < 4; r++)
            for (int c = 0; c < 4; c++) {
                double s = 0;
                for (int k = 0; k < 4; k++) s += mat[r][k] * o.mat[k][c];
                res[r][c] = s;
            }
        return new Mat4(res);
    }

    public double get(int r, int c) {
        return mat[r][c];
    }

    public Mat4 transpose() {
        double[][] res = new double[4][4];
        for (int r = 0; r < 4; r++)
            for (int c = 0; c < 4; c++) res[c][r] = mat[r][c];
        return new Mat4(res);
    }

    public boolean approxEquals(Mat4 o, double eps) {
        for (int r = 0; r < 4; r++)
            for (int c = 0; c < 4; c++)
                if (Math.abs(mat[r][c] - o.mat[r][c]) > eps) return false;
        return true;
    }
}
