
public class Matrix {
    public int[][] M;
    public boolean transposed;

    Matrix(int[][] M, boolean transposed) {
        this.M = M;
        this.transposed = transposed;
    }

    public int getMatrix(int r, int c) {
        if (transposed)
            return M[c][r];
        return M[r][c];
    }

    public int[][] exportMatrix() {
        int[][] m = new int[M[0].length][M.length];
        for (int i = 0; i < m.length; i++)
            for (int j = 0; j < m[0].length; j++)
                m[i][j] = M[j][i];
        return m;
    }
}
