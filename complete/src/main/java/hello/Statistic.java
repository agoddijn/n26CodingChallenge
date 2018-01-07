package hello;

// Class to represent a Statistic
public class Statistic {

    private final float sum, avg, max, min;
    private final long count;

    public Statistic(float sum, float avg, float max, float min, long count) {
        this.sum = sum;
        this.avg = avg;
        this.max = max;
        this.min = min;
        this.count = count;
    }

    public float getSum() {
        return this.sum;
    }

    public float getAvg() {
        return this.avg;
    }

    public float getMax() {
        return this.max;
    }

    public float getMin() {
        return min;
    }

    public long getCount() {
        return count;
    }

}
