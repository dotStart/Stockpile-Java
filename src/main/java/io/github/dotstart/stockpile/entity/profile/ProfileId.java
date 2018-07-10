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
import io.github.dotstart.stockpile.rpc.Profile.ProfileIdOrBuilder;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents a name -> profile assignment along with its respective validity period.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public class ProfileId {

  private final UUID id;
  private final String name;
  private final Instant firstSeenAt;
  private final Instant lastSeenAt;
  private final Instant validUntil;

  public ProfileId(
      @NonNull UUID id,
      @NonNull String name,
      @NonNull Instant firstSeenAt,
      @NonNull Instant lastSeenAt,
      @NonNull Instant validUntil) {
    this.id = id;
    this.name = name;
    this.firstSeenAt = firstSeenAt;
    this.lastSeenAt = lastSeenAt;
    this.validUntil = validUntil;
  }

  public ProfileId(@NonNull ProfileIdOrBuilder rpc) {
    this.id = UUID.fromString(rpc.getId());
    this.name = rpc.getName();
    this.firstSeenAt = Instant.ofEpochSecond(rpc.getFirstSeenAt());
    this.lastSeenAt = Instant.ofEpochSecond(rpc.getLastSeenAt());
    this.validUntil = Instant.ofEpochSecond(rpc.getValidUntil());
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
  public Instant getFirstSeenAt() {
    return this.firstSeenAt;
  }

  @NonNull
  public Instant getLastSeenAt() {
    return this.lastSeenAt;
  }

  @NonNull
  public Instant getValidUntil() {
    return this.validUntil;
  }

  /**
   * Evaluates whether this assignment is still considered valid at the current time.
   *
   * @return true of valid, false otherwise.
   */
  public boolean isValid() {
    return this.isValid(Instant.now());
  }

  /**
   * Evaluates whether this assignment is still considered valid at a given time.
   *
   * @param at a timestamp.
   * @return true if valid, false otherwise.
   */
  public boolean isValid(@NonNull Instant at) {
    return !at.isBefore(this.firstSeenAt) && !at.isAfter(this.validUntil);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof ProfileId)) {
      return false;
    }
    ProfileId profileId = (ProfileId) o;
    return Objects.equals(this.id, profileId.id) &&
        Objects.equals(this.name, profileId.name) &&
        Objects.equals(this.firstSeenAt, profileId.firstSeenAt) &&
        Objects.equals(this.lastSeenAt, profileId.lastSeenAt) &&
        Objects.equals(this.validUntil, profileId.validUntil);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return Objects.hash(this.id, this.name, this.firstSeenAt, this.lastSeenAt, this.validUntil);
  }
}
