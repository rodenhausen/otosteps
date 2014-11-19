package edu.arizona.biosemantics.oto2.oto.server;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.bridge.SLF4JBridgeHandler;

import edu.arizona.biosemantics.bioportal.client.BioPortalClient;
import edu.arizona.biosemantics.common.log.LogLevel;
import edu.arizona.biosemantics.oto2.oto.server.db.ConnectionPool;
import edu.arizona.biosemantics.oto2.oto.server.db.OntologyDAO;
import edu.arizona.biosemantics.oto2.oto.server.db.Query;

public class OTOServletContextListener implements ServletContextListener {
	private ConnectionPool connectionPool;

	@Override
	public void contextDestroyed(ServletContextEvent event) {
		log(LogLevel.INFO, "Destroy oto context " + event.getServletContext().getContextPath());
		try {
			log(LogLevel.INFO, "Shutting down conntection pool");
			connectionPool.shutdown();
			log(LogLevel.INFO, "Closing bioportal client");
			OntologyDAO.bioportalClient.close();
		} catch (Exception e) {
			log(LogLevel.ERROR, "Exception shutting down oto context", e);
		}
	}

	@Override
	public void contextInitialized(ServletContextEvent event) {
		log(LogLevel.INFO, "Initializing oto context at context path: " + event.getServletContext().getContextPath());
		log(LogLevel.INFO, "Configuration used " + Configuration.asString());
		
		log(LogLevel.INFO, "Install Java logging to SLF4J");
		SLF4JBridgeHandler.removeHandlersForRootLogger();
		SLF4JBridgeHandler.install();
		
		try {
			// init connection pool
			log(LogLevel.INFO, "Initializing connection pool");
			connectionPool = new ConnectionPool();
			Query.connectionPool = connectionPool;
			
			log(LogLevel.INFO, "Initializing bioportal client");
			OntologyDAO.bioportalClient = new BioPortalClient(Configuration.bioportalUrl, Configuration.bioportalApiKey);
			OntologyDAO.bioportalClient.open();
		} catch (Exception e) {
			log(LogLevel.ERROR, "Exception initializing oto context", e);
		}
	}
}
