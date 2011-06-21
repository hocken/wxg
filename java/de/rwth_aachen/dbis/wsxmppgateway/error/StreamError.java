/*
 * Copyright 2011 Christian Hocken, Dominik Renzel,
 * Chair of Computer Science 5 (Information Systems) at RWTH Aachen University, Germany.*
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
package de.rwth_aachen.dbis.wsxmppgateway.error;

import de.rwth_aachen.dbis.wsxmppgateway.XMPPConstants;

/**
 * StreamError is a wrapper class for an XMPP stream error
 * @see XMPPConstants
 * @author Patrick Schlebusch (schlebu@dbis.rwth-aachen.de) & Christian Hocken (hocken@dbis.rwth-aachen.de)
 * @version $Revision 0.1 $, $Date: 2011/06/21 08:54:16 $
 */
public class StreamError {
	private int code;
	private String message;
	private String language;
	private String appContent;
	
	/**
	 * Creates a new stream error
	 * @param code the error code according to {@link XMPPConstants}
	 */
	public StreamError(int code) {
		this.code = code;
	}

	/**
	 * Creates a new stream error
	 * @param code the error code according to {@link XMPPConstants}
	 * @param message A message that can be passed to the recipient
	 * @param language The language of the message. Valid values are "de", "en", "fr", etc.
	 */
	public StreamError(int code, String message, String language) {
		this.code = code;
		this.message = message;
		this.language = language;
	}
	
	/**
	 * Creates a new stream error
	 * @param code the error code according to {@link XMPPConstants}
	 * @param message A message that can be passed to the recipient
	 * @param language The language of the message. Valid values are "de", "en", "fr", etc.
	 * @param appContent an additional message filled by XEP handlers
	 */
	public StreamError(int code, String message, String language, String appContent) {
		this(code, message, language);
		this.appContent = appContent;
	}

	/**
	 * Get the error code
	 * @return The error code according to {@link XMPPConstants}
	 */
	public int getCode() {
		return code;
	}

	/**
	 * Get the message
	 * @return The message if set, otherwise null
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Get the language of the message
	 * @return The language of the message if set, otherwise null
	 */
	public String getLanguage() {
		return language;
	}
	
	/**
	 * Get the application content
	 * @return The application content if set, otherwise null
	 */
	public String getAppContent() {
		return appContent;
	}
	
	/**
	 * 
	 * @return true if the message has been set
	 */
	public boolean hasMessage() {
		return (message != null);
	}
	
	/**
	 * 
	 * @return true if the language of the message has been set
	 */
	public boolean hasLanguage() {
		return (language != null);
	}

	/**
	 * 
	 * @return true if the application content has been set
	 */
	public boolean hasAppContent() {
		return (appContent != null);
	}

}
