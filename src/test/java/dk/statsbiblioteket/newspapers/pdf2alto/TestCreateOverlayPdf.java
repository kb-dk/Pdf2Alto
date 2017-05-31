package dk.statsbiblioteket.newspapers.pdf2alto;

public class TestCreateOverlayPdf {

  
  public static void main(String[] args){
    
    try{      
      //change this path of a PDF file
      String pdfInputFile = "pdfs/20170102_aarhusstiftstidende_section01_page001_ast20170102x11#0001.pdf";
      String pdfOverlayFile = "target/overlay.pdf";
            
      PDF2AltoGenerator.makePdfOverlay(pdfInputFile, pdfOverlayFile);
      }
      catch(Exception e){
        e.printStackTrace();
      }
      
    
  }
  
}
