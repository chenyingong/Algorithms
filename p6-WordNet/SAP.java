import java.util.ArrayList;

import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.LinearProbingHashST;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.StdOut;
/**
 * Algorithms 4th, assignment 6
 * @author Chenyin Gong
 *
 */
public class SAP
{
    private static final int INFINITY = Integer.MAX_VALUE;
    private final Digraph G;
    private LinearProbingHashST<Integer, Boolean> markedA;
    private LinearProbingHashST<Integer, Boolean> markedB;
    private LinearProbingHashST<Integer, Integer> distA;
    private LinearProbingHashST<Integer, Integer> distB;
    private int ancestor;           // common ancestor in SAP
    private int length;             // length of SAP
    /**
     * constructor takes a digraph (not necessarily a DAG).
     * @param G a digraph
     */
    public SAP(Digraph G)
    {
        this.G = new Digraph(G);    // make a defensive copy of G
    }
    
    // throw an IllegalArgumentException unless {@code 0 <= v < V}
    private void validateVertex(int v) 
    {
        if (v < 0 || v >= G.V())
            throw new IllegalArgumentException("vertex " + v + " is not between 0 and " + (G.V()-1));
    }
    
    /**
     * returns length of shortest ancestral path between v and w.
     * @param v vertex v
     * @param w vertex w
     * @return length of shortest ancestral path between v and w;
     *          -1 if no such path
     */
    public int length(int v, int w)
    {
        validateVertex(v);
        validateVertex(w);
        bfs(v, w);
        return length;
    }
    
    /**
     * returns a common ancestor of v and w that participates in
     * a shortest ancestral path; -1 if no such path.
     * @param v the vertex v
     * @param w the vertex w
     * @return a common ancestor of v and w that participates in
     *          a shortest ancestral path; -1 if no such path
     */
    public int ancestor(int v, int w)
    {
        bfs(v, w);
        return ancestor;
    }
    
    /**
     * returns length of shortest ancestral path between any vertex
     * in v and any vertex in w; -1 if no such path.
     * @param v a set of vertices
     * @param w a set of vertices
     * @return length of shortest ancestral path between any vertex
     *          in v and any vertex in w; -1 if no such path
     */
    public int length(Iterable<Integer> v, Iterable<Integer> w)
    {
        if (v == null || w == null)
            throw new IllegalArgumentException("invalid argument: null");
        bfs(v, w);
        return length;
    }
    
    /**
     * returns a common ancestor that participates in shortest ancestral
     * path; -1 if no such path.
     * @param v a set of vertices
     * @param w a set of vertices
     * @return a common ancestor that participates in shortest ancestral
     *          path; -1 if no such path
     */
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w)
    {
        if (v == null || w == null)
            throw new IllegalArgumentException("invalid argument: null");
        bfs(v, w);
        return ancestor;
    }
    
    /**************************************************************** 
     *      Helper functions -- BREADTH FIRST DIRECTED PATHS
     ****************************************************************/
    private void bfs(Iterable<Integer> a, Iterable<Integer> b)
    {   
        Queue<Integer> queueA = new Queue<>();
        Queue<Integer> queueB = new Queue<>();
        markedA = new LinearProbingHashST<>();
        markedB = new LinearProbingHashST<>();
        distA   = new LinearProbingHashST<>();
        distB   = new LinearProbingHashST<>();
        
        for (Integer v : a)
        {   
            if (v == null) throw new IllegalArgumentException("invalid argument: null");
            validateVertex(v);
            queueA.enqueue(v);
            markedA.put(v, true);
            distA.put(v, 0);
        }
        for (Integer w : b)
        {
            if (w == null) throw new IllegalArgumentException("invalid argument: null");
            validateVertex(w);
            if (markedA.contains(w)) // check whether sharing the same vertex in A and B
            {
                ancestor = w;
                length = 0;
                return;
            }
            queueB.enqueue(w);
            markedB.put(w, true);
            distB.put(w, 0);
        }
        
        bfs(queueA, queueB);
    }
    
    private void bfs(int v, int w)
    {   
        validateVertex(v);
        validateVertex(w);
        if (v == w)
        {
            ancestor = v;
            length = 0;
            return;
        }
        
        Queue<Integer> queueA = new Queue<>();
        Queue<Integer> queueB = new Queue<>();
        markedA = new LinearProbingHashST<>();
        markedB = new LinearProbingHashST<>();
        distA   = new LinearProbingHashST<>();
        distB   = new LinearProbingHashST<>();
        
        queueA.enqueue(v);
        markedA.put(v, true);
        distA.put(v, 0);
        
        queueB.enqueue(w);
        markedB.put(w, true);
        distB.put(w, 0);
        
        bfs(queueA, queueB);
    }
    
    private void bfs(Queue<Integer> queueA, Queue<Integer> queueB)
    {
        length = INFINITY;
        ancestor = -1;
        
        while (!queueA.isEmpty() || !queueB.isEmpty())
        {
            if (!queueA.isEmpty())
            {
                int n = queueA.size();
                for (int i = 0; i < n; i++)
                {
                    int a = queueA.dequeue();
                    for (int v : G.adj(a))
                    {
                        if (!markedA.contains(v))
                        {
                            int dist = distA.get(a) + 1;
                            if (dist >= length) return;
                            distA.put(v, dist);
                            markedA.put(v, true);
                            queueA.enqueue(v);
                        }
                        if (markedB.contains(v))
                        {
                            int len = distB.get(v) + distA.get(v);
                            if (len < length)
                            {
                                length = len;
                                ancestor = v;
                            }
                        }
                    }
                }
            }
            
            if (!queueB.isEmpty())
            {
                int n = queueB.size();
                for (int i = 0; i < n; i++)
                {
                    int b = queueB.dequeue();
                    for (int w : G.adj(b))
                    {
                        if (!markedB.contains(w))
                        {
                            int dist = distB.get(b) + 1;
                            if (dist >= length) return;
                            distB.put(w, dist);
                            markedB.put(w, true);
                            queueB.enqueue(w);
                        }
                        if (markedA.contains(w))
                        {
                            int len = distA.get(w) + distB.get(w);
                            if (len < length)
                            {
                                length = len;
                                ancestor = w;
                            }
                        }
                    }
                }
            }
        }
        if (ancestor == -1) length = -1;
        return;
    }
    
    // unit testing
    public static void main(String[] args)
    {
        In in = new In(args[0]);
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);
        
        // test file: digraph25.txt
        ArrayList<Integer> v = new ArrayList<>();
        v.add(null);
        v.add(7);
        ArrayList<Integer> w = new ArrayList<>();
        w.add(1);
        w.add(2);
        
        int length   = sap.length(v, w);
        int ancestor = sap.ancestor(v, w);
        StdOut.printf("length = %d, ancestor = %d\n", length, ancestor); 
        
        /* while (!StdIn.isEmpty())
        {
            int v = StdIn.readInt();
            int w = StdIn.readInt();
            
            int length   = sap.length(v, w);
            int ancestor = sap.ancestor(v, w);
            StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
        } */
    }
}