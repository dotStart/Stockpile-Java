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
import io.github.dotstart.stockpile.rpc.Profile.NameHistory;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Represents a complete history of changes.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public class NameChangeHistory {

  private final List<NameChange> changes;
  private final Instant validUntil;

  public NameChangeHistory(
      @NonNull List<NameChange> changes,
      @NonNull Instant validUntil) {
    this.changes = new ArrayList<>(changes);
    this.validUntil = validUntil;
  }

  public NameChangeHistory(@NonNull NameHistory rpc) {
    this.validUntil = Instant.ofEpochSecond(rpc.getValidUntil());
    this.changes = rpc.getHistoryList().stream()
        .map(NameChange::new)
        .collect(Collectors.toList());
  }

  @NonNull
  public List<NameChange> changes() {
    return Collections.unmodifiableList(this.changes);
  }

  @NonNull
  public Instant validUntil() {
    return this.validUntil;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof NameChangeHistory)) {
      return false;
    }
    NameChangeHistory that = (NameChangeHistory) o;
    return Objects.equals(this.changes, that.changes) &&
        Objects.equals(this.validUntil, that.validUntil);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return Objects.hash(this.changes, this.validUntil);
  }
}
