package tests.utils;

import tests.utils.DataStructures.*;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

// import StringUtils;


public class readData {
    private BufferedReader br;
    public int numRecords;
    public readData(String fileName) throws FileNotFoundException {
        br = new BufferedReader(new FileReader(fileName));
    }

    public InfoGraph readNextrecord() throws IOException {
        String row = br.readLine();
        if(row != null){
            String[] strArr =row.replaceAll("\t+", " ").replaceAll("\\s+", " ").split(" ");
            numRecords++;
            return new InfoGraph(strArr[0].trim(),strArr[1].trim(),strArr[2].trim(), Double.parseDouble(strArr[3].trim()));
        }
        else {
            return null;
        }

    }
    public void close() throws IOException {
        br.close();
    }

    public ArrayList<InfoGraph> readRows(String fileName) throws Exception{
        ArrayList<String> rows = new ArrayList<String>();
        ArrayList<InfoGraph> decodedRows = new ArrayList<InfoGraph>();
        

        BufferedReader br = new BufferedReader(new FileReader(fileName));

        String line;

        while((line = br.readLine()) != null){
            rows.add(line);
        }
        br.close();

        for(int i = 0; i < rows.size(); i++){
            String[] strArr =rows.get(i).replaceAll("\t+", " ").replaceAll("\\s+", " ").split(" ");
            decodedRows.add(new InfoGraph(strArr[0].trim(),strArr[1].trim(),strArr[2].trim(), Double.parseDouble(strArr[3].trim())));
        }
        System.out.println(rows.size());
        return decodedRows;
    }




    public static void main(String []args) throws Exception{
        readData rdData = new readData(args[0]);
        rdData.readRows(args[0]);
        // System.out.println();
     }
}
