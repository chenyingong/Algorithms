

import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation {
    private boolean[] blocked;
    private final WeightedQuickUnionUF uf1, uf2;
    private final int n;
    private int openedNum;

    public Percolation(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("n:" + n + " is invalid.");
        }
        this.n = n;
        openedNum = 0;
        uf1 = new WeightedQuickUnionUF(n * n + 2);
        uf2 = new WeightedQuickUnionUF(n * n + 2);
        blocked = new boolean[n * n];
        for (int i = 0; i < blocked.length; i++) {
            blocked[i] = true;
        }

        for (int i = 0; i < n; i++) {
            uf1.union(i, n * n);
            uf2.union(i, n * n);
            uf1.union((n - 1) * n + i, n * n + 1);
        }
    }

    private void validate(int row, int col) {
        if (row <= 0 || row > n || col <= 0 || col > n) {
            throw new IllegalArgumentException("row: " + row + " or col: " + col + " is invalid.");
        }
    }

    private void union(int p, int q) {
        uf1.union(p, q);
        uf2.union(p, q);
    }

    public void open(int row, int col) {
        validate(row, col);
        row--;
        col--;
        int key = row * n + col;
        if (!blocked[key]) {
            return;
        }
        blocked[key] = false;
        openedNum++;

        if (col > 0 && !blocked[key - 1]) {
            union(key, key - 1);
        }
        if (row > 0 && !blocked[key - n]) {
            union(key, key - n);
        }
        if (col < n - 1 && !blocked[key + 1]) {
            union(key, key + 1);
        }
        if (row < n - 1 && !blocked[key + n]) {
            union(key, key + n);
        }
    }

    public boolean isOpen(int row, int col) {
        validate(row, col);
        row--;
        col--;
        int key = row * n + col;
        return !blocked[key];
    }

    public boolean isFull(int row, int col) {
        validate(row, col);
        row--;
        col--;
        int key = row * n + col;
        return (uf2.connected(key, n * n) && !blocked[key]);
    }

    public int numberOfOpenSites() {
        return openedNum;
    }

    public boolean percolates() {
        if (n == 1) return !blocked[0];
        else return uf1.connected(n * n, n * n + 1);
    }

    public static void main(String[] args) {
        Percolation p = new Percolation(3);
        p.open(0, 1);
        p.open(1, 1);
        p.open(2, 1);
        StdOut.println("Percolated: " + p.percolates());

    }
}
