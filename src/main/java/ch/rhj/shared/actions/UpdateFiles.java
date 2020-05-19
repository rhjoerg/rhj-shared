package ch.rhj.shared.actions;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.function.Consumer;

import ch.rhj.shared.Config;
import ch.rhj.shared.Config.Entry;
import ch.rhj.shared.Config.Target;
import ch.rhj.shared.Utils;
import ch.rhj.shared.events.Events;
import ch.rhj.shared.events.FileUpdatedEvent;

public class UpdateFiles implements Consumer<Target> {

	public final Config config;

	public final Events<FileUpdatedEvent> updated = new Events<>();

	public UpdateFiles(Config config) {

		this.config = config;
	}

	@Override
	public void accept(Target target) {

		target.entries().forEach(entry -> update(target, entry));
	}

	private void update(Target target, Entry entry) {

		Path srcPath = Utils.path(target.reference(), entry.source());
		Path dstPath = Utils.path(target, entry.destination());

		byte[] srcBytes = bytes(srcPath);
		byte[] dstBytes = bytes(dstPath);

		if (equals(srcBytes, dstBytes)) {
			return;
		}

		write(srcBytes, dstPath);

		updated.fire(new FileUpdatedEvent(target, entry));
	}

	private byte[] bytes(Path path) {

		try {

			if (!Files.exists(path))
				return null;

			return Files.readAllBytes(path);

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private boolean equals(byte[] srcBytes, byte[] dstBytes) {

		if (dstBytes == null)
			return false;

		return Arrays.equals(srcBytes, dstBytes);
	}

	private void write(byte[] bytes, Path path) {

		try {

			if (Files.exists(path)) {
				Files.delete(path);
			}

			Files.write(path, bytes);

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
