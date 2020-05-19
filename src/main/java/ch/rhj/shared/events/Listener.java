package ch.rhj.shared.events;

import java.util.EventListener;

@FunctionalInterface
public interface Listener<E> extends EventListener {

	public void handle(E event);
}
