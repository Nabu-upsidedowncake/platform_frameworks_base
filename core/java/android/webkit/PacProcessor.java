/*
 * Copyright 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package android.webkit;

import android.annotation.NonNull;
import android.annotation.Nullable;
import android.annotation.SystemApi;
import android.net.Network;

/**
 * Class to evaluate PAC scripts.
 * @hide
 */

@SystemApi
public interface PacProcessor {

    /**
     * Returns the default PacProcessor instance.
     *
     * @return the default PacProcessor instance.
     */
    @NonNull
    static PacProcessor getInstance() {
        return WebViewFactory.getProvider().getPacProcessor();
    }

    /**
     * Returns PacProcessor instance associated with the {@link Network}.
     * The host resolution is done on this {@link Network}.
     *
     * @param networkHandle a handle representing {@link Network} handle.
     * @return PacProcessor instance for the specified network.
     * @see Network#getNetworkHandle
     * @see Network#fromNetworkHandle
     */
    @NonNull
    static PacProcessor getInstanceForNetwork(long networkHandle) {
        return WebViewFactory.getProvider().getPacProcessorForNetwork(networkHandle);
    }

    /**
     * Set PAC script to use.
     *
     * @param script PAC script.
     * @return true if PAC script is successfully set.
     */
    boolean setProxyScript(@NonNull String script);

    /**
     * Gets a list of proxy servers to use.
     * @param url The URL being accessed.
     * @return a PAC-style semicolon-separated list of valid proxy servers.
     *         For example: "PROXY xxx.xxx.xxx.xxx:xx; SOCKS yyy.yyy.yyy:yy".
     */
    @Nullable
    String findProxyForUrl(@NonNull String url);

    /**
     * Stops support for this {@link PacProcessor} and release its resources.
     * No methods of this class must be called after calling this method.
     */
    default void releasePacProcessor() {
        throw new UnsupportedOperationException("Not implemented");
    }

    /**
     * Returns a network handle associated with this {@link PacProcessor}.
     *
     * @return a network handle or 0 if a network is unspecified.
     * @see Network#getNetworkHandle
     * @see Network#fromNetworkHandle
     */
    default long getNetworkHandle() {
        throw new UnsupportedOperationException("Not implemented");
    }
}
