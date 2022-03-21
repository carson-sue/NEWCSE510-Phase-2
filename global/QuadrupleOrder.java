package global;

/** 
 * Enumeration class for QuadrupleOrder
 * 
 */

public class QuadrupleOrder {
	public static final int SPOC = 1;
	public static final int PSOC = 2;
	public static final int SC = 3;
	public static final int PC = 4;
	public static final int OC = 5;
	public static final int C = 6;

	public int quadrupleOrder;

	/**
	 * QuadrupleOrder Constructor
	 * <br>
	 * A quadruple ordering can be defined as
	 * <ul>
	 * <li>   QuadrupleOrder quadrupleOrder = new QuadrupleOrder(QuadrupleOrder.Confidence);
	 * </ul>
	 * and subsequently used as
	 * <ul>
	 * <li>   if (quadrupleOrder.quadrupleOrder == QuadrupleOrder.Confidence) ....
	 * </ul>
	 *
	 * @param _quadrupleOrder The possible sorting orderType of the quadruples
	 */

	public QuadrupleOrder (int _quadrupleOrder) 
	{
		quadrupleOrder = _quadrupleOrder;
	}

	public String toString() 
	{
		switch (quadrupleOrder) 
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
		return ("Unexpected QuadrupleOrder " + quadrupleOrder);
	}

}