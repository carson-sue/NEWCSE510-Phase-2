//package tests;
//
//import diskmgr.*;
//import global.*;
//
//import java.io.*;
//import quadrupleheap.*;
//import iterator.QuadrupleSort;
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
//    public static ArrayList<byte[]> quadruples = new ArrayList<byte[]>();
//    public static SystemDefs sysdef = null;
//    public static boolean existingdb = false;
//
//    private static void print_quadruple(Quadruple quadruple)
//            throws InvalidSlotNumberException, InvalidLabelSizeException, LHFException, LHFDiskMgrException, LHFBufMgrException, Exception
//    {
//        //System.out.println(quadruple.getSubjectID());
//        LabelHeapFile l1 = sysdef.JavabaseDB.getEntityHandle();
//        Label subject = l1.getRecord(quadruple.getSubjectID().returnLID());
//        LabelHeapFile l2 = sysdef.JavabaseDB.getPredicateHandle();
//        Label predicate = l2.getRecord(quadruple.getPredicateID().returnLID());
//        LabelHeapFile l3 = sysdef.JavabaseDB.getEntityHandle();
//        Label object = l3.getRecord(quadruple.getObjectID().returnLID());
//        System.out.println(subject.getLabelKey() + ":" + predicate.getLabelKey() + ":" + object.getLabelKey() + "("+ quadruple.getConfidence()+")");
//    }
//
//
//    public static void db_stats()
//    {
//        int reccnt = sysdef.JavabaseDB.getPredicateCnt();
//        int quadruplecnt = sysdef.JavabaseDB.getQuadrupleCnt();
//        int subjectcnt = sysdef.JavabaseDB.getSubjectCnt();
//        int objectcnt = sysdef.JavabaseDB.getObjectCnt();
//        int entitycnt = sysdef.JavabaseDB.getEntityCnt();
//
//        System.out.println("Total Predicate Cnt "+ reccnt );
//        System.out.println("Total Quadruple Count "+ quadruplecnt);
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
//        int reccnt = 0,quadruplecnt = 0,subjectcnt = 0,objectcnt = 0,entitycnt = 0;
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
//		  quadruplecnt = sysdef.JavabaseDB.getQuadrupleCnt();
//		  subjectcnt = sysdef.JavabaseDB.getSubjectCnt();
//		  objectcnt = sysdef.JavabaseDB.getObjectCnt();
//		  entitycnt = sysdef.JavabaseDB.getEntityCnt();
//
//		  System.out.println("Total Predicate Cnt "+ reccnt + "\n");
//		  System.out.println("Total Quadruple Count "+ quadruplecnt +"\n");
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
//		  quadruplecnt = sysdef.JavabaseDB.getQuadrupleCnt();
//		  subjectcnt = sysdef.JavabaseDB.getSubjectCnt();
//		  objectcnt = sysdef.JavabaseDB.getObjectCnt();
//		  entitycnt = sysdef.JavabaseDB.getEntityCnt();
//
//		  System.out.println("Total Predicate Cnt "+ reccnt + "\n");
//		  System.out.println("Total Quadruple Count "+ quadruplecnt +"\n");
//		  System.out.println("Total Subject Count "+ subjectcnt +"\n");
//		  System.out.println("Total Object Count "+ objectcnt +"\n");
//		  System.out.println("Total Entity Count "+ entitycnt +"\n");*/
//
//        //NOTE: Enable the code below to test for quadruple deletion
//
//        System.out.println("Deleting first 10 quadruples ");
//
//		/*for(i = 0; i < 10; i++)
//		  {
//		  success = sysdef.JavabaseDB.deleteQuadruple(quadruples.get(i));
//		  System.out.println("Result of deleting quadruple " + quadruples.get(i) + " : " + success);
//
//		  }
//		  System.out.println("After deletion");
//
//		  reccnt = sysdef.JavabaseDB.getPredicateCnt();
//		  quadruplecnt = sysdef.JavabaseDB.getQuadrupleCnt();
//		  subjectcnt = sysdef.JavabaseDB.getSubjectCnt();
//		  objectcnt = sysdef.JavabaseDB.getObjectCnt();
//		  entitycnt = sysdef.JavabaseDB.getEntityCnt();
//
//		  System.out.println("Total Predicate Cnt "+ reccnt + "\n");
//		  System.out.println("Total Quadruple Count "+ quadruplecnt +"\n");
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
//    public static QID insertQuadruple(byte[] quadruplePtr)
//            throws Exception
//    {
//        QID qid = null;
//        try
//        {
//            //Open Temp heap file
//            QuadrupleHeapfile Quadruple_HF = sysdef.JavabaseDB.getTrpHandle();
//            qid= Quadruple_HF.insertQuadruple(quadruplePtr);
//
//            //System.out.println("Inserting qid : " + qid);
//        }
//        catch(Exception e)
//        {
//            System.err.println ("*** Error inserting quadruple record " + e);
//            e.printStackTrace();
//            Runtime.getRuntime().exit(1);
//        }
//        return qid;
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
//        Quadruple t = null;
//        QID qid = null;
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
//            ///   creating Quadruple
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
//            Quadruple q = new Quadruple();
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
//            QID             qid;
//            QuadrupleHeapfile        f = null;
//            try {
//                f = new QuadrupleHeapfile("graph.in");
//            }
//            catch (Exception e) {
//                // System.err.println("*** error in Heapfile constructor ***");
//                status = FAIL;
//                e.printStackTrace();
//            }
//            q = new Quadruple();
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
//                        qid = insertQuadruple(q.getQuadrupleByteArray());
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
//                    QID t1 = new QID();
//                    Quadruple t2 = null;
//                    while((t2 = am.getNext(t1))!= null)
//                    {
//                        System.out.print("##############Scanning earlier records and deleting########");
//                        print_quadruple(t2);
//                        insertQuadruple(t2.getQuadrupleByteArray());
//                        sysdef.JavabaseDB.deleteQuadruple(t2.getQuadrupleByteArray());
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
//            TScan am = new TScan(sysdef.JavabaseDB.getTEMP_Quadruple_HF());
//            QuadrupleOrder sort_order = null;
//            if(indexoption ==1) //SORT ON CONFIDENCE
//            {
//                sort_order = new QuadrupleOrder(QuadrupleOrder.Confidence);
//            }
//            else if(indexoption == 2)
//            {
//                sort_order = new QuadrupleOrder(QuadrupleOrder.SubjectConfidence);
//            }
//            else if(indexoption == 3)
//            {
//                sort_order = new QuadrupleOrder(QuadrupleOrder.ObjectConfidence);
//            }
//            else if(indexoption == 4)
//            {
//                sort_order = new QuadrupleOrder(QuadrupleOrder.PredicateConfidence);
//            }
//            else if(indexoption == 5)
//            {
//                sort_order = new QuadrupleOrder(QuadrupleOrder.SubjectPredicateObjectConfidence);
//            }
//
//            QuadrupleSort tsort = new QuadrupleSort(am, sort_order , 200);
//
//            Quadruple quadruple = null;
//            while((quadruple = tsort.get_next()) != null)
//            {
//                print_quadruple(quadruple);
//                sysdef.JavabaseDB.insertQuadruple(quadruple.getQuadrupleByteArray());
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
