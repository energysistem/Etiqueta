package es.energysistem.etiqueta;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import es.energysistem.etiqueta.receivers.AdminReceiver;

/**
 * Created by Vicente Giner Tendero on 30/04/2014.
 */
public class DeviceAdmin {

    public static final int DEVICE_ADMIN_REQUEST = 9;
    public static final int START_SERVICE = 8;

    private Context context;
    private static DevicePolicyManager devicePolicyManager;
    private static ComponentName componentName;

    public DeviceAdmin(Context context) {
        this.context = context;
        devicePolicyManager = (DevicePolicyManager) context.getSystemService(
                Context.DEVICE_POLICY_SERVICE);
        componentName = new ComponentName(context, AdminReceiver.class);
    }

    public void registerDeviceAdmin() {
        if (!devicePolicyManager.isAdminActive(componentName)) {
            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
            ((Activity) context).startActivityForResult(intent, DEVICE_ADMIN_REQUEST);
        }
    }

    public void unregisterDeviceAdmin() {
        if (devicePolicyManager.isAdminActive(componentName)) {
            devicePolicyManager.removeActiveAdmin(componentName);
        }
    }

    public DevicePolicyManager getDevicePolicyManager() {
        return devicePolicyManager;
    }

    public void setDevicePolicyManager(
            final DevicePolicyManager devicePolicyManager) {
        DeviceAdmin.devicePolicyManager = devicePolicyManager;
    }

    public ComponentName getComponentName() {
        return componentName;
    }

    public void setComponentName(final ComponentName componentName) {
        DeviceAdmin.componentName = componentName;
    }
}
