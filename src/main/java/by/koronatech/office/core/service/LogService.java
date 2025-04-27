package by.koronatech.office.core.service;

import java.nio.file.Path;
import java.time.LocalDate;

public interface LogService {
    String initiateLogFileCreation(LocalDate date);
    String getLogFileStatus(String id);
    Path getLogFilePath(String id);
}