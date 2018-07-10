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
import edu.umd.cs.findbugs.annotations.Nullable;
import io.github.dotstart.stockpile.entity.profile.Profile;
import io.github.dotstart.stockpile.entity.server.Blacklist;
import io.github.dotstart.stockpile.rpc.Server.CheckBlacklistRequest;
import io.github.dotstart.stockpile.rpc.Server.CheckBlacklistResponse;
import io.github.dotstart.stockpile.rpc.Server.LoginRequest;
import io.github.dotstart.stockpile.rpc.ServerServiceGrpc.ServerServiceBlockingStub;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Provides access to various server related operations.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public class ServerOperations {

  private final ServerServiceBlockingStub service;

  public ServerOperations(@NonNull ServerServiceBlockingStub service) {
    this.service = service;
  }

  /**
   * Retrieves the complete server blacklist.
   *
   * @return a blacklist.
   */
  @NonNull
  public Blacklist getBlacklist() {
    return new Blacklist(this.service.getBlacklist(Empty.getDefaultInstance()));
  }

  /**
   * Checks the collection of hostnames or IP addresses against the blacklist and returns a list of
   * matched addresses.
   *
   * @param addresses a collection of addresses.
   * @return a collection of matched addresses.
   */
  @NonNull
  public Set<String> checkBlacklist(@NonNull Collection<String> addresses) {
    CheckBlacklistResponse rpc = this.service.checkBlacklist(
        CheckBlacklistRequest.newBuilder()
            .addAllAddresses(addresses)
            .build()
    );

    return new HashSet<>(rpc.getMatchedAddressesList());
  }

  /**
   * Performs a cache assisted login against the session API.
   *
   * @param displayName a display name.
   * @param serverId a server Id.
   * @param ip an ip address.
   * @return a profile.
   */
  @NonNull
  public Profile login(@NonNull String displayName, @NonNull String serverId, @Nullable String ip) {
    return new Profile(this.service.login(
        LoginRequest.newBuilder()
            .setDisplayName(displayName)
            .setServerId(serverId)
            .setIp(ip == null ? "" : ip)
            .build()
    ));
  }
}
