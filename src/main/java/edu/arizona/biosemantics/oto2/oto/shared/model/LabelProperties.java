package edu.arizona.biosemantics.oto2.oto.shared.model;

import com.google.gwt.editor.client.Editor.Path;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

public interface LabelProperties extends PropertyAccess<Label> {

	@Path("id")
	ModelKeyProvider<Label> key();

	@Path("name")
	LabelProvider<Label> nameLabel();

	ValueProvider<Label, String> name();

	ValueProvider<Label, String> description();
}
