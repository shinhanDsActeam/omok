package main.java.service;

import main.java.db.HistoryDAO;
import main.java.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class HistoryService {
    public static HistoryDAO historyDAO = HistoryDAO.getInstance();

    public static void recordMatchResult(int hostId, int guestId, String winner) {
        String result = "";
        if ("host".equalsIgnoreCase(winner)) {
            // host 승
            result = "WIN";
        } else if ("guest".equalsIgnoreCase(winner)) {
            // guest 승
            result = "LOSE";
        } else if ("draw".equalsIgnoreCase(winner)) {
            // 무승부
            result = "DRAW";
        }
        historyDAO.addHistory(hostId, guestId, result);
    }
}

