package com.zyxstyle.app;

import android.content.Intent;
import android.net.VpnService;
import android.os.ParcelFileDescriptor;

public class ZyxstyleVpnService extends VpnService {

    public static final String ACTION_STOP =
            "com.zyxstyle.app.ACTION_STOP_VPN";

    private ParcelFileDescriptor vpnInterface;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // Tombol STOP VPN
        if (intent != null && ACTION_STOP.equals(intent.getAction())) {
            closeVpn();
            return START_NOT_STICKY;
        }

        try {
            Builder builder = new Builder();

            builder.setSession("ZYXSTYLE PROXY");

            // Hanya membuat interface VPN.
            // Tidak ada route internet.
            builder.addAddress("10.0.0.2", 32);

            vpnInterface = builder.establish();

            if (vpnInterface == null) {
                stopSelf();
                return START_NOT_STICKY;
            }

            return START_STICKY;

        } catch (Exception e) {
            closeVpn();
            return START_NOT_STICKY;
        }
    }

    private void closeVpn() {
        if (vpnInterface != null) {
            try {
                vpnInterface.close();
            } catch (Exception ignored) {
            }

            vpnInterface = null;
        }

        stopSelf();
    }

    @Override
    public void onRevoke() {
        closeVpn();
        super.onRevoke();
    }

    @Override
    public void onDestroy() {
        closeVpn();
        super.onDestroy();
    }
}
