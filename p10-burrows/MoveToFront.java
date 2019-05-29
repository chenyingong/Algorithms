import java.util.LinkedList;

import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

/**
 * Algorithms4, assignment 10.
 * This data structure convert Burrows-Wheeler transformed file into a
 * text file in which certain characters appear more frequently than others.
 * @author Chenyin Gong
 *
 */
public class MoveToFront
{
    private static final int R = 256;  // extended ASCII alphabet size
    
    public MoveToFront() { }
    
    /**
     * apply move-to-front encoding, reading from 
     * standard input and writing to standard output
     */
    public static void encode()
    {
        // initialize and ordered sequence of the 256 extended ASCII characters
        LinkedList<Character> sequence = new LinkedList<>();
        for (char r = 0; r < R; r++)
            sequence.add(r, r);
        
        // read the input
        while (!BinaryStdIn.isEmpty())
        {
            char c = BinaryStdIn.readChar(8);
            int index = sequence.indexOf(c);
            BinaryStdOut.write(index, 8); // print the 8-bit index
            // move c to the front
            sequence.remove(index);
            sequence.addFirst(c);
        }
        BinaryStdOut.close();
    }
    
    /**
     * apply move-to-front decoding, reading from 
     * standard input and writing to standard output
     */
    public static void decode()
    {
        // initialize and ordered sequence of the 256 extended ASCII characters
        char[] sequence = new char[R];
        for (char r = 0; r < R; r++)
            sequence[r] = r;
        
        // read the input
        while (!BinaryStdIn.isEmpty())
        {
            int index = BinaryStdIn.readChar(8);
            char c = sequence[index];
            BinaryStdOut.write(c, 8); // print the 
            // move c to the front
            System.arraycopy(sequence, 0, sequence, 1, index);
            sequence[0] = c;
        }
        BinaryStdOut.close();
    }
    
    // unit tests
    public static void main(String[] args) 
    {
        if      (args[0].equals("-")) encode();
        else if (args[0].equals("+")) decode();
        else throw new IllegalArgumentException("Illegal command line argument");
    }
}