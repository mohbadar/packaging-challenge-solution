package eu.unite.challenge.exceptions;


import net.jcip.annotations.Immutable;

/**
 * Exception which is thrown when an item on a specific line
 * is malformed. For instance, it is not in the format a,b,c.
 */
@Immutable
public class LineFormatException extends FormatException {
    private static final long serialVersionUID = -7697971610531769720L;

    public LineFormatException(final int lineNo, final String msg) {
        super(String.format("Line #%d: Error - %s.", lineNo, msg));
    }

    public LineFormatException(final int lineNo, final ItemException e) {
        super(String.format("Line #%d, item %d: Error - %s.", lineNo, e.getItemNo(), e.getMsg()));
    }
}
