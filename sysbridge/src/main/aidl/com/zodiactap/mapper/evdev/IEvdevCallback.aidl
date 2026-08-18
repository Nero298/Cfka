package com.zodiactap.mapper.evdev;

import com.zodiactap.mapper.common.models.GrabbedDeviceHandle;
import com.zodiactap.mapper.common.models.EvdevDeviceInfo;

interface IEvdevCallback {
  /**
   * deviceId is the internal system bridge ID for the device. This is used rather than referencing
   * with a path because primitives have lower overhead and are safer over the JNI boundary.
   */
   boolean onEvdevEvent(int deviceId, long timeSec, long timeUsec, int type, int code, int value, int androidCode);
   void onEmergencyKillSystemBridge();
   void onGrabbedDevicesChanged(in GrabbedDeviceHandle[] devices);
   void onEvdevDevicesChanged(in EvdevDeviceInfo[] devices);
}
