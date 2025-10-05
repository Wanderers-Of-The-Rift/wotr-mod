package com.wanderersoftherift.wotr.util;

/**
 * AutoCloseable subtype that doesn't throw an exception on close
 */
public interface ExceptionlessAutoClosable extends AutoCloseable {
    void close();
}
