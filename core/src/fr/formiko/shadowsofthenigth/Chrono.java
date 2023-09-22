package fr.formiko.shadowsofthenigth;

public class Chrono {
    private long startTime;
    private long durationInMs;
    private int startHour;
    private int endHour;
    private long endElapsedTime;

    public Chrono(long durationInMs, int startHour, int endHour) {
        startTime = System.currentTimeMillis();
        this.durationInMs = durationInMs;
        this.startHour = startHour;
        if (endHour < startHour)
            endHour += 24; // endHour is the next day (ex: 20h -> 4h
        this.endHour = endHour;
        endElapsedTime = -1;
    }

    public long getElapsedTime() { return endElapsedTime == -1 ? System.currentTimeMillis() - startTime : endElapsedTime; }
    public float getPercentElapsedTime() { return Math.min((float) getElapsedTime() / (float) durationInMs, 1f); }
    public boolean isFinished() { return getElapsedTime() >= durationInMs; }
    public int getStartHour() { return startHour; }
    public int getEndHour() { return endHour; }
    public float getCurrentHourFloat() { return (startHour + (endHour - startHour) * getPercentElapsedTime()) % 24; }
    public String getCurrentHour() {
        float currentHour = getCurrentHourFloat();
        int hour = (int) currentHour;
        int minutes = (int) ((currentHour - hour) * 60);
        return timeToString(hour) + ":" + timeToString(minutes);
    }
    private String timeToString(int hour) { return hour > 9 ? "" + hour : "0" + hour; }
    public void stop() { endElapsedTime = getElapsedTime(); }

    // public static void main(String[] args) {
    // Chrono chrono = new Chrono(1000, 20, 7);
    // while (!chrono.isFinished()) {
    // System.out.println(chrono.getCurrentHour());
    // try {
    // Thread.sleep(100);
    // } catch (InterruptedException e) {
    // e.printStackTrace();
    // }
    // }
    // }

}
