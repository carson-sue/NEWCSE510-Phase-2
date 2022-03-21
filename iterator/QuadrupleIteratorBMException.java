package iterator;

import chainexception.*;
import java.lang.*;

public class QuadrupleIteratorBMException extends ChainException 
{
  public QuadrupleIteratorBMException(String s){super(null,s);}
  public QuadrupleIteratorBMException(Exception prev, String s){ super(prev,s);}
}
