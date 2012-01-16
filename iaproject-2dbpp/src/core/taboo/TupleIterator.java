package core.taboo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;


/**
 * Returns all possible <code>T_size</code>-uples from given collection.
 * Each t-uple is created only on demand, that is, when {@link #next()}
 * is called.
 * 
 * @param <E>
 */
public class TupleIterator<E> implements Iterator<List<E>> {
	
	private final Collection<? extends E> collection;
	private final Iterator<List<E>> subIterator;
	
	private List<E> currentSubElement = null;
	private Iterator<E> remElmIt = null;
	
	/**
	 * 
	 * @param T_size
	 * @param collection MUST not contain two elements that are 
	 * {@link #equals(Object)}.
	 */
	public TupleIterator(int T_size, Collection<E> collection) {
		if (T_size <= 0) {
			throw new IllegalArgumentException("T must be positive");
		}
		if (collection == null) {
			throw new IllegalArgumentException("Null collection");
		}
		
		this.collection = collection;
		
		if (T_size > 1) {
			this.subIterator = new TupleIterator<E>(T_size - 1, collection);
			this.currentSubElement = subIterator.next();
			this.remElmIt = prepareRemainingElements(currentSubElement).iterator();
		} else {
			this.subIterator = Collections.<List<E>>emptyList().iterator();
			this.currentSubElement = Collections.emptyList();
			this.remElmIt = collection.iterator();
		}
	}
	
	/**
	 * Perform set difference <code>{@link #collection} \ currentSubElement</code> 
	 * returning resulting set.
	 * 
	 * @param currentSubElement
	 * @return
	 */
	private List<E> prepareRemainingElements(List<E> currentSubElement) {
		assert currentSubElement != null : "null currentSubElement";
		
		// calculate remainingElements
		LinkedList<E> remainingElements = new LinkedList<E>(collection);
		remainingElements.removeAll(currentSubElement);
		
		return remainingElements;
	}
	
	public boolean hasNext() {
		return remElmIt.hasNext() || subIterator.hasNext();
	}

	public List<E> next() {
		assert remElmIt != null : "null remElmIt";
		assert subIterator != null : "null subIterator";
		assert currentSubElement != null : "null currentSubElement";
		
		if (!remElmIt.hasNext()) {
			if (subIterator.hasNext()) {
				currentSubElement = subIterator.next();
				this.remElmIt = prepareRemainingElements(currentSubElement).iterator();
			} else {
				throw new NoSuchElementException();
			}
		}
		
		E elm = remElmIt.next();
		
		List<E> toRet = new ArrayList<E>(currentSubElement);
		toRet.add(elm);
		
		return toRet;
	}

	public void remove() {
		throw new UnsupportedOperationException("Cannot remove");
	}
}