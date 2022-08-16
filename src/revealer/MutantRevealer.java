package revealer;

import static org.junit.Assert.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import mutants.Mutant;
import testthetests.MutantMaker;

/**
 * Contains the test cases that will reveal the mutants. Creates a mutant that
 * will be used to run the tests against.
 * 
 * Do NOT run this class directly.
 * 
 * @author Shannon Duvall original concept
 * @author Sara Sprenkle refactoring, documentation
 */
public class MutantRevealer {

	// in typical JUnit classes, this variable would _not_ be static
	private static Mutant mutant;

	@BeforeAll
	public static void getMutant() {
		// gets the mutant we're going to try to reveal
		mutant = MutantMaker.getNextMutant();
	}

	@Test
	public void testMutant() {
		// just to get you started, here's one test
		String[] words = { "a", "ab", "abc" };
		assertEquals(mutant.thirdShortest(words), "abc");
	}

	@Test
	public void testThrowsIllegalArgumentException() {
		String[] words = { "a" };
		assertThrows(IllegalArgumentException.class, () -> {
			mutant.thirdShortest(words);
		});
	}

}
