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

import edu.umd.cs.findbugs.annotations.NonNull;
import io.github.dotstart.stockpile.entity.profile.NameChangeHistory;
import io.github.dotstart.stockpile.entity.profile.ProfileId;
import io.github.dotstart.stockpile.rpc.Common;
import io.github.dotstart.stockpile.rpc.Profile;
import io.github.dotstart.stockpile.rpc.Profile.BulkIdRequest;
import io.github.dotstart.stockpile.rpc.Profile.GetIdRequest;
import io.github.dotstart.stockpile.rpc.Profile.IdRequest;
import io.github.dotstart.stockpile.rpc.Profile.NameHistory;
import io.github.dotstart.stockpile.rpc.ProfileServiceGrpc.ProfileServiceBlockingStub;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Provides access to various profile related operations.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public class ProfileOperations {

  private final ProfileServiceBlockingStub service;

  public ProfileOperations(@NonNull ProfileServiceBlockingStub service) {
    this.service = service;
  }

  /**
   * Retrieves the profile Id which is associated with a given name at a given time.
   *
   * @param displayName a display name.
   * @param at a timestamp.
   * @return a profile association or, if none is associated, an empty optional is returned instead.
   */
  @NonNull
  public Optional<ProfileId> getProfileId(@NonNull String displayName, @NonNull Instant at) {
    Profile.ProfileId rpc = this.service.getId(
        GetIdRequest.newBuilder()
            .setName(displayName)
            .setTimestamp(at.getEpochSecond())
            .build()
    );

    if (rpc.getName().isEmpty()) {
      return Optional.empty();
    }

    return Optional.of(new ProfileId(rpc));
  }

  /**
   * Retrieves multiple profile Ids which are associated with the given names at the current time.
   *
   * @param names a collection of display names.
   * @return a list of associations and their case corrected names or, if no associations were
   * found, an empty list.
   */
  @NonNull
  public List<ProfileId> bulkGetProfileId(@NonNull Collection<String> names) {
    Profile.BulkIdResponse rpc = this.service.bulkGetId(
        BulkIdRequest.newBuilder()
            .addAllNames(names)
            .build()
    );

    return rpc.getIdsList().stream()
        .map(ProfileId::new)
        .collect(Collectors.toList());
  }

  /**
   * Retrieves a history of name changes for a given profile.
   *
   * @param id a profile Id.
   * @return a list of name changes or, if no such profile exists, an empty list.
   */
  @NonNull
  public Optional<NameChangeHistory> getNameHistory(@NonNull UUID id) {
    NameHistory rpc = this.service.getNameHistory(
        IdRequest.newBuilder()
            .setId(id.toString())
            .build()
    );

    if (rpc.getHistoryList().isEmpty()) {
      return Optional.empty();
    }

    return Optional.of(new NameChangeHistory(rpc));
  }

  /**
   * Retrieves a profile based on its identifier.
   *
   * @param id a profile Id.
   * @return a profile or, if no such profile exists, an empty optional.
   */
  @NonNull
  public Optional<io.github.dotstart.stockpile.entity.profile.Profile> getProfile(
      @NonNull UUID id) {
    Common.Profile rpc = this.service.getProfile(
        IdRequest.newBuilder()
            .setId(id.toString())
            .build()
    );

    if (rpc.getId().isEmpty()) {
      return Optional.empty();
    }

    return Optional.of(new io.github.dotstart.stockpile.entity.profile.Profile(rpc));
  }
}
