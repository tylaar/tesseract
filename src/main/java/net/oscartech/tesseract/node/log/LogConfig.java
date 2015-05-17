package net.oscartech.tesseract.node.log;

/**
 * Created by tylaar on 15/5/17.
 */

import net.oscartech.tesseract.common.config.Configuration;

/**
 * Log configuration.
 *
 * @author <a href="http://github.com/kuujo">Jordan Halterman</a>
 */
public abstract class LogConfig {
    private static final String LOG_SEGMENT_SIZE = "segment.size";
    private static final String LOG_SEGMENT_INTERVAL = "segment.interval";
    private static final String LOG_FLUSH_ON_WRITE = "flush.on-write";
    private static final String LOG_FLUSH_INTERVAL = "flush.interval";

    private static final String CONFIGURATION = "log";
    private Configuration config;

    protected LogConfig() {
        config = new Configuration(CONFIGURATION);
    }

    protected LogConfig(String customizedPrefix) {
        config = new Configuration(customizedPrefix);
    }

    public LogConfig copy() {
        return (LogConfig) this.copy();
    }

    /**
     * Returns the log segment size in bytes.
     *
     * @return The log segment size in bytes.
     */
    public int getSegmentSize() {
        return config.getInt(LOG_SEGMENT_SIZE);
    }

    /**
     * Returns the log segment interval.
     *
     * @return The log segment interval.
     */
    public long getSegmentInterval() {
        long interval = config.getLong(LOG_SEGMENT_INTERVAL);
        return interval > -1 ? interval : Long.MAX_VALUE;
    }

    /**
     * Returns whether to flush the log to disk on every write.
     *
     * @return Whether to flush the log to disk on every write.
     */
    public boolean isFlushOnWrite() {
        return config.getBoolean(LOG_FLUSH_ON_WRITE);
    }

    /**
     * Returns the log flush interval.
     *
     * @return The log flush interval.
     */
    public long getFlushInterval() {
        long interval = config.getLong(LOG_FLUSH_INTERVAL);
        return interval > -1 ? interval : Long.MAX_VALUE;
    }
}