package edu.arizona.biosemantics.oto2.oto.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;
import com.sencha.gxt.widget.core.client.container.Viewport;

import edu.arizona.biosemantics.oto2.oto.client.event.SaveEvent;
import edu.arizona.biosemantics.oto2.oto.client.event.SaveEvent.SaveHandler;

public class Start implements EntryPoint {

	public void onModuleLoad() {
		int collectionId = 1;
		String secret = "my secret";
		
		Oto oto = new Oto();
		Viewport v = new Viewport();
		v.add(oto.getView().asWidget());
		RootPanel.get().add(v);
		oto.setUser("UserB");
		oto.getEventBus().addHandler(SaveEvent.TYPE, new SaveEvent.SaveHandler() {
			@Override
			public void onSave(SaveEvent event) {
				System.out.println("save called");
			}
		});
		oto.loadCollection(collectionId, secret, false);
	}

}
