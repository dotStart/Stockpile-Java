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
package io.github.dotstart.stockpile.entity.profile;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.github.dotstart.stockpile.rpc.Common;
import io.github.dotstart.stockpile.rpc.Common.ProfileOrBuilder;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public class Profile {

  private final UUID id;
  private final String name;
  private final Map<String, ProfileProperty> propertyMap;
  private final ProfileTextures textures;

  public Profile(
      @NonNull UUID id,
      @NonNull String name,
      @NonNull Map<String, ProfileProperty> propertyMap,
      @NonNull ProfileTextures textures) {
    this.id = id;
    this.name = name;
    this.propertyMap = new HashMap<>(propertyMap);
    this.textures = textures;
  }

  public Profile(@NonNull ProfileOrBuilder rpc) {
    this.id = UUID.fromString(rpc.getId());
    this.name = rpc.getName();
    this.textures = new ProfileTextures(rpc.getTextures());
    this.propertyMap = new HashMap<>();

    for (Common.ProfileProperty property : rpc.getPropertiesList()) {
      this.propertyMap.put(property.getName(), new ProfileProperty(property));
    }
  }

  @NonNull
  public UUID getId() {
    return this.id;
  }

  @NonNull
  public String getName() {
    return this.name;
  }

  @NonNull
  public Map<String, ProfileProperty> getPropertyMap() {
    return Collections.unmodifiableMap(this.propertyMap);
  }

  @NonNull
  public Optional<ProfileProperty> getProperty(@NonNull String key) {
    return Optional.ofNullable(this.propertyMap.get(key));
  }

  @NonNull
  public Optional<byte[]> getPropertyValue(@NonNull String key) {
    return this.getProperty(key).map(ProfileProperty::getValue);
  }

  @NonNull
  public ProfileTextures getTextures() {
    return this.textures;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Profile)) {
      return false;
    }
    Profile profile = (Profile) o;
    return Objects.equals(this.id, profile.id) &&
        Objects.equals(this.name, profile.name) &&
        Objects.equals(this.propertyMap, profile.propertyMap) &&
        Objects.equals(this.textures, profile.textures);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return Objects.hash(this.id, this.name, this.propertyMap, this.textures);
  }
}
