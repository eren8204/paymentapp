package com.unotag.unopay;

public class LevelStatusItem {
    private final int index;
    private final int activeCount;
    private final int freeCount;

    public LevelStatusItem(int index, int activeCount, int freeCount) {
        this.index = index;
        this.activeCount = activeCount;
        this.freeCount = freeCount;
    }

    public int getIndex() {
        return index;
    }

    public int getActiveCount() {
        return activeCount;
    }

    public int getFreeCount() {
        return freeCount;
    }
}
