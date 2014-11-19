package edu.arizona.biosemantics.oto2.oto.client.common;

import com.google.gwt.event.logical.shared.SelectionHandler;
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;

public class CopyMoveMenu extends Menu {
	
	public CopyMoveMenu(SelectionHandler<Item> copyHandler, SelectionHandler<Item> moveHandler) {
		MenuItem item = new MenuItem("Copy");
		item.addSelectionHandler(copyHandler);
		add(item);
		item = new MenuItem("Move");
		item.addSelectionHandler(moveHandler);
		add(item);
	}
	
}