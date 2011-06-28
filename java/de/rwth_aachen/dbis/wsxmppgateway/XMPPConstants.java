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
package de.rwth_aachen.dbis.wsxmppgateway;

/**
 * Constants defined in the XMPP Core and IM RFCs
 * @author Holger Janssen (janssen@dbis.rwth-aachen.de) & Christian Hocken (hocken@dbis.rwth-aachen.de)
 */
public class XMPPConstants {
	
	public static final String SUPPORTED_VERSION_MAJOR = "1";
	public static final String SUPPORTED_VERSION_MINOR = "0";

	public static final String XML_CLIENT_NS = "jabber:client";
	public static final String XML_NS_URI = "http://etherx.jabber.org/streams";
	
	public static final String XML_SERVER_NS = "jabber:server";
	public static final String XML_DIALBACK_NS = "jabber:server:dialback";
	
	public static final String ERROR_NS_URN = "urn:ietf:params:xml:ns:xmpp-streams";
	public static final String STANZA_ERROR_NS_URN = "urn:ietf:params:xml:ns:xmpp-stanzas";
	
	
	public static final int ERROR_COND_BAD_FORMAT = 1;
	public static final int ERROR_COND_BAD_NAMESPACE_PREFIX = 2;
	public static final int ERROR_COND_CONFLICT = 3;
	public static final int ERROR_COND_CONNECTION_TIMEOUT = 4;
	public static final int ERROR_COND_HOST_GONE = 5;
	public static final int ERROR_COND_HOST_UNKOWN = 6;
	public static final int ERROR_COND_IMPROPER_ADDRESSING = 7;
	public static final int ERROR_COND_INTERNAL_SERVER_ERROR = 100;
	public static final int ERROR_COND_INVALID_FROM = 8;
	public static final int ERROR_COND_INVAILD_ID = 9;
	public static final int ERROR_COND_INVALID_NAMESPACE = 10;
	public static final int ERROR_COND_INVALID_XML = 11;
	public static final int ERROR_COND_NOT_AUTHORIZED = 12;
	public static final int ERROR_COND_POLICY_VIOLATION = 13;
	public static final int ERROR_COND_REMOTE_CONNECTION_FAILED = 14;
	public static final int ERROR_COND_RESOURCE_CONTSTRAINT = 15;
	public static final int ERROR_COND_RESTRICTED_XML = 16;
	public static final int ERROR_COND_SEE_OTHER_HOST = 17;
	public static final int ERROR_COND_SYSTEM_SHUTDOWN = 18;
	public static final int ERROR_COND_UNDEFINIED_CONDITION = 19;
	public static final int ERROR_COND_UNSUPPORTED_ENCODING = 20;
	public static final int ERROR_COND_UNSUPPORTED_STANZA_TYPE = 21;
	public static final int ERROR_COND_UNSUPPORTED_VERSION = 22;
	public static final int ERROR_COND_XML_NOT_WELL_FORMED = 23;
	
	
	public static final int STANZA_ERROR_COND_BAD_REQUEST = 101;
	public static final int STANZA_ERROR_COND_CONFLICT = 102;
	public static final int STANZA_ERROR_COND_FEATURE_NOT_IMPLEMENTED = 103;
	public static final int STANZA_ERROR_COND_FORBIDDEN = 104;
	public static final int STANZA_ERROR_COND_GONE = 105;
	public static final int STANZA_ERROR_COND_INTERNAL_SERVER_ERROR = 106;
	public static final int STANZA_ERROR_COND_ITEM_NOT_FOUND = 107;
	public static final int STANZA_ERROR_COND_JID_MALFORMED = 108;
	public static final int STANZA_ERROR_COND_NOT_ACCEPTABLE = 109;
	public static final int STANZA_ERROR_COND_NOT_ALLOWED = 110;
	public static final int STANZA_ERROR_COND_NOT_AUTHORIZED = 111;
	public static final int STANZA_ERROR_COND_PAYMENT_REQUIRED = 112;
	public static final int STANZA_ERROR_COND_RECIPIENT_UNAVAILABLE = 113;
	public static final int STANZA_ERROR_COND_REDIRECT = 114;
	public static final int STANZA_ERROR_COND_REGISTRATION_REQUIRED = 115;
	public static final int STANZA_ERROR_COND_REMOTE_SERVER_NOT_FOUND = 116;
	public static final int STANZA_ERROR_COND_REMOTE_SERVER_TIMEOUT = 117;
	public static final int STANZA_ERROR_COND_RESOURCE_CONSTRAINT = 118;
	public static final int STANZA_ERROR_COND_SERVICE_UNAVAILABLE = 119;
	public static final int STANZA_ERROR_COND_SUBSCRIPTION_REQUIRED = 120;
	public static final int STANZA_ERROR_COND_UNDEFINED_CONDITION = 121;
	public static final int STANZA_ERROR_COND_UNEXPECTED_REQUEST = 122 ;
	
