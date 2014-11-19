package edu.arizona.biosemantics.oto2.oto.shared.rpc;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.arizona.biosemantics.oto2.oto.shared.model.Collection;
import edu.arizona.biosemantics.oto2.oto.shared.model.community.CommunityCollection;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface ICommunityServiceAsync {
	
	public void get(String type, AsyncCallback<CommunityCollection> callback);
		
}
