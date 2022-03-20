package global;

import bufmgr.*;
import diskmgr.*;
import catalog.*;

import java.io.IOException;

public class SystemDefs {
	public static BufMgr	JavabaseBM;
	public static rdfDB	JavabaseDB;
	public static Catalog	JavabaseCatalog;

	public static String  JavabaseDBName;
	public static String  JavabaseLogName;
	public static boolean MINIBASE_RESTART_FLAG = false;
	public static String	MINIBASE_DBNAME;

	public SystemDefs (){};

	public SystemDefs(String rdfdbname, int num_pgs, int bufpoolsize,String replacement_policy,int index)
	{
		int logsize;

		String real_logname = new String(rdfdbname);
		String real_dbname = new String(rdfdbname);

		if (num_pgs == 0) {
			logsize = 500;
		}
		else {
			logsize = 3*num_pgs;
		}

		if (replacement_policy == null) {
			replacement_policy = new String("Clock");
		}

		init_rdfDB(real_dbname,real_logname, num_pgs, logsize, bufpoolsize, replacement_policy,index);
	}




	public void init_rdfDB( String dbname, String logname,int num_pgs, int maxlogsize,int bufpoolsize, String replacement_policy, int index)
	{

		boolean status = true;
		JavabaseBM = null;
		JavabaseDB = null;
		JavabaseDBName = null;
		JavabaseLogName = null;
		JavabaseCatalog = null;

		try {
			JavabaseBM = new BufMgr(bufpoolsize, replacement_policy);
			JavabaseDB = new rdfDB();
			/*
			   JavabaseCatalog = new Catalog(); 
			 */
		}
		catch (Exception e) {
			System.err.println (""+e);
			e.printStackTrace();
			Runtime.getRuntime().exit(1);
		}

		JavabaseDBName = dbname;
		JavabaseLogName = logname;

		// create or open the DB

		if ((MINIBASE_RESTART_FLAG)||(num_pgs == 0)){//open an existing database
			try {
				JavabaseDB.openrdfDB(dbname,index); //open exisiting rdf database
			}
			catch (Exception e) {
				System.err.println (""+e);
				e.printStackTrace();
				Runtime.getRuntime().exit(1);
			}
		} 
		else {
			try {
				JavabaseDB.openrdfDB(dbname, num_pgs, index); //create a new rdf database
				JavabaseBM.flushAllPages();
			}
			catch (Exception e) {
				System.err.println (""+e);
				e.printStackTrace();
				Runtime.getRuntime().exit(1);
			}
		}
	}

	public static void close() throws PageUnpinnedException, PagePinnedException, PageNotFoundException, HashOperationException, BufMgrException, IOException, HashEntryNotFoundException, InvalidFrameNumberException, ReplacerException {
		// closing all the
		JavabaseDB.rdfcloseDB();
		JavabaseBM.flushAllPages();
		JavabaseDB.closeDB();

	}
}  