	public static final int STANZA_ERROR_TYPE_CANCEL = 201;
	public static final int STANZA_ERROR_TYPE_CONTINUE = 202;
	public static final int STANZA_ERROR_TYPE_MODIFY = 203;
	public static final int STANZA_ERROR_TYPE_AUTH = 204;
	public static final int STANZA_ERROR_TYPE_WAIT = 205;
	
	public static final int SASL_ERROR_COND_ABORTED = 301;
	public static final int SASL_ERROR_COND_INCORRECT_ENCODING = 302;
	public static final int SASL_ERROR_COND_INVALID_AUTHZID = 303;
	public static final int SASL_ERROR_COND_INVALID_MECHANISM = 304;
	public static final int SASL_ERROR_COND_MECHANISM_TOO_WEAK = 305;
	public static final int SASL_ERROR_COND_NOT_AUTHORIZED = 306;
	public static final int SASL_ERROR_COND_TEMPORARIY_AUTH_FAILURE = 307;
	
	public static final String URN_SASL    = "urn:ietf:params:xml:ns:xmpp-sasl";
	public static final String URN_TLS     = "urn:ietf:params:xml:ns:xmpp-tls";
	public static final String URN_BIND    = "urn:ietf:params:xml:ns:xmpp-bind";
	public static final String URN_SESSION = "urn:ietf:params:xml:ns:xmpp-session";
	
