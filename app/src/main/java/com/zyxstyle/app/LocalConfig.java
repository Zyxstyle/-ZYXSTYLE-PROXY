package com.zyxstyle.app;

import android.content.Context;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;

public class LocalConfig {

    public static void create(Context context, String packageName) {

        String json =
                "{\n" +
                "  \"testCodePatch\": true,\n" +
                "  \"resetGuest\": true,\n" +
                "  \"targetPackage\": \"" + packageName + "\"\n" +
                "}";

        try {
            FileOutputStream out =
                    context.openFileOutput(
                            "localconfig.json",
                            Context.MODE_PRIVATE
                    );

            out.write(
                    json.getBytes(StandardCharsets.UTF_8)
            );

            out.close();

        } catch (Exception ignored) {
        }
    }
}
