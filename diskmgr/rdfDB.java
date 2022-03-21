/* File rdfDB.java */

package diskmgr;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import global.*;
import bufmgr.*;
import quadrupleheap.*;
import labelheap.*;
import btree.*;

public class rdfDB implements GlobalConst {

	private static final int bits_per_page = MAX_SPACE * 8;
	private RandomAccessFile fp;
	private int num_pages;
	public String name;
	private int type;
	private QuadrupleHeapfile quadHF; 	  		//Quadruples Heap file to store quadruples
	private QuadrupleBTreeFile quadBT; 		//BTree Predicate file on Predicate Heap file
	private LabelHeapFile entityHF; 	  		//Entity Heap file to store subjects/objects
	private LabelBTreeFile entityBT;  		//BTree Index file on Entity Heap file
	private LabelHeapFile predHF;   		//Predicates Heap file to store predicates
	private LabelBTreeFile predBT; 	//BTree Predicate file on Predicate Heap file
	private LabelBTreeFile dupSubBT;
	private LabelBTreeFile dupObjBT;
	private QuadrupleBTreeFile indexBT; 	//BTree file for the index options given

	public QuadrupleHeapfile getQuadHF() {
		return quadHF;
	}
	public LabelHeapFile getEntityHF() {
		return entityHF;
	}
	public LabelHeapFile getPredHF() {
		return predHF;
	}

	public QuadrupleBTreeFile getIndexBT()
			throws GetFileEntryException,
			PinPageException,
			ConstructPageException {

		indexBT = new QuadrupleBTreeFile(name+"/indexBT");
		return indexBT;
	}

	public QuadrupleBTreeFile getQuadBT()
			throws GetFileEntryException,
			PinPageException,
			ConstructPageException {

		quadBT = new QuadrupleBTreeFile(name+"/quadBT");
		return quadBT;
	}
	/**
	 * Default Constructor
	 */
	public rdfDB() { }

	/**
	 * Close rdfDB
	 */
	public void rdfcloseDB()
			throws 	PageUnpinnedException,
			InvalidFrameNumberException,
			HashEntryNotFoundException,
			ReplacerException {
		try {

			if(entityBT != null) { entityBT.close(); }
			if(predBT != null) { predBT.close(); }
			if(quadBT != null) { quadBT.close(); }
			if(dupSubBT != null) { dupSubBT.close(); }
			if(dupObjBT != null) { dupObjBT.close(); }
			if(indexBT != null) { indexBT.close(); }
		}
		catch (Exception e) {
			System.err.println ("~~~ ERROR CLOSING rdfDB ~~~\n" + e);
			e.printStackTrace();
			Runtime.getRuntime().exit(1);
		}
	}

	public void openrdfDB(String dbName,int type)
	{
		try
		{
			openDB(dbName);
			initrdfDB(type);
		}
		catch (Exception e)
		{
			System.err.println (e);
			e.printStackTrace();
			Runtime.getRuntime().exit(1);
		}
	}

	public void openrdfDB(String dbName,int numPages,int type)
	{
		try
		{
			openDB(dbName,numPages);
			initrdfDB(type);
		}
		catch(Exception e)
		{
			System.err.println (e);
			e.printStackTrace();
			Runtime.getRuntime().exit(1);
		}
	}

	public void initrdfDB(int type)
	{
		int keyType = AttrType.attrString;
		this.type = type;
		PCounter.initialize();

		try {
			quadHF = new QuadrupleHeapfile(name+"/quadHF");
			entityHF = new LabelHeapFile(name+"/entityHF");
			predHF = new LabelHeapFile(name+"/predHF");
			entityBT = new LabelBTreeFile(name+"/entityBT",keyType,255,1);
			predBT = new LabelBTreeFile(name+"/predBT",keyType,255,1);
			quadBT = new QuadrupleBTreeFile(name+"/quadBT",keyType,255,1);
			dupSubBT = new LabelBTreeFile(name+"/dupSubBT",keyType,255,1);
			dupObjBT = new LabelBTreeFile(name+"/dupObjBT",keyType,255,1);
			indexBT = new QuadrupleBTreeFile(name+"/indexBT",keyType,255,1);

			entityBT.close();
			predBT.close();
			quadBT.close();
			dupSubBT.close();
			dupObjBT.close();
			indexBT.close();
		}
		catch(Exception e)
		{
			System.err.println ("~~~ ERROR INITIALIZING rdfDB ~~~\n" + e);
			e.printStackTrace();
			Runtime.getRuntime().exit(1);
		}
	}

	public int getQuadrupleCnt()
	{
		int quadCnt = 0;
		try
		{
			quadHF = new QuadrupleHeapfile(name+"/quadHF");
			quadCnt = quadHF.getRecCnt();
		}
		catch (Exception e)
		{
			System.err.println ("~~~ ERROR GETTING QUADRUPLE COUNT ~~~\n" + e);
			e.printStackTrace();
			Runtime.getRuntime().exit(1);
		}
		return quadCnt;
	}

	public int getEntityCnt()
	{
		int entityCnt = 0;
		try
		{
			entityHF = new LabelHeapFile(name+"/entityHF");
			entityCnt = entityHF.getRecCnt();
		}
		catch (Exception e)
		{
			System.err.println ("~~~ ERROR GETTING ENTITY COUNT ~~~\n" + e);
			e.printStackTrace();
			Runtime.getRuntime().exit(1);
		}
		return entityCnt;
	}

	public int getPredicateCnt()
	{
		int predCnt = 0;
		try
		{
			predHF = new LabelHeapFile(name+"/predHF");
			predCnt = predHF.getRecCnt();
		}
		catch (Exception e)
		{
			System.err.println ("~~~ ERROR GETTING PREDICATE COUNT ~~~\n" + e);
			e.printStackTrace();
			Runtime.getRuntime().exit(1);
		}
		return predCnt;
	}

	public int getSubjectCnt()
	{
		int subCnt = 0;
		KeyDataEntry entry = null;
		KeyDataEntry dupEntry = null;
		try
		{
			quadBT = new QuadrupleBTreeFile(name+"/quadBT");
			dupSubBT = new LabelBTreeFile(name+"/dupSubBT");
			QuadrupleBTFileScan scan = quadBT.new_scan(null,null); //scan through all of quadBT
			do
			{
				entry = scan.get_next();
				if(entry != null)
				{
					String label = ((StringKey)(entry.key)).getKey();
					String[] temp = label.split(":");
					String subject = temp[0] + temp[1];
					KeyClass key = new StringKey(subject);
					LabelBTFileScan dupScan = dupSubBT.new_scan(key,key);
					dupEntry = dupScan.get_next(); //scan to see if key exists or not.

					// Add to dupSubBT if not already present
					if(dupEntry == null) dupSubBT.insert(key,new LID(new PageId(Integer.parseInt(temp[1])),Integer.parseInt(temp[0])));
					dupScan.DestroyBTreeFileScan();
				}

			} while(entry != null); // go through every entry
			scan.DestroyBTreeFileScan();
			quadBT.close();

			// scan through dupSubBT and count all subjects
			LabelBTFileScan dupScan = dupSubBT.new_scan(null,null);
			do
			{
				dupEntry = dupScan.get_next();
				if(dupEntry != null) subCnt++;
			} while(dupEntry != null);
			dupScan.DestroyBTreeFileScan();
			dupSubBT.close();
		}
		catch(Exception e)
		{
			System.err.println ("~~~ ERROR GETTING SUBJECT COUNT ~~~\n" + e);
			e.printStackTrace();
			Runtime.getRuntime().exit(1);
		}

		return subCnt;
	}

