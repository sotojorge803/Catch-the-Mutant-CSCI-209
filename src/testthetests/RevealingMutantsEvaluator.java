package testthetests;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;

import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;

import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherFactory;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;

/**
 * Evaluates how good your tests in MutantRevealer are against good and bad
 * mutants.
 * 
 * Since we're writing to both System.out and System.err, sometimes messages
 * don't come out in the expected order.
 * 
 * @author Shannon Duvall - original concept
 * @author Sara Sprenkle - revise for JUnit 5, documentation, updated displayed
 *         output, minor revisions
 */
public class RevealingMutantsEvaluator {

	public final int NUM_RANDOM_TRIALS = 25;
	private static final String TESTS_PACKAGE = "revealer";
	private static final String TESTS_FILE = "MutantRevealer";
	private static final String SUCCESS_ASCII_FILE = "xmen.txt";
	private static final String FAILURE_ASCII_FILE = "sentinel.txt";

	public static void main(String[] args) {
		MutantMaker.initMutantMaker();
		RevealingMutantsEvaluator evaluator = new RevealingMutantsEvaluator();
		evaluator.run();
	}

	/**
	 * Evaluates the test suite against all the mutants -- good and bad -- and
	 * displays the results of those tests
	 */
	public void run() {
		Class<?> revealer;
		try {
			revealer = Class.forName(TESTS_PACKAGE + "." + TESTS_FILE);
			boolean wolverineSuccess = evaluateTestsOnGoodMutants(revealer);
			System.out.println();

			boolean mutantSuccess = evaluateTestsOnBadMutants(revealer);
			if (wolverineSuccess && mutantSuccess) {
				displayMessage("~~~~~~~ Good testing!  YOU CAUGHT ALL THE BAD MUTANTS! ~~~~~~~", SUCCESS_ASCII_FILE);
			} else {
				displayMessage("~~~~~~~ Oh no! The Sentinel caught the mutants before you did! ~~~~~~~",
						FAILURE_ASCII_FILE);
			}

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Try *not* to reveal the good mutants
	 * 
	 * @param revealerTestClass the class used to try to reveal the mutants
	 * @return true if all the good mutant passed all tests; otherwise, false
	 */
	public boolean evaluateTestsOnGoodMutants(Class<?> revealerTestClass) {

		final LauncherDiscoveryRequest request = setUpTestRequest(revealerTestClass);

		final Launcher launcher = LauncherFactory.create();
		final SummaryGeneratingListener listener = new SummaryGeneratingListener();

		launcher.registerTestExecutionListeners(listener);

		System.out.println("~~~~~~~~~~ Testing Wolverine: ~~~~~~~~~~ ");
		// Test it lots, since there is some randomness involved in Wolverine
		// when there are options for the correct answer
		for (int i = 0; i < NUM_RANDOM_TRIALS; i++) {
			MutantMaker.index = 0;
			launcher.execute(request);

			TestExecutionSummary summary = listener.getSummary();
			if (summary.getFailures().size() > 0) {
				System.err.println("Uh Oh.  You caught Wolverine, but he's the good mutant!");
				displayFailedTestsResult(summary, System.err);
				return false;
			}
		}
		System.out.println("Wolverine passed all the tests!");
		return true;
	}

	/**
	 * Try to reveal all the mutants
	 * 
	 * @param revealerTestClass the class used to try to reveal the mutants
	 * @return true if all the bad mutants were revealed; otherwise, false
	 */
	public boolean evaluateTestsOnBadMutants(Class<?> revealerTestClass) {
		final LauncherDiscoveryRequest request = setUpTestRequest(revealerTestClass);

		final Launcher launcher = LauncherFactory.create();
		final SummaryGeneratingListener listener = new SummaryGeneratingListener();

		launcher.registerTestExecutionListeners(listener);

		int gotAway = 0;
		int caught = 0;
		MutantMaker.index = 1; // Mutant 0 is Wolverine.

		System.out.println("~~~~~~~~~~ Testing the Mutants ~~~~~~~~~~");
		for (int i = 0; i < MutantMaker.getNumMutants() - 1; i++) {
			System.out.println("\nTesting Mutant: " + MutantMaker.index);
			int index = MutantMaker.index;
			launcher.execute(request);

			TestExecutionSummary summary = listener.getSummary();
			if (summary.getTestsFailedCount() == 0) {
				System.err.println("Mutant " + index + " made it out alive!\n");
				gotAway++;
			} else {
				System.out.println("Mutant " + index + " caught with the following test cases:");
				caught++;
				if (summary.getFailures().size() > 0) {
					displayFailedTestsResult(summary, System.out);
				}
			}
		}
		System.out.println("Successfully caught " + caught + " out of " + (gotAway + caught));
		return (caught == MutantMaker.getNumMutants() - 1);
	}

	/**
	 * Create the request to execute the given test class
	 * 
	 * @param revealerTestClass - the test class to call
	 * @return
	 */
	private LauncherDiscoveryRequest setUpTestRequest(Class<?> revealerTestClass) {
		final LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
				.selectors(selectClass(revealerTestClass)).build();
		return request;
	}

	/**
	 * Display the count of and which tests failed
	 * 
	 * @param summary - the summary of the test execution
	 * @param out TODO
	 */
	private void displayFailedTestsResult(TestExecutionSummary summary, PrintStream out) {
		out.println("Test cases failed (" + summary.getTestsFailedCount() + "): ");
		for (TestExecutionSummary.Failure failure : summary.getFailures()) {
			out.println(" - " + failure.getTestIdentifier().getDisplayName());
		}
	}

	/**
	 * Displays the message and then the contents of the files
	 * 
	 * @param message  the message to display
	 * @param filename the name of the file whose contents should be displayed
	 */
	private void displayMessage(String message, String filename) {
		System.out.println("\n " + message + " \n");
		try {
			BufferedReader br = new BufferedReader(new FileReader(filename));
			String line;
			while ((line = br.readLine()) != null) {
				System.out.println(line);
			}
			br.close();
		} catch (FileNotFoundException e) {
			System.err.println("Couldn't read in ascii art file " + filename);
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("Error reading ascii art file " + filename);
			e.printStackTrace();
		}
	}

}
