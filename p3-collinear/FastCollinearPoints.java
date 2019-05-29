import java.util.ArrayList;
import java.util.Arrays;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;

public class FastCollinearPoints
{
    private final ArrayList<LineSegment> lines = new ArrayList<>();
    private final Point[] points;
    
    public FastCollinearPoints(Point[] pointsIn)
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
        
        Point[] otherPoints = new Point[n - 1];
        Point checkPoint;
             
        for (int i = 0; i < n; i++)
        {
            checkPoint = points[i];
            // copy points without checkPoint
            for (int j = 0; j < n - 1; j++)
            {
                if (j < i) otherPoints[j] = points[j];
                else otherPoints[j] = points[j + 1];
            }
            
            Arrays.sort(otherPoints, checkPoint.slopeOrder());
            // check if any 3 (or more) adjacent points in the sorted order have equal slopes with respect to p[i] 
            int count = 2;
            for (int k = 1; k < n - 1; k++)
            {
                if (checkPoint.slopeTo(otherPoints[k-1]) == checkPoint.slopeTo(otherPoints[k]))
                {
                    count++;
                    if (k == n - 2) 
                    {
                        if (count >= 4 && checkPoint.compareTo(otherPoints[k-count+2]) == -1)
                        {
                            LineSegment line = new LineSegment(checkPoint, otherPoints[k]);
                            lines.add(line);
                        }
                    }
                }
                else
                {
                    if (count >= 4 && checkPoint.compareTo(otherPoints[k-count+1]) == -1)
                    {
                        LineSegment line = new LineSegment(checkPoint, otherPoints[k-1]);
                        lines.add(line);
                    }
                    count = 2;
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
         FastCollinearPoints collinear = new FastCollinearPoints(points);
         System.out.println("The number of line segments: " + collinear.numberOfSegments());
         for (LineSegment segment : collinear.segments())
         {
             System.out.println(segment);
             segment.draw();
         }
         StdDraw.show();
     }
}