/*
 * Copyright (C) 2019 The Android Open Source Project
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

package android.provider.settings.validators;

import static android.provider.settings.validators.SettingsValidators.ACCESSIBILITY_SHORTCUT_TARGET_LIST_VALIDATOR;
import static android.provider.settings.validators.SettingsValidators.ANY_INTEGER_VALIDATOR;
import static android.provider.settings.validators.SettingsValidators.ANY_STRING_VALIDATOR;
import static android.provider.settings.validators.SettingsValidators.BOOLEAN_VALIDATOR;
import static android.provider.settings.validators.SettingsValidators.COLON_SEPARATED_COMPONENT_LIST_VALIDATOR;
import static android.provider.settings.validators.SettingsValidators.COLON_SEPARATED_PACKAGE_LIST_VALIDATOR;
import static android.provider.settings.validators.SettingsValidators.COMMA_SEPARATED_COMPONENT_LIST_VALIDATOR;
import static android.provider.settings.validators.SettingsValidators.COMPONENT_NAME_VALIDATOR;
import static android.provider.settings.validators.SettingsValidators.JSON_OBJECT_VALIDATOR;
import static android.provider.settings.validators.SettingsValidators.LOCALE_VALIDATOR;
import static android.provider.settings.validators.SettingsValidators.NONE_NEGATIVE_LONG_VALIDATOR;
import static android.provider.settings.validators.SettingsValidators.NON_NEGATIVE_INTEGER_VALIDATOR;
import static android.provider.settings.validators.SettingsValidators.NULLABLE_COMPONENT_NAME_VALIDATOR;
import static android.provider.settings.validators.SettingsValidators.PACKAGE_NAME_VALIDATOR;
import static android.provider.settings.validators.SettingsValidators.PERCENTAGE_INTEGER_VALIDATOR;
import static android.provider.settings.validators.SettingsValidators.TILE_LIST_VALIDATOR;
import static android.provider.settings.validators.SettingsValidators.TTS_LIST_VALIDATOR;

import android.provider.Settings.Secure;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.ArraySet;

import java.util.Map;

/**
 * Validators for the Secure Settings.
 */
public class SecureSettingsValidators {
    public static final Map<String, Validator> VALIDATORS = new ArrayMap<>();

