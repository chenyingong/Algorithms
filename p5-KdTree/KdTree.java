import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;

/**
 * Algorithms 4th, Assignment 5.
 * @author Chenyin Gong
 */
public class KdTree
{
    private Node root;
    
    private static class Node
    {
        private final Point2D p;   // the point
        private RectHV rect; // the axis-aligned rectangle corresponding to this node
        private Node lb;     // the left/bottom subtree
        private Node rt;     // the right/top subtree
        private int  n;      // tree size
        public Node(Point2D p, int n)
        {
            this.p = p;
            this.n = n;
        }
    }
    
    /**
     * Construct an empty tree of points
     */
    public KdTree()
    {
        
    }
    
    /**
     * Is the tree empty?
     * @return true if the tree is empty, false otherwise
     */
    public boolean isEmpty()
    {
        return size() == 0;
    }
    
    /**
     * Number of points in the tree.
     * @return the number of points in the tree
     */
    public int size()
    {
        return size(root);
    }
    
    private int size(Node x)
    {
        if (x == null) return 0;
        else return x.n;
    }
    
    /**
     * Add the point to the tree (if it is not already in the set).
     * @param p the point to be added
     */
    public void insert(Point2D p)
    {
        if (p == null) throw new IllegalArgumentException("Argument to insert() is null.");
        root = insert(root, p, null, true, true, null);
    }
    
    private Node insert(Node node, Point2D point, Point2D fp, boolean vertical, boolean smaller, RectHV rect)
    {
        if (node == null) // point not in the tree
        {
            if (isEmpty())
            {
                root = new Node(point, 1);
                root.rect = new RectHV(0, 0, 1, 1);
                return root;
            }
            
            node = new Node(point, 1);
            setRect(node, vertical, smaller, rect, fp);
            return node;
        }
        
        if (point.equals(node.p)) return node; // point already exists, do nothing

        if (vertical)
        {
            if (point.x() < node.p.x())
                node.lb = insert(node.lb, point, node.p, false, true, node.rect); // x1 < x2
            else
                node.rt = insert(node.rt, point, node.p, false, false, node.rect); // x1 >= x2; if x1 = x2, y1 != y2
        }
        else // horizontal
        {
            if (point.y() < node.p.y())
                node.lb = insert(node.lb, point, node.p, true, true, node.rect); // y1 < y2
            else 
                node.rt = insert(node.rt, point, node.p, true, false, node.rect); // y1 >= y2; if y1 = y2, x1 != x2
        }
        node.n = size(node.lb) + size(node.rt) + 1;
        return node;
    }
    
    // set up left child rect
    private void setRect(Node node, boolean horizontal, boolean smaller, RectHV rect, Point2D fp)
    {
        double xmin = rect.xmin();
        double ymin = rect.ymin();
        double xmax = rect.xmax();
        double ymax = rect.ymax();
        
        if      (!horizontal && smaller) // left
            node.rect = new RectHV(xmin, ymin, fp.x(), ymax);
        else if (!horizontal && !smaller) // right
            node.rect = new RectHV(fp.x(), ymin, xmax, ymax); 
        else if (horizontal && smaller) // bottom
            node.rect = new RectHV(xmin, ymin, xmax, fp.y());
        else                            // top
            node.rect = new RectHV(xmin, fp.y(), xmax, ymax);
    }
    
    /**
     * Does the tree contain point p?
     * @param p point to be tested
     * @return true if p is in the set, false otherwise
     */
    public boolean contains(Point2D p)
    {
        if (p == null) throw new IllegalArgumentException("Argument to contains() is null");
        return contains(root, p, true);
    }
    
    private boolean contains(Node node, Point2D point, boolean vertical)
    {
        if (node == null) return false; // empty tree or point not in the tree
        if (point.equals(node.p)) return true; // point in the tree
        if (vertical)
        {
            if (point.x() < node.p.x()) return contains(node.lb, point, false);
            else                             return contains(node.rt, point, false);
        }
        else // horizontal
        {
            if (point.y() < node.p.y()) return contains(node.lb, point, true);
            else                             return contains(node.rt, point, true);
        }
    }
    
    /**
     * Draw all points to standard draw.
     */
    public void draw()
    {
        draw(root, true);
    }
    
    private void draw(Node node, boolean vertical)
    {
        if (node == null) return; // empty tree or getting to the bottom of the tree
        // draw a rectangle (0,0) --> (1, 1)
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.rectangle(0.5, 0.5, 0.5, 0.5);
        // draw the point
        StdDraw.setPenRadius(0.01);
        node.p.draw();
        // draw the splitting vertical line
        if (vertical)
        {
            StdDraw.setPenColor(StdDraw.RED);
            StdDraw.line(node.p.x(), node.rect.ymin(), node.p.x(), node.rect.ymax());
        }
        else
        {
            StdDraw.setPenColor(StdDraw.BLUE);
            StdDraw.line(node.rect.xmin(), node.p.y(), node.rect.xmax(), node.p.y());
        }
        draw(node.lb, !vertical);
        draw(node.rt, !vertical);
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
        range(queue, root, rect);
        return queue;
        
    }
    
    private void range(Queue<Point2D> queue, Node node, RectHV rect)
    {  
        if (node == null) return;
        if (node.rect.intersects(rect))
        {
            if (rect.contains(node.p))
                queue.enqueue(node.p);
            if (node.lb != null)
                range(queue, node.lb, rect);
            if (node.rt != null)
                range(queue, node.rt, rect);
        }
    }
    
    /**
     * A nearest neighbor in the tree to point p; null if the tree is empty
     * @param p to be tested
     * @return the point in the tree nearest to p, null if tree is empty
     */
    public Point2D nearest(Point2D p)
    {
        if (p == null) throw new IllegalArgumentException("Argument to nearest() is null.");
        if (isEmpty()) return null; // if tree is empty, return null
        
        return nearest(root, p, root.p);
    }
    
    private Point2D nearest(Node node, Point2D point, Point2D nearPoint)
    {   
        if (node == null) return nearPoint;
        if (node.p.equals(point)) return node.p;
        
        double min = nearPoint.distanceSquaredTo(point);
        
        if (Double.compare(node.rect.distanceSquaredTo(point), min) >= 0)
            return nearPoint;
        
        double dis = node.p.distanceSquaredTo(point);
        if (Double.compare(dis, min) == -1)
        {
            min = dis;
            nearPoint = node.p;
        }
        
        if (node.rt != null && node.rt.rect.distanceSquaredTo(point) == 0)
        {
            nearPoint = nearest(node.rt, point, nearPoint);
            if (node.lb == null || Double.compare(node.lb.rect.distanceSquaredTo(point), min) >= 0)
                return nearPoint;
            else
                nearPoint = nearest(node.lb, point, nearPoint);
        }
        else if (node.rt != null && node.rt.rect.distanceSquaredTo(point) > 0)
        {
            nearPoint = nearest(node.lb, point, nearPoint);
            if (Double.compare(node.rt.rect.distanceSquaredTo(point), min) >= 0)
                return nearPoint;
            else
                nearPoint = nearest(node.rt, point, nearPoint);
        }
        else nearPoint = nearest(node.lb, point, nearPoint);
        return nearPoint;
    }
}