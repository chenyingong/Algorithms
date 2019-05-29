import java.util.Comparator;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdOut;

/**
 * Algorithms 4th, Programming assignment 4: 8 Puzzle.
 * 
 * @author Chenyin Gong
 * @date 2019-02-19
 */
public class Solver
{
    private int moves;
    private boolean solvable = false;
    private final Stack<Board> solutionStack = new Stack<>();
    
    // inner class
    private class Node
    {
        Board board;
        Node predecessor;
        int moves;
        boolean isTwin;
        int manhattan;
        Node(Board board, Node predecessor, int moves, boolean isTwin)
        {
            this.board = board;
            this.predecessor = predecessor;
            this.moves = moves;
            this.isTwin = isTwin;
            this.manhattan = board.manhattan();
        }
    }
    
    // inner class comparator
    private class MyComparator implements Comparator<Node>
    {
        public int compare(Node node1, Node node2)
        {
            return (node1.manhattan + node1.moves) - (node2.manhattan + node2.moves);
        }
    }
    
    /**
     * Find a solution to the initial board (using the A* algorithm).
     * 
     * @param initial the initial board to be solved
     */
    public Solver(Board initial)
    {
        if (initial == null)
            throw new IllegalArgumentException("Argument is in valid.");
        
        MinPQ<Node> pq = new MinPQ<>(new MyComparator());
        pq.insert(new Node(initial, null, 0, false));
        pq.insert(new Node(initial.twin(), null, 0, true));
        Node node;
        
        while (true)
        {
            node = pq.delMin();
            
            if (node.board.isGoal())
            {
                if (node.isTwin)
                    this.moves = -1;
                else 
                {
                    this.solvable = true;
                    this.moves = node.moves;
                    
                    solutionStack.push(node.board);
                    while (node.predecessor != null)
                    {
                        node = node.predecessor;
                        solutionStack.push(node.board);
                    }
                }
                break;
            }
            
            for (Board neighbor : node.board.neighbors())
            {
                if (node.predecessor == null || !neighbor.equals(node.predecessor.board))
                    pq.insert(new Node(neighbor, node, node.moves + 1, node.isTwin));
            }
        }
    }
    
    /**
     * Is the initial board solvable?
     * 
     * @return true if the initial board is solvable, false otherwise
     */
    public boolean isSolvable()
    {
        return this.solvable;
    }
    
    /**
     * Minimum number of moves to solve initial board.
     * 
     * @return min number of moves to solve initial board; -1 if unsolvable
     */
    public int moves()
    {
        return this.moves;
    }
    
    /**
     * Sequence of boards in a shortest solution; null if unsolvable.
     * 
     * @return sequence of boards in a shortest solution; null if unsolvable
     */
    public Iterable<Board> solution()
    {
        if (this.solvable) return solutionStack;
        else               return null;
    }
    
    // unit tests
    public static void main(String[] args)
    {
        // create initial board from file
        In in = new In(args[0]);
        int n = in.readInt();
        int[][] blocks = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                blocks[i][j] = in.readInt();
        Board initial = new Board(blocks);
        
        // solve the puzzle
        Solver solver = new Solver(initial);
        
        // print solution to standard output
        if (!solver.isSolvable())
        {
            StdOut.println("No solution possible");
        }
        else 
        {
            StdOut.println("Minmum number of moves = " + solver.moves());
            for (Board board : solver.solution())
                StdOut.println(board);
        }
    }
}