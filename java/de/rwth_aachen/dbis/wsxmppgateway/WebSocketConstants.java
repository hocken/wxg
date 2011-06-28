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
 * Constants defined in the WebSockets protocol
 * @author Christian Hocken (hocken@dbis.rwth-aachen.de)
 */
public class WebSocketConstants {
	
	public static final int CLOSE_NORMAL = 1000;
	public static final int CLOSE_GOING_DOWN = 1001;
	public static final int CLOSE_PROTOCOL_ERROR = 1002;
	public static final int CLOSE_WRONG_ENCODING = 1003;
	public static final int CLOSE_MESSAGESIZE_EXCEEDED = 1004;

}
