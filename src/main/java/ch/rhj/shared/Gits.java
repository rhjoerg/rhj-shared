package ch.rhj.shared;

import java.util.Map;
import java.util.TreeMap;

import org.eclipse.jgit.api.Git;

public class Gits {

	private static final Map<String, Git> gits = new TreeMap<>();

	public static void register(String id, Git git) {

		gits.put(id, git);
	}

	public static boolean contains(String id) {

		return gits.containsKey(id);
	}

	public static Git get(String id) {

		return gits.get(id);
	}
}
