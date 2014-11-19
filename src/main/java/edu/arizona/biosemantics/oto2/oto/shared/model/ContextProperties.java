package edu.arizona.biosemantics.oto2.oto.shared.model;

import com.google.gwt.editor.client.Editor.Path;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

public interface ContextProperties extends PropertyAccess<Context> {

	@Path("id")
	ModelKeyProvider<Context> key();

	@Path("source")
	LabelProvider<Context> nameLabel();

	ValueProvider<Context, String> source();

	ValueProvider<Context, String> text();
	
	ValueProvider<Context, String> fullText();
}