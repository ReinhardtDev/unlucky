package com.unlucky.unlucky.logging;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LoggingService {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss:ms");
    private final File logFile = new File("LOG.txt");

    public enum ACTION {
        REGISTER_USER,
        PURCHASE
    }

    public LoggingService() {
    }

    public void log(ACTION action, Long... time) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(logFile, true))) {
            String timeStamp = LocalDateTime.now().format(formatter);
            writer.println("[" + timeStamp + "] " + action.toString() + " in " + time[0] + "ms");
            writer.flush();


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
