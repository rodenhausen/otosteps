package edu.arizona.biosemantics.oto2.oto.shared.model;

public class TrashLabel extends Label {

	public TrashLabel() {
		super();
	}

	public TrashLabel(int id, int collectionId, String name, String description) {
		super(id, collectionId, name, description);
	}

	public TrashLabel(int collectionId, String name, String description) {
		super(collectionId, name, description);
	}

	public TrashLabel(String name, String description) {
		super(name, description);
	}
	
}
