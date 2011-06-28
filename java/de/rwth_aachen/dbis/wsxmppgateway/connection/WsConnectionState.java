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

/**
 * Constants that represent the life cycle of an XMPP connection 
 * @author Christian Hocken (hocken@dbis.rwth-aachen.de)
 */
public class WsConnectionState {
	
	public static final int CONNECTED = 0;
	public static final int HANDSHAKE_COMPLETE = 100;
	public static final int HEADER_READ = 200;
	public static final int STREAM_OPENED = 300;
	public static final int STREAM_CLOSED = 400;
	public static final int DISCONNECTED = 500;
}
