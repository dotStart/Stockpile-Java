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

import com.google.common.collect.Iterators;
import com.google.protobuf.Empty;
import edu.umd.cs.findbugs.annotations.NonNull;
import io.github.dotstart.stockpile.entity.event.Event;
import io.github.dotstart.stockpile.rpc.EventServiceGrpc.EventServiceBlockingStub;
import java.util.Iterator;

/**
 * Provides various event related operations.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public class EventOperations {

  private final EventServiceBlockingStub service;

  public EventOperations(@NonNull EventServiceBlockingStub service) {
    this.service = service;
  }

  /**
   * Subscribes to the server's cache event stream.
   *
   * @return an iterator of cache events.
   */
  @NonNull
  public Iterator<Event<?, ?>> stream() {
    return Iterators.transform(this.service.streamEvents(Empty.getDefaultInstance()), Event::new);
  }
}
