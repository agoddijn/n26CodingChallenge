package hello;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Date;
import java.util.Iterator;

// Class that handles statistics synchronously
public class Statistics {

    private float max, min;
    private BigDecimal sum, avg;
    private long count;
    private Queue transactQueue = new PriorityQueue<Transaction>(100, Transaction.transactComparator);

    // Default statistics
    public Statistics() {
        this.count = 0;
        this.sum = new BigDecimal(0);
        this.max = Float.NEGATIVE_INFINITY;
        this.min = Float.POSITIVE_INFINITY;
        this.avg = new BigDecimal(0);
    }

    // Update statistics based on incoming transactions
    public synchronized void update(Transaction t) {
        this.count++;
        this.sum = this.sum.add(new BigDecimal(t.getAmount()));
        if (t.getAmount() > this.max) this.max = t.getAmount();
        if (t.getAmount() < this.min) this.min = t.getAmount();
        this.avg = this.sum.divide(new BigDecimal(this.count), RoundingMode.CEILING);
        transactQueue.add(t);
    }

    // Get the current state of statistics
    public Statistic getStats() {
        return new Statistic(this.sum.floatValue(),
                this.avg.floatValue() == 0 ? Float.NaN : this.avg.floatValue(),
                this.max,
                this.min,
                this.count);
    }

    // Check if any transactions need to be removed
    public synchronized void check() {
        Date now = new Date();
        boolean updated = false;
        boolean minChanged = false;
        boolean maxChanged = false;
        while (true) {
            Transaction t = (Transaction) transactQueue.peek();
            if (t != null && t.getTimestamp() < (now.getTime() - 60000)) {
                updated = true;
                t = (Transaction)transactQueue.poll();
                this.sum = this.sum.subtract(new BigDecimal(t.getAmount()));
                this.count--;
                if (t.getAmount() == this.min) minChanged = true;
                if (t.getAmount() == this.max) maxChanged = true;
            } else {
                break;
            }
        }

        if (updated && this.count != 0) this.avg = this.sum.divide(new BigDecimal(this.count), RoundingMode.CEILING);
        if (this.count == 0) this.avg = new BigDecimal(0);

        if (minChanged) {
            this.min = Float.POSITIVE_INFINITY;
            for (Iterator<Transaction> iter = transactQueue.iterator(); iter.hasNext();) {
                Transaction element = iter.next();
                if (element.getAmount() < this.min) this.min = element.getAmount();
            }
        }

        if (maxChanged) {
            this.max = Float.NEGATIVE_INFINITY;
            for (Iterator<Transaction> iter = transactQueue.iterator(); iter.hasNext();) {
                Transaction element = iter.next();
                if (element.getAmount() > this.max) this.max = element.getAmount();
            }
        }
    }

}
