package eu.unite.challenge.exceptions;

import lombok.Getter;
import net.jcip.annotations.Immutable;

/**
 * Exception which  is thrown when a line of the input file
 * is malformed. For instance, it is not in the format a:b.
 */
@Immutable
public class ItemException extends FormatException {
    private static final long serialVersionUID = -7697971610531769720L;

    @Getter
    private final int itemNo;

    @Getter
    private final String msg;

    public ItemException(final int itemNo, final String msg) {
        super(String.format("Item #%d: Error - %s.", itemNo, msg));
        this.itemNo = itemNo;
        this.msg = msg;
    }
}
