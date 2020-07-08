package com.github.log2c.autocarmode.service;

import android.accessibilityservice.AccessibilityService;
import android.app.Notification;
import android.app.PendingIntent;
import android.os.Build;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.ArraySet;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import androidx.annotation.RequiresApi;

import java.lang.reflect.Field;
import java.util.List;

public class AutoConfirmDriveAccessibilityService extends AccessibilityService {
    private static final String TAG = AutoConfirmDriveAccessibilityService.class.getSimpleName();

    @SuppressWarnings("unchecked")
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        final int eventType = accessibilityEvent.getEventType();
        Log.i(TAG, "onAccessibilityEvent: " + eventType);
        if (eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {// 出现啦
            Log.i(TAG, "onAccessibilityEvent: 点击开启驾车模式");
            AccessibilityNodeInfo rootInActiveWindow = getRootInActiveWindow();
            if (rootInActiveWindow != null) {
                List<AccessibilityNodeInfo> nodes = rootInActiveWindow.findAccessibilityNodeInfosByText("开启驾车场景");
                for (AccessibilityNodeInfo node : nodes) {
                    if (TextUtils.equals(node.getClassName(), "android.widget.Button")) {
                        node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    }
                }
            }
        } else if (eventType == AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED) {
            Parcelable data = accessibilityEvent.getParcelableData();
            if (data instanceof Notification) {
                try {
                    Notification notification = (Notification) data;
                    if (notification.actions.length > 0 && notification.actions[0].title.equals("关闭")) {    // 已开启
                        return;
                    }
                    Class<?> clzz = Class.forName(Notification.class.getName());
                    Field declaredField = clzz.getDeclaredField("allPendingIntents");

                    ArraySet<PendingIntent> pendingIntents = (ArraySet<PendingIntent>) declaredField.get(notification);
                    if (pendingIntents != null && !pendingIntents.isEmpty()) {
                        pendingIntents.valueAt(0).send();
                    }
                } catch (PendingIntent.CanceledException | ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
                    e.printStackTrace();
                    Log.e(TAG, "onAccessibilityEvent: ", e);
                }
            }
            Log.i(TAG, "onAccessibilityEvent: 通知结束");
        }
    }

    @Override
    public void onInterrupt() {

    }
}
