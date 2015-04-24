/*
 * Copyright Terracotta, Inc.
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

package org.ehcache.internal.tier;

import org.ehcache.exceptions.CacheAccessException;
import org.ehcache.expiry.Expirations;
import org.ehcache.spi.cache.Store;
import org.ehcache.spi.cache.tiering.AuthoritativeTier;
import org.ehcache.spi.test.After;
import org.ehcache.spi.test.Before;
import org.ehcache.spi.test.Ignore;
import org.ehcache.spi.test.SPITest;

import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test the {@link AuthoritativeTier#flush(Object, Store.ValueHolder)} contract of the
 * {@link AuthoritativeTier AuthoritativeTier} interface.
 * <p/>
 *
 * @author Aurelien Broszniowski
 */

public class AuthoritativeTierFlush<K, V> extends SPIAuthoritativeTierTester<K, V> {

  protected AuthoritativeTier<K, V> tier;

  public AuthoritativeTierFlush(final AuthoritativeTierFactory<K, V> factory) {
    super(factory);
  }

  @Before
  public void setUp() {
  }

  @After
  public void tearDown() {
    if (tier != null) {
//      tier.close();
      tier = null;
    }
  }

  @SPITest
  @Ignore(reason = "until issue 362/363 is clarified if a boolean should be returned")
  @SuppressWarnings("unchecked")
  public void entryIsFlushed() {
    K key = factory.createKey(1);
    final V value = factory.createValue(1);
    Store.ValueHolder<V> valueHolder = mock(Store.ValueHolder.class);
    when(valueHolder.expirationTime(any(TimeUnit.class))).thenReturn(1L);

    tier = factory.newStore(factory.newConfiguration(factory.getKeyType(), factory.getValueType(),
        1L, null, null, Expirations.noExpiration()));

    try {
      tier.put(key, value);
      tier.getAndFault(key);
    } catch (CacheAccessException e) {
      System.err.println("Warning, an exception is thrown due to the SPI test");
      e.printStackTrace();
    }

    assertThat(tier.flush(key, valueHolder), is(equalTo(true)));
  }

  @SPITest
  @Ignore(reason = "until issue 362/363 is clarified if a boolean should be returned")
  @SuppressWarnings("unchecked")
  public void entryIsNotFlushed() {
    K key = factory.createKey(1);
    final V value = factory.createValue(1);
    Store.ValueHolder<V> valueHolder = mock(Store.ValueHolder.class);
    when(valueHolder.expirationTime(any(TimeUnit.class))).thenReturn(1L);

    tier = factory.newStore(factory.newConfiguration(factory.getKeyType(), factory.getValueType(),
        1L, null, null, Expirations.noExpiration()));

    try {
      tier.put(key, value);
    } catch (CacheAccessException e) {
      System.err.println("Warning, an exception is thrown due to the SPI test");
      e.printStackTrace();
    }

    assertThat(tier.flush(key, valueHolder), is(equalTo(false)));
  }

  @SPITest
  @SuppressWarnings("unchecked")
  public void entryDoesNotExist() {
    K key = factory.createKey(1);
    Store.ValueHolder<V> valueHolder = mock(Store.ValueHolder.class);
    when(valueHolder.expirationTime(any(TimeUnit.class))).thenReturn(1L);

    tier = factory.newStore(factory.newConfiguration(factory.getKeyType(), factory.getValueType(),
        1L, null, null, Expirations.noExpiration()));

    assertThat(tier.flush(key, valueHolder), is(equalTo(false)));
  }

  @SPITest
  @SuppressWarnings("unchecked")
  public void exceptionWhenValueHolderIsNotAnInstanceFromTheCachingTier() {
    K key = factory.createKey(1);
    final V value = factory.createValue(1);

    tier = factory.newStore(factory.newConfiguration(factory.getKeyType(), factory.getValueType(),
        1L, null, null, Expirations.noExpiration()));

    Store.ValueHolder<V> valueHolder = null;
    try {
      tier.put(key, value);
      valueHolder = tier.get(key);
    } catch (CacheAccessException e) {
      System.err.println("Warning, an exception is thrown due to the SPI test");
    }

    try {
      tier.flush(key, valueHolder);
      throw new AssertionError();
    } catch (IllegalArgumentException e) {
      // expected
    }
  }
}