	// Same logic as getSubjectCnt() but on objects
	public int getObjectCnt()
	{
		int objCnt = 0;
		KeyDataEntry entry = null;
		KeyDataEntry dupEntry = null;
		try
		{
			quadBT = new QuadrupleBTreeFile(name+"/quadBT");
			dupObjBT = new LabelBTreeFile(name+"/dupObjBT");
			QuadrupleBTFileScan scan = quadBT.new_scan(null,null);
			do
			{
				entry = scan.get_next();
				if(entry != null)
				{
					String label = ((StringKey)(entry.key)).getKey();
					String[] temp = label.split(":");
					String object = temp[4] + temp[5];
					KeyClass key = new StringKey(object);
					LabelBTFileScan dupScan = dupObjBT.new_scan(key,key);
					dupEntry = dupScan.get_next();

					if(dupEntry == null) dupObjBT.insert(key,new LID(new PageId(Integer.parseInt(temp[4])),Integer.parseInt(temp[5])));
					dupScan.DestroyBTreeFileScan();
				}
			} while(entry != null);
			scan.DestroyBTreeFileScan();
			quadBT.close();

			LabelBTFileScan dupScan = dupObjBT.new_scan(null,null);
			do
			{
				dupEntry = dupScan.get_next();
				if(dupEntry != null) objCnt++;
			} while(dupEntry != null);
			dupScan.DestroyBTreeFileScan();
			dupObjBT.close();
		}
		catch(Exception e)
		{
			System.err.println ("~~~ ERROR GETTING OBJECT COUNT ~~~\n" + e);
			e.printStackTrace();
			Runtime.getRuntime().exit(1);
		}
		return objCnt;
	}

	public EID insertEntity(String entityLabel)
	{
		EID eid = null;
		LID lid = null;
		KeyClass key = new StringKey(entityLabel);
		KeyDataEntry entry = null;

		try
		{
			entityBT = new LabelBTreeFile(name+"/entityBT");

			LabelBTFileScan scan = entityBT.new_scan(key,key); //Scan to see if entity exists in database
			entry = scan.get_next();
			if(entry!=null)
			{
				if(entityLabel.equals(((StringKey)(entry.key)).getKey())) //Already exists, don't insert
				{
					lid =  ((LabelLeafData)entry.data).getData();
					eid = lid.returnEID();
					scan.DestroyBTreeFileScan();
					entityBT.close();
					return eid;
				}
			}
			scan.DestroyBTreeFileScan();

			//Insert into HF and BT
			lid = entityHF.insertRecord(entityLabel.getBytes());
			entityBT.insert(key,lid);
			eid = lid.returnEID();

			entityBT.close();
		}
		catch(Exception e)
		{
			System.err.println ("~~~ ERROR INSERTING ENTITY ~~~\n" + e);
			e.printStackTrace();
			Runtime.getRuntime().exit(1);
		}

		return eid;
	}

	public boolean deleteEntity(String entityLabel)
	{
		boolean success = false;
		LID lid = null;
		KeyDataEntry entry = null;
		KeyClass key = new StringKey(entityLabel);

		try
		{
			entityHF = new LabelHeapFile(name+"/entityHF");
			entityBT = new LabelBTreeFile(name+"/entityBT");

			LabelBTFileScan scan = entityBT.new_scan(key,key);
			entry = scan.get_next();
			if(entry!=null)
			{
				if(entityLabel.equals(((StringKey)(entry.key)).getKey()))
				{
					lid =  ((LabelLeafData)entry.data).getData();
					success = entityHF.deleteRecord(lid) & entityBT.Delete(key,lid); // Delete from both HF and BT
				}
			}
			scan.DestroyBTreeFileScan();
			entityBT.close();
		}
		catch(Exception e)
		{
			System.err.println ("~~~ ERROR DELETING ENTITY ~~~\n" + e);
			e.printStackTrace();
			Runtime.getRuntime().exit(1);
		}
		return success;
	}

	// Similar in logic to insertEntity
	public PID insertPredicate(String predLabel)
	{
		PID pid = null;
		LID lid = null;
		KeyClass key = new StringKey(predLabel);
		KeyDataEntry entry = null;

		try
		{
			predBT = new LabelBTreeFile(name+"/predBT");

			LabelBTFileScan scan = predBT.new_scan(key,key);
			entry = scan.get_next();
			if(entry != null)
			{
				if(predLabel.compareTo(((StringKey)(entry.key)).getKey()) == 0)
				{
					pid = ((LabelLeafData)(entry.data)).getData().returnPID();
					scan.DestroyBTreeFileScan();
					predBT.close();
					return pid;
				}
			}
			scan.DestroyBTreeFileScan();

			lid = predHF.insertRecord(predLabel.getBytes());
			predBT.insert(key,lid);
			pid = lid.returnPID();

			predBT.close();
		}
		catch(Exception e)
		{
			System.err.println ("~~~ ERROR INSERTING PREDICATE ~~~\n" + e);
			e.printStackTrace();
			Runtime.getRuntime().exit(1);
		}

		return pid;
	}

	// Similar to deleteEntity
	public boolean deletePredicate(String predLabel)
	{
		boolean success = false;
		LID lid = null;
		KeyDataEntry entry = null;
		KeyClass key = new StringKey(predLabel);

		try
		{
			predHF = new LabelHeapFile(name+"/predHF");
			predBT = new LabelBTreeFile(name+"/predBT");

			LabelBTFileScan scan = predBT.new_scan(key,key);
			entry = scan.get_next();
			if(entry!=null)
			{
				if(predLabel.equals(((StringKey)(entry.key)).getKey()))
				{
					lid =  ((LabelLeafData)entry.data).getData();
					success = predHF.deleteRecord(lid) & predBT.Delete(key,lid);
				}
			}
			scan.DestroyBTreeFileScan();
			predBT.close();
		}
		catch(Exception e)
		{
			System.err.println ("~~~ ERROR DELETING PREDICATE ~~~" + e);
			e.printStackTrace();
			Runtime.getRuntime().exit(1);
		}

		return success;
	}

	public QID insertQuadruple(byte[] quadPtr)
	{
		QID qid = null;
		KeyDataEntry entry = null;
		try
		{
			quadBT = new QuadrupleBTreeFile(name+"/quadBT");

			int subSlotNum = Convert.getIntValue(0,quadPtr);
			int subPageNo = Convert.getIntValue(4,quadPtr);
			int predSlotNum = Convert.getIntValue(8,quadPtr);
			int predPageNum = Convert.getIntValue(12,quadPtr);
			int objSlotNum = Convert.getIntValue(16,quadPtr);
			int objPageNum = Convert.getIntValue(20,quadPtr);
			double confidence = Convert.getDoubleValue(24,quadPtr);
			String key = Integer.toString(subSlotNum) +':'+ Integer.toString(subPageNo) +':'+ Integer.toString(predSlotNum) + ':' + Integer.toString(predPageNum) +':' + Integer.toString(objSlotNum) +':'+ Integer.toString(objPageNum);
			KeyClass scanKey = new StringKey(key);

			QuadrupleBTFileScan scan = quadBT.new_scan(scanKey, scanKey);
			entry = scan.get_next();
			if(entry != null)
			{
				if(key.compareTo(((StringKey)(entry.key)).getKey()) == 0)
				{
					qid = ((QuadrupleLeafData)(entry.data)).getData();
					Quadruple record = quadHF.getRecord(qid);
					double oldConfidence = record.getConfidence();
					if(oldConfidence < confidence) //TODO VALIDATE
					{
						Quadruple newRecord = new Quadruple(quadPtr,0,32);
						quadHF.updateRecord(qid,newRecord);
					}
					scan.DestroyBTreeFileScan();
					quadBT.close();
					return qid;
				}
			}
			qid = quadHF.insertQuadruple(quadPtr);
			quadBT.insert(scanKey,qid);
			scan.DestroyBTreeFileScan();

			quadBT.close();
		}
		catch(Exception e)
		{
			System.err.println ("~~~ ERROR INSERTING QUADRUPLE ~~~\n" + e);
			e.printStackTrace();
			Runtime.getRuntime().exit(1);
		}

		return qid;
	}

