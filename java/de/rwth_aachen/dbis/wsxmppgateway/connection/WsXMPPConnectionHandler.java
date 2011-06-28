/*
 * Copyright 2011 Christian Hocken, Dominik Renzel,
 * Chair of Computer Science 5 (Information Systems) at RWTH Aachen University, Germany.
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
package de.rwth_aachen.dbis.wsxmppgateway.connection;

import i5.simpleXML.Element;
import i5.simpleXML.EndOfBufferException;
import i5.simpleXML.TimeoutException;
import i5.simpleXML.XMLNoHeaderException;
import i5.simpleXML.XMLStreamParser;
import i5.simpleXML.XMLSyntaxException;
import i5.simpleXML.XMLWriter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.jetty.websocket.WebSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.rwth_aachen.dbis.wsxmppgateway.WebSocketConstants;
import de.rwth_aachen.dbis.wsxmppgateway.WebSocketXmppGateway;
import de.rwth_aachen.dbis.wsxmppgateway.XMPPConstants;
import de.rwth_aachen.dbis.wsxmppgateway.error.StreamError;
import de.rwth_aachen.dbis.wsxmppgateway.exception.StreamErrorException;


/**
 * Connection between client and gateway that has been established via the WebSockets protocol
 * @author Christian Hocken (hocken@dbis.rwth-aachen.de)
 */
public class WsXMPPConnectionHandler implements WebSocket, WebSocket.OnFrame, WebSocket.OnTextMessage, WebSocket.OnControl {
	
	private static final String PARAM_XMPP_HOSTNAME = "xmpphost";
	private static final String PARAM_XMPP_PORT = "xmppport";

	//initialize slf4j logging framework
	private static final Logger logger = LoggerFactory.getLogger(WsXMPPConnectionHandler.class);
	
	private final HttpServletRequest request; //the HTTP request that performed the upgrade
	private FrameConnection connection; //the WebSocket connection to the client
	
	//xmpp servername and port extracted from the request
	private String xmppHostname = null;
	private int xmppPort = -1;
	
	private volatile int iStatus = -1; //stores connection state according to constants in WsConnectionState
	
	//flags to store gateway behavior
	private boolean openingTagSent = false; //true, if gateway has sent an opening stream tag
	private boolean closingTagSent = false; //true, if gateway has sent a closing stream tag
	
	//flags to store client behavior
	private boolean clientSentHeader = false; //true, if client has sent an XML header
	private boolean clientSentClosingTag = false; //true, if client has sent a closing stream tag
	private boolean serverSentClosingTag = false;  //true, if XMPP server has sent a closing stream tag
	
	private WsXMPPProxy wsProxy = null;
	
	@SuppressWarnings("unused")
	private String streamXmlLang = null; //TODO: Use in validity checking
	private Element receivedElement; //the stanza lately received from the client
	
	/**
	 * Creates a new WebSocket connection handler
	 * @param request the servlet request that performed the HTTP upgrade
	 */
	public WsXMPPConnectionHandler(HttpServletRequest request) {
		this.request = request;
		this.xmppHostname = request.getParameter(PARAM_XMPP_HOSTNAME);
		try {
			this.xmppPort = Integer.parseInt(request.getParameter(PARAM_XMPP_PORT));
		}
		catch (NumberFormatException e) {
			this.xmppPort = -1;
		}
		this.iStatus = WsConnectionState.CONNECTED;
	}
	
	/**
	 * Send a stanza to the connected client
	 * @param stanza the stanza to be sent to the client
	 */
	public void sendStanza(Element stanza) {
		sendMessage(stanza.toString(false));
	}
	
	/**
	 * Send a message to the connected client
	 * @param message the message to be sent to the client
	 */
	public void sendMessage(String message) {
		try {
			logger.debug(request.getRemoteHost() + " - sending message: " + message);
			connection.sendMessage(message);
		} catch (IOException e) {
			logger.info(request.getRemoteHost() + " - failed to send message", e);
		}		
	}
	
