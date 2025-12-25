@Slf4j
@Component
public class SnowflakeIdGenerator {

    // 初始偏移时间戳 (2025-01-01)
    private final long epoch = 1735689600000L;
    
    private final long workerIdBits = 10L;
    private final long sequenceBits = 14L;

    private final long maxWorkerId = -1L ^ (-1L << workerIdBits);
    private final long workerIdShift = sequenceBits;
    private final long timestampLeftShift = sequenceBits + workerIdBits;
    private final long sequenceMask = -1L ^ (-1L << sequenceBits);

    private long workerId;
    private long sequence = 0L;
    private long lastTimestamp = -1L;

    public SnowflakeIdGenerator(@Value("${server.worker-id:1}") long workerId) {
        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException(String.format("Worker ID 范围超出限制 (0-%d)", maxWorkerId));
        }
        this.workerId = workerId;
    }

    public synchronized long nextId() {
        long timestamp = System.currentTimeMillis();

        if (timestamp < lastTimestamp) {
            // 生产级处理：时钟回拨异常
            long offset = lastTimestamp - timestamp;
            if (offset <= 5) { // 容忍 5ms 内的回拨
                try {
                    wait(offset << 1);
                    timestamp = System.currentTimeMillis();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            } else {
                throw new RuntimeException("检测到严重时钟回拨，拒绝生成 ID");
            }
        }

        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & sequenceMask;
            if (sequence == 0) {
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0L;
        }

        lastTimestamp = timestamp;

        return ((timestamp - epoch) << timestampLeftShift)
                | (workerId << workerIdShift)
                | sequence;
    }

    private long tilNextMillis(long lastTimestamp) {
        long timestamp = System.currentTimeMillis();
        while (timestamp <= lastTimestamp) {
            timestamp = System.currentTimeMillis();
        }
        return timestamp;
    }
}
