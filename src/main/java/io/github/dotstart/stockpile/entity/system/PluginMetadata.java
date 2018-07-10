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
import io.github.dotstart.stockpile.rpc.System.PluginOrBuilder;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Represents the metadata associated with a server plugin.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public class PluginMetadata {

  private final String name;
  private final String version;
  private final Set<String> authors;
  private final String website;

  public PluginMetadata(
      @NonNull String name,
      @NonNull String version,
      @NonNull Set<String> authors,
      @NonNull String website) {
    this.name = name;
    this.version = version;
    this.authors = new HashSet<>(authors);
    this.website = website;
  }

  public PluginMetadata(@NonNull PluginOrBuilder rpc) {
    this.name = rpc.getName();
    this.version = rpc.getVersion();
    this.authors = new HashSet<>(rpc.getAuthorsList());
    this.website = rpc.getWebsite();
  }

  @NonNull
  public String getName() {
    return this.name;
  }

  @NonNull
  public String getVersion() {
    return this.version;
  }

  @NonNull
  public Set<String> getAuthors() {
    return Collections.unmodifiableSet(this.authors);
  }

  @NonNull
  public String getWebsite() {
    return this.website;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof PluginMetadata)) {
      return false;
    }
    PluginMetadata that = (PluginMetadata) o;
    return Objects.equals(this.name, that.name) &&
        Objects.equals(this.version, that.version) &&
        Objects.equals(this.authors, that.authors) &&
        Objects.equals(this.website, that.website);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return Objects.hash(this.name, this.version, this.authors, this.website);
  }
}
