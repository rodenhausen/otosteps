package edu.arizona.biosemantics.oto2.oto.server.rest;

import java.util.Iterator;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;

import edu.arizona.biosemantics.common.log.LogLevel;
import edu.arizona.biosemantics.oto2.oto.server.db.DAOManager;
import edu.arizona.biosemantics.oto2.oto.server.rpc.CommunityService;
import edu.arizona.biosemantics.oto2.oto.shared.model.Collection;
import edu.arizona.biosemantics.oto2.oto.shared.model.Label;
import edu.arizona.biosemantics.oto2.oto.shared.model.TrashLabel;
import edu.arizona.biosemantics.oto2.oto.shared.model.community.CommunityCollection;

/**
 * Just a REST-like wrapper around the RPC service
 * @author thomas
 */
@Path("/community")
public class CommunityResource {

	@Context
	UriInfo uriInfo;
	@Context
	Request request;		
	
	private CommunityService communityService = new CommunityService();
		
	public CommunityResource() {
		log(LogLevel.DEBUG, "CommunityResource initialized");
	}
	
	@Path("{type}")
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public CommunityCollection get(@PathParam("type") String type) {
		try {
			CommunityCollection result = communityService.get(type);
			return result;
		} catch (Exception e) {
			log(LogLevel.ERROR, "Exception", e);
			return null;
		}
	}

	private void removeTrashLabel(Collection collection) {
		Iterator<Label> labelIterator = collection.getLabels().iterator();
		while(labelIterator.hasNext()) {
			Label label = labelIterator.next();
			if(label instanceof TrashLabel) {
				labelIterator.remove();
			}
		}
	}
}