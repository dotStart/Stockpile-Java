/*
 * Copyright 2018 Johannes Donath <johannesd@torchmind.com>
 * and other copyright owners as documented in the project's IP log.
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
package io.github.dotstart.stockpile.utility;

import com.google.protobuf.Any;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import edu.umd.cs.findbugs.annotations.NonNull;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Provides a registry which maps the types inside of DynamicAny to Java types.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public class DynamicAnyRegistry {

  /**
   * Specifies the type prefix which is used by Google's dynamic any implementation.
   */
  private static final String TYPE_PREFIX = "type.googleapis.com/";

  private final Map<String, Class<? extends Message>> rpcMap = new HashMap<>();
  private final Map<String, Function> pojoFactoryMap = new HashMap<>();

  /**
   * Registers a new message type.
   *
   * @param fqn a fully qualified type name (typically &lt;package&gt;.&lt;type&gt;).
   * @param rpcType a message type.
   * @param pojoType a Java type.
   */
  protected <M extends Message> void register(
      @NonNull String fqn,
      @NonNull Class<M> rpcType,
      @NonNull Function<M, ?> pojoFactory) {
    this.rpcMap.put(fqn, rpcType);
    this.pojoFactoryMap.put(fqn, pojoFactory);
  }

  /**
   * Reads the contents of a given any field.
   *
   * @param any an any message field.
   * @return a POJO object.
   */
  @NonNull
  @SuppressWarnings("unchecked")
  public Object read(@NonNull Any any) {
    String typeUrl = any.getTypeUrl();
    if (!typeUrl.startsWith(TYPE_PREFIX)) {
      throw new IllegalArgumentException(
          "Cannot decode object of type \"" + typeUrl + "\": Unknown type");
    }
    typeUrl = typeUrl.substring(TYPE_PREFIX.length());

    Class<? extends Message> messageType = this.rpcMap.get(typeUrl);
    if (messageType == null) {
      throw new IllegalArgumentException(
          "Cannot decode object of type \"" + any.getTypeUrl() + "\": Unknown type");
    }
    Function pojoFactory = this.pojoFactoryMap.get(typeUrl);

    try {
      Message rpcObject = any.unpack(messageType);
      return pojoFactory.apply(rpcObject);
    } catch (InvalidProtocolBufferException ex) {
      throw new IllegalArgumentException(
          "Failed to convert object of type \"" + any.getTypeUrl() + "\" to RPC type " + messageType
              .getName(), ex);
    }
  }
}
