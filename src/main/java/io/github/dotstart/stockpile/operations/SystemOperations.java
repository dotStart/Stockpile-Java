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
package io.github.dotstart.stockpile.operations;

import com.google.protobuf.Empty;
import edu.umd.cs.findbugs.annotations.NonNull;
import io.github.dotstart.stockpile.entity.system.PluginMetadata;
import io.github.dotstart.stockpile.entity.system.Status;
import io.github.dotstart.stockpile.rpc.System.PluginList;
import io.github.dotstart.stockpile.rpc.SystemServiceGrpc.SystemServiceBlockingStub;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Provides access to various system related operations.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public class SystemOperations {

  private final SystemServiceBlockingStub service;

  public SystemOperations(@NonNull SystemServiceBlockingStub service) {
    this.service = service;
  }

  /**
   * Retrieves the server status.
   *
   * @return a representation of the current server status.
   */
  @NonNull
  public Status getStatus() {
    return new Status(this.service.getStatus(Empty.getDefaultInstance()));
  }

  /**
   * Retrieves a set of loaded server plugins.
   *
   * @return a list of plugins.
   */
  @NonNull
  public Set<PluginMetadata> getPluginList() {
    PluginList rpc = this.service.getPlugins(Empty.getDefaultInstance());
    return rpc.getPluginsList().stream()
        .map(PluginMetadata::new)
        .collect(Collectors.toSet());
  }
}