	/**
	 * Handle a stream error
	 * @param streamError the stream error to be sent to the client
	 */
	public void handleStreamError(StreamError streamError) {
		if (closingTagSent || iStatus >= WsConnectionState.DISCONNECTED)
			throw new IllegalStateException ( "Cannot send error stanza to an already closed stream!" );
		String xml = "";
		if(!openingTagSent)
			xml += "<stream:stream>";//TODO Add more data to opening tag (although this case will occur rarely)
		xml += "<stream:error>";		
		xml += "<" + XMPPConstants.errorCond2tagName(streamError.getCode()) + " xmlns=\"" + XMPPConstants.ERROR_NS_URN + "\" />";
		if (streamError.hasMessage()) {
			xml += "<text xmlns=\""+XMPPConstants.ERROR_NS_URN+ "\"";
			if (streamError.hasLanguage())
				xml += " xml:lang=\"" + streamError.getLanguage() + "\"";
			xml += " >" + streamError.getMessage() + "</text>";
		}
		if (streamError.hasAppContent())
			xml +=  streamError.getAppContent();
		xml += "</stream:error></stream:stream>"; //stream errors are terminal => close the stream
		sendMessage(xml);
		closingTagSent = true;
	}
	
	//******************** Methods from WebSocket interfaces ********************//
	
	@Override
	public void onHandshake(FrameConnection connection) {
		logger.info(request.getRemoteHost() + " - starting new WebSocket handler");
		this.connection = connection;
		iStatus = WsConnectionState.HANDSHAKE_COMPLETE;
	}
	
	@Override
	public void onOpen(Connection connection) {
		WebSocketXmppGateway.getConnectedSockets().add(this);
	}

	@Override
	public void onClose(int code, String message) {
		logger.info(request.getRemoteHost() + " - client is closing the connection with code " + code + " and message: " + message);
		if (!clientSentClosingTag) {
			//cleanup
			clientSentClosingTag = true;
			if(wsProxy != null)
				wsProxy.forwardClosingTagToServer();
		}
	}

	@Override
	public boolean onControl(byte controlCode, byte[] data, int offset, int length) {
		//TODO ping?! Not sure if it is handled by Jetty
		return false;
	}

	@Override
	public void onMessage(String data) {
		logger.debug(request.getRemoteHost() + " - received message: " + data);
		try {
			switch (iStatus) {
			case WsConnectionState.HANDSHAKE_COMPLETE:
				//read XML header, if present
				logger.info(request.getRemoteHost() + " - reading XML header");
				int headerEnd = data.indexOf("?>") +1;
				if (data.startsWith("<?xml") && headerEnd > 0) {
					clientSentHeader = true;
					String header = data.substring(0, headerEnd);
					readXmlHeader(header);
					data = data.substring(headerEnd + 1).trim();
				}
				iStatus = WsConnectionState.HEADER_READ;
				if (data.equals("")) //stop if no more data is sent in this message
					break;
			case WsConnectionState.HEADER_READ:
				logger.info(request.getRemoteHost() + " - opening stream");
				data.trim();
				String tagName = data.substring(1, data.indexOf(" "));
				data += "</" + tagName + ">"; //append closing tag to enable parsing
				Element root = new Element(data, false);
				if (wsProxy == null) { //not null after stream has been reseted
					String to = root.getAttribute("to");
					int port = 5222;
					//check for alias sent in request
					if (xmppHostname != null) {
						to = xmppHostname;
						if (xmppPort > -1)
							port = xmppPort;
					}
					//check for alias in cache
					else if (WebSocketXmppGateway.getAliases().containsKey(to.toLowerCase())) {
						to = WebSocketXmppGateway.getAliases().get(to.toLowerCase());	
					}
					wsProxy = new WsXMPPProxy(to, port, root);
					new Thread(wsProxy).start();
				}
				wsProxy.forwardOpeningTagToServer(clientSentHeader, root);
				iStatus = WsConnectionState.STREAM_OPENED;
				break;
			case WsConnectionState.STREAM_OPENED:
				if ("</stream:stream>".equals(data)) {
					logger.info(request.getRemoteHost() + " - closing stream");
					clientSentClosingTag = true;
					iStatus = WsConnectionState.STREAM_CLOSED;
					wsProxy.forwardClosingTagToServer();
				}
				else {
					//try to parse data
					receivedElement = new Element(data);
					logger.debug(request.getRemoteHost() + " - forwarding stanza:\n" + receivedElement.toString(false));
					wsProxy.forwardStanzaToServer(receivedElement);
				break;
				}
			case WsConnectionState.STREAM_CLOSED:
				//should not be reachable since no messages are received after the stream is closed
				//cleanup necessary?
				break;
			default:
				throw new StreamErrorException(XMPPConstants.ERROR_COND_UNDEFINIED_CONDITION, "No WebSockets message expected. The stream has already been closed!", "en");
			}
		} catch (XMLSyntaxException e) {
			logger.info(request.getRemoteAddr() + " - error during message handling", e);
			handleStreamError(new StreamError(XMPPConstants.ERROR_COND_XML_NOT_WELL_FORMED, e.getMessage(), "en"));
		} catch(UnknownHostException e) {
			logger.info(request.getRemoteAddr() + " - error during message handling", e);
			handleStreamError(new StreamError(XMPPConstants.ERROR_COND_HOST_UNKOWN, e.getMessage(), "en"));
		} catch(IOException e) {
			logger.info(request.getRemoteAddr() + " - error during message handling", e);
			handleStreamError(new StreamError(XMPPConstants.ERROR_COND_REMOTE_CONNECTION_FAILED, e.getMessage(), "en"));
		} catch (StreamErrorException e) {
			logger.info(request.getRemoteAddr() + " - error during message handling", e);
			handleStreamError(e.getStreamError());
		}
	}

