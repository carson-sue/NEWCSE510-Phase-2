package tests;

import global.*;

import java.io.*;
import quadrupleheap.*;
import java.lang.*;
import labelheap.*;

public class Report
{
	public static void main(String[] args)
	{
		String dbname = null;   //Database name 
		int indexoption = 0;    //Index option
		boolean exists = false;

		if(args.length == 2 )   //Check if the args are RDFDBNAME INDEXOPTION
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
			System.out.println("*** " + dbname + " Does Not Exist ***");
			return;
		}

		try
		{
			QuadrupleHeapfile thf = SystemDefs.JavabaseDB.getTrpHandle();
			LabelHeapFile elhf = SystemDefs.JavabaseDB.getEntityHandle();
			LabelHeapFile plhf = SystemDefs.JavabaseDB.getPredicateHandle();
			System.out.println("\n\n\n******************** Report - RDF DB Statistics ******************");
			System.out.println(" RDF Database Name		: " + dbname);
			System.out.println(" DB Size 			: " + dbfile.length() + " bytes");
			System.out.println(" Page Size 			: " + SystemDefs.JavabaseDB.db_page_size() + " bytes");
			System.out.println(" Number of Pages in DB 		: " + SystemDefs.JavabaseDB.db_num_pages());
			System.out.println(" Quadruple Size 			: " + GlobalConst.RDF_TRIPLE_SIZE + " bytes");
			System.out.println(" Total Entities 		: " + SystemDefs.JavabaseDB.getEntityCnt());
			System.out.println(" Total Subjects 		: " + SystemDefs.JavabaseDB.getSubjectCnt());
			System.out.println(" Total Predicates 		: " + SystemDefs.JavabaseDB.getPredicateCnt());
			System.out.println(" Total Objects 			: " + SystemDefs.JavabaseDB.getObjectCnt());
			System.out.println(" Total Quadruples			: " + SystemDefs.JavabaseDB. getQuadrupleCnt());
			System.out.println(" Page Replacement Policy 	: Clock");
			System.out.println("\n --------- Heap Files ---------");
			System.out.println(" Quadruple File Name		: " + dbname + "/quadrupleHF");
			System.out.println(" Quadruple File Record Count	: " + thf.getRecCnt());
			System.out.println(" Entity File Name		: " + dbname + "/entityHF");
			System.out.println(" Entity File Record Count	: " + elhf.getRecCnt());
			System.out.println(" Predicate File Name		: " + dbname + "/predicateHF");
			System.out.println(" Predicate File Record Count	: " + plhf.getRecCnt());
			System.out.println(" ------------------------------");
			System.out.println("****************************************************************\n\n\n\n");
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
		return ;
	}
}

