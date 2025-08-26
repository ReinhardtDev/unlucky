package com.unlucky.unlucky.logging;

import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class LoggingService {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    private final File logFile = new File("LOG.txt");

    public enum ACTION {
        REGISTER_USER,
        AVERAGE_TIME,
        DRAW_CLASSIC,
        DRAW_649,
        PURCHASE_TICKET_CLASSIC,
        PURCHASE_TICKET_649
    }

    public LoggingService() {
    }

    public void log(ACTION action, double time, String... userParams) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(logFile, true))) {
            String timeStamp = LocalDateTime.now().format(formatter);
            if (action != ACTION.AVERAGE_TIME && action != ACTION.PURCHASE_TICKET_CLASSIC && action != ACTION.PURCHASE_TICKET_649) {
                writer.println("[" + timeStamp + "] " + action + " in " + time + "ms");
                writer.flush();
            } else if (action == ACTION.AVERAGE_TIME) {
                writer.println("[" + timeStamp + "] " + action + ": " + time + "ms");
            } else {
                writer.println("[" + timeStamp + "] " +
                        action + " by user: " +
                        userParams[0] + " with quantity " +
                        userParams[1] + " in " +
                        time + "ms");
            }



        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
