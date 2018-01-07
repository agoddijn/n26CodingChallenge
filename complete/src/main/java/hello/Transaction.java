package hello;

import java.util.Comparator;

public class Transaction {

    private float amount;
    private long timestamp;

    public Transaction(float amount, long timestamp) {
        this.amount = amount;
        this.timestamp = timestamp;
    }

    public Transaction() {
//        Dummy constructor
    }

    public float getAmount() {
        return this.amount;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public static final Comparator<Transaction> transactComparator = new Comparator<Transaction>() {
        public int compare(Transaction t1, Transaction t2) {
            long stamp1 = t1.getTimestamp();
            long stamp2 = t2.getTimestamp();
            if (stamp1 < stamp2) return -1;
            if (stamp1 == stamp2) return 0;
            return 1;
        }
    };

}
