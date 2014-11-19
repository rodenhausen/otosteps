package edu.arizona.biosemantics.oto2.oto.shared.model;

import java.util.List;

import com.google.gwt.editor.client.Editor.Path;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

public interface TermProperties extends PropertyAccess<Term> {

	  @Path("id")
	  ModelKeyProvider<Term> key();
	   
	  @Path("term")
	  LabelProvider<Term> nameLabel();
	 
	  ValueProvider<Term, String> term();
	   
	  ValueProvider<Term, List<Term>> synonyms();
	  
	  ValueProvider<Term, List<Context>> contexts();
	  
	  ValueProvider<Term, List<Comment>> comments();
	
}
