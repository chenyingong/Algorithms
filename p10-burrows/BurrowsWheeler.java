/***********************************************************
 * mobydick.txt:    1048576 bits
 * Huffman     :    583888  bits  compression ratio 55.7%
 * Burrows-Wheeler: 401888  bits  compression ratio 38.3%
 ***********************************************************/
import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

/**
 * Algorithms4, assignment 10.
 * This data structure transforms a message into a form that
 * is more amenable to compression, i.e. rearranging the characters
 * in the input so that there are lots of clusters with repeated characters.
 * @author Chenyin Gong
 * 
 */
public class BurrowsWheeler
{
    public BurrowsWheeler() { }
    
    /**
     * Apply Burrows-Wheeler transform, reading from standard input
     * and writing to standard output.
     */
    public static void transform()
    {
        // read the input
        String s = BinaryStdIn.readString();
        
        // find the last column of sorted suffix array t[] and first in which
        // the original string ends up
        CircularSuffixArray csa = new CircularSuffixArray(s);
        int first = -1, n = csa.length();
        char[] t = new char[n];
        for (int i = 0; i < n; i++)
        {
            int index = csa.index(i);
            if (index == 0)
            {
                t[i] = s.charAt(n-1);
                first = i;
            }
            else t[i] = s.charAt(index-1);
        }
        
        // print the 32-bit integer first
        assert first != -1;   // make sure first >= 0 exists
        BinaryStdOut.write(first);
        
        // print t[]
        for (int i = 0; i < n; i++)
            BinaryStdOut.write(t[i], 8);
        
        // close output stream
        BinaryStdOut.close();
    }
    
    /**
     * Apply Burrows-Wheeler inverse transform, reading
     * from standard input and writing to standard output.
     */
    public static void inverseTransform()
    {
        // read the input
        int first = BinaryStdIn.readInt();
        String t  = BinaryStdIn.readString(); // last column
        
        // construct next[]
        int[] next = sort(t);
        
        // invert the message given t, first, and next[]
        int count = 0, len = next.length;
        while (count < len)
        {
            first = next[first];
            BinaryStdOut.write(t.charAt(first));
            count++;
        }
        BinaryStdOut.close();
    }
    
    // key-indexed counting algorithm: it is stable!
    private static int[] sort(String s)
    {
        int R = 256, n = s.length();
        int[] aux = new int[n];
        
        int [] count = new int[R+1];
        for (int i = 0; i < n; i++)
            count[s.charAt(i)+1]++;
        
        for (int r = 0; r < R; r++)
            count[r+1] += count[r];
        
        for (int i = 0; i < n; i++)
            aux[count[s.charAt(i)]++] = i;
        return aux;
    }
    
    // unit tests
    public static void main(String[] args) 
    {
        if      (args[0].equals("-")) transform();
        else if (args[0].equals("+")) inverseTransform();
        else throw new IllegalArgumentException("Illegal command line argument");
    }
}