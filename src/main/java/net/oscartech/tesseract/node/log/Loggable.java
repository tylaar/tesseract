package net.oscartech.tesseract.node.log;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by tylaar on 15/5/17.
 */
public interface Loggable extends Closeable {
    /**
     * Opens the logger.
     */
    void open() throws IOException;

    /**
     * Returns a boolean indicating whether the logger is empty.
     *
     * @return Indicates whether the logger is empty.
     */
    boolean isEmpty();

    /**
     * Returns a boolean indicating whether the log is open.
     *
     * @return Indicates whether the log is open.
     */
    boolean isOpen();

    /**
     * Returns the size in bytes.
     *
     * @return The size in bytes.
     * @throws IllegalStateException If the log is not open.
     */
    long size();

    /**
     * Returns the number of entries.
     *
     * @return The number of entries.
     * @throws IllegalStateException If the log is not open.
     */
    long entryCount();

    /**
     * Appends an entry to the logger.
     *
     * @param entry The entry to append.
     * @return The appended entry index.
     * @throws IllegalStateException If the log is not open.
     * @throws NullPointerException If the entry is null.
     * @throws java.io.IOException If a new segment cannot be opened
     */
    long appendEntry(ByteBuffer entry) throws IOException;

    /**
     * Returns the immutable first index in the log.
     *
     * @return The first index in the log.
     * @throws java.lang.IllegalStateException If the log is not open.
     */
    long index();

    /**
     * Returns the index of the first entry in the log.
     *
     * @return The index of the first entry in the log or {@code null} if the log is empty.
     * @throws IllegalStateException If the log is not open.
     */
    Long firstIndex();

    /**
     * Returns the index of the last entry in the log.
     *
     * @return The index of the last entry in the log or {@code null} if the log is empty.
     * @throws IllegalStateException If the log is not open.
     */
    Long lastIndex();

    /**
     * Returns a boolean indicating whether the log contains an entry at the given index.
     *
     * @param index The index of the entry to check.
     * @return Indicates whether the log contains the given index.
     * @throws IllegalStateException If the log is not open.
     */
    boolean containsIndex(long index);

    /**
     * Gets an entry from the log.
     *
     * @param index The index of the entry to get.
     * @return The entry at the given index, or {@code null} if the entry doesn't exist.
     * @throws IllegalStateException If the log is not open.
     */
    ByteBuffer getEntry(long index);

    /**
     * Removes all entries after the given index (exclusive).
     *
     * @param index The index after which to remove entries.
     * @throws IllegalStateException If the log is not open.
     * @throws LogException If a new segment cannot be opened
     */
    void removeAfter(long index);

    /**
     * Flushes the log to disk.
     *
     * @throws IllegalStateException If the log is not open.
     */
    void flush();

    /**
     * Closes the logger.
     */
    @Override
    void close() throws IOException;

    /**
     * Returns a boolean indicating whether the log is closed.
     *
     * @return Indicates whether the log is closed.
     */
    boolean isClosed();

    /**
     * Deletes the logger.
     */
    void delete();

}
