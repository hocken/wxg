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
package de.rwth_aachen.dbis.wsxmppgateway.exception;

import de.rwth_aachen.dbis.wsxmppgateway.XMPPConstants;
import de.rwth_aachen.dbis.wsxmppgateway.error.StreamError;

/**
 * A throwable wrapper for class {@link StreamError}
 * @see XMPPConstants
 * @author Patrick Schlebusch (schlebu@dbis.rwth-aachen.de) & Christian Hocken (hocken@dbis.rwth-aachen.de)
 * @version $Revision 0.1 $, $Date: 2011/06/21 08:54:16 $
 */
public class StreamErrorException extends Exception {
	
	/**
	 * generated serial version UID
	 */
	private static final long serialVersionUID = -8885817580289448845L;
	private StreamError streamError;
	
	/**
	 * Creates a new stream error exception
	 * @param code the error code according to {@link XMPPConstants}
	 */
	public StreamErrorException(int code) {
		streamError = new StreamError(code);
	}
	
	/**
	 * Creates a new stream error exception
	 * @param code the error code according to {@link XMPPConstants}
	 * @param message A message that can be passed to the recipient
	 * @param language The language of the message. Valid values are "de", "en", "fr", etc.
	 */
	public StreamErrorException(int code, String message,
			String language) {
		super(message);
		streamError = new StreamError(code, message, language);
	}

	/**
	 * Get the wrapped stream error
	 * @return The wrapped stream error
	 */
	public StreamError getStreamError() {
		return streamError;
	}
	
}