	public static final int DEFAULT_SERVER_PORT = 5269;
	public static final int DEFAULT_CLIENT_PORT = 5222;
	
	
	/**
	 * returns the corresponding xml tag name for the given error condition number.
	 * (refer to the ERROR_COND_... constants)
	 *
	 * @param    condition           an int
	 *
	 * @return   a String
	 *
	 */
	public static String errorCond2tagName ( int condition ) {
		switch ( condition ) {
			case ERROR_COND_BAD_FORMAT: return "bad-format";
			case ERROR_COND_BAD_NAMESPACE_PREFIX: return "bad-namespace-prefix";
			case ERROR_COND_CONFLICT: return "conflict";
			case ERROR_COND_CONNECTION_TIMEOUT: return "connection-timeout";
			case ERROR_COND_HOST_GONE: return "host-gone";
			case ERROR_COND_HOST_UNKOWN: return "host-unknown";
			case ERROR_COND_IMPROPER_ADDRESSING: return "improper-addressing";
			case ERROR_COND_INTERNAL_SERVER_ERROR: return "internal-server-error";
			case ERROR_COND_INVALID_FROM: return "invalid-from";
			case ERROR_COND_INVAILD_ID: return "invalid-id";
			case ERROR_COND_INVALID_NAMESPACE: return "invalid-namespace";
			case ERROR_COND_INVALID_XML: return "invalid-xml";
			case ERROR_COND_NOT_AUTHORIZED: return "not-authorized";
			case ERROR_COND_POLICY_VIOLATION: return "policy-violation";
			case ERROR_COND_REMOTE_CONNECTION_FAILED: return "remote-connection-failed";
			case ERROR_COND_RESOURCE_CONTSTRAINT: return "resource-constraint";
			case ERROR_COND_RESTRICTED_XML: return "restricted-xml";
			case ERROR_COND_SEE_OTHER_HOST: return "see-other-host";
			case ERROR_COND_SYSTEM_SHUTDOWN: return "system-shutdown";
			case ERROR_COND_UNDEFINIED_CONDITION: return "undefined-condition";
			case ERROR_COND_UNSUPPORTED_ENCODING: return "unsupported-encoding";
			case ERROR_COND_UNSUPPORTED_STANZA_TYPE: return "unsupported-stanza-type";
			case ERROR_COND_UNSUPPORTED_VERSION: return "unsupported-version";
			case ERROR_COND_XML_NOT_WELL_FORMED: return "xml-not-well-formed";
			default: throw new IllegalArgumentException ( "Unkown Error Condition" );
		}
	}
	
	
	/**
	 * returns the corresponding xml tag name for the given stanza error condition number.
	 * (refer to the STANZA_ERROR_COND_... constants)
	 *
	 * @param    condition           an int
	 *
	 * @return   a String
	 *
	 */
	public static String stanzaCond2tagName ( int condition ) {
		switch ( condition ) {
			case STANZA_ERROR_COND_BAD_REQUEST: return "bad-request";
			case STANZA_ERROR_COND_CONFLICT: return "conflict";
			case STANZA_ERROR_COND_FEATURE_NOT_IMPLEMENTED: return "not-implemented";
			case STANZA_ERROR_COND_FORBIDDEN: return "forbiden";
			case STANZA_ERROR_COND_GONE: return "gone";
			case STANZA_ERROR_COND_INTERNAL_SERVER_ERROR: return "internal-server-error";
			case STANZA_ERROR_COND_ITEM_NOT_FOUND: return "item-not-found";
			case STANZA_ERROR_COND_JID_MALFORMED: return "jid-malformed";
			case STANZA_ERROR_COND_NOT_ACCEPTABLE: return "not-acceptable";
			case STANZA_ERROR_COND_NOT_ALLOWED: return "not-allowed";
			case STANZA_ERROR_COND_NOT_AUTHORIZED: return "not-authorized";
			case STANZA_ERROR_COND_PAYMENT_REQUIRED: return "payment-required";
			case STANZA_ERROR_COND_RECIPIENT_UNAVAILABLE: return "recipient-unavailable";
			case STANZA_ERROR_COND_REDIRECT: return "redirect";
			case STANZA_ERROR_COND_REGISTRATION_REQUIRED: return "registration-required";
			case STANZA_ERROR_COND_REMOTE_SERVER_NOT_FOUND: return "remote-server-not-found";
			case STANZA_ERROR_COND_REMOTE_SERVER_TIMEOUT: return "remote-server-timeout";
			case STANZA_ERROR_COND_RESOURCE_CONSTRAINT: return "resource-constraint";
			case STANZA_ERROR_COND_SERVICE_UNAVAILABLE: return "service-unavailable";
			case STANZA_ERROR_COND_SUBSCRIPTION_REQUIRED: return "subscription-required";
			case STANZA_ERROR_COND_UNDEFINED_CONDITION: return "undefinied-condition";
			case STANZA_ERROR_COND_UNEXPECTED_REQUEST: return "unexpected-request";
	
			default: throw new IllegalArgumentException ( "Unkown stanza error condition id" );
		}
	}
	
	/**
	 * returns the corresponding xml type attribute value for the given stanza error condition number.
	 * (refer to the STANZA_ERROR_TYPE_... constants)
	 *
	 * @param    type           an int
	 *
	 * @return   a String
	 *
	 */
	public static String stanzaCondType2Attribute ( int type ) {
		switch ( type ) {
			case STANZA_ERROR_TYPE_CANCEL: return "cancel";
			case STANZA_ERROR_TYPE_CONTINUE: return "continue";
			case STANZA_ERROR_TYPE_MODIFY: return "modify";
			case STANZA_ERROR_TYPE_AUTH: return "auth";
			case STANZA_ERROR_TYPE_WAIT: return "wait";
			default: throw new IllegalArgumentException ( "Unkown stanza error type id: " + type );
		}
	}
	
	
	
	/**
	 * returns the tag name for an sasl error condition constant (refer to the SASL_ERROR_COND_... constants)
	 *
	 * @param    error               an int
	 *
	 * @return   a String
	 *
	 */
	public static String saslCond2Tag ( int error ) {
		switch ( error ) {
			case SASL_ERROR_COND_ABORTED: return "aborted";
			case SASL_ERROR_COND_INCORRECT_ENCODING: return "incorrect-encoding";
			case SASL_ERROR_COND_INVALID_AUTHZID: return "invalid-authzid";
			case SASL_ERROR_COND_INVALID_MECHANISM: return "invalid-mechanism";
			case SASL_ERROR_COND_MECHANISM_TOO_WEAK: return "mechanism-too-weak";
			case SASL_ERROR_COND_NOT_AUTHORIZED: return "not-authorized";
			case SASL_ERROR_COND_TEMPORARIY_AUTH_FAILURE: return "temporary-auth-failure";
			default:
				throw new IllegalArgumentException ( "Unkown SASL error condiftion" );
		}
	}
	
}
