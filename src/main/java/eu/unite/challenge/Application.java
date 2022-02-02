package eu.unite.challenge;

import eu.unite.challenge.algorithms.AbstractProblemSolver;
import eu.unite.challenge.algorithms.BranchAndBoundAlgoImpl;
import eu.unite.challenge.exceptions.FileFormatException;
import eu.unite.challenge.utils.FileParserUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootApplication
public class Application {

    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) throws IOException, FileFormatException {
        SpringApplication.run(Application.class, args);

        if (args.length != 1) {
            logger.error("Please give the correct path to test cases as an argument.");
            return;
        }
        FileParserUtility fileParserUtility = new FileParserUtility(args[0]);

        /*
         * The default solver is BranchAndBoundAlgoImpl, but
         * it can be changed here. Also, notice the use of
         * parallelStream() for further efficiency
         */
        List<AbstractProblemSolver> abstractProblemSolvers = fileParserUtility.parse().parallelStream()
                .map(BranchAndBoundAlgoImpl::new)
                .collect(Collectors.toList());

        logger.info("------");
        for (AbstractProblemSolver solver : abstractProblemSolvers)
            logger.info("{}", solver);
    }

}
