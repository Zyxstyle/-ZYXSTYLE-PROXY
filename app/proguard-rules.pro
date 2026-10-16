# Keep Android components
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Service

# Keep Zyxstyle VPN service
-keep class com.zyxstyle.app.ZyxstyleVpnService { *; }

# Keep Shizuku API
-keep class rikka.shizuku.** { *; }
-keep class dev.rikka.shizuku.** { *; }
