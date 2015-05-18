package net.oscartech.tesseract.node.log;

/**
 * It might be a memory one, also might be a file.
 * Created by tylaar on 15/5/17.
 */
public interface LogSegment {

    long id();

    long index();

    long timestamp();
}