    static {
        VALIDATORS.put(Secure.BUGREPORT_IN_POWER_MENU, BOOLEAN_VALIDATOR);
        VALIDATORS.put(Secure.ALLOW_MOCK_LOCATION, BOOLEAN_VALIDATOR);
        VALIDATORS.put(Secure.USB_MASS_STORAGE_ENABLED, BOOLEAN_VALIDATOR);
        VALIDATORS.put(Secure.ACCESSIBILITY_DISPLAY_INVERSION_ENABLED, BOOLEAN_VALIDATOR);
        VALIDATORS.put(
                Secure.ACCESSIBILITY_DISPLAY_DALTONIZER,
                new DiscreteValueValidator(new String[] {"-1", "0", "11", "12", "13"}));
        VALIDATORS.put(Secure.ACCESSIBILITY_DISPLAY_DALTONIZER_ENABLED, BOOLEAN_VALIDATOR);
        VALIDATORS.put(Secure.ACCESSIBILITY_DISPLAY_MAGNIFICATION_ENABLED, BOOLEAN_VALIDATOR);
        VALIDATORS.put(
                Secure.ACCESSIBILITY_DISPLAY_MAGNIFICATION_NAVBAR_ENABLED, BOOLEAN_VALIDATOR);
        VALIDATORS.put(Secure.ADAPTIVE_CHARGING_ENABLED, BOOLEAN_VALIDATOR);
        VALIDATORS.put(Secure.ADAPTIVE_SLEEP, BOOLEAN_VALIDATOR);
        VALIDATORS.put(Secure.CAMERA_AUTOROTATE, BOOLEAN_VALIDATOR);
        VALIDATORS.put(Secure.AUTOFILL_SERVICE, NULLABLE_COMPONENT_NAME_VALIDATOR);
        VALIDATORS.put(
                Secure.ACCESSIBILITY_DISPLAY_MAGNIFICATION_SCALE,
                new InclusiveFloatRangeValidator(1.0f, Float.MAX_VALUE));
        VALIDATORS.put(
                Secure.ENABLED_ACCESSIBILITY_SERVICES, COLON_SEPARATED_COMPONENT_LIST_VALIDATOR);
        VALIDATORS.put(
                Secure.ENABLED_ACCESSIBILITY_AUDIO_DESCRIPTION_BY_DEFAULT, BOOLEAN_VALIDATOR);
        VALIDATORS.put(Secure.ENABLED_VR_LISTENERS, COLON_SEPARATED_COMPONENT_LIST_VALIDATOR);
        VALIDATORS.put(
                Secure.TOUCH_EXPLORATION_GRANTED_ACCESSIBILITY_SERVICES,
                COLON_SEPARATED_COMPONENT_LIST_VALIDATOR);
        VALIDATORS.put(Secure.TOUCH_EXPLORATION_ENABLED, BOOLEAN_VALIDATOR);
        VALIDATORS.put(Secure.WEAR_TALKBACK_ENABLED, BOOLEAN_VALIDATOR);
        VALIDATORS.put(Secure.ACCESSIBILITY_ENABLED, BOOLEAN_VALIDATOR);
        VALIDATORS.put(
                Secure.ACCESSIBILITY_SHORTCUT_TARGET_SERVICE,
                ACCESSIBILITY_SHORTCUT_TARGET_LIST_VALIDATOR);
        // technically either ComponentName or class name, but there's proper value
        // validation at callsites, so allow any non-null string
        VALIDATORS.put(Secure.ACCESSIBILITY_BUTTON_TARGET_COMPONENT, value -> value != null);
        VALIDATORS.put(Secure.ACCESSIBILITY_SHORTCUT_DIALOG_SHOWN, BOOLEAN_VALIDATOR);
        VALIDATORS.put(Secure.ACCESSIBILITY_SHORTCUT_ON_LOCK_SCREEN, BOOLEAN_VALIDATOR);
        VALIDATORS.put(Secure.ACCESSIBILITY_HIGH_TEXT_CONTRAST_ENABLED, BOOLEAN_VALIDATOR);
        VALIDATORS.put(Secure.CONTRAST_LEVEL, new InclusiveFloatRangeValidator(-1f, 1f));
        VALIDATORS.put(
                Secure.ACCESSIBILITY_CAPTIONING_PRESET,
                new DiscreteValueValidator(new String[] {"-1", "0", "1", "2", "3", "4"}));
        VALIDATORS.put(Secure.ACCESSIBILITY_CAPTIONING_ENABLED, BOOLEAN_VALIDATOR);
        VALIDATORS.put(Secure.ACCESSIBILITY_CAPTIONING_LOCALE, LOCALE_VALIDATOR);
        VALIDATORS.put(Secure.ACCESSIBILITY_CAPTIONING_BACKGROUND_COLOR, ANY_INTEGER_VALIDATOR);
        VALIDATORS.put(Secure.ACCESSIBILITY_CAPTIONING_FOREGROUND_COLOR, ANY_INTEGER_VALIDATOR);
        VALIDATORS.put(
                Secure.ACCESSIBILITY_CAPTIONING_EDGE_TYPE,
                new DiscreteValueValidator(new String[] {"0", "1", "2"}));
        VALIDATORS.put(Secure.ACCESSIBILITY_CAPTIONING_EDGE_COLOR, ANY_INTEGER_VALIDATOR);
        VALIDATORS.put(
                Secure.ACCESSIBILITY_CAPTIONING_TYPEFACE,
                new DiscreteValueValidator(
                        new String[] {"DEFAULT", "MONOSPACE", "SANS_SERIF", "SERIF"}));
        VALIDATORS.put(
                Secure.ACCESSIBILITY_CAPTIONING_FONT_SCALE,
                new InclusiveFloatRangeValidator(0.5f, 2.0f));
        VALIDATORS.put(Secure.ACCESSIBILITY_CAPTIONING_WINDOW_COLOR, ANY_INTEGER_VALIDATOR);
        VALIDATORS.put(Secure.FONT_WEIGHT_ADJUSTMENT, ANY_INTEGER_VALIDATOR);
        VALIDATORS.put(Secure.REDUCE_BRIGHT_COLORS_LEVEL, PERCENTAGE_INTEGER_VALIDATOR);
        VALIDATORS.put(Secure.REDUCE_BRIGHT_COLORS_PERSIST_ACROSS_REBOOTS, BOOLEAN_VALIDATOR);
        VALIDATORS.put(Secure.TTS_DEFAULT_RATE, NON_NEGATIVE_INTEGER_VALIDATOR);
        VALIDATORS.put(Secure.TTS_DEFAULT_PITCH, NON_NEGATIVE_INTEGER_VALIDATOR);
        VALIDATORS.put(Secure.TTS_DEFAULT_SYNTH, PACKAGE_NAME_VALIDATOR);
        VALIDATORS.put(Secure.TTS_ENABLED_PLUGINS, new PackageNameListValidator(" "));
        VALIDATORS.put(Secure.TTS_DEFAULT_LOCALE, TTS_LIST_VALIDATOR);
        VALIDATORS.put(Secure.SHOW_IME_WITH_HARD_KEYBOARD, BOOLEAN_VALIDATOR);
        VALIDATORS.put(Secure.WIFI_NETWORKS_AVAILABLE_NOTIFICATION_ON, BOOLEAN_VALIDATOR);
        VALIDATORS.put(Secure.WIFI_NETWORKS_AVAILABLE_REPEAT_DELAY, NON_NEGATIVE_INTEGER_VALIDATOR);
        VALIDATORS.put(Secure.WIFI_NUM_OPEN_NETWORKS_KEPT, NON_NEGATIVE_INTEGER_VALIDATOR);
        VALIDATORS.put(Secure.MOUNT_PLAY_NOTIFICATION_SND, BOOLEAN_VALIDATOR);
        VALIDATORS.put(Secure.MOUNT_UMS_AUTOSTART, BOOLEAN_VALIDATOR);
        VALIDATORS.put(Secure.MOUNT_UMS_PROMPT, BOOLEAN_VALIDATOR);
        VALIDATORS.put(Secure.MOUNT_UMS_NOTIFY_ENABLED, BOOLEAN_VALIDATOR);
        VALIDATORS.put(Secure.DOUBLE_TAP_TO_WAKE, BOOLEAN_VALIDATOR);
        VALIDATORS.put(Secure.WAKE_GESTURE_ENABLED, BOOLEAN_VALIDATOR);
        VALIDATORS.put(Secure.LONG_PRESS_TIMEOUT, NON_NEGATIVE_INTEGER_VALIDATOR);
        VALIDATORS.put(Secure.CAMERA_GESTURE_DISABLED, BOOLEAN_VALIDATOR);
        VALIDATORS.put(Secure.ACCESSIBILITY_AUTOCLICK_ENABLED, BOOLEAN_VALIDATOR);
        VALIDATORS.put(Secure.ACCESSIBILITY_AUTOCLICK_DELAY, NON_NEGATIVE_INTEGER_VALIDATOR);
        VALIDATORS.put(Secure.ACCESSIBILITY_LARGE_POINTER_ICON, BOOLEAN_VALIDATOR);
        VALIDATORS.put(
                Secure.PREFERRED_TTY_MODE,
                new DiscreteValueValidator(new String[] {"0", "1", "2", "3"}));
        VALIDATORS.put(Secure.ENHANCED_VOICE_PRIVACY_ENABLED, BOOLEAN_VALIDATOR);
        VALIDATORS.put(Secure.TTY_MODE_ENABLED, BOOLEAN_VALIDATOR);
        VALIDATORS.put(Secure.RTT_CALLING_MODE, BOOLEAN_VALIDATOR);
        VALIDATORS.put(
                Secure.INCALL_POWER_BUTTON_BEHAVIOR,
                new DiscreteValueValidator(new String[] {"1", "2"}));
        VALIDATORS.put(Secure.MINIMAL_POST_PROCESSING_ALLOWED, BOOLEAN_VALIDATOR);
        VALIDATORS.put(
                Secure.MATCH_CONTENT_FRAME_RATE,
                new DiscreteValueValidator(new String[] {"0", "1", "2"}));
        VALIDATORS.put(Secure.NIGHT_DISPLAY_CUSTOM_START_TIME, NON_NEGATIVE_INTEGER_VALIDATOR);
        VALIDATORS.put(Secure.NIGHT_DISPLAY_CUSTOM_END_TIME, NON_NEGATIVE_INTEGER_VALIDATOR);
        VALIDATORS.put(Secure.NIGHT_DISPLAY_COLOR_TEMPERATURE, NON_NEGATIVE_INTEGER_VALIDATOR);
        VALIDATORS.put(Secure.NIGHT_DISPLAY_AUTO_MODE, new InclusiveIntegerRangeValidator(0, 2));
        VALIDATORS.put(Secure.DISPLAY_WHITE_BALANCE_ENABLED, BOOLEAN_VALIDATOR);
        VALIDATORS.put(Secure.SYNC_PARENT_SOUNDS, BOOLEAN_VALIDATOR);
        VALIDATORS.put(Secure.CAMERA_DOUBLE_TWIST_TO_FLIP_ENABLED, BOOLEAN_VALIDATOR);
        VALIDATORS.put(Secure.CAMERA_DOUBLE_TAP_POWER_GESTURE_DISABLED, BOOLEAN_VALIDATOR);
        VALIDATORS.put(Secure.SYSTEM_NAVIGATION_KEYS_ENABLED, BOOLEAN_VALIDATOR);
        VALIDATORS.put(Secure.QS_TILES, TILE_LIST_VALIDATOR);
        VALIDATORS.put(Secure.CONTROLS_ENABLED, BOOLEAN_VALIDATOR);
        VALIDATORS.put(Secure.POWER_MENU_LOCKED_SHOW_CONTENT, BOOLEAN_VALIDATOR);
        VALIDATORS.put(Secure.LOCKSCREEN_SHOW_CONTROLS, BOOLEAN_VALIDATOR);
        VALIDATORS.put(Secure.LOCKSCREEN_ALLOW_TRIVIAL_CONTROLS, BOOLEAN_VALIDATOR);
        VALIDATORS.put(Secure.LOCKSCREEN_SHOW_WALLET, BOOLEAN_VALIDATOR);
        VALIDATORS.put(Secure.LOCK_SCREEN_SHOW_QR_CODE_SCANNER, BOOLEAN_VALIDATOR);
        VALIDATORS.put(Secure.LOCKSCREEN_USE_DOUBLE_LINE_CLOCK, BOOLEAN_VALIDATOR);
        VALIDATORS.put(Secure.STATUS_BAR_SHOW_VIBRATE_ICON, BOOLEAN_VALIDATOR);
        VALIDATORS.put(Secure.DOZE_ENABLED, BOOLEAN_VALIDATOR);
        VALIDATORS.put(Secure.DOZE_ALWAYS_ON, BOOLEAN_VALIDATOR);
        VALIDATORS.put(Secure.DOZE_PICK_UP_GESTURE, BOOLEAN_VALIDATOR);
        VALIDATORS.put(Secure.DOZE_DOUBLE_TAP_GESTURE, BOOLEAN_VALIDATOR);
        VALIDATORS.put(Secure.DOZE_TAP_SCREEN_GESTURE, BOOLEAN_VALIDATOR);
        VALIDATORS.put(Secure.DOZE_WAKE_LOCK_SCREEN_GESTURE, BOOLEAN_VALIDATOR);
        VALIDATORS.put(Secure.DOZE_WAKE_DISPLAY_GESTURE, BOOLEAN_VALIDATOR);
        VALIDATORS.put(Secure.DOZE_QUICK_PICKUP_GESTURE, BOOLEAN_VALIDATOR);
        VALIDATORS.put(Secure.NFC_PAYMENT_DEFAULT_COMPONENT, COMPONENT_NAME_VALIDATOR);
        VALIDATORS.put(
                Secure.AUTOMATIC_STORAGE_MANAGER_DAYS_TO_RETAIN, NON_NEGATIVE_INTEGER_VALIDATOR);
        VALIDATORS.put(Secure.FACE_UNLOCK_KEYGUARD_ENABLED, BOOLEAN_VALIDATOR);
        VALIDATORS.put(Secure.FACE_UNLOCK_DISMISSES_KEYGUARD, BOOLEAN_VALIDATOR);
        VALIDATORS.put(Secure.FINGERPRINT_SIDE_FPS_KG_POWER_WINDOW, NON_NEGATIVE_INTEGER_VALIDATOR);
        VALIDATORS.put(Secure.FINGERPRINT_SIDE_FPS_BP_POWER_WINDOW, NON_NEGATIVE_INTEGER_VALIDATOR);
        VALIDATORS.put(Secure.FINGERPRINT_SIDE_FPS_ENROLL_TAP_WINDOW,
                NON_NEGATIVE_INTEGER_VALIDATOR);
        VALIDATORS.put(Secure.FINGERPRINT_SIDE_FPS_AUTH_DOWNTIME, NON_NEGATIVE_INTEGER_VALIDATOR);
        VALIDATORS.put(Secure.SFPS_PERFORMANT_AUTH_ENABLED, BOOLEAN_VALIDATOR);
        VALIDATORS.put(Secure.SHOW_MEDIA_WHEN_BYPASSING, BOOLEAN_VALIDATOR);
        VALIDATORS.put(Secure.FACE_UNLOCK_APP_ENABLED, BOOLEAN_VALIDATOR);
        VALIDATORS.put(Secure.FACE_UNLOCK_ALWAYS_REQUIRE_CONFIRMATION, BOOLEAN_VALIDATOR);
        VALIDATORS.put(Secure.ACTIVE_UNLOCK_ON_WAKE, BOOLEAN_VALIDATOR);
        VALIDATORS.put(Secure.ACTIVE_UNLOCK_ON_UNLOCK_INTENT, BOOLEAN_VALIDATOR);
        VALIDATORS.put(Secure.ACTIVE_UNLOCK_ON_BIOMETRIC_FAIL, BOOLEAN_VALIDATOR);
        VALIDATORS.put(Secure.ACTIVE_UNLOCK_ON_FACE_ERRORS, ANY_STRING_VALIDATOR);
        VALIDATORS.put(Secure.ACTIVE_UNLOCK_ON_FACE_ACQUIRE_INFO, ANY_STRING_VALIDATOR);
        VALIDATORS.put(Secure.ACTIVE_UNLOCK_ON_UNLOCK_INTENT_WHEN_BIOMETRIC_ENROLLED,
                ANY_STRING_VALIDATOR);
        VALIDATORS.put(Secure.ACTIVE_UNLOCK_WAKEUPS_CONSIDERED_UNLOCK_INTENTS,
                ANY_STRING_VALIDATOR);
        VALIDATORS.put(Secure.ACTIVE_UNLOCK_WAKEUPS_TO_FORCE_DISMISS_KEYGUARD,
                ANY_STRING_VALIDATOR);
        VALIDATORS.put(Secure.ASSIST_GESTURE_ENABLED, BOOLEAN_VALIDATOR);
        VALIDATORS.put(Secure.ASSIST_GESTURE_SILENCE_ALERTS_ENABLED, BOOLEAN_VALIDATOR);
        VALIDATORS.put(Secure.ASSIST_GESTURE_WAKE_ENABLED, BOOLEAN_VALIDATOR);
        VALIDATORS.put(Secure.ASSIST_TOUCH_GESTURE_ENABLED, BOOLEAN_VALIDATOR);
        VALIDATORS.put(Secure.ASSIST_LONG_PRESS_HOME_ENABLED, BOOLEAN_VALIDATOR);
        VALIDATORS.put(Secure.SEARCH_PRESS_HOLD_NAV_HANDLE_ENABLED, BOOLEAN_VALIDATOR);
        VALIDATORS.put(Secure.SEARCH_LONG_PRESS_HOME_ENABLED, BOOLEAN_VALIDATOR);
        VALIDATORS.put(Secure.VR_DISPLAY_MODE, new DiscreteValueValidator(new String[] {"0", "1"}));
        VALIDATORS.put(Secure.NOTIFICATION_BADGING, BOOLEAN_VALIDATOR);
        VALIDATORS.put(Secure.NOTIFICATION_DISMISS_RTL, BOOLEAN_VALIDATOR);
        VALIDATORS.put(Secure.QS_AUTO_ADDED_TILES, TILE_LIST_VALIDATOR);
        VALIDATORS.put(Secure.SCREENSAVER_ENABLED, BOOLEAN_VALIDATOR);
        VALIDATORS.put(Secure.SCREENSAVER_COMPONENTS, COMMA_SEPARATED_COMPONENT_LIST_VALIDATOR);
        VALIDATORS.put(Secure.SCREENSAVER_ACTIVATE_ON_DOCK, BOOLEAN_VALIDATOR);
        VALIDATORS.put(Secure.SCREENSAVER_ACTIVATE_ON_SLEEP, BOOLEAN_VALIDATOR);
        VALIDATORS.put(Secure.SCREENSAVER_HOME_CONTROLS_ENABLED, BOOLEAN_VALIDATOR);
        VALIDATORS.put(Secure.SHOW_FIRST_CRASH_DIALOG_DEV_OPTION, BOOLEAN_VALIDATOR);
        VALIDATORS.put(Secure.VOLUME_HUSH_GESTURE, NON_NEGATIVE_INTEGER_VALIDATOR);
        VALIDATORS.put(
                Secure.ENABLED_NOTIFICATION_LISTENERS,
                COLON_SEPARATED_COMPONENT_LIST_VALIDATOR); // legacy restore setting
        VALIDATORS.put(
                Secure.ENABLED_NOTIFICATION_ASSISTANT,
                COLON_SEPARATED_COMPONENT_LIST_VALIDATOR); // legacy restore setting
        VALIDATORS.put(
                Secure.ENABLED_NOTIFICATION_POLICY_ACCESS_PACKAGES,
                COLON_SEPARATED_PACKAGE_LIST_VALIDATOR); // legacy restore setting
        VALIDATORS.put(Secure.HUSH_GESTURE_USED, BOOLEAN_VALIDATOR);
        VALIDATORS.put(Secure.MANUAL_RINGER_TOGGLE_COUNT, NON_NEGATIVE_INTEGER_VALIDATOR);
        VALIDATORS.put(Secure.LOW_POWER_WARNING_ACKNOWLEDGED, BOOLEAN_VALIDATOR);
        VALIDATORS.put(Secure.EXTRA_LOW_POWER_WARNING_ACKNOWLEDGED, BOOLEAN_VALIDATOR);
        VALIDATORS.put(Secure.IN_CALL_NOTIFICATION_ENABLED, BOOLEAN_VALIDATOR);
        VALIDATORS.put(Secure.LOCK_SCREEN_ALLOW_PRIVATE_NOTIFICATIONS, BOOLEAN_VALIDATOR);
        VALIDATORS.put(Secure.LOCK_SCREEN_SHOW_NOTIFICATIONS, BOOLEAN_VALIDATOR);
        VALIDATORS.put(Secure.LOCK_SCREEN_SHOW_SILENT_NOTIFICATIONS, BOOLEAN_VALIDATOR);
        VALIDATORS.put(Secure.LOCK_SCREEN_SHOW_ONLY_UNSEEN_NOTIFICATIONS, BOOLEAN_VALIDATOR);
        VALIDATORS.put(Secure.SHOW_NOTIFICATION_SNOOZE, BOOLEAN_VALIDATOR);
        VALIDATORS.put(Secure.NOTIFICATION_HISTORY_ENABLED, BOOLEAN_VALIDATOR);
        VALIDATORS.put(Secure.ZEN_DURATION, ANY_INTEGER_VALIDATOR);
        VALIDATORS.put(Secure.SHOW_ZEN_UPGRADE_NOTIFICATION, BOOLEAN_VALIDATOR);
        VALIDATORS.put(Secure.SHOW_ZEN_SETTINGS_SUGGESTION, BOOLEAN_VALIDATOR);
        VALIDATORS.put(Secure.ZEN_SETTINGS_UPDATED, BOOLEAN_VALIDATOR);
        VALIDATORS.put(Secure.ZEN_SETTINGS_SUGGESTION_VIEWED, BOOLEAN_VALIDATOR);
        VALIDATORS.put(Secure.CHARGING_SOUNDS_ENABLED, BOOLEAN_VALIDATOR);
        VALIDATORS.put(Secure.CHARGING_VIBRATION_ENABLED, BOOLEAN_VALIDATOR);
        VALIDATORS.put(
                Secure.ACCESSIBILITY_NON_INTERACTIVE_UI_TIMEOUT_MS, NON_NEGATIVE_INTEGER_VALIDATOR);
        VALIDATORS.put(
                Secure.ACCESSIBILITY_INTERACTIVE_UI_TIMEOUT_MS, NON_NEGATIVE_INTEGER_VALIDATOR);
        VALIDATORS.put(Secure.USER_SETUP_COMPLETE, BOOLEAN_VALIDATOR);
        VALIDATORS.put(Secure.ASSIST_GESTURE_SETUP_COMPLETE, BOOLEAN_VALIDATOR);
        VALIDATORS.put(Secure.TRUST_AGENTS_EXTEND_UNLOCK, BOOLEAN_VALIDATOR);
        VALIDATORS.put(Secure.LOCK_SCREEN_CUSTOM_CLOCK_FACE, JSON_OBJECT_VALIDATOR);
        VALIDATORS.put(Secure.LOCK_SCREEN_WHEN_TRUST_LOST, BOOLEAN_VALIDATOR);
        VALIDATORS.put(Secure.SKIP_GESTURE, BOOLEAN_VALIDATOR);
        /*
         * Only used if FeatureFlag "settings_skip_direction_mutable" is enabled.
         * If feature flag is disabled, should assume SKIP_DIRECTION = 0.
         *      0 / false = right to left to advance to next
         *      1 / true = left to right to advance to next
         */
        VALIDATORS.put(Secure.SKIP_DIRECTION, BOOLEAN_VALIDATOR);
        VALIDATORS.put(Secure.SILENCE_GESTURE, BOOLEAN_VALIDATOR);
        VALIDATORS.put(Secure.THEME_CUSTOMIZATION_OVERLAY_PACKAGES, JSON_OBJECT_VALIDATOR);
        VALIDATORS.put(Secure.NAV_BAR_FORCE_VISIBLE, BOOLEAN_VALIDATOR);
        VALIDATORS.put(Secure.NAV_BAR_KIDS_MODE, BOOLEAN_VALIDATOR);
        VALIDATORS.put(
                Secure.NAVIGATION_MODE, new DiscreteValueValidator(new String[] {"0", "1", "2"}));
        VALIDATORS.put(Secure.NAVIGATION_MODE_RESTORE,
                new DiscreteValueValidator(new String[] {"-1", "0", "1", "2"}));
        VALIDATORS.put(Secure.BACK_GESTURE_INSET_SCALE_LEFT,
                new InclusiveFloatRangeValidator(0.0f, Float.MAX_VALUE));
        VALIDATORS.put(Secure.BACK_GESTURE_INSET_SCALE_RIGHT,
                new InclusiveFloatRangeValidator(0.0f, Float.MAX_VALUE));
        VALIDATORS.put(Secure.TRACKPAD_GESTURE_BACK_ENABLED, BOOLEAN_VALIDATOR);
        VALIDATORS.put(Secure.TRACKPAD_GESTURE_HOME_ENABLED, BOOLEAN_VALIDATOR);
        VALIDATORS.put(Secure.TRACKPAD_GESTURE_OVERVIEW_ENABLED, BOOLEAN_VALIDATOR);
        VALIDATORS.put(Secure.TRACKPAD_GESTURE_NOTIFICATION_ENABLED, BOOLEAN_VALIDATOR);
        VALIDATORS.put(Secure.TRACKPAD_GESTURE_QUICK_SWITCH_ENABLED, BOOLEAN_VALIDATOR);
        VALIDATORS.put(Secure.AWARE_ENABLED, BOOLEAN_VALIDATOR);
        VALIDATORS.put(Secure.SKIP_GESTURE_COUNT, NON_NEGATIVE_INTEGER_VALIDATOR);
        VALIDATORS.put(Secure.SKIP_TOUCH_COUNT, NON_NEGATIVE_INTEGER_VALIDATOR);
        VALIDATORS.put(Secure.SILENCE_ALARMS_GESTURE_COUNT, NON_NEGATIVE_INTEGER_VALIDATOR);
        VALIDATORS.put(Secure.SILENCE_TIMER_GESTURE_COUNT, NON_NEGATIVE_INTEGER_VALIDATOR);
        VALIDATORS.put(Secure.SILENCE_CALL_GESTURE_COUNT, NON_NEGATIVE_INTEGER_VALIDATOR);
        VALIDATORS.put(Secure.SILENCE_ALARMS_TOUCH_COUNT, NON_NEGATIVE_INTEGER_VALIDATOR);
        VALIDATORS.put(Secure.SILENCE_TIMER_TOUCH_COUNT, NON_NEGATIVE_INTEGER_VALIDATOR);
        VALIDATORS.put(Secure.SILENCE_CALL_TOUCH_COUNT, NON_NEGATIVE_INTEGER_VALIDATOR);
        VALIDATORS.put(Secure.AWARE_TAP_PAUSE_GESTURE_COUNT, NON_NEGATIVE_INTEGER_VALIDATOR);
        VALIDATORS.put(Secure.AWARE_TAP_PAUSE_TOUCH_COUNT, NON_NEGATIVE_INTEGER_VALIDATOR);
        VALIDATORS.put(Secure.ODI_CAPTIONS_ENABLED, BOOLEAN_VALIDATOR);
        VALIDATORS.put(Secure.DARK_MODE_DIALOG_SEEN, BOOLEAN_VALIDATOR);
        VALIDATORS.put(Secure.UI_NIGHT_MODE, NON_NEGATIVE_INTEGER_VALIDATOR);
        VALIDATORS.put(Secure.UI_NIGHT_MODE_CUSTOM_TYPE, NON_NEGATIVE_INTEGER_VALIDATOR);
        VALIDATORS.put(Secure.DARK_THEME_CUSTOM_START_TIME, NONE_NEGATIVE_LONG_VALIDATOR);
        VALIDATORS.put(Secure.DARK_THEME_CUSTOM_END_TIME, NONE_NEGATIVE_LONG_VALIDATOR);
        VALIDATORS.put(Secure.GLOBAL_ACTIONS_PANEL_ENABLED, BOOLEAN_VALIDATOR);
        VALIDATORS.put(Secure.AWARE_LOCK_ENABLED, BOOLEAN_VALIDATOR);
        VALIDATORS.put(Secure.DISPLAY_DENSITY_FORCED, NON_NEGATIVE_INTEGER_VALIDATOR);
        VALIDATORS.put(Secure.TAP_GESTURE, BOOLEAN_VALIDATOR);
        VALIDATORS.put(Secure.PEOPLE_STRIP, BOOLEAN_VALIDATOR);
        VALIDATORS.put(Secure.MEDIA_CONTROLS_RESUME, BOOLEAN_VALIDATOR);
        VALIDATORS.put(Secure.MEDIA_CONTROLS_RECOMMENDATION, BOOLEAN_VALIDATOR);
        VALIDATORS.put(Secure.MEDIA_CONTROLS_LOCK_SCREEN, BOOLEAN_VALIDATOR);
        VALIDATORS.put(Secure.ACCESSIBILITY_MAGNIFICATION_MODE,
                new InclusiveIntegerRangeValidator(
                        Secure.ACCESSIBILITY_MAGNIFICATION_MODE_FULLSCREEN,
                        Secure.ACCESSIBILITY_MAGNIFICATION_MODE_WINDOW));
        VALIDATORS.put(Secure.ACCESSIBILITY_MAGNIFICATION_CAPABILITY,
                new InclusiveIntegerRangeValidator(
                        Secure.ACCESSIBILITY_MAGNIFICATION_MODE_FULLSCREEN,
                        Secure.ACCESSIBILITY_MAGNIFICATION_MODE_ALL));
        VALIDATORS.put(Secure.ACCESSIBILITY_MAGNIFICATION_FOLLOW_TYPING_ENABLED, BOOLEAN_VALIDATOR);
        VALIDATORS.put(Secure.ACCESSIBILITY_MAGNIFICATION_ALWAYS_ON_ENABLED, BOOLEAN_VALIDATOR);
        VALIDATORS.put(Secure.ACCESSIBILITY_MAGNIFICATION_JOYSTICK_ENABLED, BOOLEAN_VALIDATOR);
        VALIDATORS.put(
                Secure.ACCESSIBILITY_BUTTON_TARGETS,
                ACCESSIBILITY_SHORTCUT_TARGET_LIST_VALIDATOR);
        VALIDATORS.put(Secure.ONE_HANDED_MODE_ACTIVATED, BOOLEAN_VALIDATOR);
        VALIDATORS.put(Secure.ONE_HANDED_MODE_ENABLED, BOOLEAN_VALIDATOR);
        VALIDATORS.put(Secure.ONE_HANDED_MODE_TIMEOUT, ANY_INTEGER_VALIDATOR);
        VALIDATORS.put(Secure.TAPS_APP_TO_EXIT, BOOLEAN_VALIDATOR);
        VALIDATORS.put(Secure.SWIPE_BOTTOM_TO_NOTIFICATION_ENABLED, BOOLEAN_VALIDATOR);
        VALIDATORS.put(Secure.EMERGENCY_GESTURE_ENABLED, BOOLEAN_VALIDATOR);
        VALIDATORS.put(Secure.EMERGENCY_GESTURE_SOUND_ENABLED, BOOLEAN_VALIDATOR);
        VALIDATORS.put(Secure.EMERGENCY_GESTURE_UI_SHOWING, BOOLEAN_VALIDATOR);
        VALIDATORS.put(
                Secure.EMERGENCY_GESTURE_UI_LAST_STARTED_MILLIS, NONE_NEGATIVE_LONG_VALIDATOR);
        VALIDATORS.put(Secure.ADAPTIVE_CONNECTIVITY_ENABLED, BOOLEAN_VALIDATOR);
        VALIDATORS.put(
                Secure.ASSIST_HANDLES_LEARNING_TIME_ELAPSED_MILLIS, NONE_NEGATIVE_LONG_VALIDATOR);
        VALIDATORS.put(Secure.ASSIST_HANDLES_LEARNING_EVENT_COUNT, NON_NEGATIVE_INTEGER_VALIDATOR);
        VALIDATORS.put(Secure.ACCESSIBILITY_BUTTON_MODE,
                new InclusiveIntegerRangeValidator(
                        Secure.ACCESSIBILITY_BUTTON_MODE_NAVIGATION_BAR,
                        Secure.ACCESSIBILITY_BUTTON_MODE_GESTURE));
        VALIDATORS.put(Secure.ACCESSIBILITY_FLOATING_MENU_SIZE,
                new DiscreteValueValidator(new String[] {"0", "1"}));
        VALIDATORS.put(Secure.ACCESSIBILITY_FLOATING_MENU_ICON_TYPE,
                new DiscreteValueValidator(new String[] {"0", "1"}));
        VALIDATORS.put(Secure.ACCESSIBILITY_FLOATING_MENU_OPACITY,
                new InclusiveFloatRangeValidator(0.0f, 1.0f));
        VALIDATORS.put(Secure.ACCESSIBILITY_FLOATING_MENU_FADE_ENABLED, BOOLEAN_VALIDATOR);
        VALIDATORS.put(Secure.CLIPBOARD_SHOW_ACCESS_NOTIFICATIONS, BOOLEAN_VALIDATOR);
        VALIDATORS.put(Secure.NOTIFICATION_BUBBLES, BOOLEAN_VALIDATOR);
        VALIDATORS.put(Secure.LOCATION_TIME_ZONE_DETECTION_ENABLED, BOOLEAN_VALIDATOR);
        VALIDATORS.put(Secure.LOCATION_SHOW_SYSTEM_OPS, BOOLEAN_VALIDATOR);
        VALIDATORS.put(Secure.DEVICE_STATE_ROTATION_LOCK, value -> {
            if (TextUtils.isEmpty(value)) {
                return true;
            }
            String[] intValues = value.split(":");
            if (intValues.length % 2 != 0) {
                return false;
            }
            InclusiveIntegerRangeValidator enumValidator =
                    new InclusiveIntegerRangeValidator(
                            Secure.DEVICE_STATE_ROTATION_LOCK_IGNORED,
                            Secure.DEVICE_STATE_ROTATION_LOCK_UNLOCKED);
            ArraySet<String> keys = new ArraySet<>();
            for (int i = 0; i < intValues.length - 1; ) {
                String entryKey = intValues[i++];
                String entryValue = intValues[i++];
                if (!NON_NEGATIVE_INTEGER_VALIDATOR.validate(entryKey)
                        || !enumValidator.validate(entryValue)) {
                    return false;
                }
                // If the same device state key was specified more than once, this is invalid
                if (!keys.add(entryKey)) {
                    return false;
                }
            }
            return true;
        });
        VALIDATORS.put(Secure.ODI_CAPTIONS_VOLUME_UI_ENABLED, BOOLEAN_VALIDATOR);
        VALIDATORS.put(Secure.WEAR_TALKBACK_ENABLED, BOOLEAN_VALIDATOR);
        VALIDATORS.put(Secure.HBM_SETTING_KEY, BOOLEAN_VALIDATOR);
        VALIDATORS.put(Secure.BLUETOOTH_LE_BROADCAST_PROGRAM_INFO, ANY_STRING_VALIDATOR);
        VALIDATORS.put(Secure.BLUETOOTH_LE_BROADCAST_CODE, ANY_STRING_VALIDATOR);
        VALIDATORS.put(Secure.BLUETOOTH_LE_BROADCAST_APP_SOURCE_NAME, ANY_STRING_VALIDATOR);
        VALIDATORS.put(Secure.CUSTOM_BUGREPORT_HANDLER_APP, ANY_STRING_VALIDATOR);
        VALIDATORS.put(Secure.CUSTOM_BUGREPORT_HANDLER_USER, ANY_INTEGER_VALIDATOR);
        VALIDATORS.put(Secure.LOCK_SCREEN_WEATHER_ENABLED, BOOLEAN_VALIDATOR);
        VALIDATORS.put(Secure.HEARING_AID_RINGTONE_ROUTING,
                new DiscreteValueValidator(new String[] {"0", "1", "2"}));
        VALIDATORS.put(Secure.HEARING_AID_CALL_ROUTING,
                new DiscreteValueValidator(new String[] {"0", "1", "2"}));
        VALIDATORS.put(Secure.HEARING_AID_MEDIA_ROUTING,
                new DiscreteValueValidator(new String[] {"0", "1", "2"}));
        VALIDATORS.put(Secure.HEARING_AID_SYSTEM_SOUNDS_ROUTING,
                new DiscreteValueValidator(new String[] {"0", "1", "2"}));
        VALIDATORS.put(Secure.ACCESSIBILITY_FONT_SCALING_HAS_BEEN_CHANGED, BOOLEAN_VALIDATOR);
        VALIDATORS.put(Secure.BUTTON_BACKLIGHT_TIMEOUT, NON_NEGATIVE_INTEGER_VALIDATOR);
        VALIDATORS.put(Secure.BUTTON_BRIGHTNESS, NON_NEGATIVE_INTEGER_VALIDATOR);
    }
}
