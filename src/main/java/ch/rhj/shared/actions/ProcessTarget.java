package ch.rhj.shared.actions;

import java.util.function.Consumer;

import ch.rhj.shared.Config;
import ch.rhj.shared.Config.Target;

public class ProcessTarget implements Consumer<Target> {

	public final CloneRepository cloneRepository;
	public final UpdateFiles updateFiles;
	public final AddChanges addChanges;
	public final CommitAndPush commitAndPush;

	public ProcessTarget(Config config) {

		this.cloneRepository = new CloneRepository(config);
		this.updateFiles = new UpdateFiles(config);
		this.addChanges = new AddChanges();
		this.commitAndPush = new CommitAndPush(config);
	}

	@Override
	public void accept(Target target) {

		cloneRepository.apply(target.reference());
		cloneRepository.apply(target);

		updateFiles.accept(target);

		if (addChanges.apply(target) > 0) {

			commitAndPush.accept(target);
		}
	}
}
