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
package io.github.dotstart.stockpile.entity.system;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.github.dotstart.stockpile.rpc.System.StatusOrBuilder;
import java.time.Instant;
import java.util.Objects;

/**
 * Represents a set of basic status information on a server.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public class Status {

  private final String brand;
  private final String version;
  private final String versionFull;
  private final String commitHash;
  private final Instant buildTimestamp;

  public Status(
      @NonNull String brand,
      @NonNull String version,
      @NonNull String versionFull,
      @NonNull String commitHash,
      @NonNull Instant buildTimestamp) {
    this.brand = brand;
    this.version = version;
    this.versionFull = versionFull;
    this.commitHash = commitHash;
    this.buildTimestamp = buildTimestamp;
  }

  public Status(@NonNull StatusOrBuilder rpc) {
    this.brand = rpc.getBrand();
    this.version = rpc.getVersion();
    this.versionFull = rpc.getVersionFull();
    this.commitHash = rpc.getCommitHash();
    this.buildTimestamp = Instant.ofEpochSecond(rpc.getBuildTimestamp());
  }

  @NonNull
  public String getBrand() {
    return this.brand;
  }

  @NonNull
  public String getVersion() {
    return this.version;
  }

  @NonNull
  public String getVersionFull() {
    return this.versionFull;
  }

  @NonNull
  public String getCommitHash() {
    return this.commitHash;
  }

  @NonNull
  public Instant getBuildTimestamp() {
    return this.buildTimestamp;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Status)) {
      return false;
    }
    Status status = (Status) o;
    return Objects.equals(this.brand, status.brand) &&
        Objects.equals(this.version, status.version) &&
        Objects.equals(this.versionFull, status.versionFull) &&
        Objects.equals(this.commitHash, status.commitHash) &&
        Objects.equals(this.buildTimestamp, status.buildTimestamp);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return Objects
        .hash(this.brand, this.version, this.versionFull, this.commitHash, this.buildTimestamp);
  }
}
