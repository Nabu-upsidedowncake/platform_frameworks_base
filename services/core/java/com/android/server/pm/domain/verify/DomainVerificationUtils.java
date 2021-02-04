/*
 * Copyright (C) 2020 The Android Open Source Project
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

package com.android.server.pm.domain.verify;

import android.annotation.CheckResult;
import android.annotation.NonNull;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;

final class DomainVerificationUtils {

    /**
     * Consolidates package exception messages. A generic unavailable message is included since
     * the caller doesn't bother to check why the package isn't available.
     */
    @CheckResult
    static NameNotFoundException throwPackageUnavailable(@NonNull String packageName)
            throws NameNotFoundException {
        throw new NameNotFoundException("Package " + packageName + " unavailable");
    }

    static boolean isDomainVerificationIntent(Intent intent) {
        return intent.isWebIntent()
                && intent.hasCategory(Intent.CATEGORY_BROWSABLE)
                && intent.hasCategory(Intent.CATEGORY_DEFAULT);
    }
}
