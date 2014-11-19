package edu.arizona.biosemantics.oto2.oto.shared.model;

public class HighlightLabel extends Label {

	public HighlightLabel() { }
	
	public HighlightLabel(String name, String description) {
		super(name, description);
	}
	
	public HighlightLabel(int collectionId, String name, String description) {
		super(collectionId, name, description);
	}
	
	public HighlightLabel(int id, int collectionId, String name, String description) {
		super(id, collectionId, name, description);
	}
	
}
