import edu.princeton.cs.algs4.Stack;

/**
 * Algorithms 4th, Programming assignment 4: 8 Puzzle.
 * 
 * @author Chenyin Gong
 * @date 2019-02-19
 */
public class Board
{
    private final int n;
    private final char[][] blocks;
    
    /**
     * Construct a board from an n-by-n array of blocks.
     * (where blocks[i][j] = block in row i, column j)
     * 
     * @param blocks an n-by-n array
     */
    public Board(int[][] blocks) 
    {
        this.n = blocks.length;
        // corner cases
        for (int i = 0; i < n; i++) // blocks.length is the number of rows
        {
            if (n != blocks[i].length) 
                throw new IllegalArgumentException("Numbers of rows and columns of the array should be equal.");
        }
        
        this.blocks = new char[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                this.blocks[i][j] = (char) blocks[i][j];
    }
    
    /**
     * Board dimension n.
     * 
     * @return board dimension n
     */
    public int dimension()
    {
        return this.n;
    }
    
    /**
     * Number of blocks out of place.
     * 
     * @return number of blocks out of place
     */
    public int hamming()
    {
        // if is not blank
        int hamming = 0;
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                if (blocks[i][j] != 0 && blocks[i][j] != i * n + 1 + j) hamming++;
        return hamming;
    }
    
    /**
     * Sum of Manhattan distances between blocks and goal.
     * 
     * @return sum of Manhattan distances between blocks and goal
     */
    public int manhattan()
    {
        // if is not blank
        int manhattan = 0;
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                if (blocks[i][j] != 0)
                {
                    int row = (blocks[i][j] - 1) / n;
                    int column = (blocks[i][j] - 1) - row * n;
                    manhattan += Math.abs(i - row) + Math.abs(j - column);
                }
        return manhattan;
    }
    
    /**
     * Is this board the goal board?
     * 
     * @return true if the board is the goal board, otherwise false;
     */
    public boolean isGoal()
    {
        return hamming() == 0;
    }
    
    /**
     * A board that is obtained by exchanging any pair of blocks.
     * 
     * @return a board that is obtained by exchanging any pair of blocks
     */
    public Board twin()
    {
        // copy the blocks
        int[][] blocksTwin = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                blocksTwin[i][j] = blocks[i][j];
        
        int swap;
        int column1 = 0; // local variable must be initialized
        int column2 = 0;
        
        // find the first location to be swapped on the first row 
        for (int j = 0; j < n; j++)
        {
            if (blocksTwin[0][j] != 0) 
            {
                
                column1 = j;
                break;
            }
        }
        
        // find the second location on the second row to swap with the first one
        for (int j = 0; j < n; j++)
        {
            if (blocksTwin[1][j] != 0) 
            {
                column2 = j;
                break;
            }
        }
        
        // swap them
        swap = blocksTwin[0][column1];
        blocksTwin[0][column1] = blocksTwin[1][column2];
        blocksTwin[1][column2] = swap;
        
        Board twinBoard = new Board(blocksTwin);
        return twinBoard;
    }
    
    /**
     * Does this board equal y?
     * 
     * @return true if this board equal y, false otherwise
     */
    public boolean equals(Object y)
    {
        if (y == this) return true;
        if (y == null) return false;
        if (y.getClass() != this.getClass()) return false;
        Board that = (Board) y;
        if (this.n != that.n) return false;
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                if (this.blocks[i][j] != that.blocks[i][j]) return false;
        return true;
    }
    
    /**
     * All neighboring boards.
     * 
     * @return a stack of its neighbors
     */
    public Iterable<Board> neighbors()
    {
        Stack<Board> neighbors = new Stack<>();
        
        // find the blank square
        int row0 = 0;
        int column0 = 0;
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
              if (blocks[i][j] == 0)
              {
                  row0 = i;
                  column0 = j;
              }
        
        // the left neighbor
        if (column0 - 1 >= 0)
        {
            int[][] leftBlocks = new int[n][n];
            for (int i = 0; i < n; i++)
                for (int j = 0; j < n; j++)
                    leftBlocks[i][j] = blocks[i][j];
            
            // swap blank square with left square
            int swap = leftBlocks[row0][column0];
            leftBlocks[row0][column0] = leftBlocks[row0][column0 - 1];
            leftBlocks[row0][column0 - 1] = swap;
            Board left = new Board(leftBlocks);
            
            neighbors.push(left);
        }
        
        // the right neighbor
        if (column0 + 1 < n)
        {
            int[][] rightBlocks = new int[n][n];
            for (int i = 0; i < n; i++)
                for (int j = 0; j < n; j++)
                    rightBlocks[i][j] = blocks[i][j];
            
            // swap blank square with left square
            int swap = rightBlocks[row0][column0];
            rightBlocks[row0][column0] = rightBlocks[row0][column0 + 1];
            rightBlocks[row0][column0 + 1] = swap;
            Board right = new Board(rightBlocks);
            
            neighbors.push(right);
        }
        
        // the up neighbor
        if (row0 - 1 >= 0)
        {
            int[][] upBlocks = new int[n][n];
            for (int i = 0; i < n; i++)
                for (int j = 0; j < n; j++)
                    upBlocks[i][j] = blocks[i][j];
            
            // swap blank square with left square
            int swap = upBlocks[row0][column0];
            upBlocks[row0][column0] = upBlocks[row0 - 1][column0];
            upBlocks[row0 - 1][column0] = swap;
            Board up = new Board(upBlocks);
            
            neighbors.push(up);
        }
        
        // the down neighbor
        if (row0 + 1 < n)
        {
            int[][] downBlocks = new int[n][n];
            for (int i = 0; i < n; i++)
                for (int j = 0; j < n; j++)
                    downBlocks[i][j] = blocks[i][j];
            
            // swap blank square with left square
            int swap = downBlocks[row0][column0];
            downBlocks[row0][column0] = downBlocks[row0 + 1][column0];
            downBlocks[row0 + 1][column0] = swap;
            Board down = new Board(downBlocks);
            
            neighbors.push(down);
        }
        
        return neighbors;
    }
    
    /**
     * String representation of this board.
     * 
     * @return string representation of this board
     */
    public String toString() 
    {
        StringBuilder s = new StringBuilder();
        s.append(n + "\n");
        for (int i = 0; i < n; i++)
        {
            for (int j = 0; j < n; j++)
            {
                s.append(String.format("%2d ", (int) blocks[i][j]));
            }
            s.append("\n");
        }
        return s.toString(); 
    }
}