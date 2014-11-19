package edu.arizona.biosemantics.oto2.oto.shared.rpc;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import edu.arizona.biosemantics.oto2.oto.shared.model.Collection;
import edu.arizona.biosemantics.oto2.oto.shared.model.community.CommunityCollection;


/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("community")
public interface ICommunityService extends RemoteService {
	
	public CommunityCollection get(String type);
	
}
