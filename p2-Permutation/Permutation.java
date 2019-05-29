import edu.princeton.cs.algs4.StdIn;

/**
 * Take an integer k as a command-line argument,
 * read in a sequence of strings from standard input,
 * and print exactly k of them, uniformly at random. 
 * Print each item from the sequence at most once.
 * @author Chenyin Gong
 *
 */
public class Permutation
{
	public static void main(String[] args)
	{
		int k = Integer.parseInt(args[0]);
		RandomizedQueue<String> rq = new RandomizedQueue<>();
		
		while (!StdIn.isEmpty())
		{
			String string = StdIn.readString();
			rq.enqueue(string);
		}
		
		for (int i = 0; i < k; i++)
			System.out.println(rq.dequeue());
	}
}