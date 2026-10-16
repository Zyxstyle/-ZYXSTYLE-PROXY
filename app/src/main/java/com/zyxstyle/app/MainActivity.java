package com.zyxstyle.app;

import rikka.shizuku.Shizuku;

import android.app.Activity;
import android.os.Bundle;
import android.provider.Settings;
import android.content.Intent;
import android.net.Uri;
import android.net.VpnService;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import android.widget.VideoView;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class MainActivity extends Activity {

    private LinearLayout root;
    private VideoView backgroundVideo;
    private TextView dragStatus;
    private TextView vpnStatus;
    private Switch vpnSwitch;
    private TextView vpnSwitchStatus;
    private boolean vpnEnabled = false;
    private static final int VPN_REQUEST_CODE = 1001;

    private int cyan = Color.rgb(0, 229, 255);
    private int dark = Color.rgb(12, 16, 24);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UpdateWarning.check(this);
        getWindow().setFlags(android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN, android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if (!Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + getPackageName())
            );
            startActivity(intent);
        }

        root = new LinearLayout(this);
        backgroundVideo = new FullScreenVideoView(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.TRANSPARENT);
        root.setPadding(30, 70, 30, 30);

        GradientDrawable bg = new GradientDrawable();
        bg.setColor(dark);
        root.setBackgroundResource(R.drawable.zyxstyle_bg);
        root.setBackgroundColor(Color.TRANSPARENT);

        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setPadding(5, 5, 5, 10);

        ImageView headerIcon = new ImageView(this);
        headerIcon.setImageResource(R.drawable.zyxstyle_icon);
        headerIcon.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

        LinearLayout.LayoutParams iconParams =
                new LinearLayout.LayoutParams(58, 58);
        iconParams.rightMargin = 14;
        header.addView(headerIcon, iconParams);

        FrameLayout headerEffect = new FrameLayout(this);

TextView title = new TextView(this);
title.setText("ZYXSTYLE");
title.setTextSize(27);
title.setTypeface(null, 1);

android.graphics.LinearGradient titleGradient =
        new android.graphics.LinearGradient(
                0, 0, 260, 0,
                new int[]{
                        Color.rgb(255, 40, 55),
                        Color.rgb(170, 40, 180),
                        Color.rgb(0, 210, 255)
                },
                null,
                android.graphics.Shader.TileMode.CLAMP
        );

title.getPaint().setShader(titleGradient);

headerEffect.addView(
        title,
        new FrameLayout.LayoutParams(-1, 70)
);

header.addView(
        headerEffect,
        new LinearLayout.LayoutParams(-2, 70)
);

