package diskmgr;

import labelheap.*;
import btree.*;
import global.*;
import quadrupleheap.*;
import iterator.*;


public class Stream{
	private static String dbName;
	private static int sortoption = 1;    //Index option
	private static EID entityobjecqid = new EID();
	private static EID entitysubjecqid = new EID();
	private static EID entitypredicateid = new EID();
	private static QuadrupleHeapfile Result_HF = null;
	static boolean subject_null = false;
	static boolean object_null = false;
	static boolean predicate_null = false;
	static boolean confidence_null = false;
	private TScan Titer = null;
	private QuadrupleSort tsort = null;
	private int SORT_TRIPLE_NUM_PAGES = 16;
	private boolean scan_entire_heapfile = false;
	private String _subjectFilter;
	private String _predicateFilter;
	private String _objectFilter;
	private double _confidenceFilter;
	private boolean scan_on_BT = false;
	private Quadruple scan_on_BT_quadruple = null;

	public QuadrupleOrder get_sort_order()
	{
		QuadrupleOrder sort_order = null;

		switch(sortoption)
		{
			case 1:
				sort_order = new QuadrupleOrder(QuadrupleOrder.SPOC);
				break;

			case 2:
				sort_order = new QuadrupleOrder(QuadrupleOrder.PSOC);
				break;

			case 3:
				sort_order = new QuadrupleOrder(QuadrupleOrder.SC);
				break;

			case 4:
				sort_order = new QuadrupleOrder(QuadrupleOrder.PC);
				break;

			case 5:
				sort_order = new QuadrupleOrder(QuadrupleOrder.OC);
				break;

			case 6:
				sort_order = new QuadrupleOrder(QuadrupleOrder.C);
				break;
		}
		return sort_order;
	}

	//Retrieves next quadruple in stream
	public Quadruple getNext() throws Exception
	{
		try
		{
			Quadruple quadruple = null;
			if(scan_on_BT)
			{
				if(scan_on_BT_quadruple!=null)
				{
					Quadruple temp = new Quadruple(scan_on_BT_quadruple);
					scan_on_BT_quadruple = null;
					return temp;
				}
			}
			else
			{
				while((quadruple = tsort.get_next()) != null)
				{
					if(scan_entire_heapfile == false)
					{
						return quadruple;
					}
					else
					{
						boolean result = true;
						double confidence = quadruple.getConfidence();
						Label subject = SystemDefs.JavabaseDB.getEntityHF().getRecord(quadruple.getSubjectID().returnLID());
						Label object = SystemDefs.JavabaseDB.getEntityHF().getRecord(quadruple.getObjectID().returnLID());
						Label predicate = SystemDefs.JavabaseDB.getPredHF().getRecord(quadruple.getPredicateID().returnLID());

						if(!subject_null)
						{
							result = result & (_subjectFilter.compareTo(subject.getLabelKey()) == 0);
						}
						if(!object_null)
						{
							result = result & (_objectFilter.compareTo(object.getLabelKey()) == 0 );
						}
						if(!predicate_null)
						{
							result = result & (_predicateFilter.compareTo(predicate.getLabelKey())==0);
						}
						if(!confidence_null)
						{
							result = result & (confidence >= _confidenceFilter);
						}
						if(result)
						{
							return quadruple;
						}
					}
				}
			}
		}
		catch(Exception e)
		{
			System.out.println("Error in Stream get next\n"+e);
		}
		return null;
	}


	//Closes the stream
	public void closeStream()
	{
		try
		{
			if(Titer!=null)
			{
				Titer.closescan();
			}
			if(Result_HF != null && Result_HF != SystemDefs.JavabaseDB.getQuadHF())
			{
				Result_HF.deleteFile();
			}
			if(tsort!=null)
			{
				tsort.close(); //Close the stream iterator
			}
		}
		catch(Exception e)
		{
			System.out.println("Error closing Stream"+e);
		}
	}

