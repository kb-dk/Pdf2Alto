package dk.statsbiblioteket.newspapers.pdf2alto;

public class TestCreateAlto {

  public static void main(String[] args){
    try{
    
    //change this path of a PDF file
    String pdfInputFile= "/media/teg/500GB/ALTO-test/130019436056_bw.pdf";
    String altoFile = "/media/teg/500GB/ALTO-test/13130019436056_bw.xml"; //will create this file
    
    
    PDF2AltoGenerator.makeAlto(pdfInputFile, altoFile, "(none)", 1);
    }
    catch(Exception e){
      e.printStackTrace();
    }
    
    
  }
  
  
}
