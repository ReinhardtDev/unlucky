package com.unlucky.unlucky.logging;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LoggingService {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    private final File logFile = new File("LOG.txt");

    public enum ACTION {
        REGISTER_USER,
        AVERAGE_TIME,
        PURCHASE
    }

    public LoggingService() {
    }

    public void log(ACTION action, double... time) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(logFile, true))) {
            String timeStamp = LocalDateTime.now().format(formatter);
            if (action != ACTION.AVERAGE_TIME) {
                writer.println("[" + timeStamp + "] " + action.toString() + " in " + time[0] + "ms");
                writer.flush();
            } else {
                writer.println("[" + timeStamp + "] " + action + ": " + time[0] + "ms");
            }



        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
