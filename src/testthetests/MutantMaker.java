package testthetests;

import mutants.Mutant;
import mutants.Wolverine;

/**
 * Generates the mutants used to evaluate the test suite (in Killer.java)
 * 
 * @author Shannon Duvall - original concept
 * @author Sara Sprenkle
 */
public class MutantMaker {
	private static final String MUTANT_CLASS_NAME = "mutants.Mutant";
	private static Mutant[] mutants;
	private static final int numMutants = 16; // This # is 1 + number of baddies
	public static int index = 0;

	/**
	 * Instantiate each type of mutant
	 */
	public static void initMutantMaker() {
		mutants = new Mutant[numMutants];
		mutants[0] = new Wolverine();
		for (int i = 1; i < numMutants; i++) {
			Class<?> mutantClass;
			try {
				mutantClass = Class.forName(MUTANT_CLASS_NAME + i);
				mutants[i] = (Mutant) mutantClass.getDeclaredConstructor().newInstance();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Cycles through the mutants, returning the next mutant
	 * 
	 * @return the next mutant
	 */
	public static Mutant getNextMutant() {
		Mutant mutant = mutants[index];
		index = (index + 1) % mutants.length;
		return mutant;
	}

	/**
	 *
	 * Get the current mutant
	 * 
	 * @return the current mutant
	 */
	public static Mutant getMutant() {
		Mutant mutant = mutants[index];
		return mutant;
	}

	/**
	 * Return the total number of mutants (the good and the bad)
	 * 
	 * @return the total number of mutants (the good and the bad)
	 */
	public static int getNumMutants() {
		return mutants.length;
	}
}
