package ch.rhj.shared.events;

import ch.rhj.shared.Config.Repository;

public class RepositoryClonedEvent {

	public final Repository repository;

	public RepositoryClonedEvent(Repository repository) {
		this.repository = repository;
	}
}
