import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Programming Assignment 2: Deques and Randomized Queues
 * <p>
 * This program is about Deques using linked lists.
 * @author Chenyin Gong
 *
 */
public class Deque<Item> implements Iterable<Item>
{
	private Node first;
	private Node last;
	private int n;
	private int operates;  // count add and remove operations.
	
	// nested class Node
	private class Node
	{
		private Item item;
		private Node prev;
		private Node next;
		// constructor 
	}
	
	// Initializes an empty Deque
	public Deque()
	{
		first = null;
		last = null;
		n = 0; 
	}
	
	public boolean isEmpty()
	{
		return first == null;
	}
	
	public int size()
	{
		return n;
	}
	
	public void addFirst(Item item)
	{
		if (item == null)
		{
			throw new IllegalArgumentException("Argument: " + item + " is invalid.");
		}
		Node oldFirst = this.first;
		first = new Node();
		first.item = item;
		first.prev = null;
		if (n == 0) 
		{
			last = first;
			last.next = null;
			
		}
		else 
		{
			oldFirst.prev = first;
			first.next = oldFirst;
		}
		n++;
		operates++;
	}
	
	public void addLast(Item item)
	{
		if (item == null)
		{
			throw new IllegalArgumentException("Argument: " + item + " is invalid.");
		}
		Node oldLast = last;
		last = new Node();
		last.item = item;
		last.next = null;
		if (this.isEmpty())
		{
			first = last;
			first.prev = null;	
		}
		else 
		{
			oldLast.next = last;
			last.prev = oldLast;
		}
		n++;
		operates++;
	}
	
	public Item removeFirst()
	{
		if (this.isEmpty())
		{
			throw new NoSuchElementException("Deque is empty, cannot remove.");
		}
		Item item = first.item;
		first = first.next;
		if (this.isEmpty()) last = null;
		else first.prev = null;
		n--;
		operates++;
		return item;
	}
	
	public Item removeLast()
	{
		if (this.isEmpty())
		{
			throw new NoSuchElementException("Deque is empty, cannot remove.");
		}
		Item item = last.item;
		last = last.prev;
		n--;
		operates++;
		if (n == 0) first = last;
		else last.next = null;
		return item;
	}

	public Iterator<Item> iterator()
	{
		return new DequeIterator();
	}
	
	// over items in order from front to end
	private class DequeIterator implements Iterator<Item>
	{
		private Node current = first;
		private int count = operates;
		
		public boolean hasNext()
		{
			if (count != operates)
			{
				throw new ConcurrentModificationException();
			}
			return current != null;
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
			if (current == null)
			{
				throw new NoSuchElementException("No more items to return.");
			}
			Item item = current.item;
			current = current.next;
			return item;
		}
	}
}