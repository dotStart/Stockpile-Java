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
package io.github.dotstart.stockpile.entity.server;

import com.google.common.hash.Hashing;
import com.google.common.net.InetAddresses;
import edu.umd.cs.findbugs.annotations.NonNull;
import io.github.dotstart.stockpile.rpc.Server.BlacklistOrBuilder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * <p>Represents a complete server blacklist.</p>
 *
 * <p>Server blacklists consist of multiple sha1 hashes (each respective hostname or IP address is
 * encoded using the ISO-8859-1 encoding and passed to the sha1 function) which identify each
 * respective blacklisted address or wildcards.</p>
 *
 * <p>Wildcards are resolved from the smallest range up to the largest. As such, hostnames will be
 * resolved starting from "sub.example.tld" and process over "*.example.tld" up to "*.tld" until a
 * matching hash is found or the elements are exhausted. The same applies to IP addresses where this
 * schema is invoked in reverse: 10.100.200.1 -&gt; 10.100.200.* -&gt; 10.100.* -&gt; 10.*</p>
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public class Blacklist {

  private static final Pattern INET_SPLIT_PATTERN = Pattern.compile("\\.");
  private final Set<String> hashes;

  public Blacklist(@NonNull Collection<String> hashes) {
    this.hashes = new HashSet<>(hashes);
  }

  public Blacklist(@NonNull BlacklistOrBuilder rpc) {
    this.hashes = new HashSet<>(rpc.getHashesList());
  }

  /**
   * Computes the hash of an arbitrary input value.
   *
   * @param in an input address.
   * @return a hash.
   */
  @NonNull
  private static String computeHash(@NonNull CharSequence in) {
    return Hashing.sha1().hashString(in, StandardCharsets.ISO_8859_1).toString();
  }

  /**
   * Evaluates whether the passed hostname or IP address has been blacklisted.
   *
   * @param address an address.
   * @return true if blacklisted, false otherwise.
   */
  public boolean isBlacklisted(@NonNull String address) {
    if (InetAddresses.isInetAddress(address)) {
      return this.isBlacklistedInetAddress(address);
    }

    return this.isBlacklistedHostname(address);
  }

  /**
   * Evaluates whether the specified IP address has been blacklisted.
   *
   * @param address an address.
   * @return true if blacklisted, false otherwise.
   */
  public boolean isBlacklistedInetAddress(@NonNull CharSequence address) {
    List<String> elements = Arrays.asList(INET_SPLIT_PATTERN.split(address));
    if (elements.size() != 4) {
      throw new IllegalArgumentException(
          "Illegal address: Must contain exactly 4 elements but contains " + elements.size());
    }

    for (int i = 3; i > 0; --i) {
      String addr = elements.subList(0, i).stream()
          .collect(Collectors.joining(".")) + ".*";
      String hash = computeHash(addr);

      if (this.hashes.contains(hash)) {
        return true;
      }
    }

    return false;
  }

  /**
   * Evaluates whether the specified hostname has been blacklisted.
   *
   * @param address a hostname.
   * @return true if blacklisted, false otherwise.
   */
  public boolean isBlacklistedHostname(@NonNull CharSequence address) {
    List<String> elements = Arrays.asList(INET_SPLIT_PATTERN.split(address));
    if (elements.isEmpty()) {
      throw new IllegalArgumentException("Illegal TLD: must at least contain a single segment");
    }

    for (int i = 1; i < elements.size(); ++i) {
      String addr = "*." + elements.subList(i, elements.size() - 1).stream()
          .collect(Collectors.joining("."));
      String hash = computeHash(addr);

      if (this.hashes.contains(hash)) {
        return true;
      }
    }

    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Blacklist)) {
      return false;
    }
    Blacklist blacklist = (Blacklist) o;
    return Objects.equals(this.hashes, blacklist.hashes);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return Objects.hash(this.hashes);
  }
}
