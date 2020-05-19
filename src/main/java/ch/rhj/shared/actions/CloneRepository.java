package ch.rhj.shared.actions;

import static ch.rhj.shared.Utils.credentialsProvider;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Function;

import org.eclipse.jgit.api.Git;

import ch.rhj.shared.Config;
import ch.rhj.shared.Config.Repository;
import ch.rhj.shared.Gits;
import ch.rhj.shared.events.Events;
import ch.rhj.shared.events.RepositoryClonedEvent;

public class CloneRepository implements Function<Repository, Git> {

	public final Config config;

	public final Events<RepositoryClonedEvent> cloned = new Events<>();

	public CloneRepository(Config config) {

		this.config = config;
	}

	@Override
	public Git apply(Repository repository) {

		String id = repository.id();

		if (Gits.contains(id)) {
			return Gits.get(id);
		}

		try {

			Git git = Git.cloneRepository() //
					.setBranch(repository.branch()) //
					.setCredentialsProvider(credentialsProvider(config.credentials(id))) //
					.setDirectory(directory(id)) //
					.setURI(repository.uri()) //
					.call();

			Gits.register(id, git);

			cloned.fire(new RepositoryClonedEvent(repository));

			return git;

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected File directory(String id) {

		Path repositories = Paths.get(config.directory());

		try {
			Files.createDirectories(repositories);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		return repositories.resolve(id).toFile();
	}
}
