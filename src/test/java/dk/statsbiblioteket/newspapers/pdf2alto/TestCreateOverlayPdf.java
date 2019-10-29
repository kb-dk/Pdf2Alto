package dk.statsbiblioteket.newspapers.pdf2alto;

public class TestCreateOverlayPdf {

  
  public static void main(String[] args){
    
    
    try{      
      //change this path of a PDF file
      String pdfInputFile = "/media/teg/500GB/ALTO-test/130019436056_bw.pdf";
      String pdfOverlayFile = "/media/teg/500GB/ALTO-test/130019436056_bw_overlay.pdf";
            
      PDF2AltoGenerator.makePdfOverlay(pdfInputFile, pdfOverlayFile);
      }
      catch(Exception e){
        e.printStackTrace();
      }
      
    
  }
  
}
