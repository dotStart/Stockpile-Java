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
package io.github.dotstart.stockpile;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.github.dotstart.stockpile.operations.EventOperations;
import io.github.dotstart.stockpile.operations.ProfileOperations;
import io.github.dotstart.stockpile.operations.ServerOperations;
import io.github.dotstart.stockpile.operations.SystemOperations;
import io.github.dotstart.stockpile.rpc.EventServiceGrpc;
import io.github.dotstart.stockpile.rpc.ProfileServiceGrpc;
import io.github.dotstart.stockpile.rpc.ServerServiceGrpc;
import io.github.dotstart.stockpile.rpc.SystemServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import java.util.concurrent.TimeUnit;

/**
 * Provides a client for an arbitrary Stockpile server instance.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public class Stockpile implements AutoCloseable {

  private final ManagedChannel channel;
  private final EventOperations eventOperations;
  private final ProfileOperations profileOperations;
  private final ServerOperations serverOperations;
  private final SystemOperations systemOperations;

  public Stockpile(@NonNull String hostname) {
    this(hostname, 36623);
  }

  public Stockpile(@NonNull String hostname, int port) {
    this(
        ManagedChannelBuilder.forAddress(hostname, port)
            .usePlaintext()
            .build()
    );
  }

  public Stockpile(@NonNull ManagedChannel channel) {
    this.channel = channel;
    this.eventOperations = new EventOperations(EventServiceGrpc.newBlockingStub(channel));
    this.profileOperations = new ProfileOperations(
        ProfileServiceGrpc.newBlockingStub(channel));
    this.serverOperations = new ServerOperations(ServerServiceGrpc.newBlockingStub(channel));
    this.systemOperations = new SystemOperations(SystemServiceGrpc.newBlockingStub(channel));
  }

  @NonNull
  public EventOperations eventOperations() {
    return this.eventOperations;
  }

  @NonNull
  public ProfileOperations profileOperations() {
    return this.profileOperations;
  }

  @NonNull
  public ServerOperations serverOperations() {
    return this.serverOperations;
  }

  @NonNull
  public SystemOperations systemOperations() {
    return this.systemOperations;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void close() throws InterruptedException {
    this.channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
  }
}
