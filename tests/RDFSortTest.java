package tests;

import global.*;
import java.io.*;
import quadrupleheap.*;
import java.lang.*;
import labelheap.*;
import iterator.*;

public class RDFSortTest
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
			sysdef = new SystemDefs(dbname,0,500,"Clock",indexoption);
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
			QuadrupleOrder sort_order = new QuadrupleOrder(QuadrupleOrder.PredicateConfidence);
			QuadrupleSort tsort = new QuadrupleSort(am, sort_order , 200);
			int count = 0;
			Quadruple quadruple = null;
			while((quadruple = tsort.get_next()) != null)
			{
				System.out.println("Confidence--> "+quadruple.getConfidence());
				//System.out.println(quadruple.getSubjectID());
				LabelHeapFile l1 = sysdef.JavabaseDB.getEntityHandle();
				Label subject = l1.getRecord(quadruple.getSubjectID().returnLID());
				System.out.println("Subject--> "+subject.getLabelKey());
				LabelHeapFile l2 = sysdef.JavabaseDB.getPredicateHandle();
				Label predicate = l2.getRecord(quadruple.getPredicateID().returnLID());
				System.out.println("Predicate--> "+predicate.getLabelKey());
				LabelHeapFile l3 = sysdef.JavabaseDB.getEntityHandle();
				Label object = l3.getRecord(quadruple.getObjectID().returnLID());
				System.out.println("Object--> "+object.getLabelKey());
				System.out.println("*****************************");
				count++;
			}
			tsort.close();
			System.out.println("-- Count="+count +" --");
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
		System.out.println("** SORTING DONE **");
		return ;
	}
}
