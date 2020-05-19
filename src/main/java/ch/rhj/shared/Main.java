package ch.rhj.shared;

import static java.lang.String.format;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.DosFileAttributeView;

import ch.rhj.shared.Config.Entry;
import ch.rhj.shared.Config.Reference;
import ch.rhj.shared.Config.Target;
import ch.rhj.shared.actions.ProcessTarget;
import ch.rhj.shared.events.ChangeAddedEvent;
import ch.rhj.shared.events.CommittedAndPushedEvent;
import ch.rhj.shared.events.FileUpdatedEvent;
import ch.rhj.shared.events.RepositoryClonedEvent;

public class Main {

	private static void onRepositoryCloned(RepositoryClonedEvent event) {

		System.out.println(format("cloned '%1$s@%2$s'", event.repository.id(), event.repository.branch()));
	}

	private static void onFileUpdated(FileUpdatedEvent event) {

		Target target = event.target;
		Reference reference = target.reference();
		Entry entry = event.entry;

		Path root = Paths.get(reference.config().directory());
		Path srcPath = Utils.path(reference, entry.source());
		Path dstPath = Utils.path(target, entry.destination());

		System.out.println(format("updated %1$s -> %2$s", root.relativize(srcPath), root.relativize(dstPath)));
	}

	private static void onChangeAdded(ChangeAddedEvent event) {

		System.out.println(format("staged: %1$s ~ %2$s", event.target.id(), event.diffEntry.getNewPath()));
	}

	private static void onCommittedAndPushed(CommittedAndPushedEvent event) {

		System.out.println(format("committed and pushed: %1$s", event.target.id()));
	}

	public static void main(String[] args) throws Exception {

		String configFile = args.length == 0 ? "shared-config.xml" : args[0];

		Config config = Config.config(Paths.get(configFile));
		ProcessTarget processTarget = new ProcessTarget(config);

		processTarget.cloneRepository.cloned.add(Main::onRepositoryCloned);
		processTarget.updateFiles.updated.add(Main::onFileUpdated);
		processTarget.addChanges.added.add(Main::onChangeAdded);
		processTarget.commitAndPush.committedAndPushed.add(Main::onCommittedAndPushed);

		deleteDirectory(config);

		config.references().flatMap(r -> r.targets()).forEach(processTarget);
	}

	private static void deleteDirectory(Config config) throws Exception {

		Path start = Paths.get(config.directory());

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
