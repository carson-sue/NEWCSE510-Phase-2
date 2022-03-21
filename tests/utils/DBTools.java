package tests.utils;

import global.*;
import labelheap.Label;
import labelheap.LabelHeapfile;
import tripleheap.Triple;

public class DBTools {
    public static void db_stats(SystemDefs sysdef)
    {
        int reccnt = sysdef.JavabaseDB.getPredicateCnt();
        int triplecnt = sysdef.JavabaseDB.getQuadrupleCnt();
        int subjectcnt = sysdef.JavabaseDB.getSubjectCnt();
        int objectcnt = sysdef.JavabaseDB.getObjectCnt();
        int entitycnt = sysdef.JavabaseDB.getEntityCnt();

        System.out.println("Total Predicate Cnt "+ reccnt );
        System.out.println("Total Triple Count "+ triplecnt);
        System.out.println("Total Subject Count "+ subjectcnt);
        System.out.println("Total Object Count "+ objectcnt);
        System.out.println("Total Entity Count "+ entitycnt);
    }
    private static void print_triple(Triple triple, SystemDefs sysdef)
            throws  Exception
    {
        //System.out.println(triple.getSubjectID());
        LabelHeapfile l1 = sysdef.JavabaseDB.getEntityHF();
        Label subject = l1.getRecord(triple.getSubjectID().returnLID());
        LabelHeapfile l2 = sysdef.JavabaseDB.getPredHF();
        Label predicate = l2.getRecord(triple.getPredicateID().returnLID());
        LabelHeapfile l3 = sysdef.JavabaseDB.getEntityHF();
        Label object = l3.getRecord(triple.getObjectID().returnLID());
        System.out.println(subject.getLabelKey() + ":" + predicate.getLabelKey() + ":" + object.getLabelKey() + "("+ triple.getConfidence()+")");
    }
}
