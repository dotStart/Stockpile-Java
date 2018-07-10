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
package io.github.dotstart.stockpile.entity.event;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import io.github.dotstart.stockpile.entity.profile.NameChangeHistory;
import io.github.dotstart.stockpile.entity.profile.Profile;
import io.github.dotstart.stockpile.entity.profile.ProfileId;
import io.github.dotstart.stockpile.entity.server.Blacklist;
import io.github.dotstart.stockpile.rpc.Common;
import io.github.dotstart.stockpile.rpc.Events;
import io.github.dotstart.stockpile.rpc.Profile.NameHistory;
import io.github.dotstart.stockpile.rpc.Server;
import io.github.dotstart.stockpile.utility.DynamicAnyRegistry;
import java.util.Objects;
import java.util.UUID;

/**
 * <p>Represents a cache event.</p>
 *
 * <p>At the moment, cache events are limited to updates in which a cache entry is populated.
 * Specifically, we currently recognize the following entry types:</p>
 *
 * <ul>
 * <li>ProfileId Assignments</li>
 * <li>Name Histories</li>
 * <li>Profiles</li>
 * <li>Blacklists</li>
 * </ul>
 *
 * <p>Note that events about purge requests are not passed at the moment.</p>
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public class Event<K, V> {

  private static DynamicAnyRegistry KEY_REGISTRY = new DynamicAnyRegistry() {
    {
      this.register("rpc.IdKey", Events.IdKey.class, (key) -> UUID.fromString(key.getId()));
      this.register("rpc.ProfileIdKey", Events.ProfileIdKey.class, ProfileIdKey::new);
    }
  };
  private static DynamicAnyRegistry VALUE_REGISTRY = new DynamicAnyRegistry() {
    {
      this.register("rpc.ProfileId", io.github.dotstart.stockpile.rpc.Profile.ProfileId.class,
          ProfileId::new);
      this.register("rpc.NameHistory", NameHistory.class, NameChangeHistory::new);
      this.register("rpc.Profile", Common.Profile.class, Profile::new);
      this.register("rpc.Blacklist", Server.Blacklist.class, Blacklist::new);
    }
  };

  private final Type type;
  private final K key;
  private final V value;

  public Event(
      @NonNull Type type,
      @Nullable K key,
      @NonNull V value) {
    Class<?> keyType = type.getKeyType();
    if ((keyType == void.class && key != null) || !keyType.isInstance(key)) {
      throw new IllegalArgumentException(
          "Illegal key for event type " + type + ": Expected key of type " + keyType.getName()
              + " but got " + key.getClass().getName());
    }
    if (!type.getValueType().isInstance(value)) {
      throw new IllegalArgumentException(
          "Illegal value for event type " + type + ": Expected value of type " + type.getValueType()
              .getName() + " but got " + value.getClass().getName());
    }

    this.type = type;
    this.key = key;
    this.value = value;
  }

  @SuppressWarnings("unchecked") // TODO: Not particularly happy with this concept
  public Event(@NonNull Events.Event rpc) {
    this.type = Type.values()[rpc.getTypeValue()];
    this.key = (K) (rpc.hasKey() ? KEY_REGISTRY.read(rpc.getKey()) : null);
    this.value = (V) VALUE_REGISTRY.read(rpc.getObject());

    if (this.key != null && !this.type.getKeyType().isInstance(this.key)) {
      throw new IllegalArgumentException(
          "Illegal event key: Expected object of type " + this.type.getKeyType().getName()
              + " but got " + this.key.getClass().getName());
    }
    if (!this.type.getValueType().isInstance(this.value)) {
      throw new IllegalArgumentException(
          "Illegal event payload: Expected object of type " + this.type.getValueType().getName()
              + " but got " + this.value.getClass().getName());
    }
  }

  @NonNull // type_url: "type.googleapis.com/rpc.ProfileIdKey"
  public Type getType() {
    return this.type;
  }

  @Nullable
  public K getKey() {
    return this.key;
  }

  @NonNull
  public V getValue() {
    return this.value;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Event)) {
      return false;
    }
    Event<?, ?> event = (Event<?, ?>) o;
    return this.type == event.type &&
        Objects.equals(this.key, event.key) &&
        Objects.equals(this.value, event.value);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return Objects.hash(this.type, this.key, this.value);
  }

  /**
   * Provides a list of valid event types.
   */
  public enum Type {
    PROFILE_ID(ProfileIdKey.class, ProfileId.class),
    NAME_HISTORY(UUID.class, NameHistory.class),
    PROFILE(UUID.class, Profile.class),
    BLACKLIST(void.class, Blacklist.class);

    private final Class<?> keyType;
    private final Class<?> valueType;

    Type(@NonNull Class<?> keyType, @NonNull Class<?> valueType) {
      this.keyType = keyType;
      this.valueType = valueType;
    }

    /**
     * Evaluates whether this event type defines a key.
     *
     * @return true if a key is passed, false otherwise.
     */
    public boolean hasKey() {
      return this.keyType != void.class;
    }

    /**
     * Retrieves the key type which is typically passed along with this event.
     *
     * @return a key type.
     */
    @NonNull
    public Class<?> getKeyType() {
      return this.keyType;
    }

    /**
     * Retrieves the value type which is passed with this event.
     *
     * @return a value type.
     */
    @NonNull
    public Class<?> getValueType() {
      return this.valueType;
    }
  }
}
