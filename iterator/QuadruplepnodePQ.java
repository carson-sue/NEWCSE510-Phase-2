package iterator;

import global.*;

import java.io.*;

/**
 * Implements a sorted binary tree for quadruples.
 * abstract methods <code>enq</code> and <code>deq</code> are used to add 
 * or remove elements from the tree.
 */  
public abstract class QuadruplepnodePQ
{
  /** number of elements in the tree */
  protected int count;

  /** the sorting order (on subject,object,predicate,confidence) */
  protected QuadrupleOrder  sort_order;

  /**
   * class constructor, set <code>count</code> to <code>0</code>.
   */
  public QuadruplepnodePQ() { count = 0; } 

  /**
   * returns the number of elements in the tree.
   * @return number of elements in the tree.
   */
  public int length(){ return count; }

  /** 
   * tests whether the tree is empty
   * @return true if tree is empty, false otherwise
   */
  public boolean empty() { return count == 0; }
  

  /**
   * insert an quadruple element in the tree in the correct order.
   * @param item the element to be inserted
   * @exception IOException from lower layers
   * @exception QuadrupleUtilsException error in quadruple compare routines
   */
  abstract public void  Quadrupleenq(Quadruplepnode  item)
  throws IOException, UnknowAttrType, QuadrupleUtilsException;

  /**
   * removes the minimum (Ascending)
   * from the tree.
   * @return the element removed, null if the tree is empty
   */
  abstract public Quadruplepnode Quadrupledeq();
	
  /**
   * compares two elements.
   * @param a one of the element for comparison
   * @param b the other element for comparison
   * @return  <code>0</code> if the two are equal,
   *          <code>1</code> if <code>a</code> is greater,
   *         <code>-1</code> if <code>b</code> is greater
   * @exception IOException from lower layers
   * @exception QuadrupleUtilsException error in quadruple compare routines
   */
  public int QuadruplepnodeCMP(Quadruplepnode a, Quadruplepnode b) 
  throws IOException, UnknowAttrType, QuadrupleUtilsException
  {
    int ans = QuadrupleUtils.CompareQuadrupleWithQuadruple(sort_order, a.quadruple, b.quadruple);
    return ans;
  }

  /**
   * tests whether the two elements are equal.
   * @param a one of the element for comparison
   * @param b the other element for comparison
   * @return <code>true</code> if <code>a == b</code>,
   *         <code>false</code> otherwise
   * @exception IOException from lower layers
   * @exception QuadrupleUtilsException error in quadruple compare routines
   */  
  public boolean QuadruplepnodeEQ(Quadruplepnode a, Quadruplepnode b) 
  throws IOException, UnknowAttrType, QuadrupleUtilsException
  {
    return QuadruplepnodeCMP(a, b) == 0;
  }
  
}
