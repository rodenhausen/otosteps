package edu.arizona.biosemantics.oto2.oto.shared.model;

import com.google.gwt.editor.client.Editor.Path;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

import edu.arizona.biosemantics.oto2.oto.shared.model.TypedContext.Type;

public interface TypedContextProperties extends PropertyAccess<TypedContext> {

	@Path("id")
	ModelKeyProvider<TypedContext> key();

	@Path("source")
	LabelProvider<TypedContext> nameLabel();

	ValueProvider<TypedContext, String> source();

	ValueProvider<TypedContext, String> text();
	
	ValueProvider<TypedContext, String> typeString();
}