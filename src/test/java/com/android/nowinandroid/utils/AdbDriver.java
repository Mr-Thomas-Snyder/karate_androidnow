package com.android.nowinandroid.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AdbDriver {
    private static final Logger logger = LoggerFactory.getLogger(AdbDriver.class);
    private static final String ADB_PATH = "C:\\Users\\saiko\\AppData\\Local\\Android\\Sdk\\platform-tools\\adb.exe";
    private static final String DUMP_FILE = "window_dump.xml";

    public String runCommand(String command) {
        try {
            logger.debug("Executing: {} {}", ADB_PATH, command);
            Process process = Runtime.getRuntime().exec(ADB_PATH + " " + command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
            process.waitFor();
            return output.toString().trim();
        } catch (Exception e) {
            logger.error("Command failed: " + command, e);
            return "";
        }
    }

    public void dumpWindow() {
        runCommand("shell uiautomator dump /sdcard/" + DUMP_FILE);
        runCommand("pull /sdcard/" + DUMP_FILE + " .");
    }

    private Document getDump() {
        for (int i = 0; i < 3; i++) {
            dumpWindow();
            java.io.File file = new java.io.File(DUMP_FILE);
            if (file.exists() && file.length() > 0) {
                try {
                    return Jsoup.parse(file, "UTF-8", "");
                } catch (Exception e) {
                    logger.warn("Failed to parse dump, retry {}", i);
                }
            }
            sleep(1000);
        }
        return null;
    }

    public int[] findCenter(String attrValue, String attrName) {
        Document doc = getDump();
        if (doc == null) return null;

        String query = String.format("node[%s=\"%s\"]", attrName, attrValue);
        Elements elements = doc.select(query);
        
        if (elements.isEmpty() && (attrName.equals("text") || attrName.equals("content-desc") || attrName.equals("resource-id"))) {
            query = String.format("node[%s*=\"%s\"]", attrName, attrValue);
            elements = doc.select(query);
        }

        if (elements.isEmpty()) return null;

        String bounds = elements.first().attr("bounds"); // [x1,y1][x2,y2]
        Pattern p = Pattern.compile("\\[(\\d+),(\\d+)\\]\\[(\\d+),(\\d+)\\]");
        Matcher m = p.matcher(bounds);
        if (m.find()) {
            int x1 = Integer.parseInt(m.group(1));
            int y1 = Integer.parseInt(m.group(2));
            int x2 = Integer.parseInt(m.group(3));
            int y2 = Integer.parseInt(m.group(4));
            return new int[]{(x1 + x2) / 2, (y1 + y2) / 2};
        }
        return null;
    }

    public boolean click(String text) {
        for (int i = 0; i < 3; i++) {
            int[] coords = findCenter(text, "text");
            if (coords == null) coords = findCenter(text, "content-desc");
            if (coords == null) coords = findCenter(text, "resource-id");
            
            if (coords != null) {
                runCommand("shell input tap " + coords[0] + " " + coords[1]);
                return true;
            }
            sleep(1000);
        }
        logger.warn("Could not find element with text/desc/id: {}", text);
        return false;
    }

    public void back() {
        runCommand("shell input keyevent 4");
    }

    public void home() {
        runCommand("shell input keyevent 3");
    }

    public void resetApp(String pkg) {
        runCommand("shell pm clear " + pkg);
        runCommand("shell pm grant " + pkg + " android.permission.POST_NOTIFICATIONS");
        runCommand("shell am start -n " + pkg + "/com.google.samples.apps.nowinandroid.MainActivity");
        
        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < 15000) {
            if (getForegroundPackage().contains(pkg)) {
                logger.info("App {} successfully in foreground", pkg);
                return;
            }
            sleep(1000);
        }
    }

    public void dismissPermissionDialog() {
        if (exists("Allow")) click("Allow");
    }

    public boolean waitForForegroundPackage(String pkg, int timeoutSec) {
        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < timeoutSec * 1000) {
            String fg = getForegroundPackage();
            if (fg.contains(pkg) || (!pkg.contains("nowinandroid") && !fg.contains("nowinandroid"))) {
                return true;
            }
            sleep(1000);
        }
        return false;
    }

    public boolean scrollUntilVisible(String text) {
        for (int i = 0; i < 5; i++) {
            if (exists(text)) return true;
            swipeUp();
            sleep(1000);
        }
        return exists(text);
    }

    public String getForegroundPackage() {
        String output = runCommand("shell dumpsys activity activities");
        Pattern[] patterns = {
            Pattern.compile("mFocusedWindow=Window\\{[^ ]+ [^ ]+ ([^/\\} ]+)"),
            Pattern.compile("mResumedActivity: [^ ]+ ([^/\\} ]+)"),
            Pattern.compile("mCurrentFocus=Window\\{[^ ]+ [^ ]+ ([^/\\} ]+)")
        };
        for (Pattern p : patterns) {
            Matcher m = p.matcher(output);
            if (m.find()) return m.group(1);
        }
        return "";
    }

    public boolean waitFor(String text, int timeoutSec) {
        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < timeoutSec * 1000) {
            if (exists(text)) return true;
            sleep(1000);
        }
        logger.warn("Element '{}' not found after {}s", text, timeoutSec);
        return false;
    }

    public boolean waitForLog(String pattern, int timeoutSec) {
        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < timeoutSec * 1000) {
            String logs = runCommand("logcat -d");
            if (logs.contains(pattern)) return true;
            sleep(1000);
        }
        return false;
    }

    public void clearLog() {
        runCommand("logcat -c");
    }

    public byte[] screenshot() {
        String remotePath = "/sdcard/screen.png";
        String localPath = "target/last_screen.png";
        new java.io.File("target").mkdirs();
        runCommand("shell screencap -p " + remotePath);
        runCommand("pull " + remotePath + " " + localPath);
        java.io.File file = new java.io.File(localPath);
        if (!file.exists()) return null;
        try {
            return java.nio.file.Files.readAllBytes(file.toPath());
        } catch (Exception e) {
            return null;
        }
    }

    public void cleanup() {
        runCommand("shell rm /sdcard/" + DUMP_FILE);
        runCommand("shell rm /sdcard/screen.png");
    }

    public boolean exists(String text) {
        Document doc = getDump();
        if (doc == null) return false;
        return !doc.select("node[text*=\"" + text + "\"]").isEmpty() || 
               !doc.select("node[content-desc*=\"" + text + "\"]").isEmpty() ||
               !doc.select("node[resource-id*=\"" + text + "\"]").isEmpty();
    }
    
    public void sleep(int ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) {}
    }

    public void swipeUp() {
        runCommand("shell input swipe 500 1500 500 500 500");
    }
}
