package org.systemsbiology.xtandem.taxonomy;

/**
 * org.systemsbiology.xtandem.taxonomy.CannotAccessDatabaseException
 * User: steven
 * Date: 5/5/11
 */
public class CannotAccessDatabaseException extends RuntimeException {
    public static final CannotAccessDatabaseException[] EMPTY_ARRAY = {};

    /**
     * Constructs a new runtime exception with the specified detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method.
     */
    public CannotAccessDatabaseException(final String message) {
        super(message);
    }
}
