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
import edu.umd.cs.findbugs.annotations.Nullable;
import io.github.dotstart.stockpile.rpc.Common.ProfileTexturesOrBuilder;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Represents a parsed set of player textures.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public class ProfileTextures {

  private final Instant timestamp;
  private final UUID profileId;
  private final String profileName;
  private final URL skinUrl;
  private final URL capeUrl;

  public ProfileTextures(
      @NonNull Instant timestamp,
      @NonNull UUID profileId,
      @NonNull String profileName,
      @Nullable URL skinUrl,
      @Nullable URL capeUrl) {
    this.timestamp = timestamp;
    this.profileId = profileId;
    this.profileName = profileName;
    this.skinUrl = skinUrl;
    this.capeUrl = capeUrl;
  }

  public ProfileTextures(@NonNull ProfileTexturesOrBuilder rpc) {
    this.timestamp = Instant.ofEpochSecond(rpc.getTimestamp());
    this.profileId = UUID.fromString(rpc.getProfileId());
    this.profileName = rpc.getProfileName();

    // TODO: This sucks (same goes for core implementation of this)
    try {
      this.skinUrl = !rpc.getSkinUrl().isEmpty() ? new URL(rpc.getSkinUrl()) : null;
      this.capeUrl = !rpc.getCapeUrl().isEmpty() ? new URL(rpc.getCapeUrl()) : null;
    } catch (MalformedURLException ex) {
      throw new IllegalArgumentException("Illegal skin url", ex);
    }
  }

  @NonNull
  public Instant getTimestamp() {
    return this.timestamp;
  }

  @NonNull
  public UUID getProfileId() {
    return this.profileId;
  }

  @NonNull
  public String getProfileName() {
    return this.profileName;
  }

  @NonNull
  public Optional<URL> getSkinUrl() {
    return Optional.ofNullable(this.skinUrl);
  }

  @NonNull
  public Optional<URL> getCapeUrl() {
    return Optional.ofNullable(this.capeUrl);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof ProfileTextures)) {
      return false;
    }
    ProfileTextures that = (ProfileTextures) o;
    return Objects.equals(this.timestamp, that.timestamp) &&
        Objects.equals(this.profileId, that.profileId) &&
        Objects.equals(this.profileName, that.profileName) &&
        Objects.equals(this.skinUrl, that.skinUrl) &&
        Objects.equals(this.capeUrl, that.capeUrl);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return Objects
        .hash(this.timestamp, this.profileId, this.profileName, this.skinUrl, this.capeUrl);
  }
}
