/**
 * Algorithms4, assignment 10.
 * This data structure describes the abstraction of a sorted array of
 * the n circular suffixes of a string of length n.
 * @author Chenyin Gong
 * args: txt files in burrows
 */
public class CircularSuffixArray
{
    private static final int R = 256;     // extended ASCII alphabet size
    private static final int CUTOFF = 15; // cutoff to insertion sort
    private final int len;  // length of s
    private int[] indices;  // index[i]: index of the original suffix that appears ith in the sorted array
    
    /**
     * Constructs a circular suffix array of s.
     * Sorts the suffixes by using the most-significant-digit-first algorithm.
     * @param s given string
     */
    public CircularSuffixArray(String s)
    {
        if (s == null) throw new IllegalArgumentException("argument is null");
        char[] a = s.toCharArray();
        len = a.length;
        int[] aux = new int[len];
        indices   = new int[len];
        for (int i = 0; i < len; i++)
            indices[i] = i;
        sort(a, 0, len-1, 0, aux);
    }
    
    // MSD
    private void sort(char[] a, int lo, int hi, int d, int[] aux)
    {
        // cutoff to insertion sort for small subarrays
        if (hi <= lo + CUTOFF)
        {
            insertion(a, lo, hi, d);
            return;
        }
        
        // compute frequency counts
        int[] count = new int[R+2];
        for (int i = lo; i <= hi; i++)
        {
            // if d = length of string, let dth char of suffix -1
            if (d == len) count[1]++; 
            else 
            {
                int index, temp = indices[i] + d;
                if (temp < len) index = temp;
                else            index = temp - len;
                count[a[index]+2]++;
            }
        }
        
        // transform counts to indices
        for (int r = 0; r < R + 1; r++)
            count[r+1] += count[r];
        
        // distribute
        for (int i = lo; i <= hi; i++)
        {
            if (d == len) aux[count[0]++] = indices[i]; 
            else 
            {
                int index, temp = indices[i] + d;
                if (temp < len) index = temp;
                else            index = temp - len;
                aux[count[a[index]+1]++] = indices[i];
            }
        }
        
        // copy back
        for (int i = lo; i <= hi; i++)
            indices[i] = aux[i-lo];
        
        // recursively sort for each character (excludes sentinel -1)
        for (int r = 0; r < R; r++)
            sort(a, lo+count[r], lo+count[r+1]-1, d+1, aux);
    }
    
    // insertion sort, starting at dth character
    private void insertion(char[] a, int lo, int hi, int d)
    {
        for (int i = lo; i <= hi; i++)
        {   
            for (int j = i; j > lo && less(a, j, j-1, d); j--)
                exch(j, j-1);
        }     
    }
    
    // exchange a[i] and a[j]
    private void exch(int i, int j)
    {
        int temp = indices[i];
        indices[i] = indices[j];
        indices[j] = temp;
    }
    
    private boolean less(char[] a, int v, int w, int d)
    {
        for (int i = d; i < len; i++)
        {
            int index1, index2, temp1 = indices[v] + i, temp2 = indices[w] + i;
            if (temp1 < len) index1 = temp1;
            else             index1 = temp1 - len;
            if (temp2 < len) index2 = temp2;
            else             index2 = temp2 - len;
            
            if (a[index1] < a[index2]) return true;
            if (a[index1] > a[index2]) return false;
        }
        return false; // the same
    }
    
    /**
     * Returns the length of s.
     * @return the length of s
     */
    public int length()
    {
        return len;
    }
    
    /**
     * Returns index (of the original suffix) of ith sorted suffix.
     * @param i ith position in the sorted suffix
     * @return index (of the original suffix) of ith sorted suffix
     */
    public int index(int i)
    {
        if (i < 0 || i >= len) throw new IllegalArgumentException("argument is outside the prescribed range");
        return indices[i];
    }
}