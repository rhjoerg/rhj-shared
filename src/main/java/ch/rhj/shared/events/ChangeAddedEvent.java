package ch.rhj.shared.events;

import org.eclipse.jgit.diff.DiffEntry;

import ch.rhj.shared.Config.Target;

public class ChangeAddedEvent {

	public final Target target;
	public final DiffEntry diffEntry;

	public ChangeAddedEvent(Target target, DiffEntry diffEntry) {

		this.target = target;
		this.diffEntry = diffEntry;
	}
}
