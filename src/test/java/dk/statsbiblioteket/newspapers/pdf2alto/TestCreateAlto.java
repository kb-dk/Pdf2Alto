package dk.statsbiblioteket.newspapers.pdf2alto;

public class TestCreateAlto {

  public static void main(String[] args){
    try{
    
    //change this path of a PDF file
    String pdfInputFile= "pdfs/20170102_aarhusstiftstidende_section01_page001_ast20170102x11#0001.pdf";
    String altoFile = "target/alto.xml"; //will create this file
    
    
    PDF2AltoGenerator.makeAlto(pdfInputFile, altoFile, "20170408_aarhusstiftstidende.jp2", 1);
    }
    catch(Exception e){
      e.printStackTrace();
    }
    
    
  }
  
  
}
