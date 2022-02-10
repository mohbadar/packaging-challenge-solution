package eu.unite.challenge.algorithms;

import eu.unite.challenge.dataobjects.Package;
import eu.unite.challenge.dataobjects.RecordInstance;
import lombok.Getter;

import java.util.SortedSet;

/**
 * The {@code Solver} class provides a base class for various
 * algorithms implementing the solution to the given problem.
 * It has an {@code abstract} method called {@code solve}.
 * Various algorithms which want to solve the problem can
 * implement this method.
 */
@Getter
public abstract class AbstractProblemSolver {
    private final Package bag;

    /**
     * The constructor receives an instance of RecordInstance,
     * calls solve on it, and initializes the field {@code bag}
     * upon receiving the response.
     *
     * @param recordInstance An instance of the problem
     */
    AbstractProblemSolver(final RecordInstance recordInstance) {
        if (recordInstance == null) {
            bag = null;
            return;
        }
        SortedSet<Integer> sortedSolution = solve(recordInstance);
        bag = new Package(recordInstance.getMap(), sortedSolution);
    }

    @Override
    public final String toString() {
        return (bag == null) ? "ERR" : bag.getResult();
    }

    /**
     * Solves the package problem for the given instance.
     *
     * @param recordInstance An instance of the problem
     * @return A sorted set containing the indices of items in the solution
     */
    protected abstract SortedSet<Integer> solve(final RecordInstance recordInstance);
}
