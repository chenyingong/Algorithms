import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

import edu.princeton.cs.algs4.StdRandom;

/**
 * Programming Assignment 2: Deques and Randomized Queues
 * <p>
 * This program is about Randomized Queue using arrays.
 * @author Chenyin Gong
 *
 */
public class RandomizedQueue<Item> implements Iterable<Item>
{
	private Item[] q = (Item[]) new Object[2]; // queue elements
	private int n = 0;                         // number of elements on queue
	private int operates = 0;                  // count add and remove operations.
	
	
	// Initialize an empty randomized queue
	public RandomizedQueue()
	{
		
	}
	
	/**
	 * Is this queue empty?
	 * @return true if this queue is empty; false otherwise
	 */
	public boolean isEmpty()
	{
		return n == 0;
	}
	
	/**
	 * Returns the number of items in this queue.
	 * @return the number of items in this queue
	 */
	public int size()
	{
		return n;
	}
	
	// resize the underlying array
	private void resize(int capacity)
	{
		Item[] temp = (Item[]) new Object[capacity];
		for (int i = 0; i < n; i++)
			temp[i] = q[i];
		q = temp;
	}
	
	/**
	 * Adds the item to this queue
	 * @param item the item to add
	 */
	public void enqueue(Item item)
	{
		// corner case: argument is null
		if (item == null)
		{
			throw new IllegalArgumentException("Argument: " + item + " is invalid.");
		}
		// double size of array if necessary and recopy to front of array
		if (n == q.length) resize(2 * q.length); // double size of array if necessary
		q[n++] = item;                        // add item
		operates++;
	}
	
	/**
	 * remove and return a random item
	 * @return the random removed item
	 */
	public Item dequeue()
	{
		// corner case: queue is empty, cannot remove items
		if (n == 0)
		{
			throw new NoSuchElementException("Queue is empty, cannot remove.");
		}
		int p = StdRandom.uniform(n);  // generate a pseudo-random integer between 0 and n−1
		Item item = q[p];
		q[p] = q[n-1];                 // assign the last item to p-th location
		q[--n] = null;                 // to avoid loitering
		operates++;
		// shrink size of array if necessary
		if (n > 0 && n == q.length/4) resize(q.length/2);
		return item;
	}
	
	/**
	 * return a random item (but do not remove it)
	 * @return the random item without removing it
	 */
	public Item sample()
	{
		// corner case: queue is empty, cannot remove items
		if (n == 0)
		{
			throw new NoSuchElementException("Queue is empty, cannot remove.");
		}
		int p = StdRandom.uniform(n);
		return q[p];
	}
	
	/**
	 * Returns an iterator that iterates over the items in this queue.
	 * @return an iterator that iterates over the items in this queue
	 */
	public Iterator<Item> iterator()
	{
		return new ArrayIterator();
	}
	
	// an iterator, doesn't implement remove() since it's optional
	// next() iterating over items in random order
	private class ArrayIterator implements Iterator<Item>
	{
		private int i = 0;
		private int count = operates;
		private int[] a = new int[n];
		
		{
			for (int j = 0; j < n; j++)
				a[j] = j;
			StdRandom.shuffle(a);
		}
		public boolean hasNext()
		{
			if (count != operates)
			{
				throw new ConcurrentModificationException();
			}
			return i < n;
		}
		public void remove()
		{
			throw new UnsupportedOperationException();
		}
		public Item next()
		{
			if (count != operates)
			{
				throw new ConcurrentModificationException();
			}
			if (i >= n)
			{
				throw new NoSuchElementException("No more items to return.");
			}
			Item item = q[a[i]];
			i++;
			return item;
		}
	}
}