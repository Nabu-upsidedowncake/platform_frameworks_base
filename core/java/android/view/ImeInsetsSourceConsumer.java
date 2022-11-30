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
 * limitations under the License
 */

package android.view;

import static android.view.ImeInsetsSourceConsumerProto.INSETS_SOURCE_CONSUMER;
import static android.view.ImeInsetsSourceConsumerProto.IS_HIDE_ANIMATION_RUNNING;
import static android.view.ImeInsetsSourceConsumerProto.IS_REQUESTED_VISIBLE_AWAITING_CONTROL;
import static android.view.ImeInsetsSourceConsumerProto.IS_SHOW_REQUESTED_DURING_HIDE_ANIMATION;
import static android.view.InsetsState.ITYPE_IME;

import android.annotation.Nullable;
import android.os.IBinder;
import android.util.proto.ProtoOutputStream;
import android.view.SurfaceControl.Transaction;
import android.view.inputmethod.InputMethodManager;

import com.android.internal.inputmethod.ImeTracing;

import java.util.function.Supplier;

/**
 * Controls the visibility and animations of IME window insets source.
 * @hide
 */
public final class ImeInsetsSourceConsumer extends InsetsSourceConsumer {

    /**
     * Tracks whether we have an outstanding request from the IME to show, but weren't able to
     * execute it because we didn't have control yet.
     */
    private boolean mIsRequestedVisibleAwaitingControl;

    private boolean mIsHideAnimationRunning;

    /**
     * Tracks whether {@link WindowInsetsController#show(int)} or
     * {@link InputMethodManager#showSoftInput(View, int)} is called during IME hide animation.
     * If it was called, we should not call {@link InputMethodManager#notifyImeHidden(IBinder)},
     * because the IME is being shown.
     */
    private boolean mIsShowRequestedDuringHideAnimation;

    public ImeInsetsSourceConsumer(
            InsetsState state, Supplier<Transaction> transactionSupplier,
            InsetsController controller) {
        super(ITYPE_IME, state, transactionSupplier, controller);
    }

    @Override
    public boolean onAnimationStateChanged(boolean running) {
        if (!running) {
            ImeTracing.getInstance().triggerClientDump(
                    "ImeInsetsSourceConsumer#onAnimationFinished",
                    mController.getHost().getInputMethodManager(), null /* icProto */);
        }
        final boolean insetsChanged = super.onAnimationStateChanged(running);
        final boolean showRequested = (mController.getRequestedVisibleTypes() & getType()) != 0;
        if (showRequested) {
            onShowRequested();
        } else {
            mIsRequestedVisibleAwaitingControl = false;
            if (!running) {
                // Remove IME surface as IME has finished hide animation, if there is no pending
                // show request.
                if (!mIsShowRequestedDuringHideAnimation) {
                    notifyHidden();
                    removeSurface();
                }
            }
            // Here is reached
            // (1) before the hide animation starts.
            // (2) after the hide animation ends.
            // (3) if the IME is not controllable (animationFinished == true in this case).
            // We should reset mIsShowRequestedDuringHideAnimation in all cases.
            mIsHideAnimationRunning = running;
            mIsShowRequestedDuringHideAnimation = false;
        }
        return insetsChanged;
    }

    @Override
    public void onWindowFocusGained(boolean hasViewFocus) {
        super.onWindowFocusGained(hasViewFocus);
        getImm().registerImeConsumer(this);
        if ((mController.getRequestedVisibleTypes() & getType()) != 0 && getControl() == null) {
            mIsRequestedVisibleAwaitingControl = true;
        }
    }

    @Override
    public void onWindowFocusLost() {
        super.onWindowFocusLost();
        getImm().unregisterImeConsumer(this);
        mIsRequestedVisibleAwaitingControl = false;
    }

    @Override
    public boolean applyLocalVisibilityOverride() {
        ImeTracing.getInstance().triggerClientDump(
                "ImeInsetsSourceConsumer#applyLocalVisibilityOverride",
                mController.getHost().getInputMethodManager(), null /* icProto */);
        return super.applyLocalVisibilityOverride();
    }

