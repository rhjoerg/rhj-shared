package ch.rhj.shared.actions;

import java.util.List;
import java.util.function.Function;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.diff.DiffEntry;

import ch.rhj.shared.Config.Target;
import ch.rhj.shared.Gits;
import ch.rhj.shared.events.ChangeAddedEvent;
import ch.rhj.shared.events.Events;

public class AddChanges implements Function<Target, Integer> {

	public final Events<ChangeAddedEvent> added = new Events<>();

	@Override
	public Integer apply(Target target) {

		Git git = Gits.get(target.id());

		try {

			List<DiffEntry> diffEntries = git.diff().call();

			diffEntries.forEach(diffEntry -> addChange(git, target, diffEntry));
			return diffEntries.size();

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void addChange(Git git, Target target, DiffEntry diffEntry) {

		try {

			switch (diffEntry.getChangeType()) {

			case ADD:
			case MODIFY:
				git.add().addFilepattern(diffEntry.getNewPath()).call();
				added.fire(new ChangeAddedEvent(target, diffEntry));
				break;

			default:
				throw new UnsupportedOperationException();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
