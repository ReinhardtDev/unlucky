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
        TOTAL_TIME,
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

            switch (action) {
                case REGISTER_USER:
                    writer.println("[" + timeStamp + "] " + action + " with name: " + userParams[0] + ", in " + time + "ms");
                    writer.flush();
                    break;

                case AVERAGE_TIME:
                    writer.println("[" + timeStamp + "] " + action + ": " + time + "ms per request");
                    break;

                case TOTAL_TIME:
                    writer.println("[" + timeStamp + "] " + action + ": " + time + "ms");
                    break;

                case PURCHASE_TICKET_CLASSIC:
                    writer.println("[" + timeStamp + "] " +
                            action + " by user: " +
                            userParams[0] + ", with quantity " +
                            userParams[1] + " in " +
                            time + "ms");
                    break;

                case PURCHASE_TICKET_649:
                    writer.println("[" + timeStamp + "] " +
                            action + " by user: " +
                            userParams[0] + ", with numbers " +
                            userParams[1] + " in " +
                            time + "ms");
                    break;

                default:
                    writer.println("[" + timeStamp + "] " + action + " in " + time + "ms");
                    break;
            }

        } catch (IOException e) {
            throw new RuntimeException("Error writing to log file", e);
        }
    }

    public void logStage(String stage, boolean nextStage) {
        try(PrintWriter writer = new PrintWriter(new FileWriter(logFile, true))) {
            String timeStamp = LocalDateTime.now().format(formatter);
            writer.println("[-------------------------------" + timeStamp + "-------------------------------]");
            writer.println("[" + timeStamp + "] " + stage);
            if (nextStage) {
                writer.println("Starting next stage...");
                writer.println("---------------------------------------------------------------------------------------");
            }
        } catch (IOException e) {
            throw new RuntimeException("Error writing stage to log file", e);
        }
    }
}
