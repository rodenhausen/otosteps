package edu.arizona.biosemantics.oto2.oto.shared.model;

import com.google.gwt.editor.client.Editor.Path;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

public interface CommentProperties extends PropertyAccess<Comment> {

		@Path("id")
		ModelKeyProvider<Comment> key();

		@Path("user")
		ValueProvider<Comment, String> user();

		@Path("comment")
		ValueProvider<Comment, String> text();

	}