package ch.rhj.shared.actions;

import static ch.rhj.shared.Utils.credentialsProvider;

import java.time.Instant;
import java.util.function.Consumer;

import org.eclipse.jgit.api.Git;

import ch.rhj.shared.Config;
import ch.rhj.shared.Config.Target;
import ch.rhj.shared.Gits;
import ch.rhj.shared.events.CommittedAndPushedEvent;
import ch.rhj.shared.events.Events;

public class CommitAndPush implements Consumer<Target> {

	public final Config config;

	public final Events<CommittedAndPushedEvent> committedAndPushed = new Events<>();

	public CommitAndPush(Config config) {
		this.config = config;
	}

	@Override
	public void accept(Target target) {

		try {

			String id = target.id();
			Git git = Gits.get(id);

			git.commit().setMessage("rhj-shared " + Instant.now().toString()).call();
			git.push().setCredentialsProvider(credentialsProvider(config.credentials(id))).call();

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
