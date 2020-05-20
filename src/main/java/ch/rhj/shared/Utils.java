package ch.rhj.shared;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import ch.rhj.shared.Config.Credentials;
import ch.rhj.shared.Config.Repository;

public interface Utils {

	public static CredentialsProvider credentialsProvider(Credentials source) {

		String username = resolveCredential(source.username());
		String password = resolveCredential(source.password());

		return new UsernamePasswordCredentialsProvider(username, password);
	}

	private static String resolveCredential(String string) {

		if (string.startsWith("${env.") && string.endsWith("}")) {

			string = string.substring(6, string.length() - 1);
			string = System.getenv(string);
		}

		return string;
	}

	public static Path path(Repository repository, String... more) {

		String[] moreMore = new String[more.length + 1];

		moreMore[0] = repository.id();
		System.arraycopy(more, 0, moreMore, 1, more.length);

		return Paths.get(repository.config().directory(), moreMore);
	}
}