    /**
     * Request {@link InputMethodManager} to show the IME.
     * @return @see {@link android.view.InsetsSourceConsumer.ShowResult}.
     */
    @Override
    public @ShowResult int requestShow(boolean fromIme) {
        if (fromIme) {
            ImeTracing.getInstance().triggerClientDump(
                    "ImeInsetsSourceConsumer#requestShow",
                    mController.getHost().getInputMethodManager(), null /* icProto */);
        }

        // TODO: ResultReceiver for IME.
        // TODO: Set mShowOnNextImeRender to automatically show IME and guard it with a flag.
        if (getControl() == null) {
            // If control is null, schedule to show IME when control is available.
            mIsRequestedVisibleAwaitingControl = true;
        }
        // If we had a request before to show from IME (tracked with mImeRequestedShow), reaching
        // this code here means that we now got control, so we can start the animation immediately.
        // If client window is trying to control IME and IME is already visible, it is immediate.
        if (fromIme || (mState.getSource(getInternalType()).isVisible() && getControl() != null)) {
            return ShowResult.SHOW_IMMEDIATELY;
        }

        return getImm().requestImeShow(mController.getHost().getWindowToken())
                ? ShowResult.IME_SHOW_DELAYED : ShowResult.IME_SHOW_FAILED;
    }

    /**
     * Notify {@link com.android.server.inputmethod.InputMethodManagerService} that
     * IME insets are hidden.
     */
    private void notifyHidden() {
        getImm().notifyImeHidden(mController.getHost().getWindowToken());
    }

    @Override
    public void removeSurface() {
        final IBinder window = mController.getHost().getWindowToken();
        if (window != null) {
            getImm().removeImeSurface(window);
        }
    }

    @Override
    public boolean setControl(@Nullable InsetsSourceControl control, int[] showTypes,
            int[] hideTypes) {
        ImeTracing.getInstance().triggerClientDump("ImeInsetsSourceConsumer#setControl",
                mController.getHost().getInputMethodManager(), null /* icProto */);
        if (!super.setControl(control, showTypes, hideTypes)) {
            return false;
        }
        if (control == null && !mIsRequestedVisibleAwaitingControl) {
            mController.setRequestedVisibleTypes(0 /* visibleTypes */, getType());
            removeSurface();
        }
        if (control != null) {
            mIsRequestedVisibleAwaitingControl = false;
        }
        return true;
    }

    @Override
    protected boolean isRequestedVisibleAwaitingControl() {
        return super.isRequestedVisibleAwaitingControl() || mIsRequestedVisibleAwaitingControl;
    }

    @Override
    public void onPerceptible(boolean perceptible) {
        super.onPerceptible(perceptible);
        final IBinder window = mController.getHost().getWindowToken();
        if (window != null) {
            getImm().reportPerceptible(window, perceptible);
        }
    }

    @Override
    public void dumpDebug(ProtoOutputStream proto, long fieldId) {
        final long token = proto.start(fieldId);
        super.dumpDebug(proto, INSETS_SOURCE_CONSUMER);
        proto.write(IS_REQUESTED_VISIBLE_AWAITING_CONTROL, mIsRequestedVisibleAwaitingControl);
        proto.write(IS_HIDE_ANIMATION_RUNNING, mIsHideAnimationRunning);
        proto.write(IS_SHOW_REQUESTED_DURING_HIDE_ANIMATION, mIsShowRequestedDuringHideAnimation);
        proto.end(token);
    }

    /**
     * Called when {@link #onAnimationStateChanged(boolean)} or
     * {@link InputMethodManager#showSoftInput(View, int)} is called.
     */
    public void onShowRequested() {
        if (mIsHideAnimationRunning) {
            mIsShowRequestedDuringHideAnimation = true;
        }
    }

    private InputMethodManager getImm() {
        return mController.getHost().getInputMethodManager();
    }
}
