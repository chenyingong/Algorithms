import java.util.ArrayList;

import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.DirectedCycle;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.SET;
import edu.princeton.cs.algs4.ST;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

/**
 * Algorithms 4th, assignment 6
 * @author Chenyin Gong
 *
 */
public class WordNet
{
    private final ST<String, SET<Integer>> st;  // use SET to store duplicate nouns, e.g. word
    private final ArrayList<String> al;
    private final SAP sap;
    /**
     * Constructor takes the same of the two input files.
     * @param synsets file containing all noun synsets in WordNet
     * @param hypernyms file containing the hypernym realtionships
     */
    public WordNet(String synsets, String hypernyms)
    {
        if (synsets == null || hypernyms == null)
            throw new IllegalArgumentException("calls with null key");
        st = new ST<>();          // store id-noun(separately) pairs from file synsets
        al = new ArrayList<>();   // store id-nouns pairs from file synsets
        In syn = new In(synsets);
        while (syn.hasNextLine())
        {
            SET<Integer> set;
            String[] a = syn.readLine().split(",");
            int id = Integer.parseInt(a[0]);
            al.add(id, a[1]);
            String noun = a[1];
            if (noun.contains(" ")) // 34,AIDS acquired_immune_deficiency_syndrome, ...
            {
                String[] nouns = noun.split(" ");
                for (String n : nouns)
                {
                    if (st.contains(n)) st.get(n).add(id);
                    else
                    {
                        set = new SET<>();
                        set.add(id);
                        st.put(n, set);
                    }
                }
            }
            else
            {
                if (st.contains(noun)) st.get(noun).add(id);
                else
                {
                    set = new SET<>();
                    set.add(id);
                    st.put(noun, set);
                }
            }
        }
        
        // add edges to a rooted DAG G 
        Digraph G = new Digraph(al.size());
        In hyp = new In(hypernyms);
        while (hyp.hasNextLine())
        {
            String[] a = hyp.readLine().split(",");
            int id = Integer.parseInt(a[0]);
            for (int i = 1; i < a.length; i++)
                G.addEdge(id, Integer.parseInt(a[i]));
        }
        
        if (!isRootedDAG(G))
            throw new IllegalArgumentException("Digraph is not a rooted DAG");
        
        sap = new SAP(G);
    }
    
    /**
     * Returns true if G is a rooted DAG; false otherwise.
     * @param G
     * @return true if G is a rooted DAG; false otherwise
     */
    private boolean isRootedDAG(Digraph G)
    {
        SET<Integer> roots = new SET<>(); // set storing roots
        for (int v = 0; v < G.V(); v++)
        {
            if (G.outdegree(v) == 0) 
            {
                roots.add(v);
                if (roots.size() > 1) return false; // more than one root
            }
        }
        // is G acyclic?
        DirectedCycle dc = new DirectedCycle(G);
        if (dc.hasCycle()) return false;
        return true;
    }
    
    /**
     * Returns all WordNet nouns.
     * @return all WordNet nouns
     */
    public Iterable<String> nouns()
    {
        return st.keys();
    }
    
    /**
     * Returns true if word is a WordNet noun; false otherwise.
     * @param word a word
     * @return true if word is a WordNet noun; false otherwise
     */
    public boolean isNoun(String word)
    {
        return st.contains(word);
    }
    
    /**
     * Returns the set of id of a WordNet noun.
     * @param noun a WordNet noun
     * @return the set of id of a WordNet noun
     */
    private SET<Integer> id(String noun)
    {
        return st.get(noun);
    }
    
    /**
     * Returns the noun name given an id.
     * @param i the id
     * @return the noun name given an id
     */
    private String name(int i)
    {
        return al.get(i);
    }
    
    /**
     * Returns distance(SAP) between nounA and nounB.
     * @param nounA a WordNet noun 
     * @param nounB a WordNet noun
     * @return distance(SAP) between nounA and nounB
     */
    public int distance(String nounA, String nounB)
    {
        if (!isNoun(nounA) || !isNoun(nounB))
            throw new IllegalArgumentException("nounA or nounB is not WordNet noun");
        SET<Integer> v = id(nounA);
        SET<Integer> w = id(nounB);
        return sap.length(v, w);
    }
    
    /**
     * Returns the common ancestor of nounA and nounB.
     * @param nounA a WordNet noun
     * @param nounB a WordNet noun
     * @return the common ancestor of nounA and nounB
     */
    public String sap(String nounA, String nounB)
    {
        if (!isNoun(nounA) || !isNoun(nounB))
            throw new IllegalArgumentException("nounA or nounB is not WordNet noun");
        SET<Integer> v = id(nounA);
        SET<Integer> w = id(nounB);
        int a = sap.ancestor(v, w);
        return name(a);
    }
    
    // unit testing
    public static void main(String[] args)
    {
        String synsets = args[0];
        String hypernyms = args[1];
        WordNet wordnet = new WordNet(synsets, hypernyms);
        
        while (!StdIn.isEmpty())
        {
            String a = StdIn.readString();
            String b = StdIn.readString();
            int length = wordnet.distance(a, b);
            String ancestor = wordnet.sap(a, b);
            StdOut.printf("length = %d, ancestor = %s\n", length, ancestor);
        }
    }
    
}