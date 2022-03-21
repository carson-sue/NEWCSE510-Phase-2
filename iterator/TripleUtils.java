package iterator;

import labelheap.*;
import tripleheap.*;
import global.*;
import java.io.*;
import java.lang.*;

/**
 *some useful method when processing Tuple 
 */
public class TripleUtils
{
  
/**
   * This function compares a tuple with another tuple in respective field, and
   *  returns:
   *
   *    0        if the two are equal,
   *    1        if the triple is greater,
   *   -1        if the triple is smaller
   *
   *@param    t1        one triple.
   *@param    t2        another triple.
   *@param    triple_fld_no the field numbers in the triples to be compared.
   *@exception TupleUtilsException exception from this class
   *@return   0        if the two are equal,
   *          1        if the triple is greater,
   *         -1        if the triple is smaller                              
   */
  public static int CompareTripleWithValue(Triple t1, Triple t2, int triple_fld_no)
    throws TripleUtilsException
    {
		Label L1, L2;
		String s1, s2;
		double c1, c2;
		LID l1, l2;
		char[] c = new char[1];
		c[0] = Character.MIN_VALUE;
		LabelHeapFile hf;
    
	if(triple_fld_no == 0)		// Compare subjects
	{
	  try {
	    l1 = t1.getSubjectID().returnLID();
		l2 = t2.getSubjectID().returnLID();
		hf = SystemDefs.JavabaseDB.getEntityHandle();
	  } catch (Exception ex){
	    throw new TripleUtilsException(ex, "Exception is caught by TripleUtils.java");
	  }
	}
	else if(triple_fld_no == 1)	// Compare predicates
	{  
	  try {
	    l1 = t1.getPredicateID().returnLID();
		l2 = t2.getPredicateID().returnLID();
		hf = SystemDefs.JavabaseDB.getPredicateHandle();
	  } catch (Exception ex){
	    throw new TripleUtilsException(ex, "Exception is caught by TripleUtils.java");
	  }
	}
	else if(triple_fld_no == 2)	// Compare objects
	{  
	  try {
	    l1 = t1.getObjectID().returnLID();
		l2 = t2.getObjectID().returnLID();
		hf = SystemDefs.JavabaseDB.getEntityHandle();
	  } catch (Exception ex){
	    throw new TripleUtilsException(ex, "Exception is caught by TripleUtils.java");
	  }
	}
	else if (triple_fld_no == 3)//Compare confidence
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
	    throw new TripleUtilsException(ex, "Exception is caught by TripleUtils.java");
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
	throw new TripleUtilsException(ex, "Exception is caught by TripleUtils.java");
	}

	if (s1.compareTo(s2)>0)
		return 1;
	if (s1.compareTo(s2)<0)
		return -1;
	return 0;
	}
	
  /**
   * This function compares a triple with another triple in respective field, and
   *  returns:
   *
   *    0        if the two are equal,
   *    1        if the triple is greater,
   *   -1        if the triple is smaller,
   *
   *@param    t1        one triple.
   *@param    t2        another triple.
   *@exception TripleUtilsException exception from this class
   *@return   0        if the two are equal,
   *          1        if the triple is greater,
   *         -1        if the triple is smaller,                              
   */
  public static int CompareTripleWithTriple(TripleOrder orderType, Triple t1, Triple t2)
  throws TripleUtilsException
  {
	int comparison = -2;

	if(orderType.tripleOrder == TripleOrder.SPOC)
	{
		comparison = CompareTripleWithValue(t1, t2, 0);
		if(comparison == 0)
		{
			comparison = CompareTripleWithValue(t1, t2, 1);
			if(comparison == 0)
			{
				comparison = CompareTripleWithValue(t1, t2, 2);
				if(comparison == 0)
				{
					comparison = CompareTripleWithValue(t1, t2, 3);
				}
			}	
		}
	}
	else if(orderType.tripleOrder == TripleOrder.PSOC)
	{
		comparison = CompareTripleWithValue(t1, t2, 1);
		if(comparison == 0)
		{
			comparison = CompareTripleWithValue(t1, t2, 0);
			if(comparison == 0)
			{
				comparison = CompareTripleWithValue(t1, t2, 2);
				if(comparison == 0)
				{
					comparison = CompareTripleWithValue(t1, t2, 3);
				}
			}	
		}
	}
	else if(orderType.tripleOrder == TripleOrder.SC)
	{
		comparison = CompareTripleWithValue(t1, t2, 0);
		if(comparison == 0)
		{
			comparison = CompareTripleWithValue(t1, t2, 3);	
		}
	}
	else if(orderType.tripleOrder == TripleOrder.PC)
	{
		comparison = CompareTripleWithValue(t1, t2, 1);
		if(comparison == 1)
		{
			comparison = CompareTripleWithValue(t1, t2, 3);	
		}
	}
	else if(orderType.tripleOrder == TripleOrder.OC)
	{
		comparison = CompareTripleWithValue(t1, t2, 2);
		if(comparison == 0)
		{
			comparison = CompareTripleWithValue(t1, t2, 3);	
		}
	}
	else if(orderType.tripleOrder == TripleOrder.C)
		comparison = CompareTripleWithValue(t1, t2, 3);
	return comparison;
  }
  
  /**
   *set up a triple in specified field from a triple
   *@param value the triple to be set 
   *@param triple the given triple
   *@exception IOException some I/O fault
   *@exception TupleUtilsException exception from this class
   */  
  public static void SetValue(Triple value, Triple  triple)
  {
	  value.tripleCopy(triple); 
  }
}




