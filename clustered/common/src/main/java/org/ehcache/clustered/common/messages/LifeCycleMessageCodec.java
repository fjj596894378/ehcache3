/*
 * Copyright Terracotta, Inc.
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
package org.ehcache.clustered.common.messages;

import org.ehcache.clustered.common.store.Util;

import java.nio.ByteBuffer;

/**
 *
 */
class LifeCycleMessageCodec {

  public byte[] encode(LifecycleMessage message) {
    byte[] encodedMsg = Util.marshall(message);
    ByteBuffer buffer = ByteBuffer.allocate(1 + encodedMsg.length);
    buffer.put(EhcacheEntityMessage.Type.LIFECYCLE_OP.getOpCode());
    buffer.put(encodedMsg);
    return buffer.array();
  }

  public EhcacheEntityMessage decode(byte[] payload) {
    ByteBuffer message = ByteBuffer.wrap(payload);
    byte[] encodedMsg = new byte[message.capacity() - 1];
    message.get();
    message.get(encodedMsg, 0, encodedMsg.length);
    EhcacheEntityMessage entityMessage = (EhcacheEntityMessage) Util.unmarshall(encodedMsg);
    return entityMessage;
  }

}