	@Override
	public boolean onFrame(byte flags, byte opcode, byte[] data, int offset, int length) {
		return false;
	}
	
	/**
	 * Read the XML header sent by the client and check conformance
	 * @param data the XML header sent by the client
	 * @throws XMLSyntaxException 
	 */
	private void readXmlHeader(String data) throws XMLSyntaxException {
		//TODO check validity
	}

	/**
	 * Reset the stream after starting TLS encryption and SASL authentication
	 */
	private void resetStream() {
		//reset connection state
		iStatus = WsConnectionState.HANDSHAKE_COMPLETE;
	}

	/**
	 * Close the WebSocket connection
	 */
	private void closeConnection() {
		closeConnection(WebSocketConstants.CLOSE_NORMAL);
	}
	
	/**
	 * Close the WebSocket connection
	 * @param status the status code to be sent to the client
	 */
	private void closeConnection(int status) {
		connection.disconnect();
		iStatus = WsConnectionState.DISCONNECTED;
		WebSocketXmppGateway.getConnectedSockets().remove(this);
	}
	
	//******************** Private class for proxy handling ********************//
	
	/**
	 * Connection between gateway and the remote XMPP server
	 * 
	 * @author Christian Hocken (hocken@dbis.rwth-aachen.de)
	 */
	private class WsXMPPProxy implements Runnable {
		private String hostname;
		private int port;
		private Socket socket;
		private InputStream input;
		private OutputStream output;
		private XMLStreamParser xmlParser;
		private XMLWriter xmlWriter;
		
		private boolean serverSentHeader = false;
		
		//stores the status of the connection according to constants in WsConnectionState
		private volatile int iProxyStatus = -1;
		
		/**
		 * Creates a remote connection to an XMPP server
		 * @param hostname the hostname of the XMPP server
		 * @param port the port of the XMPP server
		 * @param root the opening stream element received from the WebSocket client
		 * @throws UnknownHostException if the hostname of the XMPP server is unknown
		 * @throws IOException if the connection can not be established
		 */
		public WsXMPPProxy(String hostname, int port, Element root) throws UnknownHostException, IOException{
			this.hostname = hostname;
			this.port = port;
			socket = new Socket(hostname, port);
			socket.setSoTimeout(WebSocketXmppGateway.getTimeout());
			input = socket.getInputStream();
			output = socket.getOutputStream();
			xmlParser = new XMLStreamParser(input);
			xmlWriter = new XMLWriter(output);
			iProxyStatus = WsConnectionState.CONNECTED;
			logger.info(getServername(true) + " - opened connection to XMPP server");
		}
		
