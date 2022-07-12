package com.cherokeelessons.gui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;

public class SingleInstance {

    @SuppressWarnings("resource")
    public static boolean isAlreadyRunning() {
        File file;
        FileChannel fileChannel;
        File userDir = new File(System.getProperty("user.home"));
        file = new File(userDir, myLockName());

        if (!file.exists()) {
            try {
                file.createNewFile();
                file.deleteOnExit();
            } catch (IOException e) {
                throw new RuntimeException("Unable to create Single Instance lock file!", e);
            }
        }

        try {
            fileChannel = new RandomAccessFile(file, "rw").getChannel();
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Single Instance lock file vanished!", e);
        }
        try {
            if (fileChannel.tryLock() != null) {
                return false;
            }
        } catch (Exception e) {
        }
        try {
            fileChannel.close();
        } catch (IOException e1) {
        }
        return true;
    }

    private static String myLockName() {
        return "." + SingleInstance.class.getProtectionDomain().getCodeSource().getLocation().getPath()
                .replaceAll("[^a-zA-Z0-9_]", "_");
    }
}
