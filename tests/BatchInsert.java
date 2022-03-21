package tests;
import diskmgr.*;
import global.*;
import java.io.*;
import tripleheap.*;
import java.lang.*;
import java.util.ArrayList;
import labelheap.*;

import tests.utils.readData;
import tests.utils.DataStructures.InfoGraph;
import tests.utils.DBTools;

public class BatchInsert{

	public static SystemDefs sysdef = null;

	private static void print_triple(Triple triple) 
	throws InvalidLabelSizeException, LHFException, LHFDiskMgrException, LHFBufMgrException, Exception 
	{
		//System.out.println(triple.getSubjectID());
		LabelHeapFile l1 = sysdef.JavabaseDB.getEntityHandle();
		Label subject = l1.getRecord(triple.getSubjectID().returnLID());
		LabelHeapFile l2 = sysdef.JavabaseDB.getPredicateHandle();
		Label predicate = l2.getRecord(triple.getPredicateID().returnLID());
		LabelHeapFile l3 = sysdef.JavabaseDB.getEntityHandle();
		Label object = l3.getRecord(triple.getObjectID().returnLID());
		System.out.println(subject.getLabelKey() + ":" + predicate.getLabelKey() + ":" + object.getLabelKey() + "("+ triple.getConfidence()+")");
	}



	public static void main(String[] args) throws Exception {
		String dataFileName = null;
		int indexOption = 0;
		if(args.length == 3){
			dataFileName = args[0];
			File file = new File(dataFileName);
			boolean exists = file.exists();
			if(!exists)
			{
				System.out.println("Data File Not found Please check the path and add a datafile and rerun the code");
				return;
			}

			indexOption = Integer.parseInt(args[1]);
			if(indexOption <= 0 || indexOption > 6)
			{
				System.out.println("Please provide an index option from 1-6");
				return;
			}
		}
		else{
			System.out.println("Please provide exactly 3 arguments -> DataFileName IndexOption DatabaseName");
		}

		String dbname = args[2];

		// initialise the DB
		File dbfile = new File(dbname);
		if(dbfile.exists()) sysdef = new SystemDefs(dbname,0,1000,"Clock",indexOption);
		else sysdef = new SystemDefs(dbname,10000,1000,"Clock",indexOption);

		// start reading data file
		readData rd = new readData(dataFileName);
		InfoGraph ig;

		EID sid = null;
		PID pid = null;
		EID oid = null;
		double confidence = 0.0;
		boolean flag = false;
		int i =0;
		while((ig = rd.readNextrecord()) != null){
//			System.out.println("before inserting subject : "+ ig.subject);
			sid = sysdef.JavabaseDB.insertEntity(ig.subject);
			//System.out.println("subject count : "+sysdef.JavabaseDB.getSubjectCnt());
//			LabelHeapFile l1 = sysdef.JavabaseDB.getEntityHandle();
//			Label subject = l1.getRecord(sid.returnLID());
//			System.out.println("inserted subject :"+subject.getLabelKey());
			pid = sysdef.JavabaseDB.insertPredicate(ig.predicate);
			oid = sysdef.JavabaseDB.insertEntity(ig.object);
			confidence = ig.confidence;
			flag = true;
			// adding Quadruples to heap file
			Triple t = new Triple();
			t.setSubjectID(sid);
			t.setPredicateID(pid);
			t.setObjectID(oid);
			t.setConfidence(confidence);
			sysdef.JavabaseDB.insertTriple(t.getTripleByteArray());
			print_triple(t);
			i++;
		}

		if(flag == false){
			System.out.println("There are no records in the datafile please add records and try again");
		}

		switch (indexOption){
			case 1 :
				System.out.println("");
			case 2:
				System.out.println("");
			case 3 :
				System.out.println("");
			case 4:
				System.out.println("");
			case 5:
				System.out.println("");
		}

		sysdef.JavabaseDB.createIndex(indexOption);
		DBTools.db_stats(sysdef);
		sysdef.close();
		System.out.println("Total Page Writes "+ PCounter.wcounter);
		System.out.println("Total Page Reads "+ PCounter.rcounter);
	}
}