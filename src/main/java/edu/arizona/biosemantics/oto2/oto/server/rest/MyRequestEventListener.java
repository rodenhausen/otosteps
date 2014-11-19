package edu.arizona.biosemantics.oto2.oto.server.rest;

import org.glassfish.jersey.server.monitoring.RequestEvent;
import org.glassfish.jersey.server.monitoring.RequestEventListener;

import edu.arizona.biosemantics.common.log.LogLevel;

public class MyRequestEventListener implements RequestEventListener {
    private final int requestNumber;
    private final long startTime;
 
    public MyRequestEventListener(int requestNumber) {
        this.requestNumber = requestNumber;
        startTime = System.currentTimeMillis();
    }
 
    @Override
    public void onEvent(RequestEvent event) {
        switch (event.getType()) {
            case RESOURCE_METHOD_START:
            	log(LogLevel.DEBUG, "Resource method "
                    + event.getUriInfo().getMatchedResourceMethod()
                        .getHttpMethod()
                    + " started for request " + requestNumber);
                break;
            case FINISHED:
            	log(LogLevel.DEBUG, "Request " + requestNumber
                    + " finished. Processing time "
                    + (System.currentTimeMillis() - startTime) + " ms.");
                break;
        }
    }
}