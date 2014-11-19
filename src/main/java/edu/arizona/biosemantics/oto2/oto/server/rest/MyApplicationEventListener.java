package edu.arizona.biosemantics.oto2.oto.server.rest;

import org.glassfish.jersey.server.monitoring.ApplicationEvent;
import org.glassfish.jersey.server.monitoring.ApplicationEventListener;
import org.glassfish.jersey.server.monitoring.RequestEvent;
import org.glassfish.jersey.server.monitoring.RequestEventListener;

import edu.arizona.biosemantics.common.log.LogLevel;

public class MyApplicationEventListener implements ApplicationEventListener {
    private volatile int requestCnt = 0;
 
    @Override
    public void onEvent(ApplicationEvent event) {
        switch (event.getType()) {
            case INITIALIZATION_FINISHED:
            	log(LogLevel.DEBUG, "Application " + event.getResourceConfig().getApplicationName()
                        + " was initialized.");
                break;
            case DESTROY_FINISHED:
            	log(LogLevel.DEBUG, "Application "
                    + event.getResourceConfig().getApplicationName() + " destroyed.");
                break;
        }
    }
 
    @Override
    public RequestEventListener onRequest(RequestEvent requestEvent) {
        requestCnt++;
        log(LogLevel.DEBUG, "Request " + requestCnt + " started.");
        // return the listener instance that will handle this request.
        return new MyRequestEventListener(requestCnt);
    }
}