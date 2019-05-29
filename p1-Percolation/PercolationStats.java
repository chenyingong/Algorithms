import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;
import edu.princeton.cs.algs4.Stopwatch;

public class PercolationStats {
    private final double stddev;
    private final double mean;
    private final double confidenceLo;
    private final double confidenceHi;

    public PercolationStats(int n, int trials) {
        double confidence95 = 1.96;
        if (n <= 0 || trials <= 0) {
            throw new IllegalArgumentException(
                    "n: " + n + " or trials: " + trials + " is invalid.");
        }
        double[] trialsResults = new double[trials];
        for (int i = 0; i < trialsResults.length; i++) {
            trialsResults[i] = generateRandomPercolationModel(n);
        }
        mean = StdStats.mean(trialsResults);
        stddev = StdStats.stddev(trialsResults);
        confidenceLo = mean - confidence95 * stddev / Math.sqrt(trials);
        confidenceHi = mean + confidence95 * stddev / Math.sqrt(trials);
    }

    private double generateRandomPercolationModel(int n) {
        Percolation p = new Percolation(n);
        double openCount = 0;
        while (!p.percolates()) {
            int row = StdRandom.uniform(n) + 1;
            int col = StdRandom.uniform(n) + 1;

            if (!p.isOpen(row, col)) {
                p.open(row, col);
                openCount++;
            }
        }
        return openCount / (n * n);
    }

    public double mean() {
        return mean;
    }

    public double stddev() {
        return stddev;
    }

    public double confidenceLo() {
        return confidenceLo;
    }

    public double confidenceHi() {
        return confidenceHi;
    }

    public static void main(String[] args) {
        Stopwatch s = new Stopwatch();
        int n = Integer.parseInt(args[0]);
        int t = Integer.parseInt(args[1]);
        PercolationStats ps = new PercolationStats(n, t);
        StdOut.println("time used               = " + s.elapsedTime());
        StdOut.println("mean                    = " + ps.mean());
        StdOut.println("stddev                  = " + ps.stddev());
        StdOut.println(
                "95% confidence interval = " + "[" + ps.confidenceLo() + "," + ps.confidenceHi()
                        + "]");
    }
}