	public boolean deleteQuadruple(byte[] quadPtr)
	{
		boolean success = false;
		QID qid = null;
		KeyDataEntry entry = null;
		try
		{
			quadBT = new QuadrupleBTreeFile(name+"/quadBT");

			int subSlotNum = Convert.getIntValue(0,quadPtr);
			int subPageNum = Convert.getIntValue(4,quadPtr);
			int predSlotNum = Convert.getIntValue(8,quadPtr);
			int predPageNum = Convert.getIntValue(12,quadPtr);
			int objSlotNum = Convert.getIntValue(16,quadPtr);
			int objPageNum = Convert.getIntValue(20,quadPtr);
			String key = Integer.toString(subSlotNum) +':'+ Integer.toString(subPageNum) +':'+ Integer.toString(predSlotNum) + ':' + Integer.toString(predPageNum) +':' + Integer.toString(objSlotNum) +':'+ Integer.toString(objPageNum);
			KeyClass scanKey = new StringKey(key);

			QuadrupleBTFileScan scan = quadBT.new_scan(scanKey, scanKey);
			entry = scan.get_next();
			if(entry != null)
			{
				if(key.compareTo(((StringKey)(entry.key)).getKey()) == 0)
				{
					qid = ((QuadrupleLeafData)(entry.data)).getData();
					if(qid != null)
					{
						success = quadHF.deleteRecord(qid);
						if(scanKey != null) success = success & quadBT.Delete(scanKey,qid);
					}
				}
			}
			scan.DestroyBTreeFileScan();

			quadBT.close();
		}
		catch(Exception e)
		{
			System.err.println ("~~~ ERROR DELETING QUADRUPLE ~~~\n" + e);
			e.printStackTrace();
			Runtime.getRuntime().exit(1);
		}

		return success;
	}

	public Stream openStream(String dbname, int orderType, String subjectFilter, String predicateFilter, String objectFilter, double confidenceFilter)
	{
		Stream streamObj= null;
		try {
			streamObj = new Stream(dbname, orderType, subjectFilter,  predicateFilter, objectFilter, confidenceFilter);
		} catch (Exception e) {
			System.err.println ("~~~ ERROR OPENING STREAM ~~~\n" + e);
			e.printStackTrace();
			Runtime.getRuntime().exit(1);
		}

		return streamObj;

	}

	public void createIndex(int type)
	{

		switch(type)
		{
			case 1:
				createIndex1();
				break;

			case 2:
				createIndex2();
				break;

			case 3:
				createIndex3();
				break;

			case 4:
				createIndex4();
				break;

			case 5:
				createIndex5();
				break;

		}
	}

	public void createIndex1()
    {
            //Unclustered BTree Index file on subject, predicate, object and confidence
            try
            {
                    //destroy existing index first
                    if(indexBT != null)
                    {
                            indexBT.close();
                            indexBT.destroyFile();
                            destroyIndex(name+"/indexBT");
                    }

                    //create new
                    int keytype = AttrType.attrString;
                    indexBT = new QuadrupleBTreeFile(name+"/indexBT",keytype,255,1);
                    indexBT.close();

                    //scan sorted heap file and insert into btree index
                    indexBT = new QuadrupleBTreeFile(name+"/indexBT");
                    quadHF = new QuadrupleHeapfile(name+"/quadrupleHF");
                    entityHF = new LabelHeapFile(name+"/entityHF");
					predHF = new LabelHeapFile(name+"/predicateHF");
                    TScan am = new TScan(quadHF);

                    Quadruple quadruple = null;
                    QID qid = new QID();
                    double confidence = 0.0;
                    while((quadruple = am.getNext(qid)) != null)
                    {
                            confidence = quadruple.getConfidence();
                            String temp = Double.toString(confidence);
                            Label subject = entityHF.getRecord(quadruple.getSubjectID().returnLID());
							Label predicate = predHF.getRecord(quadruple.getPredicateID().returnLID());
							Label object = entityHF.getRecord(quadruple.getObjectID().returnLID());

                            //System.out.println("Subject--> "+subject.getLabelKey());

							List<Integer> types = new ArrayList<Integer>();
							types.add(AttrType.attrString);
							types.add(AttrType.attrString);
							types.add(AttrType.attrString);
							types.add(AttrType.attrDouble);
                            KeyClass key = new StringKey(subject.getLabelKey()+":"+predicate.getLabelKey()+":"+object.getLabelKey()+":"+temp, types);
                            //System.out.println("Inserting into Btree key"+ subject.getLabelKey() + ":" + temp + " qid "+qid);
                            indexBT.insert(key,qid);
                    }
                    /*
                    QuadrupleBTFileScan scan = indexBT.new_scan(null,null);
                    KeyDataEntry entry = null;
                    while((entry = scan.get_next())!= null)
                    {
                            System.out.println("Key found : " + ((StringKey)(entry.key)).getKey());
                    }
                    scan.DestroyBTreeFileScan();
                    */
                    am.closescan();
                    indexBT.close();
            }
            catch(Exception e)
            {
                    System.err.println ("* Error creating Index for option2 " + e);
                    e.printStackTrace();
                    Runtime.getRuntime().exit(1);
            }

    }

	public void createIndex2()
    {
            //Unclustered BTree on confidence using sorted Heap File
		//Unclustered BTree Index file on subject, predicate, object and confidence
		try
		{
			//destroy existing index first
			if(indexBT != null)
			{
				indexBT.close();
				indexBT.destroyFile();
				destroyIndex(name+"/indexBT");
			}

			//create new
			int keytype = AttrType.attrString;
			indexBT = new QuadrupleBTreeFile(name+"/indexBT",keytype,255,1);
			indexBT.close();

			//scan sorted heap file and insert into btree index
			indexBT = new QuadrupleBTreeFile(name+"/indexBT");
			quadHF = new QuadrupleHeapfile(name+"/quadrupleHF");
			entityHF = new LabelHeapFile(name+"/entityHF");
			predHF = new LabelHeapFile(name+"/predicateHF");
			TScan am = new TScan(quadHF);

			Quadruple quadruple = null;
			QID qid = new QID();
			double confidence = 0.0;
			while((quadruple = am.getNext(qid)) != null)
			{
				confidence = quadruple.getConfidence();
				String temp = Double.toString(confidence);
				Label subject = entityHF.getRecord(quadruple.getSubjectID().returnLID());
				Label predicate = predHF.getRecord(quadruple.getPredicateID().returnLID());
				Label object = entityHF.getRecord(quadruple.getObjectID().returnLID());

				//System.out.println("Subject--> "+subject.getLabelKey());

				List<Integer> types = new ArrayList<Integer>();
				types.add(AttrType.attrString);
				types.add(AttrType.attrString);
				types.add(AttrType.attrString);
				types.add(AttrType.attrDouble);
				KeyClass key = new StringKey(object.getLabelKey()+":"+predicate.getLabelKey()+":"+subject.getLabelKey()+":"+temp, types);
				//System.out.println("Inserting into Btree key"+ subject.getLabelKey() + ":" + temp + " qid "+qid);
				indexBT.insert(key,qid);
			}
                    /*
                    QuadrupleBTFileScan scan = indexBT.new_scan(null,null);
                    KeyDataEntry entry = null;
                    while((entry = scan.get_next())!= null)
                    {
                            System.out.println("Key found : " + ((StringKey)(entry.key)).getKey());
                    }
                    scan.DestroyBTreeFileScan();
                    */
			am.closescan();
			indexBT.close();
		}
		catch(Exception e)
		{
			System.err.println ("* Error creating Index for option2 " + e);
			e.printStackTrace();
			Runtime.getRuntime().exit(1);
		}

    }

