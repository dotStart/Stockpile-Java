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
import io.github.dotstart.stockpile.rpc.Events;
import java.time.Instant;
import java.util.Objects;

/**
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public class ProfileIdKey {

  private final String name;
  private final Instant at;

  public ProfileIdKey(
      @NonNull String name,
      @NonNull Instant at) {
    this.name = name;
    this.at = at;
  }

  public ProfileIdKey(@NonNull Events.ProfileIdKey rpc) {
    this.name = rpc.getName();
    this.at = Instant.ofEpochSecond(rpc.getAt());
  }

  @NonNull
  public String name() {
    return this.name;
  }

  @NonNull
  public Instant at() {
    return this.at;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof ProfileIdKey)) {
      return false;
    }
    ProfileIdKey that = (ProfileIdKey) o;
    return Objects.equals(this.name, that.name) &&
        Objects.equals(this.at, that.at);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return Objects.hash(this.name, this.at);
  }
}
