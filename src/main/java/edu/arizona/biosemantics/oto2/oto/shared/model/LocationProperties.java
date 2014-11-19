package edu.arizona.biosemantics.oto2.oto.shared.model;

import com.google.gwt.editor.client.Editor.Path;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

public interface LocationProperties extends PropertyAccess<Location> {
	
	public class CategorizationNameValueProvider implements ValueProvider<Location, String> {

		@Override
		public String getValue(Location object) {
			return object.getCategorization().getName();
		}

		@Override
		public void setValue(Location object, String value) {
			object.getCategorization().setName(value);
		}

		@Override
		public String getPath() {
			return "categorization/name";
		}
		
	}
	
  @Path("categorization")
  ModelKeyProvider<Location> key();
   
  @Path("instance")
  LabelProvider<Location> nameLabel();
 
  ValueProvider<Location, String> instance();
   
  ValueProvider<Location, Label> categorization();
  
  CategorizationNameValueProvider categorizationName();
  
}