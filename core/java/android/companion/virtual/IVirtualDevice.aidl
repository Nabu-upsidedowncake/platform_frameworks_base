/*
 * Copyright (C) 2021 The Android Open Source Project
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

package android.companion.virtual;

import android.app.PendingIntent;
import android.companion.virtual.audio.IAudioConfigChangedCallback;
import android.companion.virtual.audio.IAudioRoutingCallback;
import android.companion.virtual.sensor.IVirtualSensorStateChangeCallback;
import android.companion.virtual.sensor.VirtualSensorConfig;
import android.companion.virtual.sensor.VirtualSensorEvent;
import android.graphics.Point;
import android.graphics.PointF;
import android.hardware.input.VirtualKeyEvent;
import android.hardware.input.VirtualMouseButtonEvent;
import android.hardware.input.VirtualMouseRelativeEvent;
import android.hardware.input.VirtualMouseScrollEvent;
import android.hardware.input.VirtualTouchEvent;
import android.os.ResultReceiver;

/**
 * Interface for a virtual device.
 *
 * @hide
 */
interface IVirtualDevice {

    /**
     * Returns the association ID for this virtual device.
     *
     * @see AssociationInfo#getId()
     */
    int getAssociationId();

    /**
     * Returns the unique device ID for this virtual device.
     */
    int getDeviceId();

    /**
     * Closes the virtual device and frees all associated resources.
     */
    void close();

    /**
     * Notifies of an audio session being started.
     */
    void onAudioSessionStarting(
            int displayId,
            IAudioRoutingCallback routingCallback,
            IAudioConfigChangedCallback configChangedCallback);

    void onAudioSessionEnded();

    void createVirtualDpad(
            int displayId,
            String inputDeviceName,
            int vendorId,
            int productId,
            IBinder token);
    void createVirtualKeyboard(
            int displayId,
            String inputDeviceName,
            int vendorId,
            int productId,
            IBinder token);
    void createVirtualMouse(
            int displayId,
            String inputDeviceName,
            int vendorId,
            int productId,
            IBinder token);
    void createVirtualTouchscreen(
            int displayId,
            String inputDeviceName,
            int vendorId,
            int productId,
            IBinder token,
            in Point screenSize);
    void unregisterInputDevice(IBinder token);
    int getInputDeviceId(IBinder token);
    boolean sendDpadKeyEvent(IBinder token, in VirtualKeyEvent event);
    boolean sendKeyEvent(IBinder token, in VirtualKeyEvent event);
    boolean sendButtonEvent(IBinder token, in VirtualMouseButtonEvent event);
    boolean sendRelativeEvent(IBinder token, in VirtualMouseRelativeEvent event);
    boolean sendScrollEvent(IBinder token, in VirtualMouseScrollEvent event);
    boolean sendTouchEvent(IBinder token, in VirtualTouchEvent event);

    /**
     * Creates a virtual sensor, capable of injecting sensor events into the system.
     */
    @JavaPassthrough(annotation="@android.annotation.RequiresPermission(android.Manifest.permission.CREATE_VIRTUAL_DEVICE)")
    void createVirtualSensor(IBinder tokenm, in VirtualSensorConfig config);

    /**
     * Removes the sensor corresponding to the given token from the system.
     */
    @JavaPassthrough(annotation="@android.annotation.RequiresPermission(android.Manifest.permission.CREATE_VIRTUAL_DEVICE)")
    void unregisterSensor(IBinder token);

    /**
     * Sends an event to the virtual sensor corresponding to the given token.
     */
    @JavaPassthrough(annotation="@android.annotation.RequiresPermission(android.Manifest.permission.CREATE_VIRTUAL_DEVICE)")
    boolean sendSensorEvent(IBinder token, in VirtualSensorEvent event);

    /**
     * Launches a pending intent on the given display that is owned by this virtual device.
     */
    void launchPendingIntent(
            int displayId, in PendingIntent pendingIntent, in ResultReceiver resultReceiver);
    PointF getCursorPosition(IBinder token);

    /** Sets whether to show or hide the cursor while this virtual device is active. */
    void setShowPointerIcon(boolean showPointerIcon);
}
