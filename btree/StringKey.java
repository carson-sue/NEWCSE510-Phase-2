package btree;
import global.*;

import java.util.List;

/**  StringKey: It extends the KeyClass.
 *   It defines the string Key.
 */ 
public class StringKey extends KeyClass {

  private String key;
  private String delimiter;
  public String[] individualKeys;
  public List<Integer> types;

  public String toString(){
     return key;
  }

  /** Class constructor
   *  @param     s   the value of the string key to be set 
   */
  public StringKey(String s) {
    key = new String(s);
    delimiter = ":";
    individualKeys = key.split(delimiter);
  }

  public StringKey(String s, String delimiter) {
    key = new String(s);
    this.delimiter = delimiter;
    String[] individualKeys = key.split(delimiter);
  }

  public StringKey(String s, String delimiter, List<Integer> types) {
    key = new String(s);
    this.delimiter = delimiter;
    String[] individualKeys = key.split(delimiter);
    this.types = types;
  }

  public StringKey(String s, List<Integer>  types) {
    key = new String(s);
    this.delimiter = ":";
    String[] individualKeys = key.split(delimiter);
    this.types = types;
  }

  /** get a copy of the istring key
  *  @return the reference of the copy 
  */ 
  public String getKey() {return new String(key);}

  /** set the string key value
   */ 
  public void setKey(String s) { key=new String(s);}


  public int compareTo(StringKey key2){
    int compare = 0;
    if(this.individualKeys.length != key2.individualKeys.length){
      return -2;
    }
    else{

      if(types.size() == this.individualKeys.length) {
        for (int i = 0; i < this.individualKeys.length; i++) {
          if (AttrType.attrString == types.get(i)) {
            compare = this.individualKeys[i].compareTo(this.individualKeys[i]);
          } else if (AttrType.attrDouble == types.get(i)) {
            if (Double.parseDouble(this.individualKeys[i]) < Double.parseDouble(key2.individualKeys[i])) {
              compare = -1;
            } else if (Double.parseDouble(this.individualKeys[i]) < Double.parseDouble(key2.individualKeys[i])) {
              compare = 1;
            }
            if (compare != 0) {
              return compare;
            }
          }
        }
      }

      else{
        for (int i = 0; i < this.individualKeys.length; i++) {
          if (AttrType.attrString == types.get(i)) {
            compare = this.individualKeys[i].compareTo(this.individualKeys[i]);
          }
          if(compare != 0){
            return compare;
          }
        }
      }
      return compare;

    }


  }
}
