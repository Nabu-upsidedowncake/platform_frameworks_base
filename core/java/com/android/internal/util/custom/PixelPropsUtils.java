/*
 * Copyright (C) 2020 The Pixel Experience Project
 *               2022 StatiXOS
 *               2021-2022 crDroid Android Project
 *               2019-2023 Evolution X
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.internal.util.custom;

import android.app.ActivityTaskManager;
import android.app.Application;
import android.app.TaskStackListener;
import android.content.ComponentName;
import android.content.Context;
import android.content.res.Resources;
import android.os.Binder;
import android.os.Build;
import android.os.Process;
import android.os.SystemProperties;
import android.text.TextUtils;
import android.util.Log;

import com.android.internal.R;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class PixelPropsUtils {

    private static final String PACKAGE_FINSKY = "com.android.vending";
    private static final String PACKAGE_GMS = "com.google.android.gms";
    private static final String PROCESS_GMS_UNSTABLE = PACKAGE_GMS + ".unstable";
    private static final String PACKAGE_SI = "com.google.android.settings.intelligence";
    private static final String SAMSUNG = "com.samsung.";
    private static final String SPOOF_MUSIC_APPS = "persist.sys.disguise_props_for_music_app";

    private static final String TAG = PixelPropsUtils.class.getSimpleName();
    private static final boolean DEBUG = false;

    private static final String[] sCertifiedProps =
            Resources.getSystem().getStringArray(R.array.config_certifiedBuildProperties);

    private static final Boolean sEnablePixelProps =
            Resources.getSystem().getBoolean(R.bool.config_enablePixelProps);

    private static final Map<String, Object> propsToChangeGeneric;
    private static final Map<String, Object> propsToChangePixelFold;
    private static final Map<String, Object> propsToChangeRecentPixel;
    private static final Map<String, Object> propsToChangePixel5;
    private static final Map<String, Object> propsToChangeMeizu;
    private static final Map<String, Object> propsToChangePixelXL;
    private static final Map<String, ArrayList<String>> propsToKeep;

    // Packages to Spoof as the most recent Pixel device
    private static final String[] packagesToChangeRecentPixel = {
                "com.android.chrome",
                "com.breel.wallpapers20",
                "com.microsoft.android.smsorganizer",
                "com.nothing.smartcenter",
                "com.nhs.online.nhsonline",
                "com.amazon.avod.thirdpartyclient",
                "com.disney.disneyplus",
                "in.startv.hotstar",
                "com.google.android.apps.emojiwallpaper",
                "com.google.android.wallpaper.effects",
                "com.google.pixel.livewallpaper",
                "com.google.android.apps.wallpaper.pixel",
                "com.google.android.inputmethod.latin",
                "com.google.android.apps.wallpaper",
                "com.google.android.apps.customization.pixel",
                "com.google.android.apps.privacy.wildlife",
                "com.google.android.apps.subscriptions.red"
    };

    private static final ArrayList<String> packagesToChangePixelFold = 
        new ArrayList<String> (
            Arrays.asList(
        ));

    private static final ArrayList<String> extraPackagesToChange = 
        new ArrayList<String> (
            Arrays.asList(
        ));

    private static final String[] customGoogleCameraPackages = {
            "com.google.android.MTCL83",
            "com.google.android.UltraCVM",
            "com.google.android.apps.cameralite"
    };

    // Packages to Keep with original device
    private static final String[] packagesToKeep = {
                "com.google.android.as",
                "com.google.android.apps.motionsense.bridge",
                "com.google.android.euicc",
                "com.google.ar.core",
                "com.google.android.youtube",
                "com.google.android.apps.youtube.kids",
                "com.google.android.apps.youtube.music",
                "com.google.android.apps.wearables.maestro.companion",
                "com.google.android.apps.subscriptions.red",
                "com.google.android.apps.tachyon",
                "com.google.android.apps.tycho",
                "com.google.android.apps.restore",
                "com.google.oslo",
                "it.ingdirect.app"
    };

    // Packages to Spoof as Meizu
    private static final String[] packagesToChangeMeizu = {
            "com.netease.cloudmusic",
            "com.tencent.qqmusic",
            "com.kugou.android",
            "com.kugou.android.lite",
            "cmccwm.mobilemusic",
            "cn.kuwo.player",
            "com.meizu.media.music"
    };

    private static final ComponentName GMS_ADD_ACCOUNT_ACTIVITY = ComponentName.unflattenFromString(
            "com.google.android.gms/.auth.uiflows.minutemaid.MinuteMaidActivity");

    private static volatile boolean sIsGms, sIsFinsky, sIsSetupWizard;
    private static volatile String sProcessName;

    private static final String sNetflixModel =
            Resources.getSystem().getString(R.string.config_netflixSpoofModel);

    static {
        propsToKeep = new HashMap<>();
        propsToKeep.put(PACKAGE_SI, new ArrayList<>(Collections.singletonList("FINGERPRINT")));
        propsToChangeGeneric = new HashMap<>();
        propsToChangeGeneric.put("TYPE", "user");
        propsToChangeGeneric.put("TAGS", "release-keys");
        propsToChangePixelFold = new HashMap<>();
        propsToChangePixelFold.put("BRAND", "google");
        propsToChangePixelFold.put("MANUFACTURER", "Google");
        propsToChangePixelFold.put("DEVICE", "felix");
        propsToChangePixelFold.put("PRODUCT", "felix");
        propsToChangePixelFold.put("HARDWARE", "felix");
        propsToChangePixelFold.put("MODEL", "Pixel Fold");
        propsToChangePixelFold.put("ID", "UP1A.231105.003");
        propsToChangePixelFold.put("FINGERPRINT", "google/felix/felix:14/UP1A.231105.003/11010452:user/release-keys");
        propsToChangeRecentPixel = new HashMap<>();
        propsToChangeRecentPixel.put("BRAND", "google");
        propsToChangeRecentPixel.put("MANUFACTURER", "Google");
        propsToChangeRecentPixel.put("DEVICE", "tangorpro");
        propsToChangeRecentPixel.put("PRODUCT", "tangorpro");
        propsToChangeRecentPixel.put("HARDWARE", "tangorpro");
        propsToChangeRecentPixel.put("MODEL", "Pixel Tab");
        propsToChangeRecentPixel.put("ID", "UP1A.231105.003");
        propsToChangeRecentPixel.put("FINGERPRINT", "google/tangorpro/tangorpro:14/UP1A.231105.003/11010452:user/release-keys");
        propsToChangePixel5 = new HashMap<>();
        propsToChangePixel5.put("BRAND", "google");
        propsToChangePixel5.put("MANUFACTURER", "Google");
        propsToChangePixel5.put("DEVICE", "redfin");
        propsToChangePixel5.put("PRODUCT", "redfin");
        propsToChangePixel5.put("HARDWARE", "redfin");
        propsToChangePixel5.put("MODEL", "Pixel 5");
        propsToChangePixel5.put("ID", "UP1A.231105.001");
        propsToChangePixel5.put("FINGERPRINT", "google/redfin/redfin:14/UP1A.231105.001/10817346:user/release-keys");
        propsToChangeMeizu = new HashMap<>();
        propsToChangeMeizu.put("BRAND", "meizu");
        propsToChangeMeizu.put("MANUFACTURER", "Meizu");
        propsToChangeMeizu.put("DEVICE", "m1892");
        propsToChangeMeizu.put("DISPLAY", "Flyme");
        propsToChangeMeizu.put("PRODUCT", "meizu_16thPlus_CN");
        propsToChangeMeizu.put("MODEL", "meizu 16th Plus");
        propsToChangePixelXL = new HashMap<>();
        propsToChangePixelXL.put("BRAND", "google");
        propsToChangePixelXL.put("MANUFACTURER", "Google");
        propsToChangePixelXL.put("DEVICE", "marlin");
        propsToChangePixelXL.put("PRODUCT", "marlin");
        propsToChangePixelXL.put("HARDWARE", "marlin");
        propsToChangePixelXL.put("MODEL", "Pixel XL");
        propsToChangePixelXL.put("ID", "QP1A.191005.007.A3");
        propsToChangePixelXL.put("FINGERPRINT", "google/marlin/marlin:10/QP1A.191005.007.A3/5972272:user/release-keys");
    }

    private static String getBuildID(String fingerprint) {
        Pattern pattern = Pattern.compile("([A-Za-z0-9]+\\.\\d+\\.\\d+\\.\\w+)");
        Matcher matcher = pattern.matcher(fingerprint);

        if (matcher.find()) {
            return matcher.group(1);
        }
        return "";
    }

    private static String getDeviceName(String fingerprint) {
        String[] parts = fingerprint.split("/");
        if (parts.length >= 2) {
            return parts[1];
        }
        return "";
    }

    private static boolean isGoogleCameraPackage(String packageName) {
        return packageName.startsWith("com.google.android.GoogleCamera") ||
            Arrays.asList(customGoogleCameraPackages).contains(packageName);
    }

    private static boolean shouldTryToCertifyDevice() {
        if (!sIsGms) return false;

        final boolean[] shouldCertify = {true};

        setPropValue("TIME", System.currentTimeMillis());

        final boolean was = isGmsAddAccountActivityOnTop();
        final String reason = "GmsAddAccountActivityOnTop";
        if (!was) {
            spoofBuildGms();
        }
        dlog("Skip spoofing build for GMS, because " + reason + "!");
        TaskStackListener taskStackListener = new TaskStackListener() {
            @Override
            public void onTaskStackChanged() {
                final boolean isNow = isGmsAddAccountActivityOnTop();
                if (isNow ^ was) {
                    dlog(String.format("%s changed: isNow=%b, was=%b, killing myself!", reason, isNow, was));
                    shouldCertify[0] = false;
                }
            }
        };
        try {
            ActivityTaskManager.getService().registerTaskStackListener(taskStackListener);
        } catch (Exception e) {
            Log.e(TAG, "Failed to register task stack listener!", e);
            spoofBuildGms();
        }
        return shouldCertify[0];
    }

    private static void spoofBuildGms() {
        if (sCertifiedProps == null || sCertifiedProps.length == 0) return;
        // Alter build parameters to avoid hardware attestation enforcement
        setPropValue("BRAND", sCertifiedProps[0]);
        setPropValue("MANUFACTURER", sCertifiedProps[1]);
        setPropValue("ID", sCertifiedProps[2].isEmpty() ? getBuildID(sCertifiedProps[6]) : sCertifiedProps[2]);
        setPropValue("DEVICE", sCertifiedProps[3].isEmpty() ? getDeviceName(sCertifiedProps[6]) : sCertifiedProps[3]);
        setPropValue("PRODUCT", sCertifiedProps[4].isEmpty() ? getDeviceName(sCertifiedProps[6]) : sCertifiedProps[4]);
        setPropValue("MODEL", sCertifiedProps[5]);
        setPropValue("FINGERPRINT", sCertifiedProps[6]);
        setPropValue("TYPE", sCertifiedProps[7].isEmpty() ? "user" : sCertifiedProps[7]);
        setPropValue("TAGS", sCertifiedProps[8].isEmpty() ? "release-keys" : sCertifiedProps[8]);
    }

    public static void setProps(Context context) {
        final String packageName = context.getPackageName();
        final String processName = Application.getProcessName();

        propsToChangeGeneric.forEach((k, v) -> setPropValue(k, v));
        if (packageName == null || processName == null || packageName.isEmpty()) {
            return;
        }

        String procName = packageName;

        if (procName.equals("com.android.vending")) {
            sIsFinsky = true;
        } else if (procName.equals("com.google.android.gms")) {
            sIsGms = true;
        } else if (procName.equals("com.google.android.setupwizard")) {
            sIsSetupWizard = true;
        }
        if (shouldTryToCertifyDevice()) {
            return;
        }
        if (isGoogleCameraPackage(packageName)) {
            return;
        }
        Map<String, Object> propsToChange = new HashMap<>();
        sProcessName = processName;
        sIsGms = packageName.equals(PACKAGE_GMS) && processName.equals(PROCESS_GMS_UNSTABLE);
        sIsFinsky = packageName.equals(PACKAGE_FINSKY);

        if (sIsGms) {
            if (shouldTryToCertifyDevice()) {
                dlog("Spoofing build for GMS");
                spoofBuildGms();
            }
        } else if (packageName.equals(PACKAGE_GMS)) {
            setPropValue("TIME", System.currentTimeMillis());
        } else if (packageName.startsWith("com.google.")
                || packageName.startsWith(SAMSUNG)
                || packagesToChangePixelFold.contains(processName)
                || Arrays.asList(packagesToChangeRecentPixel).contains(packageName)) {

            if (!sEnablePixelProps) {
                dlog("Pixel props is disabled by config");
                return;
            } else if (Arrays.asList(packagesToChangeRecentPixel).contains(packageName)) {
                propsToChange.putAll(propsToChangeRecentPixel);
            } else if (packagesToChangePixelFold.contains(processName)) {
                propsToChange = propsToChangePixelFold;
            } else {
                propsToChange.putAll(propsToChangePixel5);
            }
            if (processName.equals("com.google.android.apps.photos")) {
                    propsToChange = propsToChangePixelXL;
            }
        } else if ((SystemProperties.getBoolean(SPOOF_MUSIC_APPS, false)) &&
                (Arrays.asList(packagesToChangeMeizu).contains(packageName))) {
            propsToChange.putAll(propsToChangeMeizu);
        }
        if (propsToChange == null || propsToChange.isEmpty()) return;
        dlog("Defining props for: " + packageName);
        for (Map.Entry<String, Object> prop : propsToChange.entrySet()) {
            String key = prop.getKey();
            Object value = prop.getValue();
            if (propsToKeep.containsKey(packageName) && propsToKeep.get(packageName).contains(key)) {
                dlog("Not defining " + key + " prop for: " + packageName);
                continue;
            }
            dlog("Defining " + key + " prop for: " + packageName);
            setPropValue(key, value);
        }
        // Set proper indexing fingerprint
        if (packageName.equals(PACKAGE_SI)) {
            setPropValue("FINGERPRINT", String.valueOf(Build.TIME));
            return;
        }
        if (!sNetflixModel.isEmpty() && packageName.equals("com.netflix.mediaclient")) {
            if (DEBUG) Log.d(TAG, "Setting model to " + sNetflixModel + " for Netflix");
            setPropValue("MODEL", sNetflixModel);
            return;
        }
    }

    private static void setPropValue(String key, Object value) {
        try {
            dlog("Defining prop " + key + " to " + value.toString());
            Field field = Build.class.getDeclaredField(key);
            field.setAccessible(true);
            field.set(null, value);
            field.setAccessible(false);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            Log.e(TAG, "Failed to set prop " + key, e);
        }
    }

    private static void setVersionField(String key, Object value) {
        try {
            dlog("Defining version field " + key + " to " + value.toString());
            Field field = Build.VERSION.class.getDeclaredField(key);
            field.setAccessible(true);
            field.set(null, value);
            field.setAccessible(false);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            Log.e(TAG, "Failed to set version field " + key, e);
        }
    }

    private static void setVersionFieldString(String key, String value) {
        try {
            Field field = Build.VERSION.class.getDeclaredField(key);
            field.setAccessible(true);
            field.set(null, value);
            field.setAccessible(false);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            Log.e(TAG, "Failed to spoof Build." + key, e);
        }
    }

    private static boolean isGmsAddAccountActivityOnTop() {
        try {
            final ActivityTaskManager.RootTaskInfo focusedTask =
                    ActivityTaskManager.getService().getFocusedRootTaskInfo();
            return focusedTask != null && focusedTask.topActivity != null
                    && focusedTask.topActivity.equals(GMS_ADD_ACCOUNT_ACTIVITY);
        } catch (Exception e) {
            Log.e(TAG, "Unable to get top activity!", e);
        }
        return false;
    }

    private static boolean isCallerSafetyNet() {
        return sIsGms && Arrays.stream(Thread.currentThread().getStackTrace())
                            .anyMatch(elem -> elem.getClassName().toLowerCase()
                                .contains("droidguard"));
    }

    public static void onEngineGetCertificateChain() {
        // Check stack for SafetyNet or Play Integrity
        if ((isCallerSafetyNet() || sIsFinsky) && !sIsSetupWizard && shouldTryToCertifyDevice()) {
            dlog("Blocked key attestation sIsGms=" + sIsGms + " sIsFinsky=" + sIsFinsky);
            throw new UnsupportedOperationException();
        }
    }

    public static void dlog(String msg) {
        if (DEBUG) Log.d(TAG, "[" + sProcessName + "] " + msg);
    }
}
