package com.zodiactap.mapper.api;

import android.view.KeyEvent;
import android.view.MotionEvent;

interface IKeyEventRelayServiceCallback {
    boolean onKeyEvent(in KeyEvent event);
    boolean onMotionEvent(in MotionEvent event);
}