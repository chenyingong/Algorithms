import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.SET;
import edu.princeton.cs.algs4.StdOut;

/**
 * Algorithms 4th, assignment 9.
 * @author Chenyin Gong
 * args[0]: dictionary file; args[1] boggleBoard file
 */
public class BoggleSolver
{
    private Node root;
    
    // R-way trie node
    private static class Node
    {
        private char c;                  // character
        private Node left, mid, right;   // left, middle, and right subtries
        private Object val;              // value associated with string
    }
    
    /**
     * Initializes the data structure using the given array of strings as the dictionary.
     * @param dictionary the given dictionary
     */
    public BoggleSolver(String[] dictionary)
    {
        for (String word : dictionary)
            put(word);
    }
    
    // inserts the key-value pair into the symbol table, overwriting the old value
    // with the new value if the key is already in the symbol table
    private void put(String key)
    {
        if (key == null)
            throw new IllegalArgumentException("calls put() with null key");
        root = put(root, key, 0);
    }
    
    private Node put(Node x, String key, int d)
    {
        char c = key.charAt(d);
        if (x == null)
        {
            x = new Node();
            x.c = c;
        }
        if      (c < x.c)              x.left  = put(x.left, key, d);
        else if (c > x.c)              x.right = put(x.right, key, d);
        else if (d < key.length() - 1) x.mid   = put(x.mid, key, d+1);
        else                           x.val   = 1;
        return x;
    }
    
    // does this symbol table contain the given key?
    private boolean contains(String key)
    {
        if (key == null)
            throw new IllegalArgumentException("calls contains() with null argument");
        if (key.length() == 0) throw new IllegalArgumentException("key must have length >= 1");
        Node x = get(root, key, 0);
        if (x == null) return false;
        if (x.val != null) return true;
        return false;
    }
    
    private Node get(Node x, String key, int d)
    {
        if (x == null) return null;
        if (key.length() == 0) throw new IllegalArgumentException("key must have length >= 1");
        char c = key.charAt(d);
        if      (c < x.c)              return get(x.left, key, d);
        else if (c > x.c)              return get(x.right, key, d);
        else if (d < key.length() - 1) return get(x.mid, key, d+1);
        else                           return x;
    }
    
    private Node get(Node x, char c)
    {
        String c1 = String.valueOf(c);
        return get(x, c1, 0);
    }
    
    /**
     * Returns the set of all valid words in the given Boggle board, as an Iterable. 
     * @param board the given Boggle board
     * @return the set of all valid words in the given Boggle board, as an Iterable
     */
    public Iterable<String> getAllValidWords(BoggleBoard board)
    {
        if (board == null) throw new IllegalArgumentException("argument is null");
        SET<String> list = new SET<>();   // use SET to avoid duplicates
        int cols         = board.cols();  // number of columns
        int rows         = board.rows();  // number of rows
        boolean[][] marked;               // trace to avoid dice being used more than once
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
            {
                marked = new boolean[rows][cols];
                dfs(board, list, new StringBuilder(), marked, i, j, root);
            }
        return list;
    }
    
    // depth-first search
    private void dfs(BoggleBoard board, SET<String> set, StringBuilder word, boolean[][] marked, int i, int j, Node x)
    {
        marked[i][j] = true;
        char c = board.getLetter(i, j);
        word.append(c);
        x = get(x, c);
        if (x == null) return;  // prefix query
        if (c == 'Q')           // special case: Qu
        {
            word.append('U');
            x = get(x.mid, 'U');
            if (x == null) return;  // prefix query
        }
        if (word.length() > 2 && x.val != null) set.add(word.toString());
        
        for (int m = -1; m <= 1; m++)     // m == 0, up row;   1, same row; 2, bot row
            for (int n = -1; n <= 1; n++) // n == 0, left col; 1, same col; 2, right col
                if (i + m >= 0 && i + m < board.rows() && j + n >= 0 && j + n < board.cols()
                        && !(m == 0 && n == 0) && !marked[i+m][j+n])
                {
                    dfs(board, set, word, marked, i+m, j+n, x.mid);
                    marked[i+m][j+n] = false;             // reset
                    word.deleteCharAt(word.length() - 1); // reset
                    if (word.substring(word.length()-1, word.length()).equals("Q"))
                        word.deleteCharAt(word.length() - 1); // deal with the special two-letter sequence Qu
                }
    }
    
    /**
     * Returns the score of the given word if it is in the dictionary, zero otherwise.
     * @param word the given word
     * @return the score of the given word if it is in the dictionary, zero otherwise
     */
    public int scoreOf(String word)
    {
        if (word == null) throw new IllegalArgumentException("calls scoreOf() with null word");
        if (!contains(word)) return 0; // 0 if word not in the dictionary
        int n = word.length();
        if      (n < 3)  return 0;
        else if (n < 5)  return 1;
        else if (n == 5) return 2;
        else if (n == 6) return 3;
        else if (n == 7) return 5;
        else             return 11; 
    }
    
    // unit testing
    public static void main(String[] args)
    {
        In in = new In(args[0]);
        String[] dictionary = in.readAllStrings();
        BoggleSolver solver = new BoggleSolver(dictionary);
        BoggleBoard board = new BoggleBoard(args[1]);
        int score = 0;
        for (String word : solver.getAllValidWords(board))
        {
            StdOut.println(word);
            score += solver.scoreOf(word);
        }
        StdOut.println("Score = " + score);
    }
}