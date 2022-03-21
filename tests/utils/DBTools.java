package tests.utils;

import global.*;
import labelheap.Label;
import labelheap.LabelHeapFile;
import quadrupleheap.Quadruple;

public class DBTools {
    public static void db_stats(SystemDefs sysdef)
    {
        int reccnt = sysdef.JavabaseDB.getPredicateCnt();
        int quadruplecnt = sysdef.JavabaseDB.getQuadrupleCnt();
        int subjectcnt = sysdef.JavabaseDB.getSubjectCnt();
        int objectcnt = sysdef.JavabaseDB.getObjectCnt();
        int entitycnt = sysdef.JavabaseDB.getEntityCnt();

        System.out.println("Total Predicate Cnt "+ reccnt );
        System.out.println("Total Quadruple Count "+ quadruplecnt);
        System.out.println("Total Subject Count "+ subjectcnt);
        System.out.println("Total Object Count "+ objectcnt);
        System.out.println("Total Entity Count "+ entitycnt);
    }
    private static void print_quadruple(Quadruple quadruple, SystemDefs sysdef)
            throws  Exception
    {
        //System.out.println(quadruple.getSubjectID());
        LabelHeapFile l1 = sysdef.JavabaseDB.getEntityHF();
        Label subject = l1.getRecord(quadruple.getSubjectID().returnLID());
        LabelHeapFile l2 = sysdef.JavabaseDB.getPredHF();
        Label predicate = l2.getRecord(quadruple.getPredicateID().returnLID());
        LabelHeapFile l3 = sysdef.JavabaseDB.getEntityHF();
        Label object = l3.getRecord(quadruple.getObjectID().returnLID());
        System.out.println(subject.getLabelKey() + ":" + predicate.getLabelKey() + ":" + object.getLabelKey() + "("+ quadruple.getConfidence()+")");
    }
}
