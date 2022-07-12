package com.cherokeelessons.test;

import com.cherokeelessons.gui.MainWindow;
import com.cherokeelessons.gui.MainWindow.Config;
import org.testng.annotations.Test;

import java.awt.*;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class Tests {

    private TestApp testApp;

    @Test
    public void preCleanup() throws IOException {
        Path directory = Paths.get("reports");
        if (!directory.toFile().exists()) {
            return;
        }
        Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    @Test(dependsOnMethods = "preCleanup")
    public void initGui() throws IOException {
        MainWindow.Config config = new Config() {
            @Override
            public String getApptitle() {
                return "SimpleTextUi Test";
            }

            @Override
            public Runnable getApp(String... args) throws Exception {
                return testApp;
            }
        };
        testApp = new TestApp(config);
        EventQueue.invokeLater(new MainWindow(config, new String[]{}));
    }

    @Test(dependsOnMethods = "initGui")
    public void LoggingTest() throws InterruptedException {
        Thread.sleep(10000);
        testApp.setKeepRunning(false);
        Thread.sleep(500);
    }
}