    public void createIndex3()
    {
            //Unclustered BTree Index file on object and confidence
		try
		{
			//destroy existing index first
			if(indexBT != null)
			{
				indexBT.close();
				indexBT.destroyFile();
				destroyIndex(name+"/indexBT");
			}

			//create new
			int keytype = AttrType.attrString;
			indexBT = new QuadrupleBTreeFile(name+"/indexBT",keytype,255,1);
			indexBT.close();

			//scan sorted heap file and insert into btree index
			indexBT = new QuadrupleBTreeFile(name+"/indexBT");
			quadHF = new QuadrupleHeapfile(name+"/quadrupleHF");
			entityHF = new LabelHeapFile(name+"/entityHF");
			predHF = new LabelHeapFile(name+"/predicateHF");
			TScan am = new TScan(quadHF);

			Quadruple quadruple = null;
			QID qid = new QID();
			double confidence = 0.0;
			while((quadruple = am.getNext(qid)) != null)
			{
				confidence = quadruple.getConfidence();
				String temp = Double.toString(confidence);
				Label subject = entityHF.getRecord(quadruple.getSubjectID().returnLID());
				Label object = entityHF.getRecord(quadruple.getObjectID().returnLID());

				//System.out.println("Subject--> "+subject.getLabelKey());

				List<Integer> types = new ArrayList<Integer>();
				types.add(AttrType.attrString);
				types.add(AttrType.attrDouble);
				KeyClass key = new StringKey(subject.getLabelKey()+":"+temp, types);
				//System.out.println("Inserting into Btree key"+ subject.getLabelKey() + ":" + temp + " qid "+qid);
				indexBT.insert(key,qid);
			}
                    /*
                    QuadrupleBTFileScan scan = indexBT.new_scan(null,null);
                    KeyDataEntry entry = null;
                    while((entry = scan.get_next())!= null)
                    {
                            System.out.println("Key found : " + ((StringKey)(entry.key)).getKey());
                    }
                    scan.DestroyBTreeFileScan();
                    */
			am.closescan();
			indexBT.close();
		}
		catch(Exception e)
		{
			System.err.println ("* Error creating Index for option2 " + e);
			e.printStackTrace();
			Runtime.getRuntime().exit(1);
		}


	}

    public void createIndex4()
    {
            //Unclustered BTree Index file on predicate and confidence
            try
            {
                    //destroy existing index first
                    if(indexBT != null)
                    {
                            indexBT.close();
                            indexBT.destroyFile();
                            destroyIndex(name+"/indexBT");
                    }

                    //create new
                    int keytype = AttrType.attrString;
                    indexBT = new QuadrupleBTreeFile(name+"/indexBT",keytype,255,1);
                    indexBT.close();

                    //scan sorted heap file and insert into btree index
                    indexBT = new QuadrupleBTreeFile(name+"/indexBT");
                    quadHF = new QuadrupleHeapfile(name+"/quadrupleHF");
                    predHF = new LabelHeapFile(name+"/predicateHF");
                    TScan am = new TScan(quadHF);
                    Quadruple quadruple = null;
                    QID qid = new QID();
                    double confidence = 0.0;
                    while((quadruple = am.getNext(qid)) != null)
                    {
                            confidence = quadruple.getConfidence();
                            String temp = Double.toString(confidence);
                            Label predicate = predHF.getRecord(quadruple.getPredicateID().returnLID());
                            //System.out.println("Subject--> "+subject.getLabelKey());
							List<Integer> types = new ArrayList<Integer>();
							types.add(AttrType.attrString);
							types.add(AttrType.attrDouble);
                            KeyClass key = new StringKey(predicate.getLabelKey()+":"+temp, types);
                            //System.out.println("Inserting into Btree key"+ predicate.getLabelKey() + ":" + temp + " qid "+qid);
                            indexBT.insert(key,qid);
                    }
                    /*
                    QuadrupleBTFileScan scan = indexBT.new_scan(null,null);
                    KeyDataEntry entry = null;
                    while((entry = scan.get_next())!= null)
                    {
                            System.out.println("Key found : " + ((StringKey)(entry.key)).getKey());
                    }
                    scan.DestroyBTreeFileScan();
                    */
                    am.closescan();
                    indexBT.close();
            }
            catch(Exception e)
            {
                    System.err.println ("* Error creating Index for option2 " + e);
                    e.printStackTrace();
                    Runtime.getRuntime().exit(1);
            }


    }

    public void createIndex6()
    {
		try
		{
			//destroy existing index first
			if(indexBT != null)
			{
				indexBT.close();
				indexBT.destroyFile();
				destroyIndex(name+"/indexBT");
			}

			//create new
			int keytype = AttrType.attrString;
			indexBT = new QuadrupleBTreeFile(name+"/indexBT",keytype,255,1);
			indexBT.close();

			//scan sorted heap file and insert into btree index
			indexBT = new QuadrupleBTreeFile(name+"/indexBT");
			quadHF = new QuadrupleHeapfile(name+"/quadrupleHF");
			predHF = new LabelHeapFile(name+"/predicateHF");
			TScan am = new TScan(quadHF);
			Quadruple quadruple = null;
			QID qid = new QID();
			double confidence = 0.0;
			while((quadruple = am.getNext(qid)) != null)
			{
				confidence = quadruple.getConfidence();
				String temp = Double.toString(confidence);
				Label object = entityHF.getRecord(quadruple.getObjectID().returnLID());
				//System.out.println("Subject--> "+subject.getLabelKey());
				List<Integer> types = new ArrayList<Integer>();
				types.add(AttrType.attrDouble);
				KeyClass key = new StringKey(temp, types);
				//System.out.println("Inserting into Btree key"+ predicate.getLabelKey() + ":" + temp + " qid "+qid);
				indexBT.insert(key,qid);
			}
                    /*
                    QuadrupleBTFileScan scan = indexBT.new_scan(null,null);
                    KeyDataEntry entry = null;
                    while((entry = scan.get_next())!= null)
                    {
                            System.out.println("Key found : " + ((StringKey)(entry.key)).getKey());
                    }
                    scan.DestroyBTreeFileScan();
                    */
			am.closescan();
			indexBT.close();
		}
		catch(Exception e)
		{
			System.err.println ("* Error creating Index for option2 " + e);
			e.printStackTrace();
			Runtime.getRuntime().exit(1);
		}

	}

	public void createIndex5()
	{
		try
		{
			//destroy existing index first
			if(indexBT != null)
			{
				indexBT.close();
				indexBT.destroyFile();
				destroyIndex(name+"/indexBT");
			}

			//create new
			int keytype = AttrType.attrString;
			indexBT = new QuadrupleBTreeFile(name+"/indexBT",keytype,255,1);
			indexBT.close();

			//scan sorted heap file and insert into btree index
			indexBT = new QuadrupleBTreeFile(name+"/indexBT");
			quadHF = new QuadrupleHeapfile(name+"/quadrupleHF");
			predHF = new LabelHeapFile(name+"/predicateHF");
			TScan am = new TScan(quadHF);
			Quadruple quadruple = null;
			QID qid = new QID();
			double confidence = 0.0;
			while((quadruple = am.getNext(qid)) != null)
			{
				confidence = quadruple.getConfidence();
				String temp = Double.toString(confidence);
				Label object = entityHF.getRecord(quadruple.getObjectID().returnLID());
				//System.out.println("Subject--> "+subject.getLabelKey());
				List<Integer> types = new ArrayList<Integer>();
				types.add(AttrType.attrString);
				types.add(AttrType.attrDouble);
				KeyClass key = new StringKey(object.getLabelKey()+":"+temp, types);
				//System.out.println("Inserting into Btree key"+ predicate.getLabelKey() + ":" + temp + " qid "+qid);
				indexBT.insert(key,qid);
			}
                    /*
                    QuadrupleBTFileScan scan = indexBT.new_scan(null,null);
                    KeyDataEntry entry = null;
                    while((entry = scan.get_next())!= null)
                    {
                            System.out.println("Key found : " + ((StringKey)(entry.key)).getKey());
                    }
                    scan.DestroyBTreeFileScan();
                    */
			am.closescan();
			indexBT.close();
		}
		catch(Exception e)
		{
			System.err.println ("* Error creating Index for option2 " + e);
			e.printStackTrace();
			Runtime.getRuntime().exit(1);
		}

	}

