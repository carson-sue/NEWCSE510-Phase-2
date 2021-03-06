package tests;

import diskmgr.*;
import global.*;

import java.io.*;
import quadrupleheap.*;
import labelheap.*;


public class QueryProgram {
	public static SystemDefs sysdef = null;
	static String dbname = null;   //Database name 
	static String Subject = null;
	static String Object = null;
	static String Predicate = null;
	static String Confidence = null;
	static int indexoption = 1;    //Index option
	static EID entityobjecqid = new EID();
	static EID entitysubjecqid = new EID();
	static EID entitypredicateid = new EID();
	static QuadrupleHeapfile UNSORTED_TRIPLES = null;
	boolean exists = false;
	public static double confidence = -99.0;
	public static int num_of_buf = 200;
	
	public static String get_sort_order()
	{
		
		switch(indexoption)
		{
		case 1:	
			return new String(" Sorting by Subject-Predicate-Object-Confidence");

		case 2:
			return new String(" Sorting by Predicate-Subject-Object-Confidence");
			
		case 3:	
			return new String(" Sorting by Subject-Confidence");

		case 4:
			return new String(" Sorting by Predicate-Confidence");

		case 5:	
			return new String(" Sorting by Object-Confidence");

		case 6:	
			return new String(" Sorting by Confidence");
			
		default:
			return new String(" Sorting by Subject-Predicate-Object-Confidence");
		}
		
	}
	
	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args)
	throws Exception 
	{
		if(args.length == 7 )   //Check if the args are DATABASENAME INDEXOPTION SUBJECTFILTER PREDICATEFILTER OBJECTFILTER CONFIDENCEFILTER NUMBUF
		{
			dbname = new String("/tmp/" + args[0]); 
			indexoption = Integer.parseInt(args[1]);
			Subject = new String(args[2]);
			Predicate = new String(args[3]);
			Object = new String(args[4]);
			Confidence = new String(args[5]);
			num_of_buf = Integer.parseInt(args[6]);
			
			if(num_of_buf < 50)
			{
				System.out.println("Num of bufs too low.. setting it to 50");
				num_of_buf = 50;
			}
			
			if(indexoption>6 || indexoption<0)
			{
				System.out.println("*** Sortoption only allowed within range: 1 to 6 ***");
				return;
			}

			if(Confidence.compareToIgnoreCase("null") != 0)
			{
				confidence = Double.parseDouble(Confidence);
			}

			File dbfile = new File(dbname); //Check if database already exist
			if(dbfile.exists())
			{
				//Database already present just open it
				sysdef = new SystemDefs(dbname,0,num_of_buf,"Clock",indexoption);
				
				System.out.println("\n"+get_sort_order());
				System.out.println("**************************************");
			}
			else
			{	
				System.out.println("*** Database does not exist ***");
				return;
			}

			Stream s = SystemDefs.JavabaseDB.openStream(dbname, indexoption, Subject, Predicate, Object, confidence);
			Quadruple t = null;
			while(s.getNext()!=null)
			{
				double confidence = t.getConfidence();
				Label subject = SystemDefs.JavabaseDB.getEntityHF().getRecord(t.getSubjectID().returnLID());
				Label object = SystemDefs.JavabaseDB.getEntityHF().getRecord(t.getObjectID().returnLID());
				Label predicate = SystemDefs.JavabaseDB.getPredHF().getRecord(t.getPredicateID().returnLID());
				System.out.printf("%20s %20s %80s %.17f\n",subject.getLabelKey(),predicate.getLabelKey(),object.getLabelKey(),confidence);
			}
			if(s!=null)
			{
				s.closeStream();
			}

		}
		else
		{
			System.out.println("*** Usage:QueryProgram DATABASENAME INDEXOPTION SUBJECTFILTER PREDICATEFILTER OBJECTFILTER CONFIDENCEFILTER NUMBUF***");
			return;
		}

		System.out.println("**************************************");
		System.out.println("Total Page Writes "+ PCounter.wcounter);
		System.out.println("Total Page Reads "+ PCounter.rcounter);
		SystemDefs.close();
	}
}
