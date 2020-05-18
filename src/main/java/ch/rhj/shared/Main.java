package ch.rhj.shared;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.DosFileAttributeView;
import java.time.Instant;
import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

public class Main {

	public static void main(String[] args) throws Exception {

		deleteGitDirectory();

		Config.config(Paths.get("shared.xml"));

		if (true)
			return;

		File gitDir = Files.createDirectories(Paths.get("target", "git")).toFile();

		String username = System.getenv("GITHUB_USERNAME");
		String password = System.getenv("GITHUB_PASSWORD");
		UsernamePasswordCredentialsProvider credentialsProvider = new UsernamePasswordCredentialsProvider(username, password);
		File directory = new File(gitDir, "rhj-settings-manager");

		Git git = Git.cloneRepository().setCredentialsProvider(credentialsProvider).setDirectory(directory) //
				.setURI("https://github.com/rhjoerg/rhj-settings-manager.git") //
				.setBranch("master").call();

		Path hello = directory.toPath().resolve("hello.txt");

		if (Files.exists(hello)) {
			Files.delete(hello);
		}

		Files.writeString(hello, "hello, world! " + Instant.now().toString(), UTF_8);

		List<DiffEntry> diffEntries = git.diff().call();
		for (DiffEntry diffEntry : diffEntries) {

			switch (diffEntry.getChangeType()) {

			case ADD:
				git.add().addFilepattern(diffEntry.getNewPath()).call();
				break;

			case MODIFY:
				git.add().addFilepattern(diffEntry.getNewPath()).call();
				break;

			default:
				throw new UnsupportedOperationException();
			}
		}

		git.commit().setMessage("rhj-shared " + Instant.now().toString()).call();
		git.push().setCredentialsProvider(credentialsProvider).call();
	}

	private static void deleteGitDirectory() throws Exception {

		Path start = Paths.get("target", "git");

		if (!Files.exists(start))
			return;

		Files.walkFileTree(start, new SimpleFileVisitor<Path>() {

			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {

				if (!Files.isDirectory(file)) {
					Files.getFileAttributeView(file, DosFileAttributeView.class).setReadOnly(false);
					Files.delete(file);
				}

				return super.visitFile(file, attrs);
			}

			@Override
			public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {

				Files.getFileAttributeView(dir, DosFileAttributeView.class).setReadOnly(false);
				Files.delete(dir);

				return super.postVisitDirectory(dir, exc);
			}
		});
	}
}
