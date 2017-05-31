package dk.statsbiblioteket.newspapers.pdf2alto.batchdelivery;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import org.apache.commons.io.FileUtils;

public class RenameFiles {

    /*
     * This class was just used to rename files in a batch delivery during innovation week. 
     * 
     */
	public static void main(String[] args) throws Exception{
				
		String folder="/media/teg/1200GB_SSD/alto/teg_batch/B990026100907-RT1/990026100907-01/2017-04-08-01/";
	//vestkystenesbjerg-1976-01-02-01-0005.
		ArrayList<String> files = getFilesForDir(new File(folder));
		for (String file :files){
		  int pageNumber = getPageNumberFromFileName(file);
		
		  String ext=getExtension(file);
		  
 		 String pageFill= getPageFillString(pageNumber);
		  System.out.println(file);		  
		   String newName = folder+"bt-2017-04-08-01-"+pageFill+ext;
			System.out.println(newName);
			FileUtils.moveFile(new File(file),  new File(newName));
		}
		
	}

	public static String getPageFillString(int pageNumber) throws Exception{
		if (pageNumber<10){
			return "000"+pageNumber;
		}
		if (pageNumber< 100){
			return "00"+pageNumber;
		}
		throw new Exception("pagenumber too high:"+pageNumber);
		
	}
	
public static int getPageNumberFromFileName(String name){
	int index = name.indexOf("_page");
	String pageStr = name.substring(index+5,index+8);		
	
	return new Integer(pageStr).intValue();  	
  }



public static String getExtension(String name){
	int index = name.indexOf(".");
	String ext = name.substring(index,name.length());		
	
	return ext;  	
  }


	
	public static ArrayList<String> getFilesForDir(File dir){
		if (!dir.exists()) {
			System.out.println("File does not exist:"+dir);
		}
		if (!dir.isDirectory()) {
			System.out.println("File does not directory:"+dir);
		}
		
		ArrayList<String> pdfs = new ArrayList<String>();
		   File[] files = dir.listFiles();		 
		   for (File f : files){
		    	 pdfs.add(f.getAbsolutePath());		    	  
		   }
			  		   
		Collections.sort(pdfs);
		return pdfs;
	}
	
	
}