	//Constructor
	public Stream(String rdfdbname, int orderType, String subjectFilter,String predicateFilter, String objectFilter, double confidenceFilter)
			throws Exception
	{
		sortoption = orderType;
		dbName = rdfdbname;

		if(subjectFilter.compareToIgnoreCase("null") == 0)
		{
			subject_null = true;
		}
		if(predicateFilter.compareToIgnoreCase("null") == 0)
		{
			predicate_null = true;
		}
		if(objectFilter.compareToIgnoreCase("null") == 0)
		{
			object_null = true;
		}
		if(confidenceFilter == -99.0)
		{
			confidence_null = true;
		}

		String indexoption = rdfdbname.substring(rdfdbname.lastIndexOf('_') + 1);

		if(!subject_null && !predicate_null && !object_null && !confidence_null)
		{
			ScanBTReeIndex(subjectFilter,predicateFilter,objectFilter,confidenceFilter);
			scan_on_BT = true;
		}
		else
		{
			if(Integer.parseInt(indexoption) == 1 && !confidence_null)
			{
				ScanBTConfidenceIndex(subjectFilter,predicateFilter,objectFilter,confidenceFilter);
			}
			else if(Integer.parseInt(indexoption) == 2 && !subject_null && !confidence_null)
			{
				streamBySubjectConfidence(subjectFilter,predicateFilter,objectFilter,confidenceFilter);
			}
			else if(Integer.parseInt(indexoption) == 3 && !object_null && !confidence_null)
			{
				streamByObjectConfidence(subjectFilter,predicateFilter,objectFilter,confidenceFilter);
			}
			else if(Integer.parseInt(indexoption) == 4 && !predicate_null && !confidence_null)
			{
				streamByPredicateConfidence(subjectFilter,predicateFilter,objectFilter,confidenceFilter);
			}
			else if(Integer.parseInt(indexoption) == 5 && !subject_null)
			{
				ScanBTSubjectIndex(subjectFilter,predicateFilter,objectFilter,confidenceFilter);
			}
			else
			{
				scan_entire_heapfile = true;
				ScanEntireHeapFile(subjectFilter,predicateFilter,objectFilter,confidenceFilter);
			}

			//Sort the results
			Titer = new TScan(Result_HF);
			QuadrupleOrder sort_order = get_sort_order();
			try
			{
				tsort = new QuadrupleSort(Titer, sort_order , SORT_TRIPLE_NUM_PAGES);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	public static LID GetEID(String filter) throws GetFileEntryException, PinPageException, ConstructPageException
	{
		LID eid = null;
		LabelBTreeFile Entity_BTree = new LabelBTreeFile(dbName+"/entityBT");
		KeyClass low_key = new StringKey(filter);
		KeyClass high_key = new StringKey(filter);
		KeyDataEntry entry = null;
		try
		{
			//Start Scanning BTree to check if subject is present
			LabelBTFileScan scan = Entity_BTree.new_scan(low_key,high_key);
			entry = scan.get_next();
			if(entry!=null)
			{
				eid =  ((LabelLeafData)entry.data).getData();
				scan.DestroyBTreeFileScan();
			}
			else
			{
				System.out.println("No Quadruple found with given criteria");
			}
			Entity_BTree.close();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}

		return eid;
	}

	public static LID GetPredicate(String predicateFilter)
	{
		LID predicateid = null;
		///Get the entity key from the Predicate BTREE file
		LabelBTreeFile Predicate_BTree = null;
		try
		{
			Predicate_BTree = new LabelBTreeFile(dbName+"/predicateBT");
			KeyClass low_key = new StringKey(predicateFilter);
			KeyClass high_key = new StringKey(predicateFilter);
			KeyDataEntry entry = null;

			//Start Scanning Btree to check if subject is present
			LabelBTFileScan scan1 = Predicate_BTree.new_scan(low_key,high_key);
			entry = scan1.get_next();
			if(entry!=null)
			{
				//return already existing EID ( convert lid to EID)
				predicateid =  ((LabelLeafData)entry.data).getData();
			}
			else
			{
				System.err.println("Predicate not present");
			}
			scan1.DestroyBTreeFileScan();
			Predicate_BTree.close();
		}
		catch(Exception e)
		{
			System.err.println("Predicate not present");

		}
		return predicateid;
	}


	public boolean ScanBTReeIndex(String Subject,String Predicate,String Object,double Confidence)
			throws Exception
	{
		if(GetEID(Subject) != null)
		{
			entitysubjecqid = GetEID(Subject).returnEID();
		}
		else
		{
			System.out.println("No quadruple found");
			return false;
		}

		///Get the object key from the Entity BTREE file
		if(GetEID(Object)!=null)
		{
			EID entityobjecqid = GetEID(Object).returnEID();
		}
		else
		{
			System.out.println("No quadruple found");
			return false;
		}

		if(GetPredicate(Predicate) != null)
		{
			entitypredicateid = GetPredicate(Predicate).returnEID();
		}
		else
		{
			//System.out.println("No quadruple found");
			return false;
		}

		///Get the entity key from the Predicate BTREE file
		//Compute the composite key for the Quadruple BTREE search
		String key =  entitysubjecqid.slotNo + ":" +entitysubjecqid.pageNo.pid + ":" + entitypredicateid.slotNo + ":" + entitypredicateid.pageNo.pid + ":"
				+ entityobjecqid.slotNo + ":" + entityobjecqid.pageNo.pid;
		KeyClass low_key = new StringKey(key);
		KeyClass high_key = new StringKey(key);
		KeyDataEntry entry = null;
		Label subject = null, object = null, predicate = null;

		//Start Scanning BTree to check if  predicate already present
		QuadrupleHeapfile Quadruple_HF = SystemDefs.JavabaseDB.getQuadHF();
		QuadrupleBTreeFile Quadruple_Btree = SystemDefs.JavabaseDB.getQuadBT();
		LabelHeapFile Entity_HF = SystemDefs.JavabaseDB.getEntityHF();
		LabelHeapFile Predicate_HF = SystemDefs.JavabaseDB.getPredHF();

		QuadrupleBTFileScan scan = Quadruple_Btree.new_scan(low_key,high_key);
		entry = scan.get_next();
		if(entry != null)
		{
			if(key.compareTo(((StringKey)(entry.key)).getKey()) == 0)
			{
				//return already existing QID
				QID quadrupleid = ((QuadrupleLeafData)(entry.data)).getData();
				Quadruple record = Quadruple_HF.getRecord(quadrupleid);
				double orig_confidence = record.getConfidence();
				if(orig_confidence >= Confidence)
				{
					scan_on_BT_quadruple = new Quadruple(record);
				}
			}
		}
		scan.DestroyBTreeFileScan();
		Quadruple_Btree.close();
		return true;
	}

	private void ScanBTConfidenceIndex(String subjectFilter,String predicateFilter, String objectFilter, double confidenceFilter)
			throws Exception
	{
		boolean result = true;
		KeyDataEntry entry1 = null;
		QID quadrupleid = null;
		Label subject = null, object = null, predicate = null;
		Quadruple record = null;

		QuadrupleBTreeFile Entity_TTree = SystemDefs.JavabaseDB.getIndexBT();
		QuadrupleHeapfile Quadruple_HF = SystemDefs.JavabaseDB.getQuadHF();
		LabelHeapFile Entity_HF = SystemDefs.JavabaseDB.getEntityHF();
		LabelHeapFile Predicate_HF = SystemDefs.JavabaseDB.getPredHF();

		java.util.Date date= new java.util.Date();
		Result_HF = new QuadrupleHeapfile(Long.toString(date.getTime()));

		KeyClass low_key1 = new StringKey(Double.toString(confidenceFilter));
		QuadrupleBTFileScan scan = Entity_TTree.new_scan(low_key1,null);

		while((entry1 = scan.get_next())!= null)
		{
			result = true;

			quadrupleid =  ((QuadrupleLeafData)entry1.data).getData();
			record = Quadruple_HF.getRecord(quadrupleid);
			subject = Entity_HF.getRecord(record.getSubjectID().returnLID());
			object = Entity_HF.getRecord(record.getObjectID().returnLID());
			predicate = Predicate_HF.getRecord(record.getPredicateID().returnLID());

			if(!subject_null)
			{
				result = result & (subjectFilter.compareTo(subject.getLabelKey()) == 0);
			}
			if(!object_null)
			{
				result = result & (objectFilter.compareTo(object.getLabelKey()) == 0 );
			}
			if(!predicate_null)
			{
				result = result & (predicateFilter.compareTo(predicate.getLabelKey())==0);
			}
			if(!confidence_null)
			{
				result = result & (record.getConfidence() >= confidenceFilter);
			}

			if(result)
			{
				//System.out.println("Subject::" + subject.getLabelKey()+ "\tPredicate::"+predicate.getLabelKey() + "\tObject::"+object.getLabelKey() );
				Result_HF.insertQuadruple(record.returnQuadrupleByteArray());
			}
		}
		scan.DestroyBTreeFileScan();
		Entity_TTree.close();
	}


	private void streamBySubjectConfidence(String subjectFilter,String predicateFilter,String objectFilter,double confidenceFilter)
	{
		try
		{
			QuadrupleBTreeFile Quadruple_BTreeIndex = SystemDefs.JavabaseDB.getIndexBT();

			QuadrupleHeapfile Quadruple_HF = SystemDefs.JavabaseDB.getQuadHF();
			LabelHeapFile Entity_HF = SystemDefs.JavabaseDB.getEntityHF();
			LabelHeapFile Predicate_HF = SystemDefs.JavabaseDB.getPredHF();
			java.util.Date date= new java.util.Date();
			Result_HF = new QuadrupleHeapfile(Long.toString(date.getTime()));

			KeyClass low_key = new StringKey(subjectFilter+":"+confidenceFilter);
			KeyDataEntry entry = null;
			double orig_confidence = 0;
			Quadruple record = null;
			Label subject = null, object = null, predicate = null;
			boolean result = true;

			QuadrupleBTFileScan scan = Quadruple_BTreeIndex.new_scan(low_key,null);

			QID qid = null;
			while((entry = scan.get_next())!= null)
			{
				qid = ((QuadrupleLeafData)entry.data).getData();
				record = Quadruple_HF.getRecord(qid);
				orig_confidence = record.getConfidence();
				EID subjid = record.getSubjectID();
				subject = Entity_HF.getRecord(subjid.returnLID());
				EID objid = record.getObjectID();
				object = Entity_HF.getRecord(objid.returnLID());
				PID predid = record.getPredicateID();
				predicate = Predicate_HF.getRecord(predid.returnLID());
				result = true;

				if(!subject_null)
				{
					result = result & (subjectFilter.compareTo(subject.getLabelKey()) == 0);
				}
				if(!object_null)
				{
					result = result & (objectFilter.compareTo(object.getLabelKey()) == 0 );
				}
				if(!predicate_null)
				{
					result = result & (predicateFilter.compareTo(predicate.getLabelKey())==0);
				}
				if(!confidence_null)
				{
					result = result & (orig_confidence >= confidenceFilter);
				}
				if(subjectFilter.compareTo(subject.getLabelKey()) != 0)
				{
					//System.out.println("Found next subject hence stopping");
					break;
				}
				else if(result)
				{
					//System.out.println("Subject::" + subject.getLabelKey()+ "\tPredicate::"+predicate.getLabelKey() + "\tObject::"+object.getLabelKey() );
					Result_HF.insertQuadruple(record.returnQuadrupleByteArray());
				}
			}
			scan.DestroyBTreeFileScan();
			Quadruple_BTreeIndex.close();
		}
		catch(Exception e)
		{
			System.err.println ("Error for subject and confidence index query"+e);
			e.printStackTrace();
			Runtime.getRuntime().exit(1);
		}

	}


	private void streamByObjectConfidence(String subjectFilter,String predicateFilter,String objectFilter,double confidenceFilter)
	{
		try
		{
			QuadrupleBTreeFile Quadruple_BTreeIndex = SystemDefs.JavabaseDB.getIndexBT();
			QuadrupleHeapfile Quadruple_HF = SystemDefs.JavabaseDB.getQuadHF();
			LabelHeapFile Entity_HF = SystemDefs.JavabaseDB.getEntityHF();
			LabelHeapFile Predicate_HF = SystemDefs.JavabaseDB.getPredHF();

			java.util.Date date= new java.util.Date();
			Result_HF = new QuadrupleHeapfile(Long.toString(date.getTime()));

			KeyClass low_key = new StringKey(objectFilter+":"+confidenceFilter);
			KeyDataEntry entry = null;

			QuadrupleBTFileScan scan = Quadruple_BTreeIndex.new_scan(low_key,null);
			QID qid = null;
			EID subjid = null, objid = null;
			PID predid = null;
			Label subject = null,object = null, predicate = null;
			Quadruple record = null;
			double orig_confidence = 0;
			boolean result = true;
			while((entry = scan.get_next())!= null)
			{
				qid =  ((QuadrupleLeafData)entry.data).getData();

				record = Quadruple_HF.getRecord(qid);
				orig_confidence = record.getConfidence();
				subjid = record.getSubjectID();
				subject = Entity_HF.getRecord(subjid.returnLID());
				objid = record.getObjectID();
				object = Entity_HF.getRecord(objid.returnLID());
				predid = record.getPredicateID();
				predicate = Predicate_HF.getRecord(predid.returnLID());

				result = true;

				if(!subject_null)
				{
					result = result & (subjectFilter.compareTo(subject.getLabelKey()) == 0);
				}
				if(!object_null)
				{
					result = result & (objectFilter.compareTo(object.getLabelKey()) == 0 );
				}
				if(!predicate_null)
				{
					result = result & (predicateFilter.compareTo(predicate.getLabelKey())==0);
				}
				if(!confidence_null)
				{
					result = result & (orig_confidence >= confidenceFilter);
				}
				if(objectFilter.compareTo(object.getLabelKey()) != 0)
				{
					//System.out.println("Found next object hence stopping");
					break;
				}
				else if(result)
				{
					//System.out.println("Inserting "+object.getLabelKey()+confidenceFilter);
					Result_HF.insertQuadruple(record.returnQuadrupleByteArray());
				}
			}
			scan.DestroyBTreeFileScan();
			Quadruple_BTreeIndex.close();
		}
		catch(Exception e)
		{
			System.err.println ("Error for object and confidence index query"+e);
			e.printStackTrace();
			Runtime.getRuntime().exit(1);
		}
	}


	private void streamByPredicateConfidence(String subjectFilter,String predicateFilter,String objectFilter,double confidenceFilter)
	{
		try
		{
			QuadrupleBTreeFile Quadruple_BTreeIndex = SystemDefs.JavabaseDB.getIndexBT();
			QuadrupleHeapfile Quadruple_HF = SystemDefs.JavabaseDB.getQuadHF();
			LabelHeapFile Entity_HF = SystemDefs.JavabaseDB.getEntityHF();
			LabelHeapFile Predicate_HF = SystemDefs.JavabaseDB.getPredHF();
			java.util.Date date= new java.util.Date();
			Result_HF = new QuadrupleHeapfile(Long.toString(date.getTime()));


			KeyClass low_key = new StringKey(predicateFilter+":"+confidenceFilter);
			QuadrupleBTFileScan scan = Quadruple_BTreeIndex.new_scan(low_key,null);
			KeyDataEntry entry = null;
			QID qid = null;
			EID subjid = null, objid = null;
			PID predid = null;
			Label subject = null,object = null, predicate = null;
			Quadruple record = null;
			double orig_confidence = 0;
			boolean result = true;

			while((entry = scan.get_next())!= null)
			{
				qid =  ((QuadrupleLeafData)entry.data).getData();
				//System.out.println("Quadruple found : " + ((StringKey)(entry.key)).getKey() + "qid" + qid);
				record = Quadruple_HF.getRecord(qid);
				orig_confidence = record.getConfidence();
				subjid = record.getSubjectID();
				subject = Entity_HF.getRecord(subjid.returnLID());
				objid = record.getObjectID();
				object = Entity_HF.getRecord(objid.returnLID());
				predid = record.getPredicateID();
				predicate = Predicate_HF.getRecord(predid.returnLID());

				result = true;

				if(!subject_null)
				{
					result = result & (subjectFilter.compareTo(subject.getLabelKey()) == 0);
				}
				if(!object_null)
				{
					result = result & (objectFilter.compareTo(object.getLabelKey()) == 0 );
				}
				if(!predicate_null)
				{
					result = result & (predicateFilter.compareTo(predicate.getLabelKey())==0);
				}
				if(!confidence_null)
				{
					result = result & (orig_confidence >= confidenceFilter);
				}
				if(predicateFilter.compareTo(predicate.getLabelKey()) != 0)
				{
					//System.out.println("Found next predicate hence stopping");
					break;
				}
				else if(result)
				{
					//System.out.println("Inserting "+object.getLabelKey()+confidenceFilter);
					Result_HF.insertQuadruple(record.returnQuadrupleByteArray());
				}
			}
			scan.DestroyBTreeFileScan();
			Quadruple_BTreeIndex.close();
		}
		catch(Exception e)
		{
			System.err.println ("Error for predicate and confidence index query"+e);
			e.printStackTrace();
			Runtime.getRuntime().exit(1);
		}
	}


	public static void ScanBTSubjectIndex(String subjectFilter,String predicateFilter, String objectFilter, double confidenceFilter)
			throws Exception
	{
		QuadrupleBTreeFile Entity_TTree = SystemDefs.JavabaseDB.getIndexBT();
		QuadrupleHeapfile Quadruple_HF = SystemDefs.JavabaseDB.getQuadHF();
		LabelHeapFile Entity_HF = SystemDefs.JavabaseDB.getEntityHF();
		//LabelHeapfile Predicate_HF = sysdef.JavabaseDB.getPredicateHandle();
		LabelHeapFile Predicate_HF = SystemDefs.JavabaseDB.getPredHF();
		java.util.Date date= new java.util.Date();
		Result_HF = new QuadrupleHeapfile(Long.toString(date.getTime()));

		QID quadrupleid = null;
		KeyClass low_key = new StringKey(subjectFilter);
		KeyClass high_key = new StringKey(subjectFilter);
		KeyDataEntry entryconf = null;
		Quadruple record = null;
		Label subject = null, object = null, predicate = null;
		//Start Scanning Bble.tree to check if subject is present
		QuadrupleBTFileScan scan = Entity_TTree.new_scan(low_key,high_key);
		entryconf = scan.get_next();

		try
		{
			while(entryconf!=null)
			{
				boolean result = true;
				quadrupleid =  ((QuadrupleLeafData)entryconf.data).getData();
				record = Quadruple_HF.getRecord(quadrupleid);

				subject = Entity_HF.getRecord(record.getSubjectID().returnLID());
				object = Entity_HF.getRecord(record.getObjectID().returnLID());
				predicate = Predicate_HF.getRecord(record.getPredicateID().returnLID());

				if(!object_null)
				{
					result = result & (object.getLabelKey().compareTo(objectFilter)==0);
				}
				if(!predicate_null)
				{
					result = result & (predicate.getLabelKey().compareTo(predicateFilter)==0);
				}
				//System.out.println(subject.getLabelKey()+" "+predicate.getLabelKey()+" "+object.getLabelKey()+" "+record.getConfidence());
				if(confidenceFilter <= record.getConfidence())
				{
					result = true & result;
				}
				if(result)
				{
					//System.out.println("Subject found");
					Result_HF.insertQuadruple(record.returnQuadrupleByteArray());
				}
				entryconf = scan.get_next();
			}
		}
		catch(Exception ex)
		{
			System.out.println("Exception::"+ex);
		}

		scan.DestroyBTreeFileScan();
		Entity_TTree.close();
	}

	private void ScanEntireHeapFile(String subjectFilter, String predicateFilter, String objectFilter, double confidenceFilter)
	{
		try
		{
			_subjectFilter = subjectFilter;
			_predicateFilter = predicateFilter;
			_objectFilter =  objectFilter;
			_confidenceFilter = confidenceFilter;
			Result_HF = SystemDefs.JavabaseDB.getQuadHF();
		}
		catch(Exception e)
		{
			System.err.println ("Error scanning entire heap file for query::"+e);
			e.printStackTrace();
			Runtime.getRuntime().exit(1);
		}
	}


}//End of Stream class
