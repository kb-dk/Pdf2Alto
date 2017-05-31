package dk.statsbiblioteket.newspapers.pdf2alto.batchdelivery;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;

public class CreateModFiles {



  /*
   * This class is just used to generate the XML mod files in a batch delivery
   *
   * 
   * MD5 sum in terminal: for F in *; do md5sum -b $F > ${F}.md5 ; done
   */

  public static void main(String[] args) throws Exception{	
    try{


      String folder="/media/teg/1200GB_SSD/alto/real batch/B990026100907-RT1/990026100907-01/2017-04-08-01/";
      ArrayList<String> altoSortedForDir = getAltoSortedForDir(new File( folder));

      for (String file : altoSortedForDir){
        int page =getPageNumberFromFileName(file);
        String newFileName=folder+getModNameFromFullPath(file);     	
        String xml = getModXML("bt", page);
        Files.write(Paths.get(newFileName),xml.getBytes()); //write to file
      }

    }
    catch(Exception e){
      e.printStackTrace();
    }	
  }


  private static String getModXML(String papername, int pageNumber){

    String xml=""+
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+"\n"+
        "<mods:mods xmlns:mods=\"http://www.loc.gov/mods/v3\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"http://www.loc.gov/mods/v3\" xsi:schemaLocation=\"http://www.loc.gov/mods/v3/ mods-3-1.xsd\">"+"\n"+
        "<mods:part><mods:extent unit=\"pages\">"+"\n"+
        "<mods:start>"+pageNumber+"</mods:start>"+"\n"+
        "</mods:extent>"+"\n"+
        "<mods:detail type=\"pageNumber\">"+"\n"+
        "<mods:number>"+pageNumber+"</mods:number>"+"\n"+
        "</mods:detail>"+"\n"+
        "</mods:part>"+"\n"+
        "<mods:relatedItem type=\"original\">"+"\n"+
        "<mods:identifier type=\"reel number\">990026100907-01</mods:identifier>"+"\n"+
        "<mods:identifier type=\"reel sequence number\">17A</mods:identifier>"+"\n"+
        "<mods:physicalDescription>"+"\n"+
        "<mods:form type=\"pdf\"/>"+"\n"+
        "</mods:physicalDescription>"+"\n"+
        "<mods:note type=\"noteAboutReproduction\">present</mods:note>"+"\n"+
        "</mods:relatedItem>"+"\n"+
        "<mods:relatedItem type=\"host\">"+"\n"+
        "<mods:titleInfo type=\"uniform\" authority=\"Statens Avissamling\">"+"\n"+
        "<mods:title>"+papername+"</mods:title>"+"\n"+
        "</mods:titleInfo>"+"\n"+
        "</mods:relatedItem>"+"\n"+
        "</mods:mods>"+"\n";

    return xml;

  }
  public static ArrayList<String> getAltoSortedForDir(File dir){
    if (!dir.exists()) {
      System.out.println("File does not exist:"+dir);
    }
    if (!dir.isDirectory()) {
      System.out.println("File does not directory:"+dir);
    }

    ArrayList<String> pdfs = new ArrayList<String>();
    File[] files = dir.listFiles();		 
    for (File f : files){
      if (f.getName().endsWith(".alto.xml")){
        pdfs.add(f.getAbsolutePath());		    	 
      } 
    }

    Collections.sort(pdfs);
    return pdfs;
  }

  public static String getModNameFromFullPath(String name){
    File f = new File(name);
    String fileName= f.getName();

    return fileName.replaceAll(".alto.xml", ".mods.xml");		
  }


  public static int getPageNumberFromFileName(String name){
    int index = name.indexOf("_page");
    String pageStr = name.substring(index+5,index+8);		

    return new Integer(pageStr).intValue();  	
  }

}