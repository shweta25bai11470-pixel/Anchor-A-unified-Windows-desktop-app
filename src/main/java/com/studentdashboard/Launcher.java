package com.studentdashboard;

/**
 * Plain launcher that does NOT extend javafx.application.Application.
 *
 * When the JVM entry-point class does not extend Application, the JVM does
 * not perform the "JavaFX runtime components are missing" module check at
 * startup, so JavaFX works fine even when bundled as regular JARs (as the
 * maven-shade-plugin does).
 *
 * This is the standard workaround for the classic error:
 * "Error: JavaFX runtime components are missing, and are required to run this
 * application"
 * that appears when a shaded JavaFX app is packaged with jpackage.
 */
public class Launcher {
    public static void main(String[] args) {
        Main.main(args);
    }
}