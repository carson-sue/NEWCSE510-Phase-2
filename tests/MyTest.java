//package tests;
//
//import diskmgr.*;
//import global.*;
//
//import java.io.*;
//import tripleheap.*;
//import iterator.TripleSort;
//
//import java.lang.*;
//import java.util.ArrayList;
//import java.util.Vector;
//
//import labelheap.*;
//import labelheap.InvalidSlotNumberException;
//import tests.utils.readData;
//import tests.utils.DataStructures.InfoGraph;
//
//public class MyTest{
//
//    public static ArrayList<String> entities = new ArrayList<String>();
//    public static ArrayList<String> predicates = new ArrayList<String>();
//    public static ArrayList<byte[]> triples = new ArrayList<byte[]>();
//    public static SystemDefs sysdef = null;
//    public static boolean existingdb = false;
//
//    private static void print_triple(Triple triple)
//            throws InvalidSlotNumberException, InvalidLabelSizeException, LHFException, LHFDiskMgrException, LHFBufMgrException, Exception
//    {
//        //System.out.println(triple.getSubjectID());
//        LabelHeapFile l1 = sysdef.JavabaseDB.getEntityHandle();
//        Label subject = l1.getRecord(triple.getSubjectID().returnLID());
//        LabelHeapFile l2 = sysdef.JavabaseDB.getPredicateHandle();
//        Label predicate = l2.getRecord(triple.getPredicateID().returnLID());
//        LabelHeapFile l3 = sysdef.JavabaseDB.getEntityHandle();
//        Label object = l3.getRecord(triple.getObjectID().returnLID());
//        System.out.println(subject.getLabelKey() + ":" + predicate.getLabelKey() + ":" + object.getLabelKey() + "("+ triple.getConfidence()+")");
//    }
//
//
//    public static void db_stats()
//    {
//        int reccnt = sysdef.JavabaseDB.getPredicateCnt();
//        int triplecnt = sysdef.JavabaseDB.getTripleCnt();
//        int subjectcnt = sysdef.JavabaseDB.getSubjectCnt();
//        int objectcnt = sysdef.JavabaseDB.getObjectCnt();
//        int entitycnt = sysdef.JavabaseDB.getEntityCnt();
//
//        System.out.println("Total Predicate Cnt "+ reccnt );
//        System.out.println("Total Triple Count "+ triplecnt);
//        System.out.println("Total Subject Count "+ subjectcnt);
//        System.out.println("Total Object Count "+ objectcnt);
//        System.out.println("Total Entity Count "+ entitycnt);
//    }
//
//    public static void delete_test()
//    {
//
//        int i = 0;
//        boolean success = false;
//        int reccnt = 0,triplecnt = 0,subjectcnt = 0,objectcnt = 0,entitycnt = 0;
//        //NOTE: Enable the code below to test for entity deletion
//
//		/*System.out.println("Deleting first 10 entities ");
//
//		  for(i = 0; i < 10; i++)
//		  {
//		  success = sysdef.JavabaseDB.deleteEntity(entities.get(i));
//		  System.out.println("Result of deleting entity " + entities.get(i) + " : " + success);
//
//		  }
//		  System.out.println("After deletion");
//
//		  reccnt = sysdef.JavabaseDB.getPredicateCnt();
//		  triplecnt = sysdef.JavabaseDB.getTripleCnt();
//		  subjectcnt = sysdef.JavabaseDB.getSubjectCnt();
//		  objectcnt = sysdef.JavabaseDB.getObjectCnt();
//		  entitycnt = sysdef.JavabaseDB.getEntityCnt();
//
//		  System.out.println("Total Predicate Cnt "+ reccnt + "\n");
//		  System.out.println("Total Triple Count "+ triplecnt +"\n");
//		  System.out.println("Total Subject Count "+ subjectcnt +"\n");
//		  System.out.println("Total Object Count "+ objectcnt +"\n");
//		  System.out.println("Total Entity Count "+ entitycnt +"\n");*/
//
//        //NOTE: Enable the code below to test for predicate deletion
//
//		/*System.out.println("Deleting first 10 predicates ");
//
//		  for(i = 0; i < 10; i++)
//		  {
//		  success = sysdef.JavabaseDB.deletePredicate(predicates.get(i));
//		  System.out.println("Result of deleting predicate " + predicates.get(i) + " : " + success);
//
//		  }
//		  System.out.println("After deletion");
//
//		  reccnt = sysdef.JavabaseDB.getPredicateCnt();
//		  triplecnt = sysdef.JavabaseDB.getTripleCnt();
//		  subjectcnt = sysdef.JavabaseDB.getSubjectCnt();
//		  objectcnt = sysdef.JavabaseDB.getObjectCnt();
//		  entitycnt = sysdef.JavabaseDB.getEntityCnt();
//
//		  System.out.println("Total Predicate Cnt "+ reccnt + "\n");
//		  System.out.println("Total Triple Count "+ triplecnt +"\n");
//		  System.out.println("Total Subject Count "+ subjectcnt +"\n");
//		  System.out.println("Total Object Count "+ objectcnt +"\n");
//		  System.out.println("Total Entity Count "+ entitycnt +"\n");*/
//
//        //NOTE: Enable the code below to test for triple deletion
//
//        System.out.println("Deleting first 10 triples ");
//
//		/*for(i = 0; i < 10; i++)
//		  {
//		  success = sysdef.JavabaseDB.deleteTriple(triples.get(i));
//		  System.out.println("Result of deleting triple " + triples.get(i) + " : " + success);
//
//		  }
//		  System.out.println("After deletion");
//
//		  reccnt = sysdef.JavabaseDB.getPredicateCnt();
//		  triplecnt = sysdef.JavabaseDB.getTripleCnt();
//		  subjectcnt = sysdef.JavabaseDB.getSubjectCnt();
//		  objectcnt = sysdef.JavabaseDB.getObjectCnt();
//		  entitycnt = sysdef.JavabaseDB.getEntityCnt();
//
//		  System.out.println("Total Predicate Cnt "+ reccnt + "\n");
//		  System.out.println("Total Triple Count "+ triplecnt +"\n");
//		  System.out.println("Total Subject Count "+ subjectcnt +"\n");
//		  System.out.println("Total Object Count "+ objectcnt +"\n");
//		  System.out.println("Total Entity Count "+ entitycnt +"\n");*/
//
//    }
//
//    void index_tests()
//    {
//        //delete_test();
//        //sysdef.JavabaseDB.createIndex1();
//        //sysdef.JavabaseDB.createIndex2();
//        //sysdef.JavabaseDB.createIndex3();
//        //sysdef.JavabaseDB.createIndex4();
//        //sysdef.JavabaseDB.createIndex5();
//    }
//
//    public static TID insertTriple(byte[] triplePtr)
//            throws Exception
//    {
//        TID tid = null;
//        try
//        {
//            //Open Temp heap file
//            TripleHeapfile Triple_HF = sysdef.JavabaseDB.getTrpHandle();
//            tid= Triple_HF.insertTriple(triplePtr);
//
//            //System.out.println("Inserting tid : " + tid);
//        }
//        catch(Exception e)
//        {
//            System.err.println ("*** Error inserting triple record " + e);
//            e.printStackTrace();
//            Runtime.getRuntime().exit(1);
//        }
//        return tid;
//    }
//
//    public static void main(String[] args) throws Exception {
//        Vector graph;
//        graph  = new Vector();
//        boolean FAIL = false;
//        boolean status = true;
//
//
//
//
//
//        String dbname = null;   //Database name
//        int indexval = 0;    //Index option
//        String dataFileName = null; //Datafile from which to load the data
//
//        boolean exists = false;
//
//        if(args.length == 3 )   //Check if the args are DATAFILE DATABASENAME INDEXOPTION
//        {
//            dataFileName = new String(args[0]);
//            indexval = Integer.parseInt(args[1]);
//            dbname = new String(args[2]+"_"+indexval);
//
//            //Check if datafile present
//            File file = new File(dataFileName);
//            exists = file.exists();
//            if(!exists)
//            {
//                System.out.println("*** File path:"+dataFileName+" dosent exist. ***");
//                return;
//            }
//
//            if(indexval>5 || indexval<0)
//            {
//                System.out.println("*** Indexoption only allowed within range: 1 to 5 ***");
//                return;
//            }
//
//        }
//        else
//        {
//            System.out.println("*** Usage:BatchInsert DATAFILE INDEXOPTION RDFDBNAME ***");
//            return;
//        }
//
//        readData reader = new readData();
//        ArrayList<InfoGraph> data = reader.readRows(dataFileName);
//        // readRows("./data/test.txt");
//        // for i in range
//        for(int i = 0; i < data.size(); i++){
//            graph.addElement(data.get(i));
//        }
//
//        EID sid = null, oid = null;
//        PID pid = null;
//        Triple t = null;
//        TID tid = null;
//
//
//        File dbfile = new File(dbname); //Check if database already exsist
//        if(dbfile.exists())
//        {
//            //Database already present just open it
//            sysdef = new SystemDefs(dbname,0,1000,"Clock",indexval);
//            //System.out.println("*** Opening existing database ***");
//            existingdb = true;
//        }
//        else
//        {
//            //Create new database
//            sysdef = new SystemDefs(dbname,10000,1000,"Clock",indexval);
//            //System.out.println("*** Creating existing database ***");
//        }
//
//        try
//        {
////            FileInputStream fstream = new FileInputStream(datafile);
////            // Get the object of DataInputStream
////            DataInputStream in = new DataInputStream(fstream);
////            BufferedReader br = new BufferedReader(new InputStreamReader(in));
////            String strLine;
////            float confidence = (float) 0.0;
//            //Read File Line By Line
//
//
//
//            ///   creating Triple
//            AttrType [] Stypes = new AttrType[4];
//            Stypes[0] = new AttrType (AttrType.attrString);
//            Stypes[1] = new AttrType (AttrType.attrString);
//            Stypes[2] = new AttrType (AttrType.attrString);
//            Stypes[3] = new AttrType (AttrType.attrReal);
//
//            //SOS
//            short [] Ssizes = new short [3];
//            Ssizes[0] = 100; //first elt. is 30
//            Ssizes[1] = 100;
//            Ssizes[2] = 100;
//            Triple q = new Triple();
//            try {
//                q.setHdr((short)4,Stypes, Ssizes);
//            }
//            catch (Exception e) {
//                System.err.println(e);
//                // System.err.println("*** error in quadraple.setHdr() ***");
//                status = FAIL;
//                e.printStackTrace();
//            }
//
//            int size = q.size();
//
//            // inserting the quadraple into file "sailors"
//            TID             qid;
//            TripleHeapfile        f = null;
//            try {
//                f = new TripleHeapfile("graph.in");
//            }
//            catch (Exception e) {
//                // System.err.println("*** error in Heapfile constructor ***");
//                status = FAIL;
//                e.printStackTrace();
//            }
//            q = new Triple();
//            try {
//                q.setHdr((short) 4,  Stypes, Ssizes);
//            }
//            catch (Exception e) {
//                // System.err.println("*** error in quadraple.setHdr() ***");
//                status = FAIL;
//                e.printStackTrace();
//            }
//
//
//            if (exists == false  || true){
//                for (int i=0; i<graph.size(); i++) {
//                    try {
//                        q.setStrFld(1, ((InfoGraph)graph.elementAt(i)).subject);
//                        q.setStrFld(2, ((InfoGraph)graph.elementAt(i)).predicate);
//                        q.setStrFld(3, ((InfoGraph)graph.elementAt(i)).object);
//                        q.setFloFld(4, (float)((InfoGraph)graph.elementAt(i)).confidence);
//                    }
//                    catch (Exception e) {
//                        // System.err.println("*** Heapfile error in quadraple.setStrFld() ***");
//                        status = FAIL;
//                        e.printStackTrace();
//                    }
//                    try
//                    {
//                        qid = insertTriple(q.getTripleByteArray());
//                    }
//                    catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//
//            }
//
//            if(existingdb == true)
//            {
//                try
//                {
//                    TScan am = new TScan(sysdef.JavabaseDB.getTrpHandle());
//                    TID t1 = new TID();
//                    Triple t2 = null;
//                    while((t2 = am.getNext(t1))!= null)
//                    {
//                        System.out.print("##############Scanning earlier records and deleting########");
//                        print_triple(t2);
//                        insertTriple(t2.getTripleByteArray());
//                        sysdef.JavabaseDB.deleteTriple(t2.getTripleByteArray());
//                    }
//                }
//                catch(Exception e)
//                {
//                    sort_temporary_heap_file(dbname,indexval);
//                }
//            }
//            else
//            {
//                sort_temporary_heap_file(dbname,indexval);
//
//            }
//        }
//        catch(Exception e)
//        {
//            System.out.println("BATCHINSERT ERROR :: " + e);
//        }
//        System.out.println("-------------------------------------");
//        System.out.println(" INDEX OPTIONS: ");
//        System.out.println(" 1. BTree Index file on confidence: ");
//        System.out.println(" 2. BTree Index file on subject and confidence: ");
//        System.out.println(" 3. BTree Index file on object and confidence: ");
//        System.out.println(" 4. BTree Index file on predicate and confidence: ");
//        System.out.println(" 5. BTree Index file on subject: ");
//        System.out.println("-------------------------------------");
//        System.out.println(" ||  CREATING INDEX WITH OPTION :(" + indexval + ")  ||");
//        System.out.println("-------------------------------------");
//        sysdef.JavabaseDB.createIndex(indexval);
//
//        db_stats();
//        sysdef.close();
//
//        System.out.println("Total Page Writes "+ PCounter.wcounter);
//        System.out.println("Total Page Reads "+ PCounter.rcounter);
//
//        System.out.println(" $$$$$$$$$$$$$$ BATCH INSERT PROGRAM $$$$$$$$$$$$$$");
//        return ;
//    }
//
//    private static void sort_temporary_heap_file(String dbname,int indexoption)
//    {
//        try
//        {
//            //sysdef = new SystemDefs(dbname,0,800,"Clock",indexoption);
//            System.out.println("*** Opening existing database ***");
//            TScan am = new TScan(sysdef.JavabaseDB.getTEMP_Triple_HF());
//            TripleOrder sort_order = null;
//            if(indexoption ==1) //SORT ON CONFIDENCE
//            {
//                sort_order = new TripleOrder(TripleOrder.Confidence);
//            }
//            else if(indexoption == 2)
//            {
//                sort_order = new TripleOrder(TripleOrder.SubjectConfidence);
//            }
//            else if(indexoption == 3)
//            {
//                sort_order = new TripleOrder(TripleOrder.ObjectConfidence);
//            }
//            else if(indexoption == 4)
//            {
//                sort_order = new TripleOrder(TripleOrder.PredicateConfidence);
//            }
//            else if(indexoption == 5)
//            {
//                sort_order = new TripleOrder(TripleOrder.SubjectPredicateObjectConfidence);
//            }
//
//            TripleSort tsort = new TripleSort(am, sort_order , 200);
//
//            Triple triple = null;
//            while((triple = tsort.get_next()) != null)
//            {
//                print_triple(triple);
//                sysdef.JavabaseDB.insertTriple(triple.getTripleByteArray());
//                System.out.println("*****************************");
//            }
//            tsort.close();
//
//        }
//        catch(Exception e)
//        {
//            System.out.println("TEMPORARY SORTING ERROR : INDEX OPTION ->" + indexoption );
//        }
//    }
//
//}
