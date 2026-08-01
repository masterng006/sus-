package com.yourserver.sus;

public class FlagRecord {

    private int totalFlags;
    private String lastCheck;
    private long lastFlagTime; // epoch millis

    public FlagRecord(String lastCheck) {
        this.totalFlags = 0;
        this.lastCheck = lastCheck;
        this.lastFlagTime = System.currentTimeMillis();
    }

    public void addFlag(String check) {
        this.totalFlags++;
        this.lastCheck = check;
        this.lastFlagTime = System.currentTimeMillis();
    }

    public int getTotalFlags() {
        return totalFlags;
    }

    public String getLastCheck() {
        return lastCheck;
    }

    public long getLastFlagTime() {
        return lastFlagTime;
    }

    public void setTotalFlags(int totalFlags) {
        this.totalFlags = totalFlags;
    }

    public void setLastFlagTime(long lastFlagTime) {
        this.lastFlagTime = lastFlagTime;
    }
}