	private void destroyIndex(String filename)
	{
		try
		{
			if(filename != null)
			{

				QuadrupleBTreeFile bfile = new QuadrupleBTreeFile(filename);

				QuadrupleBTFileScan scan = bfile.new_scan(null,null);
				QID qid = null;
				KeyDataEntry entry = null;
				ArrayList<KeyClass> keys = new ArrayList<KeyClass>();
				ArrayList<QID> qids = new ArrayList<QID>();
				int count = 0;

				while((entry = scan.get_next())!= null)
				{
					qid =  ((QuadrupleLeafData)entry.data).getData();
					keys.add(entry.key);
					qids.add(qid);
					count++;
				}
				scan.DestroyBTreeFileScan();

				for(int i = 0; i < count ;i++)
				{
					bfile.Delete(keys.get(i),qids.get(i));
				}

				bfile.close();

			}
		}
		catch(GetFileEntryException e1)
		{
			System.out.println("Firsttime No index present.. Expected");
		}
		catch(Exception e)
		{
			System.err.println ("*** Error destroying Index " + e);
			e.printStackTrace();
			Runtime.getRuntime().exit(1);
		}

	}

	/** Open the database with the given name.
	 *
	 * @param fname DB_name
	 *
	 * @exception IOException I/O errors
	 * @exception FileIOException file I/O error
	 * @exception InvalidPageNumberException invalid page number
	 * @exception DiskMgrException error caused by other layers
	 */
	public void openDB( String fname)
			throws IOException,
			InvalidPageNumberException,
			FileIOException,
			DiskMgrException {

		name = fname;

		// Creaat a random access file
		fp = new RandomAccessFile(fname, "rw");

		PageId pageId = new PageId();
		Page apage = new Page();
		pageId.pid = 0;

		num_pages = 1;	//temporary num_page value for pinpage to work

		pinPage(pageId, apage, false /*read disk*/);


		rdfDBFirstPage firstpg = new rdfDBFirstPage();
		firstpg.openPage(apage);
		num_pages = firstpg.getNumDBPages();

		unpinPage(pageId, false /* undirty*/);
	}

	/** DB Constructors.
	 * Create a database with the specified number of pages where the page
	 * size is the default page size.
	 *
	 * @param fname DB name
	 * @param num_pgs number of pages in DB
	 *
	 * @exception IOException I/O errors
	 * @exception InvalidPageNumberException invalid page number
	 * @exception FileIOException file I/O error
	 * @exception DiskMgrException error caused by other layers
	 */
	public void openDB( String fname, int num_pgs)
			throws IOException,
			InvalidPageNumberException,
			FileIOException,
			DiskMgrException {

		name = new String(fname);
		num_pages = (num_pgs > 2) ? num_pgs : 2;

		File DBfile = new File(name);

		DBfile.delete();

		// Creaat a random access file
		fp = new RandomAccessFile(fname, "rw");

		// Make the file num_pages pages long, filled with zeroes.
		fp.seek((long)(num_pages*MINIBASE_PAGESIZE-1));
		fp.writeByte(0);

		// Initialize space map and directory pages.

		// Initialize the first DB page
		Page apage = new Page();
		PageId pageId = new PageId();
		pageId.pid = 0;
		pinPage(pageId, apage, true /*no diskIO*/);

		rdfDBFirstPage firstpg = new rdfDBFirstPage(apage);

		firstpg.setNumDBPages(num_pages);
		unpinPage(pageId, true /*dirty*/);

		// Calculate how many pages are needed for the space map.  Reserve pages
		// 0 and 1 and as many additional pages for the space map as are needed.
		int num_map_pages = (num_pages + bits_per_page -1)/bits_per_page;

		set_bits(pageId, 1+num_map_pages, 1);

	}

	/** Close DB file.
	 * @exception IOException I/O errors.
	 */
	public void closeDB() throws IOException {
		fp.close();
	}


	/** Destroy the database, removing the file that stores it.
	 * @exception IOException I/O errors.
	 */
	public void DBDestroy()
			throws IOException {

		fp.close();
		File DBfile = new File(name);
		DBfile.delete();
	}

	/** Read the contents of the specified page into a Page object
	 *
	 * @param pageno pageId which will be read
	 * @param apage page object which holds the contents of page
	 *
	 * @exception InvalidPageNumberException invalid page number
	 * @exception FileIOException file I/O error
	 * @exception IOException I/O errors
	 */
	public  void read_page(PageId pageno, Page apage)
			throws InvalidPageNumberException,
			FileIOException,
			IOException {

		if((pageno.pid < 0)||(pageno.pid >= num_pages))
			throw new InvalidPageNumberException(null, "BAD_PAGE_NUMBER");

		// Seek to the correct page
		fp.seek((long)(pageno.pid *MINIBASE_PAGESIZE));

		// Read the appropriate number of bytes.
		byte [] buffer = apage.getpage();  //new byte[MINIBASE_PAGESIZE];
		try{
			fp.read(buffer);
			PCounter.readIncrement();
		}
		catch (IOException e) {
			throw new FileIOException(e, "DB file I/O error");
		}

	}

	/** Write the contents in a page object to the specified page.
	 *
	 * @param pageno pageId will be wrote to disk
	 * @param apage the page object will be wrote to disk
	 *
	 * @exception InvalidPageNumberException invalid page number
	 * @exception FileIOException file I/O error
	 * @exception IOException I/O errors
	 */
	public void write_page(PageId pageno, Page apage)
			throws InvalidPageNumberException,
			FileIOException,
			IOException {

		if((pageno.pid < 0)||(pageno.pid >= num_pages))
			throw new InvalidPageNumberException(null, "INVALID_PAGE_NUMBER");

		// Seek to the correct page
		fp.seek((long)(pageno.pid *MINIBASE_PAGESIZE));

		// Write the appropriate number of bytes.
		try{
			fp.write(apage.getpage());
			PCounter.writeIncrement();
		}
		catch (IOException e) {
			throw new FileIOException(e, "DB file I/O error");
		}

	}

	/** Allocate a set of pages where the run size is taken to be 1 by default.
	 *  Gives back the page number of the first page of the allocated run.
	 *  with default run_size =1
	 *
	 * @param start_page_num page number to start with
	 *
	 * @exception OutOfSpaceException database is full
	 * @exception InvalidRunSizeException invalid run size
	 * @exception InvalidPageNumberException invalid page number
	 * @exception FileIOException DB file I/O errors
	 * @exception IOException I/O errors
	 * @exception DiskMgrException error caused by other layers
	 */
	public void allocate_page(PageId start_page_num)
			throws OutOfSpaceException,
			InvalidRunSizeException,
			InvalidPageNumberException,
			FileIOException,
			DiskMgrException,
			IOException {
		allocate_page(start_page_num, 1);
	}

