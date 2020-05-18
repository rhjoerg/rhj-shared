package ch.rhj.shared;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Config {

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
	public static class Target {

		@JsonProperty("repository")
		private String repository;

		@JsonProperty("entries")
		private List<Entry> entries;

		public String repository() {
			return repository;
		}

		public Stream<Entry> entries() {
			return entries == null ? Stream.empty() : entries.stream();
		}
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Reference {

		@JsonProperty("repository")
		private String repository;

		@JsonProperty("targets")
		private List<Target> targets;

		@JsonProperty("entries")
		private List<Entry> entries;

		public String repository() {
			return repository;
		}

		public Stream<Target> targets() {
			return targets == null ? Stream.empty() : targets.stream();
		}

		public Stream<Entry> entries() {
			return entries == null ? Stream.empty() : entries.stream();
		}
	}

	@JsonProperty("references")
	private List<Reference> references;

	public Stream<Reference> references() {
		return references == null ? Stream.empty() : references.stream();
	}

	public static Config config(Path path) throws Exception {

		byte[] src = Files.readAllBytes(Paths.get("shared.xml"));
		XmlMapper mapper = new XmlMapper();

		return mapper.readValue(src, Config.class);
	}
}
