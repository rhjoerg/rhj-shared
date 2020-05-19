package ch.rhj.shared.events;

import ch.rhj.shared.Config.Entry;
import ch.rhj.shared.Config.Target;

public class FileUpdatedEvent {

	public final Target target;
	public final Entry entry;

	public FileUpdatedEvent(Target target, Entry entry) {

		this.target = target;
		this.entry = entry;
	}
}
