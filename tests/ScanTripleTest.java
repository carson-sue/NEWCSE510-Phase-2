package tests;

import diskmgr.*;
import global.*;

import java.io.*;

import quadrupleheap.*;
import java.lang.*;
import labelheap.*;
//import iterator.*;

public class ScanQuadrupleTest
{
	public static void main(String[] args)
	{
		String dbname = null;   //Database name 
		int indexoption = 0;    //Index option
		boolean exists = false;

		if(args.length == 2 )   //Check if the args are DATAFILE DATABASENAME INDEXOPTION
		{
			indexoption = Integer.parseInt(args[1]);
			dbname = new String("/tmp/"+args[0]+"_"+indexoption);

			if(indexoption>5 || indexoption<0)
			{
				System.out.println("*** Indexoption only allowed within range: 1 to 5 ***");
				return;
			}
		}
		else
		{
			System.out.println("*** Usage:RDFSortTest RDFDBNAME INDEXOPTION***");
			return;
		}


		EID sid = null, oid = null;
		PID pid = null;
		Quadruple t = null;
		QID qid = null;
		SystemDefs sysdef = null;
		int counter = 0;

		File dbfile = new File(dbname); //Check if database already exsist
		if(dbfile.exists())
		{
			//Database already present just open it
			sysdef = new SystemDefs(dbname,0,300,"Clock",indexoption);
			System.out.println("*** Opening existing database ***");
		}
		else
		{	
			System.out.println("*** NO DB ***");
			return;
		}

		try
		{
			
			TScan am = new TScan(sysdef.JavabaseDB.getTrpHandle());
			QID t1 = new QID();
			Quadruple t2 = null;
			while((t2 = am.getNext(t1))!= null)
			{
				//Quadruple t2 = am.getNext(t1);
				System.out.println(t2.getConfidence());
				System.out.println(t2.getSubjectID());
				LabelHeapFile l1 = sysdef.JavabaseDB.getEntityHandle();
				Label subject = l1.getRecord(t2.getSubjectID().returnLID());
				System.out.println(subject.getLabelKey());
				LabelHeapFile l2 = sysdef.JavabaseDB.getPredicateHandle();
				Label predicate = l2.getRecord(t2.getPredicateID().returnLID());
				System.out.println(predicate.getLabelKey());
				LabelHeapFile l3 = sysdef.JavabaseDB.getEntityHandle();
				Label object = l3.getRecord(t2.getObjectID().returnLID());
				System.out.println(object.getLength());
				System.out.println(object.getLabelKey());
			}
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
		return ;
	}
}