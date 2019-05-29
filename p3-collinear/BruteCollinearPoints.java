import java.util.ArrayList;
import java.util.Arrays;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;

public class BruteCollinearPoints
{
    private final ArrayList<LineSegment> lines;
    private final Point[] points;
    
    /**
     * Initialize a new BruteCollinearPoints.
     * Finds all line segments containing 4 points.
     * 
     * @param points an array of points
     * @throws IllegalArumentException if argument is null, or exist null point,
     * or exist repeated point
     */
    public BruteCollinearPoints(Point[] pointsIn)
    {
        // if the argument to the constructor is null
        if (pointsIn == null) throw new IllegalArgumentException("argument is null");
        // if any point in the array is null
        int n = pointsIn.length;
        for (int i = 0; i < n; i++)
        {
            if (pointsIn[i] == null) throw new IllegalArgumentException("exist null point");
        }
        // if the argument to the constructor contains a repeated point
        points = new Point[n];
        for (int i = 0; i < n; i++)
        {
            points[i] = pointsIn[i];
        }
        Arrays.sort(points); // sort points
        for (int i = 1; i < n; i++)
        {
            if (points[i-1].compareTo(points[i]) == 0) throw new IllegalArgumentException("exist repeated point");
        }
        
        lines = new ArrayList<LineSegment>();
               
        for (int i = 0; i < n - 3; i++)
        {
            for (int j = i + 1; j < n - 2; j++)
            {
                double k12 = points[j].slopeTo(points[i]);
                for (int k = j + 1; k < n - 1; k++)
                {
                    double k13 = points[k].slopeTo(points[i]);
                    if (k12 != k13) continue;
                    for (int m = k + 1; m < n; m++)
                    {
                        double k14 = points[m].slopeTo(points[i]);
                        if (k12 != k14) continue;
                        LineSegment line = new LineSegment(points[i], points[m]);
                        lines.add(line);
                    }
                }
            }
        }
    }
    
    /**
     * The number of line segments.
     * 
     * @return the number of line segments
     */
    public int numberOfSegments()
    {
        return lines.size();
    }
    
    /**
     * The line segments
     * @return the line segments
     */
    public LineSegment[] segments()
    {
        LineSegment[] segments = new LineSegment[lines.size()];
        int index = 0;
        for (LineSegment line : lines)
        {
            segments[index++] = line;
        }
        return segments;
    }
     
    /**
    * Unit tests the BruteCollinearPoints data type.
    */
    public static void main(String[] args)
    {
        // read the n points from a file
        In in = new In(args[0]);
        int n = in.readInt();
        Point[] points = new Point[n];
        for (int i = 0; i < n; i++)
        {
            int x = in.readInt();
            int y = in.readInt();
            points[i] = new Point(x, y);
        }
        
        // draw the points
        StdDraw.enableDoubleBuffering();
        StdDraw.setXscale(0, 32768);
        StdDraw.setYscale(0, 32768);
        for (Point p : points)
        {
            p.draw();
        }
        StdDraw.show();
        
        // print and draw the line segments
        BruteCollinearPoints collinear = new BruteCollinearPoints(points);
        System.out.println("The number of line segments: " + collinear.numberOfSegments());
        for (LineSegment segment : collinear.segments())
        {
            System.out.println(segment);
            segment.draw();
        }
        StdDraw.show();
    }
}