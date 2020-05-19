package ch.rhj.shared.events;

import org.eclipse.jgit.diff.DiffEntry;

public class ChangeAddedEvent {

	public final DiffEntry diffEntry;

	public ChangeAddedEvent(DiffEntry diffEntry) {
		this.diffEntry = diffEntry;
	}
}
