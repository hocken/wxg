/*
 * Copyright 2011 Christian Hocken, Dominik Renzel, Chair of Information Systems at RWTH Aachen University, Germany
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.rwth_aachen.dbis.wsxmppgateway;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.jetty.http.ssl.SslContextFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.server.ssl.SslSelectChannelConnector;
import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocketHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.rwth_aachen.dbis.wsxmppgateway.connection.WsXMPPConnectionHandler;

/**
 * This class is the main class of the WebSocket XMPP gateway. It starts a Jetty HTTP servlet server with connectors and handlers.
 * @author Christian Hocken
 * @version $Revision 0.1 $, $Date: 2011/06/21 08:54:16 $
 */
public class WebSocketXmppGateway extends Server {
	
	public static final String version = "$Revision 0.1 $, $Date: 2011/06/21 08:54:16 $";
	
	//******************** defaults  ********************//
	private static final String		DEFAULT_CONFIG_FILE = "conf/server.xml";
	private static final String		DEFAULT_ALIASES_FILE ="conf/aliases.conf";
	private static final int		DEFAULT_TIMEOUT = 5*60*1000; //5 min
	private static final String		DEFAULT_DOC_ROOT = "www";
	private static final boolean	DEFAULT_LIST_DIRECTORIES = false;
	
	//******************** private static configuration switches ********************//
	private static String configFile = DEFAULT_CONFIG_FILE;
	private static String aliasesFile = DEFAULT_ALIASES_FILE;
	
	private static String hostname = null;
	private static int webSocketPort = -1;
	private static int sslWebSocketPort = -1;
	private static int timeout = DEFAULT_TIMEOUT; //connection timeout in ms
	
	private static String keyStorePath = null;
	private static String keyStorePassword = null;
	
	private static String docRoot = DEFAULT_DOC_ROOT;
	private static boolean listDirectories = DEFAULT_LIST_DIRECTORIES;
	
	//******************** private static variables ********************//
	//initialize slf4j logging framework
	private static final Logger logger = LoggerFactory.getLogger(WebSocketXmppGateway.class);
	
	//initialize the aliases cache
	private static Map<String, String> aliases = Collections.synchronizedMap(new HashMap<String, String>());
	
	//******************** private instance variables ********************//
	//Jetty connectors
	private SelectChannelConnector connector;
	private SslSelectChannelConnector sslConnector;
	
	//Jetty handlers
	private WebSocketHandler wsHandler;
	private ResourceHandler rHandler;
	
	//List of connected WebSocket clients
	private static final List<WebSocket> connectedSockets = Collections.synchronizedList(new ArrayList<WebSocket>());
	
	//instance of the gateway
	private static WebSocketXmppGateway gateway = null;
	
	/**
	 * Creates WebSocketXmppGateway and adds connectors and handlers as defined in static configuration switches
	 */
	public WebSocketXmppGateway() {
		//add Jetty connectors
		if (webSocketPort > -1) {
			//create plain channel connector
			connector = new SelectChannelConnector();
			connector.setHost(hostname);
			connector.setPort(webSocketPort);
			addConnector(connector);
		}
		if(sslWebSocketPort > -1) {
			//create ssl channel connector
			SslContextFactory sslContextFactory = new SslContextFactory();
			sslContextFactory.setKeyStore(keyStorePath);
			sslContextFactory.setKeyStorePassword(keyStorePassword);
			sslConnector = new SslSelectChannelConnector(sslContextFactory);
			sslConnector.setHost(hostname);
			sslConnector.setPort(sslWebSocketPort);
			addConnector(sslConnector);
		}
		
		//add Jetty handlers
		wsHandler = new WebSocketHandler() {	
			@Override
			public WebSocket doWebSocketConnect(HttpServletRequest request, String subProtocol) {
				if ("xmpp".equals(subProtocol.toLowerCase()))
					return new WsXMPPConnectionHandler(request);
				else {
					//TODO
					//Exception handling for missing or wrong sub protocol
					return null;
				}
			}
		};
		setHandler(wsHandler);
		rHandler = new ResourceHandler();
		rHandler.setResourceBase(docRoot);
		rHandler.setDirectoriesListed(listDirectories);
		wsHandler.setHandler(rHandler);
	}
	
	//******************** static methods ********************//
	public static void main(String...args) {
		logger.info("Starting WebSocket XMPP gateway - " + version);
		//load properties from location specified in configFile
		loadProperties();
		//load aliases in cache
		loadAliases();
		//init and start gateway
		gateway = new WebSocketXmppGateway();
		try {
			gateway.start();
			gateway.join();
		} catch (Exception e) {
			logger.error("Cannot start WebSocket XMPP gateway", e);
			System.exit(1);
		}
	}
	
	/**
	 * load properties from config file
	 */
	private static void loadProperties() {
		Properties prop = new Properties();
		FileInputStream fis;
		try {
			fis = new FileInputStream(configFile);
			prop.loadFromXML(fis);
			hostname = prop.getProperty("Host");
			webSocketPort = Integer.parseInt(prop.getProperty("Port", "-1"));
			sslWebSocketPort = Integer.parseInt(prop.getProperty("SSLport", "-1"));
			timeout = Integer.parseInt(prop.getProperty("Timeout", DEFAULT_TIMEOUT + ""));
			keyStorePath = prop.getProperty("Keystore");
			keyStorePassword = prop.getProperty("KeystorePassword");
			docRoot = prop.getProperty("DocRoot", DEFAULT_DOC_ROOT);
			listDirectories = "true".equals(prop.getProperty("DirectoryListing", DEFAULT_LIST_DIRECTORIES + "").toLowerCase());
		} catch (Exception e) {
			logger.error("Cannot load properties from config file " + configFile,e);
			System.exit(1);
		}
	}
	
	/**
	 * load aliases from config file
	 */
	private static void loadAliases() {
		Properties prop = new Properties();
		FileInputStream fis;
		try {
			fis = new FileInputStream(aliasesFile);
			prop.load(fis);
			Enumeration<Object> e = prop.keys();
			while (e.hasMoreElements()) {
				String host = (String)e.nextElement(); //save because properties are always Strings
				aliases.put(host.toLowerCase(), prop.getProperty(host));
			}
		} catch (FileNotFoundException e) {
			logger.error("Cannot load " + aliasesFile + ". File not found!", e);
		} catch (IOException e) {
			logger.error("Cannot process " + aliasesFile, e);
		}
	}

	/**
	 * Get a list of connected WebSockets
	 * @return a list of connected WebSockets
	 */
	public static List<WebSocket> getConnectedSockets() {
		return connectedSockets;
	}

	/**
	 * Get the specified timeout
	 * @return the specified timeout in ms
	 */
	public static int getTimeout() {
		return timeout;
	}

	/**
	 * Get aliases for hostnames in the "to" attribute of the opening stream tag
	 * @return aliases for hostnames in the "to" attribute
	 */
	public static Map<String, String> getAliases() {
		return aliases;
	}
	
}
