package eu.unite.challenge.exceptions;


import net.jcip.annotations.Immutable;

/**
 * Exception which is thrown when the input file is empty,
 * or is larger than some value specified in the {@code ConstraintRules} class.
 */
@Immutable
public class FileFormatException extends FormatException {
    private static final long serialVersionUID = -7697971610531769720L;

    public FileFormatException(final String msg) {
        super(msg);
    }
}