root.addView(
                header,
                new LinearLayout.LayoutParams(-1, 85)
        );

        TextView gameTitle = makeLabel("PILIH GAME");
        root.addView(gameTitle);

        Spinner gameSpinner = new Spinner(this);

        String[] games = {
            "Free Fire",
            "Free Fire MAX"
        };

        int[] gameIcons = {
            R.drawable.free_fire_th,
            R.drawable.free_fire_max
        };

        BaseAdapter gameAdapter = new BaseAdapter() {

            @Override
            public int getCount() {
                return games.length;
            }

            @Override
            public Object getItem(int position) {
                return games[position];
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(
                    int position,
                    View convertView,
                    android.view.ViewGroup parent) {

                LinearLayout row =
                    new LinearLayout(MainActivity.this);

                row.setOrientation(LinearLayout.HORIZONTAL);
                row.setGravity(Gravity.CENTER_VERTICAL);
                row.setPadding(12, 4, 12, 4);

                ImageView icon =
                    new ImageView(MainActivity.this);

                icon.setImageResource(gameIcons[position]);
                icon.setScaleType(ImageView.ScaleType.CENTER_CROP);

                LinearLayout.LayoutParams iconParams =
                    new LinearLayout.LayoutParams(48, 48);

                iconParams.rightMargin = 14;

                row.addView(icon, iconParams);

                TextView name =
                    new TextView(MainActivity.this);

                name.setText(games[position]);
                name.setTextColor(Color.WHITE);
                name.setTextSize(16);

                row.addView(
                    name,
                    new LinearLayout.LayoutParams(-2, -2)
                );

                return row;
            }
        };

        gameSpinner.setAdapter(gameAdapter);

        root.addView(
            gameSpinner,
            new LinearLayout.LayoutParams(-1, 65)
        );

        root.addView(makeLabel("STATUS"));

        TextView gameStatus =
            makeStatus("GAME : SIAP");

        root.addView(gameStatus);

        vpnStatus =
            makeStatus("VPN : OFF");

        root.addView(vpnStatus);

        LinearLayout vpnRow =
            new LinearLayout(this);

        vpnRow.setGravity(Gravity.CENTER_VERTICAL);
        vpnRow.setPadding(20, 10, 20, 10);

        TextView vpnTitle =
            new TextView(this);

        vpnTitle.setText("VPN");
        vpnTitle.setTextColor(cyan);
        vpnTitle.setTypeface(null, 1);
        vpnTitle.setTextColor(Color.WHITE);
        vpnTitle.setTextSize(18);

        vpnSwitchStatus =
            new TextView(this);

        vpnSwitchStatus.setText("OFF");
        vpnSwitchStatus.setTextColor(Color.GRAY);
        vpnSwitchStatus.setTextSize(17);
        vpnSwitchStatus.setGravity(Gravity.CENTER);

        vpnSwitch =
            new Switch(this);

        vpnSwitch.setChecked(false);

        vpnSwitch.setOnCheckedChangeListener(
            (button, checked) -> {
                if (UpdateWarning.isLocked()) {
                    vpnSwitch.setChecked(false);
                    return;
                }

                if (checked) {

                    Intent prepareIntent =
                        VpnService.prepare(this);

                    if (prepareIntent != null) {

                        startActivityForResult(
                            prepareIntent,
                            VPN_REQUEST_CODE
                        );

                    } else {

                        startZyxstyleVpn();

                        vpnEnabled = true;

                        vpnSwitchStatus.setText("ON");
                        vpnSwitchStatus.setTextColor(cyan);
                    }

                } else {

                    Intent stopIntent =
                        new Intent(
                            this,
                            ZyxstyleVpnService.class
                        );

                    stopIntent.setAction(
                        ZyxstyleVpnService.ACTION_STOP
                    );

                    startService(stopIntent);

                    vpnEnabled = false;

                    vpnSwitchStatus.setText("OFF");
                    vpnSwitchStatus.setTextColor(Color.GRAY);

                    if (vpnStatus != null) {
                        vpnStatus.setText("VPN : OFF");
                    }
                }
            }
        );

        vpnRow.addView(
            vpnTitle,
            new LinearLayout.LayoutParams(0, 70, 1)
        );

        vpnRow.addView(
            vpnSwitchStatus,
            new LinearLayout.LayoutParams(100, 70)
        );

        vpnRow.addView(
            vpnSwitch,
            new LinearLayout.LayoutParams(70, 70)
        );

        root.addView(vpnRow);

        TextView shizukuStatus =
            makeStatus(
                "SHIZUKU : " +
                (Shizuku.pingBinder()
                    ? "TERHUBUNG"
                    : "TIDAK TERHUBUNG")
            );

        root.addView(shizukuStatus);

        root.addView(makeLabel("FITUR"));

        LinearLayout dragRow =
            new LinearLayout(this);

        dragRow.setGravity(Gravity.CENTER_VERTICAL);
        dragRow.setPadding(20, 10, 20, 10);

        TextView dragTitle =
            new TextView(this);

        dragTitle.setText("DRAG HS");
        dragTitle.setTextColor(cyan);
        dragTitle.setTypeface(null, 1);
        dragTitle.setTextColor(Color.WHITE);
        dragTitle.setTextSize(18);

        dragStatus =
            new TextView(this);

        dragStatus.setText("OFF");
        dragStatus.setTextColor(Color.GRAY);
        dragStatus.setTextSize(17);
        dragStatus.setGravity(Gravity.CENTER);

        Switch dragSwitch =
            new Switch(this);
        dragSwitch.setOnCheckedChangeListener((button, checked) -> {
            if (UpdateWarning.isLocked()) {
                dragSwitch.setChecked(false);
                return;
            }

                if (checked) {

                    dragStatus.setText("ON");
                    dragStatus.setTextColor(cyan);

                } else {

                    dragStatus.setText("OFF");
                    dragStatus.setTextColor(Color.GRAY);
                }
            }
        );

        dragRow.addView(
            dragTitle,
            new LinearLayout.LayoutParams(0, 70, 1)
        );

        dragRow.addView(
            dragStatus,
            new LinearLayout.LayoutParams(100, 70)
        );

        dragRow.addView(
            dragSwitch,
            new LinearLayout.LayoutParams(70, 70)
        );

        root.addView(dragRow);

        Button enterButton =
            new Button(this);

        enterButton.setText("ENTER GAME");
        enterButton.setTextColor(Color.WHITE);

        GradientDrawable buttonBg =
            new GradientDrawable();

        buttonBg.setColor(
            Color.rgb(0, 130, 160)
        );

        buttonBg.setCornerRadius(20);

        enterButton.setBackground(buttonBg);

        enterButton.setOnClickListener(v -> {
            if (UpdateWarning.isLocked()) {
                return;
            }
            if (!vpnEnabled) {
                new android.app.AlertDialog.Builder(MainActivity.this)
                        .setTitle("VPN BELUM AKTIF")
                        .setMessage("Aktifkan VPN terlebih dahulu sebelum membuka game.")
                        .setPositiveButton("OK", null)
                        .show();
                return;
            }

            String selectedGame =
                (String) gameSpinner.getSelectedItem();

            String packageName;

            if ("Free Fire MAX".equals(selectedGame)) {

                packageName =
                    "com.dts.freefiremax";

            } else {

                packageName =
                    "com.dts.freefireth";
            }

            // Membuat localconfig.json
            LocalConfig.create(
                MainActivity.this,
                packageName
            );

            Intent launchIntent =
                getPackageManager()
                    .getLaunchIntentForPackage(
                        packageName
                    );

            if (launchIntent != null) {

                launchIntent.addFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK
                );

                startActivity(launchIntent);

                gameStatus.setText(
                    "GAME : " +
                    selectedGame.toUpperCase()
                );

            } else {

                Toast.makeText(
                    MainActivity.this,
                    selectedGame +
                    " belum terpasang",
                    Toast.LENGTH_SHORT
                ).show();
            }
        });

        LinearLayout.LayoutParams buttonParams =
            new LinearLayout.LayoutParams(-1, 65);

        buttonParams.topMargin = 30;

        root.addView(
            enterButton,
            buttonParams
        );

        FrameLayout videoContainer = new FrameLayout(this);
        backgroundVideo.setLayoutParams(new FrameLayout.LayoutParams(-1, -1));
        videoContainer.addView(backgroundVideo);
        videoContainer.addView(root);
        setContentView(videoContainer);
        new ZyxstyleMedia(this, backgroundVideo).start();

        Shizuku.addBinderReceivedListenerSticky(
            () -> shizukuStatus.setText(
                "SHIZUKU : TERHUBUNG"
            )
        );

        Shizuku.addRequestPermissionResultListener(
            (requestCode, grantResult) -> {

                if (requestCode == 100) {

                    shizukuStatus.setText(
                        "SHIZUKU : " +
                        (
                            grantResult ==
                            android.content.pm.PackageManager
                                .PERMISSION_GRANTED
                            ? "TERHUBUNG"
                            : "IZIN DITOLAK"
                        )
                    );
                }
            }
        );

        if (
            Shizuku.pingBinder() &&
            Shizuku.checkSelfPermission() !=
            android.content.pm.PackageManager
                .PERMISSION_GRANTED
        ) {

            Shizuku.requestPermission(100);
        }
    }

    private void startZyxstyleVpn() {

        startService(
            new Intent(
                this,
                ZyxstyleVpnService.class
            )
        );

        if (vpnStatus != null) {
            vpnStatus.setText("VPN : ON");
        }
    }

    @Override
    protected void onActivityResult(
            int requestCode,
            int resultCode,
            Intent data) {

        super.onActivityResult(
            requestCode,
            resultCode,
            data
        );

        if (requestCode == VPN_REQUEST_CODE) {

            if (resultCode == RESULT_OK) {

                startZyxstyleVpn();

                vpnEnabled = true;

                if (vpnSwitchStatus != null) {

                    vpnSwitchStatus.setText("ON");
                    vpnSwitchStatus.setTextColor(cyan);
                }

            } else {

                vpnEnabled = false;

                stopService(
                    new Intent(
                        this,
                        ZyxstyleVpnService.class
                    )
                );

                if (vpnStatus != null) {
                    vpnStatus.setText("VPN : OFF");
                }

                if (vpnSwitch != null) {
                    vpnSwitch.setChecked(false);
                }

                if (vpnSwitchStatus != null) {

                    vpnSwitchStatus.setText("OFF");
                    vpnSwitchStatus.setTextColor(
                        Color.GRAY
                    );
                }

                Toast.makeText(
                    this,
                    "Izin VPN ditolak",
                    Toast.LENGTH_SHORT
                ).show();
            }
        }
    }

    private TextView makeLabel(String text) {
        TextView t = new TextView(this);

        t.setText(text);
        t.setTextSize(14);
        t.setTypeface(null, 1);
        t.setPadding(5, 25, 5, 10);

        android.graphics.LinearGradient gradient =
                new android.graphics.LinearGradient(
                        0, 0, 320, 0,
                        new int[]{
                                Color.rgb(255, 45, 65),
                                Color.rgb(190, 45, 180),
                                Color.rgb(0, 220, 255)
                        },
                        null,
                        android.graphics.Shader.TileMode.MIRROR
                );

        t.getPaint().setShader(gradient);
        t.setShadowLayer(12f, 0f, 0f, Color.rgb(0, 210, 255));

        return t;
    }

    private TextView makeStatus(String text) {
        TextView t = new TextView(this);

        t.setText(text);
        t.setTextSize(15);
        t.setPadding(15, 8, 15, 8);

        android.graphics.LinearGradient gradient =
                new android.graphics.LinearGradient(
                        0, 0, 300, 0,
                        new int[]{
                                Color.rgb(255, 60, 70),
                                Color.rgb(180, 50, 190),
                                Color.rgb(0, 220, 255)
                        },
                        null,
                        android.graphics.Shader.TileMode.MIRROR
                );

        t.getPaint().setShader(gradient);
        t.setShadowLayer(10f, 0f, 0f, Color.rgb(0, 200, 255));

        return t;
    }
}
