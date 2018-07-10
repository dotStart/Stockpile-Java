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
import io.github.dotstart.stockpile.rpc.Common.ProfilePropertyOrBuilder;
import java.util.Arrays;
import java.util.Base64;
import java.util.Objects;

/**
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public class ProfileProperty {

  private final String name;
  private final byte[] value;
  private final String signature;

  public ProfileProperty(
      @NonNull String name,
      @NonNull byte[] value,
      @NonNull String signature) {
    this.name = name;
    this.value = value;
    this.signature = signature;
  }

  public ProfileProperty(@NonNull ProfilePropertyOrBuilder rpc) {
    this.name = rpc.getName();
    this.value = Base64.getDecoder().decode(rpc.getValue());
    this.signature = rpc.getSignature();
  }

  @NonNull
  public String getName() {
    return this.name;
  }

  @NonNull
  public byte[] getValue() {
    byte[] copy = new byte[this.value.length];
    System.arraycopy(this.value, 0, copy, 0, copy.length);
    return copy;
  }

  @NonNull
  public String getSignature() {
    return this.signature;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof ProfileProperty)) {
      return false;
    }
    ProfileProperty that = (ProfileProperty) o;
    return Objects.equals(this.name, that.name) &&
        Arrays.equals(this.value, that.value) &&
        Objects.equals(this.signature, that.signature);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return Objects.hash(this.name, this.value, this.signature);
  }
}
