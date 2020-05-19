package ch.rhj.shared.events;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Events<E> {

	private final List<Listener<E>> listeners = Collections.synchronizedList(new ArrayList<>());

	public void add(Listener<E> listener) {
		listeners.add(listener);
	}

	public void remove(Listener<E> listener) {
		listeners.remove(listener);
	}

	public void fire(E event) {
		new ArrayList<>(this.listeners).forEach(listener -> listener.handle(event));
	}
}
