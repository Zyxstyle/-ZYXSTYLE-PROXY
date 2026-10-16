package com.zyxstyle.app;

import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageInfo;

public class UpdateWarning {

    private static boolean updateLocked = false;

    public static boolean isLocked() {
        return updateLocked;
    }

    public static void check(Context context) {
        try {
            PackageInfo info = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);

            long currentVersion = info.getLongVersionCode();

            android.content.SharedPreferences prefs =
                    context.getSharedPreferences(
                            "zyxstyle_update",
                            Context.MODE_PRIVATE
                    );

            boolean pending =
                    prefs.getBoolean("pending_update", false);

            long pendingVersion =
                    prefs.getLong("pending_version", -1);

            if (pending && pendingVersion == currentVersion) {
                updateLocked = true;

                new AlertDialog.Builder(context)
                        .setTitle("⚠ PERINGATAN UPDATE")
                        .setMessage(
                                "ZYXSTYLE PROXY telah diperbarui.\n\n" +
                                "Fitur aplikasi dikunci sampai proses update selesai."
                        )
                        .setPositiveButton("OK", null)
                        .setCancelable(false)
                        .show();

                return;
            }

            long lastVersion =
                    prefs.getLong("last_version", -1);

            if (lastVersion != -1 && currentVersion > lastVersion) {
                prefs.edit()
                        .putBoolean("pending_update", true)
                        .putLong("pending_version", currentVersion)
                        .apply();

                updateLocked = true;

                new AlertDialog.Builder(context)
                        .setTitle("⚠ PERINGATAN UPDATE")
                        .setMessage(
                                "ZYXSTYLE PROXY telah diperbarui.\n\n" +
                                "Fitur aplikasi dikunci sampai proses update selesai."
                        )
                        .setPositiveButton("OK", null)
                        .setCancelable(false)
                        .show();

                return;
            }

            prefs.edit()
                    .putLong("last_version", currentVersion)
                    .apply();

        } catch (Exception ignored) {
        }
    }
}
