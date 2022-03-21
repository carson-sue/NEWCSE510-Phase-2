package iterator;

import labelheap.*;
import quadrupleheap.*;
import global.*;
import java.io.*;
import java.lang.*;

/**
 *some useful method when processing Tuple 
 */
public class QuadrupleUtils
{
  
/**
   * This function compares a tuple with another tuple in respective field, and
   *  returns:
   *
   *    0        if the two are equal,
   *    1        if the quadruple is greater,
   *   -1        if the quadruple is smaller
   *
   *@param    t1        one quadruple.
   *@param    t2        another quadruple.
   *@param    quadruple_fld_no the field numbers in the quadruples to be compared.
   *@exception TupleUtilsException exception from this class
   *@return   0        if the two are equal,
   *          1        if the quadruple is greater,
   *         -1        if the quadruple is smaller                              
   */
  public static int CompareQuadrupleWithValue(Quadruple t1, Quadruple t2, int quadruple_fld_no)
    throws QuadrupleUtilsException
    {
		Label L1, L2;
		String s1, s2;
		double c1, c2;
		LID l1, l2;
		char[] c = new char[1];
		c[0] = Character.MIN_VALUE;
		LabelHeapFile hf;
    
	if(quadruple_fld_no == 0)		// Compare subjects
	{
	  try {
	    l1 = t1.getSubjectID().returnLID();
		l2 = t2.getSubjectID().returnLID();
		hf = SystemDefs.JavabaseDB.getEntityHF();
	  } catch (Exception ex){
	    throw new QuadrupleUtilsException(ex, "Exception is caught by QuadrupleUtils.java");
	  }
	}
	else if(quadruple_fld_no == 1)	// Compare predicates
	{  
	  try {
	    l1 = t1.getPredicateID().returnLID();
		l2 = t2.getPredicateID().returnLID();
		hf = SystemDefs.JavabaseDB.getPredHF();
	  } catch (Exception ex){
	    throw new QuadrupleUtilsException(ex, "Exception is caught by QuadrupleUtils.java");
	  }
	}
	else if(quadruple_fld_no == 2)	// Compare objects
	{  
	  try {
	    l1 = t1.getObjectID().returnLID();
		l2 = t2.getObjectID().returnLID();
		hf = SystemDefs.JavabaseDB.getEntityHF();
	  } catch (Exception ex){
	    throw new QuadrupleUtilsException(ex, "Exception is caught by QuadrupleUtils.java");
	  }
	}
	else if (quadruple_fld_no == 3)//Compare confidence
	{  
	  try
	  { 
		c1 = t1.getConfidence();
		c2 = t2.getConfidence();
		if (c1 <  c2) 
			return -1;
		if (c1 >  c2)
			return  1;
		return 0;
	  } catch (Exception ex){
	    throw new QuadrupleUtilsException(ex, "Exception is caught by QuadrupleUtils.java");
	  }
	}
	else
	{
		return -2;
	}

	try {
		if(l1.pageNo.pid < 0)
			s1 = new String(c);
		else {
			L1 = hf.getRecord(l1);
			s1 = L1.getLabelKey();
		}
		if(l2.pageNo.pid < 0)
			s2 = new String(c);
		else {
			L2 = hf.getRecord(l2);
			s2 = L2.getLabelKey();
		}
	} catch (Exception ex){
	throw new QuadrupleUtilsException(ex, "Exception is caught by QuadrupleUtils.java");
	}

	if (s1.compareTo(s2)>0)
		return 1;
	if (s1.compareTo(s2)<0)
		return -1;
	return 0;
	}
	
  /**
   * This function compares a quadruple with another quadruple in respective field, and
   *  returns:
   *
   *    0        if the two are equal,
   *    1        if the quadruple is greater,
   *   -1        if the quadruple is smaller,
   *
   *@param    t1        one quadruple.
   *@param    t2        another quadruple.
   *@exception QuadrupleUtilsException exception from this class
   *@return   0        if the two are equal,
   *          1        if the quadruple is greater,
   *         -1        if the quadruple is smaller,                              
   */
  public static int CompareQuadrupleWithQuadruple(QuadrupleOrder orderType, Quadruple t1, Quadruple t2)
  throws QuadrupleUtilsException
  {
	int comparison = -2;

	if(orderType.quadrupleOrder == QuadrupleOrder.SPOC)
	{
		comparison = CompareQuadrupleWithValue(t1, t2, 0);
		if(comparison == 0)
		{
			comparison = CompareQuadrupleWithValue(t1, t2, 1);
			if(comparison == 0)
			{
				comparison = CompareQuadrupleWithValue(t1, t2, 2);
				if(comparison == 0)
				{
					comparison = CompareQuadrupleWithValue(t1, t2, 3);
				}
			}	
		}
	}
	else if(orderType.quadrupleOrder == QuadrupleOrder.PSOC)
	{
		comparison = CompareQuadrupleWithValue(t1, t2, 1);
		if(comparison == 0)
		{
			comparison = CompareQuadrupleWithValue(t1, t2, 0);
			if(comparison == 0)
			{
				comparison = CompareQuadrupleWithValue(t1, t2, 2);
				if(comparison == 0)
				{
					comparison = CompareQuadrupleWithValue(t1, t2, 3);
				}
			}	
		}
	}
	else if(orderType.quadrupleOrder == QuadrupleOrder.SC)
	{
		comparison = CompareQuadrupleWithValue(t1, t2, 0);
		if(comparison == 0)
		{
			comparison = CompareQuadrupleWithValue(t1, t2, 3);	
		}
	}
	else if(orderType.quadrupleOrder == QuadrupleOrder.PC)
	{
		comparison = CompareQuadrupleWithValue(t1, t2, 1);
		if(comparison == 1)
		{
			comparison = CompareQuadrupleWithValue(t1, t2, 3);	
		}
	}
	else if(orderType.quadrupleOrder == QuadrupleOrder.OC)
	{
		comparison = CompareQuadrupleWithValue(t1, t2, 2);
		if(comparison == 0)
		{
			comparison = CompareQuadrupleWithValue(t1, t2, 3);	
		}
	}
	else if(orderType.quadrupleOrder == QuadrupleOrder.C)
		comparison = CompareQuadrupleWithValue(t1, t2, 3);
	return comparison;
  }
  
  /**
   *set up a quadruple in specified field from a quadruple
   *@param value the quadruple to be set 
   *@param quadruple the given quadruple
   *@exception IOException some I/O fault
   *@exception TupleUtilsException exception from this class
   */  
  public static void SetValue(Quadruple value, Quadruple  quadruple)
  {
	  value.quadrupleCopy(quadruple); 
  }
}



