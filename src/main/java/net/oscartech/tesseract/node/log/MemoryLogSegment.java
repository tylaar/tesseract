package net.oscartech.tesseract.node.log;

import net.oscartech.tesseract.common.util.Check;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by tylaar on 15/5/17.
 */
public class MemoryLogSegment extends AbstractLogSegment {
    private final MemoryLogManager manager;
    private long timestamp;
    private TreeMap<Long, ByteBuffer> log;
    private int size;


    public MemoryLogSegment(final long id, final long firstIndex, MemoryLogManager manager) {
        super(id, firstIndex);
        this.manager = manager;
    }

    public MemoryLogManager getManager() {
        return manager;
    }

    @Override
    public long timestamp() {
        return timestamp;
    }

    public int getSize() {
        return size;
    }


    @Override
    public void open() throws IOException {
        checkIfIsNotOpen();
        if (log == null) {
            log = new TreeMap<>();
            size = 0;
            timestamp = System.nanoTime();
        }
    }

    @Override
    public boolean isEmpty() {
        return log == null || log.isEmpty();
    }

    @Override
    public boolean isOpen() {
        return log != null;
    }

    @Override
    public long size() {
        checkIfIsOpen();
        return size;
    }

    @Override
    public long entryCount() {
        checkIfIsOpen();
        return log.size();
    }

    @Override
    public long appendEntry(final ByteBuffer entry) throws IOException {
        Check.isNotNull(entry, "entry");
        checkIfIsOpen();
        long index = firstIndex;
        if (!log.isEmpty()) {
            index = log.lastKey() + 1;
        }
        log.put(index, entry);
        size += entry.limit();
        return index;
    }

    @Override
    public Long firstIndex() {
        checkIfIsOpen();
        return log.isEmpty() ? null : log.firstKey();
    }

    @Override
    public Long lastIndex() {
        checkIfIsOpen();
        return log.isEmpty() ? null : log.lastKey();
    }

    @Override
    public boolean containsIndex(final long index) {
        checkIfIsOpen();
        return log.containsKey(index);
    }

    @Override
    public ByteBuffer getEntry(final long index) {
        checkIfIsOpen();
        checkContainsIndex(index);
        ByteBuffer buff = log.get(index);
        buff.rewind();
        return buff;
    }

    @Override
    public void removeAfter(final long index) {
        checkIfIsOpen();
        if (index < firstIndex) {
            log.clear();
            size = 0;
        } else {
            checkContainsIndex(index);
            for (Map.Entry<Long, ByteBuffer> entry : log.entrySet()) {
                ByteBuffer value = log.remove(entry.getKey());
                if (value != null) {
                    size -= value.limit();
                }
            }
        }
    }

    @Override
    public void flush() {
        checkIfIsOpen();
        return;
    }

    @Override
    public void close() throws IOException {
        checkIfIsOpen();
        return;
    }

    @Override
    public boolean isClosed() {
        return log == null;
    }

    @Override
    public void delete() {
        if (log != null) {
            log.clear();
            log = null;
            size = 0;
        }
    }
}
