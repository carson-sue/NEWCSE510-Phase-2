package global;

/** 
 * Enumeration class for TripleOrder
 * 
 */

public class TripleOrder {
	public static final int SPOC = 1;
	public static final int PSOC = 2;
	public static final int SC = 3;
	public static final int PC = 4;
	public static final int OC = 5;
	public static final int C = 6;

	public int tripleOrder;

	/**
	 * TripleOrder Constructor
	 * <br>
	 * A triple ordering can be defined as
	 * <ul>
	 * <li>   TripleOrder tripleOrder = new TripleOrder(TripleOrder.Confidence);
	 * </ul>
	 * and subsequently used as
	 * <ul>
	 * <li>   if (tripleOrder.tripleOrder == TripleOrder.Confidence) ....
	 * </ul>
	 *
	 * @param _tripleOrder The possible sorting orderType of the triples
	 */

	public TripleOrder (int _tripleOrder) 
	{
		tripleOrder = _tripleOrder;
	}

	public String toString() 
	{
		switch (tripleOrder) 
		{
			case SPOC:
				return "SubjectPredicateObjectConfidence";
			case PSOC:
				return "PredicateSubjectObjectConfidence";
			case SC:
				return "SubjectConfidence";
			case PC:
				return "PredicateConfidence";
			case OC:
				return "ObjectConfidence";
			case C:
				return "Confidence";
		}
		return ("Unexpected TripleOrder " + tripleOrder);
	}

}
