package eu.unite.challenge.exceptions;


import net.jcip.annotations.Immutable;

/**
 * Superclass for various exceptions corresponding to malformed problems
 */
@Immutable
public class FormatException extends Exception {
    private static final long serialVersionUID = 6024573891483048767L;

    public FormatException(final String msg) {
        super(msg);
    }
}