		@Override
		public void run() {
			try {
				while (!Thread.currentThread().isInterrupted() && iProxyStatus < WsConnectionState.DISCONNECTED) {
					switch (iProxyStatus) {
						case WsConnectionState.CONNECTED:
							logger.info(getServername(true) + " - reading xml header");
							try {
								xmlParser.readHeader( true );
								serverSentHeader = true;
							} catch ( XMLNoHeaderException e ) {
								xmlParser.setHeaderRead();
							}
							iProxyStatus = WsConnectionState.HEADER_READ;
							//no break; necessary since root element is expected to be received next
						case WsConnectionState.HEADER_READ:
							logger.info(getServername(true) + " - opening stream");
							xmlParser.openRoot(true);
							Element root = xmlParser.getRoot();
							iProxyStatus = WsConnectionState.STREAM_OPENED;
							//forward header and root tag
							forwardOpeningTagToClient(serverSentHeader, root);
							//no break; necessary since stanzas are expected to be received next
						case WsConnectionState.STREAM_OPENED:
							Element stanza = xmlParser.getNextElement(true, true);
							if (stanza == null && xmlParser.isRootClosed()) {
								serverSentClosingTag = true;
								forwardClosingTagToClient();
								break;
							}
							else if (stanza.hasAttribute("xmlns") && stanza.getAttribute("xmlns").equals(XMPPConstants.URN_TLS)) {
								//do not forward stanza to client
								break;
							}
							else if (stanza.hasAttribute("xmlns") && stanza.getAttribute("xmlns").equals(XMPPConstants.URN_SASL)) {
								if ("success".equals(stanza.getName())) {
									resetStream();
								}
							}
							//forward stanza to client
							sendStanza(stanza);
							break;
						case WsConnectionState.STREAM_CLOSED:
							if (!socket.isClosed())
								socket.close();
							iProxyStatus = WsConnectionState.DISCONNECTED;
							break;
						default:
							throw new IllegalStateException("Illegal state (" + iProxyStatus + ") in WebSocket proxy!");
					}
				}
				
			} catch (EndOfBufferException e) {
				logger.info(getServername(true) + " - error during message handling", e);
				handleStreamError(new StreamError(XMPPConstants.ERROR_COND_REMOTE_CONNECTION_FAILED));
			} catch (XMLSyntaxException e) {
				logger.info(getServername(true) + " - error during message handling", e);
				handleStreamError( new StreamError(XMPPConstants.ERROR_COND_INVALID_XML, "Syntax error in xml stream!", "en") );
			} catch (TimeoutException e) {
				logger.info(getServername(true) + " - error during message handling", e);
				handleStreamError( new StreamError(XMPPConstants.ERROR_COND_CONNECTION_TIMEOUT) );
			} catch (IOException e) {
				logger.info(getServername(true) + " - error during message handling", e);
				handleStreamError(new StreamError(XMPPConstants.ERROR_COND_REMOTE_CONNECTION_FAILED));
			} catch (IllegalStateException e) {
				logger.error(getServername(true) + " - error during message handling", e);
				handleStreamError(new StreamError(XMPPConstants.ERROR_COND_INTERNAL_SERVER_ERROR));
			}
			
			//we need to close the connection to the XMPP server when the run loop has stopped
			if (!socket.isClosed()) {
				try{
					socket.close();
				} catch(IOException e){}
			}
			iProxyStatus = WsConnectionState.DISCONNECTED;
			
			//cleanup if an error has occured
			if (!serverSentClosingTag) {
				serverSentClosingTag = true;
				forwardClosingTagToClient();
			}
		}
		
		/**
		 * Reset the stream after starting TLS encryption and SASL authentication.
		 * Reopen the XML parser and the XML writer 
		 */
		private void resetStream() {
			//reset connection state in client connection
			WsXMPPConnectionHandler.this.resetStream();
			//reset XML stream parser
			xmlParser = new XMLStreamParser(input);
			xmlWriter = new XMLWriter(output);
			//reset connection state
			iProxyStatus = WsConnectionState.CONNECTED;
		}
		
