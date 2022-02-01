package eu.unite.challenge.exceptions;


import net.jcip.annotations.Immutable;

/**
 * Exception that is thrown if the problem instance is
 * greater than some value specified in the {@code ConstraintRules} class.
 */
@Immutable
public class OutOfRangeProblemSizeException extends RuntimeException {
    private static final long serialVersionUID = 1429313029444819610L;

    public OutOfRangeProblemSizeException(final String msg) {
        super(msg);
    }
}
