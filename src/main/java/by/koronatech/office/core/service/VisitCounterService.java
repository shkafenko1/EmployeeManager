package by.koronatech.office.core.service;

public interface VisitCounterService {
    void incrementVisit(String url);
    long getVisitCount(String url);
}