	/** user specified run_size
	 *
	 * @param start_page_num the starting page id of the run of pages
	 * @param runsize the number of page need allocated
	 *
	 * @exception OutOfSpaceException No space left
	 * @exception InvalidRunSizeException invalid run size
	 * @exception InvalidPageNumberException invalid page number
	 * @exception FileIOException file I/O error
	 * @exception IOException I/O errors
	 * @exception DiskMgrException error caused by other layers
	 */
	public void allocate_page(PageId start_page_num, int runsize)
			throws OutOfSpaceException,
			InvalidRunSizeException,
			InvalidPageNumberException,
			FileIOException,
			DiskMgrException,
			IOException {

		if(runsize < 0) throw new InvalidRunSizeException(null, "Negative run_size");
		int run_size = runsize;
		int num_map_pages = (num_pages + bits_per_page -1)/bits_per_page;
		int current_run_start = 0;
		int current_run_length = 0;


		// This loop goes over each page in the space map.
		PageId pgid = new PageId();
		byte [] pagebuf;
		int byteptr;

		for(int i=0; i< num_map_pages; ++i) {// start forloop01

			pgid.pid = 1 + i;
			// Pin the space-map page.

			Page apage = new Page();
			pinPage(pgid, apage, false /*read disk*/);

			pagebuf = apage.getpage();
			byteptr = 0;

			// get the num of bits on current page
			int num_bits_this_page = num_pages - i*bits_per_page;
			if(num_bits_this_page > bits_per_page)
				num_bits_this_page = bits_per_page;

			// Walk the page looking for a sequence of 0 bits of the appropriate
			// length.  The outer loop steps through the page's bytes, the inner
			// one steps through each byte's bits.
			for(; num_bits_this_page>0
					&& current_run_length < run_size; ++byteptr) {// start forloop02


				Integer intmask = new Integer(1);
				Byte mask = new Byte(intmask.byteValue());
				byte tmpmask = mask.byteValue();


				while (mask.intValue()!=0 && (num_bits_this_page>0)
						&&(current_run_length < run_size))

				{
					if( (pagebuf[byteptr] & tmpmask ) != 0)
					{
						current_run_start += current_run_length + 1;
						current_run_length = 0;
					}
					else{
						++current_run_length;
					}


					tmpmask <<=1;
					mask = new Byte(tmpmask);
					--num_bits_this_page;
				}


			}//end of forloop02
			// Unpin the space-map page.

			unpinPage(pgid, false /*undirty*/);

		}// end of forloop01
		if(current_run_length >= run_size)
		{
			start_page_num.pid = current_run_start;
			set_bits(start_page_num, run_size, 1);

			return;
		}

		throw new OutOfSpaceException(null, "No space left");
	}

	/** Deallocate a set of pages starting at the specified page number and
	 * a run size can be specified.
	 *
	 * @param start_page_num the start pageId to be deallocate
	 * @param run_size the number of pages to be deallocated
	 *
	 * @exception InvalidRunSizeException invalid run size
	 * @exception InvalidPageNumberException invalid page number
	 * @exception FileIOException file I/O error
	 * @exception IOException I/O errors
	 * @exception DiskMgrException error caused by other layers
	 */
	public void deallocate_page(PageId start_page_num, int run_size)
			throws InvalidRunSizeException,
			InvalidPageNumberException,
			IOException,
			FileIOException,
			DiskMgrException {

		if(run_size < 0) throw new InvalidRunSizeException(null, "Negative run_size");

		set_bits(start_page_num, run_size, 0);
	}

	/** Deallocate a set of pages starting at the specified page number
	 *  with run size = 1
	 *
	 * @param start_page_num the start pageId to be deallocate
	 *
	 * @exception InvalidRunSizeException invalid run size
	 * @exception InvalidPageNumberException invalid page number
	 * @exception FileIOException file I/O error
	 * @exception IOException I/O errors
	 * @exception DiskMgrException error caused by other layers
	 *
	 */
	public void deallocate_page(PageId start_page_num)
			throws InvalidRunSizeException,
			InvalidPageNumberException,
			IOException,
			FileIOException,
			DiskMgrException {

		set_bits(start_page_num, 1, 0);
	}

	/** Adds a file entry to the header page(s).
	 *
	 * @param fname file entry name
	 * @param start_page_num the start page number of the file entry
	 *
	 * @exception FileNameTooLongException invalid file name (too long)
	 * @exception InvalidPageNumberException invalid page number
	 * @exception InvalidRunSizeException invalid DB run size
	 * @exception DuplicateEntryException entry for DB is not unique
	 * @exception OutOfSpaceException database is full
	 * @exception FileIOException file I/O error
	 * @exception IOException I/O errors
	 * @exception DiskMgrException error caused by other layers
	 */
	public void add_file_entry(String fname, PageId start_page_num)
			throws FileNameTooLongException,
			InvalidPageNumberException,
			InvalidRunSizeException,
			DuplicateEntryException,
			OutOfSpaceException,
			FileIOException,
			IOException,
			DiskMgrException {

		if(fname.length() >= MAX_NAME)
			throw new FileNameTooLongException(null, "DB filename too long");
		if((start_page_num.pid < 0)||(start_page_num.pid >= num_pages))
			throw new InvalidPageNumberException(null, " DB bad page number");

		// Does the file already exist?

		if( get_file_entry(fname) != null)
			throw new DuplicateEntryException(null, "DB fileentry already exists");

		Page apage = new Page();

		boolean found = false;
		int free_slot = 0;
		PageId hpid = new PageId();
		PageId nexthpid = new PageId(0);
		rdfDBHeaderPage dp;
		do
		{// Start DO01
			//  System.out.println("start do01");
			hpid.pid = nexthpid.pid;

			// Pin the header page
			pinPage(hpid, apage, false /*read disk*/);

			// This complication is because the first page has a different
			// structure from that of subsequent pages.
			if(hpid.pid==0)
			{
				dp = new rdfDBFirstPage();
				((rdfDBFirstPage) dp).openPage(apage);
			}
			else
			{
				dp = new rdfDBDirectoryPage();
				((rdfDBDirectoryPage) dp).openPage(apage);
			}

			nexthpid = dp.getNextPage();
			int entry = 0;

			PageId tmppid = new PageId();
			while(entry < dp.getNumOfEntries())
			{
				dp.getFileEntry(tmppid, entry);
				if(tmppid.pid == INVALID_PAGE)  break;
				entry ++;
			}

			if(entry < dp.getNumOfEntries())
			{
				free_slot = entry;
				found = true;
			}
			else if (nexthpid.pid != INVALID_PAGE)
			{
				// We only unpin if we're going to continue looping.
				unpinPage(hpid, false /* undirty*/);
			}

		}while((nexthpid.pid != INVALID_PAGE)&&(!found)); // End of DO01

		// Have to add a new header page if possible.
		if(!found)
		{
			try{
				allocate_page(nexthpid);
			}
			catch(Exception e){         //need rethrow an exception!!!!
				unpinPage(hpid, false /* undirty*/);
				e.printStackTrace();
			}

			// Set the next-page pointer on the previous directory page.
			dp.setNextPage(nexthpid);
			unpinPage(hpid, true /* dirty*/);

			// Pin the newly-allocated directory page.
			hpid.pid = nexthpid.pid;

			pinPage(hpid, apage, true/*no diskIO*/);
			dp = new rdfDBDirectoryPage(apage);

			free_slot = 0;
		}

		// At this point, "hpid" has the page id of the header page with the free
		// slot; "pg" points to the pinned page; "dp" has the directory_page
		// pointer; "free_slot" is the entry number in the directory where we're
		// going to put the new file entry.

		dp.setFileEntry(start_page_num, fname, free_slot);

		unpinPage(hpid, true /* dirty*/);

	}

