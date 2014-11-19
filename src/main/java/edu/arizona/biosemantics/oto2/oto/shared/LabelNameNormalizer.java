package edu.arizona.biosemantics.oto2.oto.shared;

public class LabelNameNormalizer {

	public static String normalize(String name) {
		name = name.replaceAll("\\s+", "_");
		name = name.replaceAll("\\p{Punct}+", "_");
		return name;
	}
	
}
