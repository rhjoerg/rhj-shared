package ch.rhj.shared;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Config {

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

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Repository {

		@JsonProperty(value = "id", required = true)
		private String id;

		@JsonProperty(value = "git", required = true)
		private String git;

		@JsonProperty("credentials")
		private Credentials credentials;

		@JsonProperty("entries")
		private List<Entry> entries;

		public String id() {
			return id;
		}

		public String git() {
			return git;
		}

		public Credentials credentials() {
			return credentials;
		}

		public Stream<Entry> entries() {
			return entries == null ? Stream.empty() : entries.stream();
		}
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Target extends Repository {
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Reference extends Repository {

		@JsonProperty("targets")
		private List<Target> targets;

		public Stream<Target> targets() {
			return targets == null ? Stream.empty() : targets.stream();
		}
	}

	@JsonProperty("credentials")
	private Credentials credentials;

	@JsonProperty("references")
	private List<Reference> references;

	public Credentials credentials() {
		return credentials();
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

	public static Config config(Path path) throws Exception {

		byte[] src = Files.readAllBytes(Paths.get("shared.xml"));
		XmlMapper mapper = new XmlMapper();

		return mapper.readValue(src, Config.class);
	}
}
