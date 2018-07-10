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
import io.github.dotstart.stockpile.rpc.Profile.NameHistoryEntryOrBuilder;
import java.time.Instant;
import java.util.Objects;

/**
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public class NameChange {

  private final String name;
  private final Instant changeToAt;
  private final Instant validUntil;

  public NameChange(
      @NonNull String name,
      @NonNull Instant changeToAt,
      @NonNull Instant validUntil) {
    this.name = name;
    this.changeToAt = changeToAt;
    this.validUntil = validUntil;
  }

  public NameChange(@NonNull NameHistoryEntryOrBuilder rpc) {
    this.name = rpc.getName();
    this.changeToAt = Instant.ofEpochSecond(rpc.getChangedToAt());
    this.validUntil = Instant.ofEpochSecond(rpc.getValidUntil());
  }

  @NonNull
  public String getName() {
    return this.name;
  }

  @NonNull
  public Instant getChangeToAt() {
    return this.changeToAt;
  }

  @NonNull
  public Instant getValidUntil() {
    return this.validUntil;
  }

  /**
   * Evaluates whether this name assignment is valid at the current time.
   *
   * @return true if valid, false otherwise.
   */
  public boolean isValid() {
    return this.isValid(Instant.now());
  }

  /**
   * Evaluates whether this name assignment is valid at a given time.
   *
   * @param at an arbitrary time.
   * @return true if valid, false otherwise.
   */
  public boolean isValid(@NonNull Instant at) {
    return !at.isBefore(this.changeToAt) && !at.isAfter(this.validUntil);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof NameChange)) {
      return false;
    }
    NameChange that = (NameChange) o;
    return Objects.equals(this.name, that.name) &&
        Objects.equals(this.changeToAt, that.changeToAt) &&
        Objects.equals(this.validUntil, that.validUntil);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return Objects.hash(this.name, this.changeToAt, this.validUntil);
  }
}
