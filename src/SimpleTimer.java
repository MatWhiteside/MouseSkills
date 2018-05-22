public class SimpleTimer {
    private double totalTime = 0.00;
    private double startTime = 0.00;

    public void start() {
        startTime = System.currentTimeMillis();
    }

    public void updateValue() {
        totalTime += (System.currentTimeMillis() - startTime);
        start();
    }

    public void stop() {
        totalTime += (System.currentTimeMillis() - startTime);
    }

    public double getTotalTime() {
        return totalTime;
    }

    public void reset() {
        totalTime = 0.00;
        startTime = 0.00;
    }
}
