import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

/**
 * Algorithms 4th, assignment 6
 * @author Chenyin Gong
 *
 */
public class Outcast
{
    private final WordNet wordnet;
    /**
     * Constructor takes a WordNet object.
     * @param wordnet a WordNet object
     */
    public Outcast(WordNet wordnet)
    {
        this.wordnet = wordnet;
    }
    
    /**
     * Returns an outcast given an array of WordNet nouns.
     * @param nouns an array of WordNet nouns
     * @return an outcast given an array of WordNet nouns
     */
    public String outcast(String[] nouns)
    {
        if (nouns == null)
            throw new IllegalArgumentException("calls outcast() with null key");
        if (nouns.length <= 1)
            throw new IllegalArgumentException("nouns must contain at least two nouns");

        int max = Integer.MIN_VALUE;
        String outcast = null;
        for (String noun : nouns)
        {
            int dist = distance(noun, nouns);
            if (dist > max)
            {
                outcast = noun;
                max = dist;
            }
        }
        return outcast;
        
    }
    
    /**
     * Returns sum of distances between the noun and every other nouns.
     * @param noun the noun
     * @param nouns an array of other nouns
     * @return sum of distances between the noun and every other nouns
     */
    private int distance(String noun, String[] nouns)
    {
        if (nouns == null)
            throw new IllegalArgumentException("calls distance() with null key");
        
        int dist = 0;
        for (String n : nouns)
            dist += wordnet.distance(noun, n);
        return dist;
    }
    
    // unit testing
    public static void main(String[] args)
    {
        WordNet wordnet = new WordNet(args[0], args[1]);
        Outcast outcast = new Outcast(wordnet);
        for (int t = 2; t < args.length; t++)
        {
            In in = new In(args[t]);
            String[] nouns = in.readAllStrings();
            StdOut.println(args[t] + ": " + outcast.outcast(nouns));
        }
    }
    
}