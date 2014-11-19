package edu.arizona.biosemantics.oto2.oto.shared.model;

import com.google.gwt.editor.client.Editor.Path;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

public interface OntologyProperties extends PropertyAccess<Ontology> {
	
  @Path("id")
  ModelKeyProvider<Ontology> key();
   
  @Path("label")
  LabelProvider<Ontology> acronymLabel();
 
  ValueProvider<Ontology, String> acronym();
   
  ValueProvider<Ontology, String> name();
  
  ValueProvider<Ontology, String> id();
}