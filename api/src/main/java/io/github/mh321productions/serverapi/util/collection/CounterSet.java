package io.github.mh321productions.serverapi.util.collection;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * Interne Klasse: Wrapper für Set, die zählt, wie oft ein Objekt hinzugefügt wurde
 * 
 * @author 321Productions
 *
 * @param <E> Die Art von Objekt
 */
public class CounterSet<E> implements Set<E> {
	
	private HashMap<E, Integer> entries = new HashMap<E, Integer>();

	@Override
	public int size() {
		return entries.size();
	}

	@Override
	public boolean isEmpty() {
		return entries.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return entries.containsKey(o);
	}

	@NotNull
	@Override
	public Iterator<E> iterator() {
		return entries.keySet().iterator();
	}

	@NotNull
	@Override
	public Object[] toArray() {
		return entries.keySet().toArray();
	}

	@NotNull
	@Override
	public <T> T[] toArray(@NotNull T[] a) {
		return entries.keySet().toArray(a);
	}

	@Override
	public boolean add(E e) {
		if (entries.containsKey(e)) {
			entries.put(e, entries.get(e) + 1);
			return false;
		}
		
		entries.put(e, 1);
		return true;
	}

	@Override
	public boolean remove(Object o) {
		if (!entries.containsKey(o)) return false;
		else if (entries.get(o) == 1) entries.remove(o);
		else entries.put((E) o, entries.get(o) - 1);
		return true;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return entries.keySet().containsAll(c);
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		boolean result = false;
		for (E e: c) if (add(e)) result = true;
		return result;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		ArrayList<E> remove = new ArrayList<E>(entries.size());
		for (E e: entries.keySet()) if (!c.contains(e)) remove.add(e);
		
		if (!remove.isEmpty()) {
			removeAll(remove);
			return true;
		}
		return false;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		for (Object o: c) remove(o);
		return false;
	}

	@Override
	public void clear() {
		entries.clear();
	}
	
	public int getCountOf(E e) {
		Integer value = entries.get(e);
		return value == null ? 0 : value;
	}

}
