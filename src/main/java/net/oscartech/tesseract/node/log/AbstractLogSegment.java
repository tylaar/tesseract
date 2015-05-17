package net.oscartech.tesseract.node.log;

import java.util.Comparator;

/**
 * Created by tylaar on 15/5/17.
 */
public abstract class AbstractLogSegment extends AbstractLoggable implements LogSegment, Comparator<AbstractLogSegment> {
    protected final long id;
    protected long firstIndex;

    public AbstractLogSegment(final long id, final long firstIndex) {
        this.id = id;
        this.firstIndex = firstIndex;
    }

    public long id() {
        return id;
    }

    public long index() {
        return firstIndex;
    }

    @Override
    public String toString() {
        return "AbstractLogSegment{" +
                "id=" + id +
                ", firstIndex=" + firstIndex +
                '}';
    }

    @Override
    public int compare(final AbstractLogSegment o1, final AbstractLogSegment o2) {
        return Long.compare(o1.firstIndex, o2.firstIndex);
    }
}
