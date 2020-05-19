package ch.rhj.shared.events;

import ch.rhj.shared.Config.Target;

public class CommittedAndPushedEvent {

	public final Target target;

	public CommittedAndPushedEvent(Target target) {
		this.target = target;
	}
}
