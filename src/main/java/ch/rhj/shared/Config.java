package ch.rhj.shared;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Config {

	// ----------------------------------------------------------------------------------------------------------------

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Credentials {

		@JsonProperty("username")
		private String username;

		@JsonProperty("password")
		private String password;

		public String username() {
			return username;
		}

		public String password() {
			return password;
		}
	}

	// ----------------------------------------------------------------------------------------------------------------

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Entry {

		@JsonProperty("source")
		private String source;

		@JsonProperty("destination")
		private String destination;

		public String source() {
			return source;
		}

		public String destination() {
			return destination == null ? source() : destination;
		}
	}

	// ----------------------------------------------------------------------------------------------------------------

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static abstract class Repository {

		@JsonProperty(value = "id", required = true)
		private String id;

		@JsonProperty(value = "uri", required = true)
		private String uri;

		@JsonProperty(value = "branch")
		private String branch;

		@JsonProperty("credentials")
		private Credentials credentials;

		@JsonProperty("entries")
		private List<Entry> entries;

		public String id() {
			return id;
		}

		public String uri() {
			return uri;
		}

		public String branch() {
			return branch == null ? "master" : branch;
		}

		public Credentials credentials() {
			return credentials;
		}

		public Stream<Entry> entries() {
			return entries == null ? Stream.empty() : entries.stream();
		}

		public abstract Config config();
	}

	// ----------------------------------------------------------------------------------------------------------------

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Target extends Repository {

		@JsonIgnore
		private Reference reference;

		public Reference reference() {
			return reference;
		}

		@Override
		public Stream<Entry> entries() {

			return Stream.concat(super.entries(), reference.entries());
		}

		@Override
		public Config config() {
			return reference.config();
		}

		protected void link(Reference reference) {

			this.reference = reference;
		}
	}

	// ----------------------------------------------------------------------------------------------------------------

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Reference extends Repository {

		@JsonIgnore
		private Config config;

		@JsonProperty("targets")
		private List<Target> targets;

		@Override
		public Config config() {
			return config;
		}

		public Stream<Target> targets() {
			return targets == null ? Stream.empty() : targets.stream();
		}

		@Override
		public Stream<Entry> entries() {
			return Stream.concat(super.entries(), config.entries());
		}

		protected void link(Config config) {

			this.config = config;

			targets().forEach(t -> t.link(this));
		}
	}

	// ----------------------------------------------------------------------------------------------------------------

	@JsonProperty("directory")
	private String directory;

	@JsonProperty("credentials")
	private Credentials credentials;

	@JsonProperty("references")
	private List<Reference> references;

	@JsonProperty("entries")
	private List<Entry> entries;

	public String directory() {
		return directory == null ? "repositories" : directory;
	}

	public Credentials credentials() {
		return credentials;
	}

	public Credentials credentials(String id) {

		Optional<Credentials> credentials;

		credentials = references().filter(r -> r.id().equals(id)).findFirst().map(r -> r.credentials());

		if (credentials.isEmpty())
			credentials = references().flatMap(r -> r.targets()).filter(t -> t.id().equals(id)).findFirst().map(t -> t.credentials());

		return credentials.orElse(credentials());
	}

	public Stream<Reference> references() {
		return references == null ? Stream.empty() : references.stream();
	}

	public Stream<Entry> entries() {
		return entries == null ? Stream.empty() : entries.stream();
	}

	protected Config link() {

		references().forEach(r -> r.link(this));

		return this;
	}

	public static Config config(Path path) throws Exception {

		return new XmlMapper().readValue(Files.readAllBytes(path), Config.class).link();
	}
}