		/**
		 * Forward the opening stream tag received from the client to the XMPP server
		 * @param sendXmlHeader true if an XML header should be sent
		 * @param root the opening stream tag
		 * @throws XMLSyntaxException if the passed stream element is malicious
		 */
		public void forwardOpeningTagToServer(boolean sendXmlHeader, Element root) throws XMLSyntaxException {
			synchronized (xmlWriter) {
				if ( sendXmlHeader )
					xmlWriter.print( "<?xml version=\"1.0\"?>" );
				xmlWriter.print ( "<stream:stream " );
				for (Enumeration<String> e = root.getAttributeNames(); e.hasMoreElements(); ) {
					String attribute = e.nextElement();
					xmlWriter.printXMLAttributes(attribute, root.getAttribute(attribute));
				}
				xmlWriter.print ( ">" );
				xmlWriter.flush();
			}			
		}
		
		/**
		 * Forward the opening stream tag received from the server to the client
		 * @param sendXmlHeader true if an XML header should be sent
		 * @param root the opening stream tag
		 * @throws XMLSyntaxException if the passed stream element is malicious
		 */
		private void forwardOpeningTagToClient(boolean sendXmlHeader, Element root) throws XMLSyntaxException {
			String xml = "";
			if (sendXmlHeader)
				xml += "<?xml version=\"1.0\"?>";
			xml += "<stream:stream ";
			for (Enumeration<String> e = root.getAttributeNames(); e.hasMoreElements(); ) {
				String attribute = e.nextElement();
				xml += attribute + "=\"" + root.getAttribute(attribute) + "\"";
			}
			xml +=">";
			sendMessage(xml);
			openingTagSent = true;
		}
		
		/**
		 * Forward the closing stream tag received from the client to the server.
		 * If the server is the party that closed the stream the connection to the client and
		 * to the XMPP server is closed. Otherwise the handler waits for a reply from the server.
		 */
		public void forwardClosingTagToServer() {
			synchronized (xmlWriter) {
				xmlWriter.print("</stream:stream>");
				xmlWriter.flush();
			}
			if (serverSentClosingTag) {
				//XMPP server sent initial closing tag. This is the reply of the client -> close connection
				iProxyStatus = WsConnectionState.STREAM_CLOSED;
				try {
					socket.close();
				} catch (IOException e) {}
				iProxyStatus = WsConnectionState.DISCONNECTED;
				closeConnection();
			}
			else {
				//client sent initial closing tag -> wait for reply.
			}
		}
		
		/**
		 * Forward the closing stream tag received from the server to the client.
		 * If the client is the party that closed the stream the connection to the client and
		 * to the XMPP server is closed.Otherwise the handler waits for a reply from the client.
		 */		
		private void forwardClosingTagToClient() {
			sendMessage("</stream:stream>");
			closingTagSent = true;
			if (clientSentClosingTag) {
				//client sent initial closing tag. This is the reply of the XMPP server -> close connection
				iProxyStatus = WsConnectionState.STREAM_CLOSED;
				try {
					socket.close();
				} catch (IOException e) {}
				iProxyStatus = WsConnectionState.DISCONNECTED;
				closeConnection();
			}
			else {
				//server sent initial closing tag -> wait for reply.
			}
		}

		/**
		 * Forward a stanza received from the client to the XMPP server
		 * @param stanza The stanza received from the client
		 */
		public void forwardStanzaToServer(Element stanza) {
			synchronized (xmlWriter) {
				xmlWriter.print(stanza.toString(false));
				xmlWriter.flush();
			}
		}
		
		/**
		 * Get the server name of the XMPP server
		 * @param printDedicatedClient if true, the hostname of the dedicated client is printed in brackets after the XMPP server hostname
		 * @return the server name of the XMPP server
		 */
		public String getServername(boolean printDedicatedClient) {
			String servername =  hostname + ":" + port;
			if(printDedicatedClient)
				servername += " (" + request.getRemoteHost() + ")";
			return servername;
		}

	}

}
