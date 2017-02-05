import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import au.com.bytecode.opencsv.CSVReader;

class ConvertCSVtoSQL
{
    public static void main (String[] args) throws IOException
    {

    	File inFile = getFileFromSrc("src/MobileContentAuditForDev_012617.csv");
    	List<String[]> rows = readFileIO(inFile);
    	String updateScriptString = creatSQLFile(rows, 5);
    	String rollBackScriptString = creatSQLFile(rows, 4);
    	
    	//Update script
    	File outfile = getFileFromSrc("src/update-script.sql");
    	writeFileIO(outfile, updateScriptString);
    	
    	//Rollback script
    	File rollbackfile = getFileFromSrc("src/rollback-script.sql");
    	writeFileIO(rollbackfile, rollBackScriptString);
    }
    
    
	public static String creatSQLFile(List<String[]> allRows, int dynamicValueIndex) { 
		String outPut = "";
		System.out.println("number of rows that will processed size=" + allRows.size());
         
        //Read CSV line by line and use the string array as you want
       for(String[] row : allRows){
    	   //System.out.println(Arrays.toString(row));
    	   
    	   String dynamicValueFixed = fixSingleQuoteSpecialChars(row[dynamicValueIndex]);
    	   
    	   boolean shouldSkipRow = shouldSkipRow(dynamicValueFixed);
    	   
    	   if (!shouldSkipRow) {
        	   // start
        	   outPut = outPut + "--" + row[0] + "\n";
        	   outPut = outPut + "SELECT [dbo].[tblDynamicPage] SET RowUpdatedDateTime = GetDate(),  dynamicValue = '"+dynamicValueFixed+"', \n";
        	   outPut = outPut + " WHERE dynamickey = '" + row[1] +"', dynamicForm = '" + row[2] + "', dynamicItem = '" + row[3]+"'; \n";   
    	   }
       }
       //System.out.println(outPut);
       return outPut;
	}
    
	public static boolean shouldSkipRow(String input) {
		if (input == null || input.length() == 0 ) {
			return true;
		}
		if (input.toLowerCase().trim().equals("no change")) {
			return true;
		}
		return false;
	}
	
	public static String fixSingleQuoteSpecialChars(String input) {
		StringBuffer sb = new StringBuffer();
		CharacterIterator charIterator = new CharacterIterator(input);
		
		while (charIterator.hasNext()) {
		  Character next = charIterator.next();
		  Character prev = charIterator.perviousValue();
		  //System.out.println("next='" + next + "'  prev='" + prev + "'");
		  boolean isNextCharWhiteSpace = (next == ' ');
		  if (prev != null && prev == '?' && !isNextCharWhiteSpace) {
			  sb.replace(sb.length()-1, sb.length(), "&#39;");
			  sb.append(next);
		  }
		  else if (next == '\'') {
			  sb.append("&#39;");
		  }
		  else {
			  sb.append(next);  
		  }
		}
		input = sb.toString();
		return input;
	}
	
	public static List<String[]> readFileIO(File file) {
        char cvsSplitBy = ',';	 
        
        //Build reader instance
        CSVReader reader;
        List<String[]> allRows = null;
		try {
			// Build reader instance
			// Read file object
			// Default seperator is cvsSplitBy
			// Default quote character is double quote
			// Start reading from line number 1 (line numbers start from zero)
			reader = new CSVReader(new FileReader(file), cvsSplitBy, '"', 1);
			// Read all rows at once
			allRows = reader.readAll();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
        return allRows;
	}
        
    
	public static File getFileFromSrc(String path) {
		File file = new File(path);
		System.out.println("\t" + "Path : " + file.getAbsolutePath());
		return file;
	}
	
	public static void writeFileIO(File file, String content) {
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {

			content = content != null ? content : "This is the content to write into file. Nothing was passsed in.........\n";

			bw.write(content);

			// no need to close it.
			//bw.close();

			System.out.println("----- writeFileIO Done ----------");

		} catch (IOException e) {

			e.printStackTrace();

		}
	}
  
}
