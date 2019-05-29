import java.util.TreeSet;

import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdDraw;

/**
 * Algorithms 4th, Assignment 5.
 * @author Chenyin Gong
 */
public class PointSET
{
    private final TreeSet<Point2D> points;
    
    /**
     * Construct an empty set of points
     */
    public PointSET()
    {
        points = new TreeSet<Point2D>();
    }
    
    /**
     * Is the set empty?
     * @return true if the set is empty, false otherwise
     */
    public boolean isEmpty()
    {
        return points.isEmpty();
    }
    
    /**
     * Number of points in the set.
     * @return the number of points in the set
     */
    public int size()
    {
        return points.size();
    }
    
    /**
     * Add the point to the set (if it is not already in the set).
     * @param p the point to be added
     */
    public void insert(Point2D p)
    {
        if (p == null) throw new IllegalArgumentException("Argument to insert() is null.");
        points.add(p);
    }
    
    /**
     * Does the set contain point p?
     * @param p point to be tested
     * @return true if p is in the set, false otherwise
     */
    public boolean contains(Point2D p)
    {
        if (p == null) throw new IllegalArgumentException("Argument to contains() is null.");
        return points.contains(p);
    }
    
    /**
     * Draw all points to standard draw.
     */
    public void draw()
    {
        StdDraw.setPenColor(StdDraw.BLACK);
        for (Point2D point : points)
            point.draw();
    }
    
    /**
     * All points that are inside the rectangle (or on the boundary).
     * @param rect the rectangle to be tested
     * @return Queue of points that are inside the rectangle (or on the boundary)
     */
    public Iterable<Point2D> range(RectHV rect)
    {
        if (rect == null) throw new IllegalArgumentException("Argument to range() is null.");
        Queue<Point2D> queue = new Queue<>();
        for (Point2D point : points)
        {
            if (rect.contains(point))
                queue.enqueue(point);
        }
        return queue;
    }
    
    /**
     * A nearest neighbor in the set to point p; null if the set is empty
     * @param p to be tested
     * @return the point in the set nearest to p, null if set is empty
     */
    public Point2D nearest(Point2D p)
    {
        if (p == null) throw new IllegalArgumentException("Argument to nearest() is null.");
        
        // if set is empty, return null
        if (isEmpty()) return null;
        
        Stack<Point2D> stack = new Stack<>();
        double min = Double.POSITIVE_INFINITY;
        for (Point2D point : points)
        {   
            double dis = p.distanceSquaredTo(point);
            if (dis < min)
            {
                min = dis;
                stack.push(point);
            }
        }
        return stack.pop();
    }
}