	/** Delete the entry corresponding to a file from the header page(s).
	 *
	 * @param fname file entry name
	 *
	 * @exception FileEntryNotFoundException file does not exist
	 * @exception FileIOException file I/O error
	 * @exception IOException I/O errors
	 * @exception InvalidPageNumberException invalid page number
	 * @exception DiskMgrException error caused by other layers
	 */
	public void delete_file_entry(String fname)
			throws FileEntryNotFoundException,
			IOException,
			FileIOException,
			InvalidPageNumberException,
			DiskMgrException {

		Page apage = new Page();
		boolean found = false;
		int slot = 0;
		PageId hpid = new PageId();
		PageId nexthpid = new PageId(0);
		PageId tmppid = new PageId();
		rdfDBHeaderPage dp;

		do
		{ // startDO01
			hpid.pid = nexthpid.pid;

			// Pin the header page.
			pinPage(hpid, apage, false/*read disk*/);

			// This complication is because the first page has a different
			// structure from that of subsequent pages.
			if(hpid.pid==0)
			{
				dp = new rdfDBFirstPage();
				((rdfDBFirstPage)dp).openPage(apage);
			}
			else
			{
				dp = new rdfDBDirectoryPage();
				((rdfDBDirectoryPage) dp).openPage(apage);
			}
			nexthpid = dp.getNextPage();

			int entry = 0;

			String tmpname;
			while(entry < dp.getNumOfEntries())
			{
				tmpname = dp.getFileEntry(tmppid, entry);

				if((tmppid.pid != INVALID_PAGE)&&
						(tmpname.compareTo(fname) == 0)) break;
				entry ++;
			}

			if(entry < dp.getNumOfEntries())
			{
				slot = entry;
				found = true;
			}
			else
			{
				unpinPage(hpid, false /*undirty*/);
			}

		} while((nexthpid.pid != INVALID_PAGE) && (!found)); // EndDO01

		if(!found)  // Entry not found - nothing deleted
			throw new FileEntryNotFoundException(null, "DB file not found");

		// Have to delete record at hpnum:slot
		tmppid.pid = INVALID_PAGE;
		dp.setFileEntry(tmppid, "\0", slot);

		unpinPage(hpid, true /*dirty*/);

	}

	/** Get the entry corresponding to the given file.
	 *
	 * @param name file entry name
	 *
	 * @exception IOException I/O errors
	 * @exception FileIOException file I/O error
	 * @exception InvalidPageNumberException invalid page number
	 * @exception DiskMgrException error caused by other layers
	 */
	public PageId get_file_entry(String name)
			throws IOException,
			FileIOException,
			InvalidPageNumberException,
			DiskMgrException {

		Page apage = new Page();
		boolean found = false;
		int slot = 0;
		PageId hpid = new PageId();
		PageId nexthpid = new PageId(0);
		rdfDBHeaderPage dp;

		do
		{// Start DO01

			// System.out.println("get_file_entry do-loop01: "+name);
			hpid.pid = nexthpid.pid;

			// Pin the header page.
			pinPage(hpid, apage, false /*no diskIO*/);

			// This complication is because the first page has a different
			// structure from that of subsequent pages.
			if(hpid.pid==0)
			{
				dp = new rdfDBFirstPage();
				((rdfDBFirstPage) dp).openPage(apage);
			}
			else
			{
				dp = new rdfDBDirectoryPage();
				((rdfDBDirectoryPage) dp).openPage(apage);
			}
			nexthpid = dp.getNextPage();

			int entry = 0;
			PageId tmppid = new PageId();
			String tmpname;

			while(entry < dp.getNumOfEntries())
			{
				tmpname = dp.getFileEntry(tmppid, entry);

				if((tmppid.pid != INVALID_PAGE)&&
						(tmpname.compareTo(name) == 0)) break;
				entry ++;
			}
			if(entry < dp.getNumOfEntries())
			{
				slot =  entry;
				found = true;
			}

			unpinPage(hpid, false /*undirty*/);

		}while((nexthpid.pid!=INVALID_PAGE)&&(!found));// End of DO01

		if(!found)  // Entry not found - don't post error, just fail.
		{
			//  System.out.println("entry NOT found");
			return null;
		}

		PageId startpid = new PageId();
		dp.getFileEntry(startpid, slot);
		return startpid;
	}

	/** Functions to return some characteristics of the database.
	 */
	public String db_name(){return name;}
	public int db_num_pages(){return num_pages;}
	public int db_page_size(){return MINIBASE_PAGESIZE;}

	/** Print out the space map of the database.
	 * The space map is a bitmap showing which
	 * pages of the db are currently allocated.
	 *
	 * @exception FileIOException file I/O error
	 * @exception IOException I/O errors
	 * @exception InvalidPageNumberException invalid page number
	 * @exception DiskMgrException error caused by other layers
	 */
	public void dump_space_map()
			throws DiskMgrException,
			IOException,
			FileIOException,
			InvalidPageNumberException

	{

		System.out.println ("********  IN DUMP");
		int num_map_pages = (num_pages + bits_per_page -1)/bits_per_page;
		int bit_number = 0;

		// This loop goes over each page in the space map.
		PageId pgid = new PageId();
		System.out.println ("num_map_pages = " + num_map_pages);
		System.out.println ("num_pages = " + num_pages);
		for(int i=0; i< num_map_pages; i++)
		{//start forloop01

			pgid.pid = 1 + i;   //space map starts at page1
			// Pin the space-map page.
			Page apage = new Page();
			pinPage(pgid, apage, false/*read disk*/);

			// How many bits should we examine on this page?
			int num_bits_this_page = num_pages - i*bits_per_page;
			System.out.println ("num_bits_this_page = " + num_bits_this_page);
			System.out.println ("num_pages = " + num_pages);
			if ( num_bits_this_page > bits_per_page )
				num_bits_this_page = bits_per_page;

			// Walk the page looking for a sequence of 0 bits of the appropriate
			// length.  The outer loop steps through the page's bytes, the inner
			// one steps through each byte's bits.

			int pgptr = 0;
			byte [] pagebuf = apage.getpage();
			int mask;
			for ( ; num_bits_this_page > 0; pgptr ++)
			{// start forloop02

				for(mask=1;
					mask < 256 && num_bits_this_page > 0;
					mask=(mask<<1), --num_bits_this_page, ++bit_number )
				{//start forloop03

					int bit = pagebuf[pgptr] & mask;
					if((bit_number%10) == 0)
						if((bit_number%50) == 0)
						{
							if(bit_number>0) System.out.println("\n");
							System.out.print("\t" + bit_number +": ");
						}
						else System.out.print(' ');

					if(bit != 0) System.out.print("1");
					else System.out.print("0");

				}//end of forloop03

			}//end of forloop02

			unpinPage(pgid, false /*undirty*/);

		}//end of forloop01

		System.out.println();


	}

	/** Set runsize bits starting from start to value specified
	 */
	private void set_bits( PageId start_page, int run_size, int bit )
			throws InvalidPageNumberException,
			FileIOException,
			IOException,
			DiskMgrException {

		if((start_page.pid<0) || (start_page.pid+run_size > num_pages))
			throw new InvalidPageNumberException(null, "Bad page number");

		// Locate the run within the space map.
		int first_map_page = start_page.pid/bits_per_page + 1;
		int last_map_page = (start_page.pid+run_size-1)/bits_per_page +1;
		int first_bit_no = start_page.pid % bits_per_page;

		// The outer loop goes over all space-map pages we need to touch.

		for(PageId pgid = new PageId(first_map_page);
			pgid.pid <= last_map_page;
			pgid.pid = pgid.pid+1, first_bit_no = 0)
		{//Start forloop01

			// Pin the space-map page.
			Page pg = new Page();


			pinPage(pgid, pg, false/*no diskIO*/);


			byte [] pgbuf = pg.getpage();

			// Locate the piece of the run that fits on this page.
			int first_byte_no = first_bit_no/8;
			int first_bit_offset = first_bit_no%8;
			int last_bit_no = first_bit_no + run_size -1;

			if(last_bit_no >= bits_per_page )
				last_bit_no = bits_per_page - 1;

			int last_byte_no = last_bit_no / 8;

			// This loop actually flips the bits on the current page.
			int cur_posi = first_byte_no;
			for(;cur_posi <= last_byte_no; ++cur_posi, first_bit_offset=0)
			{//start forloop02

				int max_bits_this_byte = 8 - first_bit_offset;
				int num_bits_this_byte = (run_size > max_bits_this_byte?
						max_bits_this_byte : run_size);

				int imask =1;
				int temp;
				imask = ((imask << num_bits_this_byte) -1)<<first_bit_offset;
				Integer intmask = new Integer(imask);
				Byte mask = new Byte(intmask.byteValue());
				byte bytemask = mask.byteValue();

				if(bit==1)
				{
					temp = (pgbuf[cur_posi] | bytemask);
					intmask = new Integer(temp);
					pgbuf[cur_posi] = intmask.byteValue();
				}
				else
				{

					temp = pgbuf[cur_posi] & (255^bytemask);
					intmask = new Integer(temp);
					pgbuf[cur_posi] = intmask.byteValue();
				}
				run_size -= num_bits_this_byte;

			}//end of forloop02

			// Unpin the space-map page.

			unpinPage(pgid, true /*dirty*/);

		}//end of forloop01

	}

