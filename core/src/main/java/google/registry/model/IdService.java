// Copyright 2021 The Nomulus Authors. All Rights Reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
package google.registry.model;

import static com.google.common.base.Preconditions.checkState;

import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.common.annotations.VisibleForTesting;
import google.registry.beam.common.RegistryPipelineWorkerInitializer;
import google.registry.config.RegistryEnvironment;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Allocates a globally unique {@link Long} number to use as an Ofy {@code @Id}.
 *
 * <p>In non-test, non-beam environments the Id is generated by Datastore, otherwise it's from an
 * atomic long number that's incremented every time this method is called.
 */
public final class IdService {

  /**
   * A placeholder String passed into DatastoreService.allocateIds that ensures that all ids are
   * initialized from the same id pool.
   */
  private static final String APP_WIDE_ALLOCATION_KIND = "common";

  /**
   * Counts of used ids for use in unit tests or Beam.
   *
   * <p>Note that one should only use self-allocate Ids in Beam for entities whose Ids are not
   * important and are not persisted back to the database, i. e. nowhere the uniqueness of the ID is
   * required.
   */
  private static final AtomicLong nextSelfAllocatedId = new AtomicLong(1); // ids cannot be zero

  private static final boolean isSelfAllocated() {
    return RegistryEnvironment.UNITTEST.equals(RegistryEnvironment.get())
        || "true".equals(System.getProperty(RegistryPipelineWorkerInitializer.PROPERTY, "false"));
  }

  /** Allocates an id. */
  // TODO(b/201547855): Find a way to allocate a unique ID without datastore.
  public static long allocateId() {
    return isSelfAllocated()
        ? nextSelfAllocatedId.getAndIncrement()
        : DatastoreServiceFactory.getDatastoreService()
            .allocateIds(APP_WIDE_ALLOCATION_KIND, 1)
            .iterator()
            .next()
            .getId();
  }

  /** Resets the global self-allocated id counter (i.e. sets the next id to 1). */
  @VisibleForTesting
  public static void resetSelfAllocatedId() {
    checkState(
        isSelfAllocated(), "Can only call resetSelfAllocatedId() in unit tests or Beam pipelines");
    nextSelfAllocatedId.set(1); // ids cannot be zero
  }
}