package dk.statsbiblioteket.newspapers.pdf2alto.batchdelivery;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

import dk.statsbiblioteket.newspapers.pdf2alto.PDF2AltoGenerator;

public class ProcessPdf2AltoRunner {


  /*
   * This class is just used to generate ALTO from a folder with PDF's
   *
   */

  public static HashSet<String> pagesFolders = new  HashSet<String> ();

  public static void main(String[] args) throws Exception{

    try{

      String topLevel="/media/teg/1200GB_SSD/arhusstiftstidende/";
      findPagesFolders(new File(topLevel)); //All folders with names pages. These contains the PDFS.


      for ( String pageFolder : pagesFolders){
        System.out.println("folder:"+pageFolder);
        ArrayList<String> pdfsSortedForDir = getPdfsSortedForDir(new File( pageFolder));

        for (String currentPdf : pdfsSortedForDir){


          System.out.println("processing:"+currentPdf);
          String jp2FileName =  getJp2NameFromFullPath(currentPdf);
          String xmlFileName =  getXmlNameFromFullPath(currentPdf);		
          int pageNumber = getPageNumberFromFileName(currentPdf);					

          PDF2AltoGenerator.makeAlto(currentPdf, "/media/teg/1200GB_SSD/alto/alto_xml/"+xmlFileName,"image\\"+  jp2FileName,pageNumber);				
        }

      }

    }
    catch(Exception e){
      e.printStackTrace();
    }

  }

  public static void findPagesFolders(File dir) {

    File[] files = dir.listFiles();
    for (File file : files) {
      if (file.isDirectory()  ) {
        if (file.getAbsolutePath().endsWith("pages")){
          pagesFolders.add(file.getAbsolutePath());
        }
        findPagesFolders(file);
      }
    }		
  }

  public static ArrayList<String> getPdfsSortedForDir(File dir){
    if (!dir.exists()) {
      System.out.println("File does not exist:"+dir);
    }
    if (!dir.isDirectory()) {
      System.out.println("File does not directory:"+dir);
    }

    ArrayList<String> pdfs = new ArrayList<String>();
    File[] files = dir.listFiles();		 
    for (File f : files){
      if (f.getName().endsWith(".pdf")){
        pdfs.add(f.getAbsolutePath());		    	 
      } 
    }

    Collections.sort(pdfs);
    return pdfs;
  }

  public static String getJp2NameFromFullPath(String name){
    File f = new File(name);
    String fileName= f.getName();

    return fileName.replaceAll(".pdf", ".jp2");		
  }


  public static String getXmlNameFromFullPath(String name){
    File f = new File(name);
    String fileName= f.getName();

    return fileName.replaceAll(".pdf", ".alto.xml");		
  }

  public static int getPageNumberFromFileName(String name){
    int index = name.indexOf("_page");
    String pageStr = name.substring(index+5,index+8);		

    return new Integer(pageStr).intValue();  	
  }

}