	/**
	 * short cut to access the pinPage function in bufmgr package.
	 * @see bufmgr.pinPage
	 */
	private void pinPage(PageId pageno, Page page, boolean emptyPage)
			throws DiskMgrException {

		try {
			SystemDefs.JavabaseBM.pinPage(pageno, page, emptyPage);
		}
		catch (Exception e) {
			throw new DiskMgrException(e,"DB.java: pinPage() failed");
		}

	} // end of pinPage

	/**
	 * short cut to access the unpinPage function in bufmgr package.
	 * @see bufmgr.unpinPage
	 */
	private void unpinPage(PageId pageno, boolean dirty)
			throws DiskMgrException {

		try {
			SystemDefs.JavabaseBM.unpinPage(pageno, dirty);
		}
		catch (Exception e) {
			throw new DiskMgrException(e,"DB.java: unpinPage() failed");
		}

	} // end of unpinPage

}//end of rdfDB class

/** Super class of the directory page and first page
 */
class rdfDBHeaderPage implements PageUsedBytes, GlobalConst {

	protected static final int NEXT_PAGE = 0;
	protected static final int NUM_OF_ENTRIES = 4;
	protected static final int START_FILE_ENTRIES = 8;
	protected static final int SIZE_OF_FILE_ENTRY = 4 + MAX_NAME + 2;

	protected byte [] data;

	/**
	 * Default constructor
	 */
	public rdfDBHeaderPage ()
	{  }

	/**
	 * Constrctor of class rdfDBHeaderPage
	 * @param page a page of Page object
	 * @param pageusedbytes number of bytes used on the page
	 * @exception IOException
	 */
	public rdfDBHeaderPage(Page page, int pageusedbytes)
			throws IOException
	{
		data = page.getpage();
		PageId pageno = new PageId();
		pageno.pid = INVALID_PAGE;
		setNextPage(pageno);

		PageId temppid = getNextPage();

		int num_entries  = (MAX_SPACE - pageusedbytes) /SIZE_OF_FILE_ENTRY;
		setNumOfEntries(num_entries);

		for ( int index=0; index < num_entries; ++index )
			initFileEntry(INVALID_PAGE,  index);
	}

	/**
	 * set the next page number
	 * @param pageno next page ID
	 * @exception IOException I/O errors
	 */
	public void setNextPage(PageId pageno)
			throws IOException
	{
		Convert.setIntValue(pageno.pid, NEXT_PAGE, data);
	}

	/**
	 * return the next page number
	 * @return next page ID
	 * @exception IOException I/O errors
	 */
	public PageId getNextPage()
			throws IOException
	{
		PageId nextPage = new PageId();
		nextPage.pid= Convert.getIntValue(NEXT_PAGE, data);
		return nextPage;
	}

	/**
	 * set number of entries on this page
	 * @param numEntries the number of entries
	 * @exception IOException I/O errors
	 */

	protected void setNumOfEntries(int numEntries)
			throws IOException
	{
		Convert.setIntValue (numEntries, NUM_OF_ENTRIES, data);
	}

	/**
	 * return the number of file entries on the page
	 * @return number of entries
	 * @exception IOException I/O errors
	 */
	public int getNumOfEntries()
			throws IOException
	{
		return Convert.getIntValue(NUM_OF_ENTRIES, data);
	}

	/**
	 * initialize file entries as empty
	 * @param empty invalid page number (=-1)
	 * @param entryNo file entry number
	 * @exception IOException I/O errors
	 */
	private void initFileEntry(int empty, int entryNo)
			throws IOException {
		int position = START_FILE_ENTRIES + entryNo * SIZE_OF_FILE_ENTRY;
		Convert.setIntValue (empty, position, data);
	}

	/**
	 * set file entry
	 * @param pageNo page ID
	 * @param fname the file name
	 * @param entryNo file entry number
	 * @exception IOException I/O errors
	 */
	public  void setFileEntry(PageId pageNo, String fname, int entryNo)
			throws IOException {

		int position = START_FILE_ENTRIES + entryNo * SIZE_OF_FILE_ENTRY;
		Convert.setIntValue (pageNo.pid, position, data);
		Convert.setStrValue (fname, position +4, data);
	}

	/**
	 * return file entry info
	 * @param pageNo page Id
	 * @param entryNo the file entry number
	 * @return file name
	 * @exception IOException I/O errors
	 */
	public String getFileEntry(PageId pageNo, int entryNo)
			throws IOException {

		int position = START_FILE_ENTRIES + entryNo * SIZE_OF_FILE_ENTRY;
		pageNo.pid = Convert.getIntValue (position, data);
		return (Convert.getStrValue (position+4, data, MAX_NAME + 2));
	}

}

/**
 * rdfDBFirstPage class which is a subclass of rdfDBHeaderPage class
 */
class rdfDBFirstPage extends rdfDBHeaderPage {

	protected static final int NUM_DB_PAGE = MINIBASE_PAGESIZE -4;

	/**
	 * Default construtor
	 */
	public rdfDBFirstPage()  { super();}

	/**
	 * Constructor of class rdfDBFirstPage class
	 * @param page a page of Page object
	 * @exception IOException I/O errors
	 */
	public rdfDBFirstPage(Page page)
			throws IOException
	{
		super(page, FIRST_PAGE_USED_BYTES);
	}

	/** open an exist DB first page
	 * @param page a page of Page object
	 */
	public void openPage(Page page)
	{
		data = page.getpage();
	}


	/**
	 * set number of pages in the DB
	 * @param num the number of pages in DB
	 * @exception IOException I/O errors
	 */
	public void setNumDBPages(int num)
			throws IOException
	{
		Convert.setIntValue (num, NUM_DB_PAGE, data);
	}

	/**
	 * return the number of pages in the DB
	 * @return number of pages in DB
	 * @exception IOException I/O errors
	 */
	public int getNumDBPages()
			throws IOException {

		return (Convert.getIntValue(NUM_DB_PAGE, data));
	}

}

/**
 * rdfDBDirectoryPage class which is a subclass of rdfDBHeaderPage class
 */
class rdfDBDirectoryPage extends rdfDBHeaderPage  { //implements PageUsedBytes

	/**
	 * Default constructor
	 */
	public rdfDBDirectoryPage ()  { super(); }

	/**
	 * Constructor of rdfDBDirectoryPage class
	 * @param page a page of Page object
	 * @exception IOException
	 */
	public rdfDBDirectoryPage(Page page)
			throws IOException
	{
		super(page, DIR_PAGE_USED_BYTES);
	}

	/** open an exist DB directory page
	 * @param page a page of Page object
	 */
	public void openPage(Page page)
	{
		data = page.getpage();
	}

}
