package dk.statsbiblioteket.newspapers.pdf2alto;
/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */




import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDStream;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.util.PDFTextStripper;
import org.apache.pdfbox.util.TextPosition;

import org.apache.pdfbox.pdmodel.common.PDRectangle;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.LinkedList;
import java.lang.Math;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * This code is but ugly, but is a prototype to try to extract text with positions from PDF's using PDFBox
 * Don't read this code and try to understand it. The superclass is high complicated.
 * IMPORTANT: Use of this class can not be multithreaded due to static variables.   
 */
public class PDF2AltoGenerator extends PDFTextStripper
{

  private static StringBuffer altoXml= new StringBuffer();
  private static boolean createOverlayPdf=false;
  private static PDDocument pdfCopy;
  private static int currentPage=0;
  private static float lastXCoordinate=0; // Use to detect 'hidden' white spaces. If there is too long between two characters, it is a new word starting.
  private static float lastYCoordinate=0;
  private static String jp2FileName;
  private static int pageNumber = 0;
 
  

  /**
   * @param pdfInputFile Full path to the PDF input file
   * @param altoXmlOutputFile Full path to the generated alto.xml file
   * @param jp2FileNameNew the full path to the jp2000 file. This will be included in the altofile.
   * @param pageNumberNew Page number. This will be included in the altofile.
   * @throws Exception If there is an error parsing the document.
   */
  public static void makeAlto(String pdfInputFile, String altoXmlOutputFile, String jp2FileNameNew, int pageNumberNew) throws Exception{
    altoXml= new StringBuffer();
    jp2FileName=jp2FileNameNew;
    pageNumber=pageNumberNew;
    createOverlayPdf=false;
    lastXCoordinate=0;
    lastYCoordinate=0;
    PDF2AltoGenerator handler = new PDF2AltoGenerator();


    handler.pdfCopy= PDDocument.load(new File(pdfInputFile));
    handler.processDocuments(pdfInputFile);            
    handler.pdfCopy.close();

    Files.write(Paths.get(altoXmlOutputFile), altoXml.toString().getBytes()); //write to file
  }


  /*
   * This method is only used for test-purpose to see the AltoText overlay 
   * 
   */
  public static void makePdfOverlay(String pdfInputFile, String pdfOverlayFile) throws Exception{
    altoXml= new StringBuffer();
    createOverlayPdf=true;
    jp2FileName=null;
    pageNumber=0;
    lastXCoordinate=0;
    lastYCoordinate=0;
    PDF2AltoGenerator handler = new PDF2AltoGenerator();

    handler.pdfCopy= PDDocument.load(new File(pdfInputFile));
    handler.processDocuments(pdfInputFile);
    handler.pdfCopy.save(pdfOverlayFile);            
    handler.pdfCopy.close();      
    createOverlayPdf=false; //just reset this test parameter      
  }


  public class MarginOffset
  {
    protected float _dx;
    protected float _dy;

    public MarginOffset(float dx, float dy)
    {
      _dx = dx;
      _dy = dy;
    }

    public float getX()
    {
      return _dx;
    }

    public float getY()
    {
      return _dy;
    }
  }

  public class WordBoxEmitter
  {
    protected LinkedList<WordBox> box_list = new LinkedList<WordBox>();
    protected Character last_character = new Character('\0');
    protected StringBuffer word = new StringBuffer("");
    protected MarginOffset _offset = new MarginOffset(0, 0);

    public void setOffset(MarginOffset offset)
    {
      _offset = offset;
    }

    protected boolean endsWord(char ch)
    {

      boolean ends= !(isAlnumOrApostrophe(ch) || isHyphen(ch));
      if (ends){
        //System.out.println("endsWord OK for:"+ch);
      }
      return ends;
    }

    protected boolean isAlnumOrApostrophe(char ch)
    {
      return Character.isLetterOrDigit(ch) || (ch == '\'');
    }

    protected boolean isHyphen(char ch)
    {
      return ch == '-';
    }

    protected void emit()
    {
      float pointsToInch1200 = (float)16.6666; 
      float mysteryHeightScale = (float)1.5;
      float height;
      float width;
      float hpos;
      float vpos;


      if (word.toString().trim().length() > 0) {

        for (WordBox wordbox : box_list) {
          width = wordbox._width * pointsToInch1200;
          height = wordbox._height * pointsToInch1200 * mysteryHeightScale;


          if (height==0 ){
            height=wordbox._fontsize*  mysteryHeightScale; //This is a pure guess to try fix height = 0.  
          }

          hpos = (wordbox._xmin + _offset.getX()) * pointsToInch1200;
          vpos = (wordbox._ymin + _offset.getY()) * pointsToInch1200 - height;

          float hpos_org=wordbox._xmin + _offset.getX();
          float vpos_org=wordbox._ymin + _offset.getY();


          //Notice the wordbox._height is 0 for the Stiften pdf, so try use font size... 
          if (createOverlayPdf){
            drawText(pdfCopy,(int)hpos_org,(int)vpos_org,(int) wordbox._width,(int)wordbox._fontsize,word.toString().trim());
          }
          //System.out.println(hpos_org +","+vpos_org +" word:"+word.toString().trim());

          appendXml( "<String HEIGHT=\""  + height +
              "\" WIDTH=\""   + width  +
              "\" HPOS=\""    + hpos   +
              "\" VPOS=\""    + vpos   +
              "\" CONTENT=\"" + word.toString().trim() +
              "\" CC=\""    + get9sForCC(word.toString().trim()) +"\""+                
              "/>" );
        }
      }
      word = new StringBuffer("");
      last_character = new Character('\0');
      box_list.clear();
    }

    //Simply just return 9 9 9 9. one 9 for each character
    private String get9sForCC(String text){
      StringBuffer b = new StringBuffer();
      if(text == null || text.length()==0){
        return "";
      }

      b.append("9"); //This is the first
      for (int i =0;i<text.length()-1;i++){
        b.append(" 9");
      }    
      return b.toString();    	    	
    }


    protected void processTextPosition(TextPosition text, MarginOffset offset)
    {
      Character current_character = text.getCharacter().toLowerCase().charAt(0);
      setOffset(offset);


      float tmpX = lastXCoordinate;
      float tmpY = lastYCoordinate;
      lastXCoordinate = text.getX();
      lastYCoordinate = text.getY();

      if (endsWord(current_character)) {
        emit();      
      }

      else if(Math.abs(text.getX() -  tmpX) > 1*text.getFontSizeInPt()){
        emit();
        word.append(current_character);
      }                  
      else {
        if (box_list.size() == 0) {
          box_list.addLast(new WordBox(text));
        }
        else if (box_list.getLast().accepts(text)) {
          box_list.getLast().extendBy(text);
        }
        else {
          if (!isHyphen(last_character)) {
            emit();
          }
          box_list.addLast(new WordBox(text));
          last_character = new Character('\0');
        }

        if (isHyphen(last_character)) {
          word = word.append(last_character);
        }
        if (isAlnumOrApostrophe(current_character)) {
          word = word.append(current_character);
        }
      }

      last_character = current_character;
    }

    protected void endOfPage()
    {
      if (box_list.size() > 0) {
        emit();
      }
    }
  }

  public class WordBox
  {
    public float _xmin;
    public float _ymin;
    public float _fontsize;
    public float _xscale;
    public float _yscale;
    public float _height;
    public float _width;

    public WordBox(TextPosition text)
    {
      _xmin     = text.getXDirAdj();
      _ymin     = text.getYDirAdj();
      _fontsize = text.getFontSize();
      _xscale   = text.getXScale();
      _yscale   = text.getYScale();
      _height   = text.getHeightDir();   
      _width    = text.getWidthDirAdj();
    }

    public boolean rejects(TextPosition text)
    {


      boolean reject= (text.getXDirAdj() < _xmin) ||
          (text.getYDirAdj() + text.getWidthOfSpace() < _ymin);          
      return reject;
    }

    public boolean accepts(TextPosition text) {
      return !rejects(text);
    }

    public void extendBy(TextPosition text)
    {
      float current_xmin = _xmin;
      float current_xmax = _xmin + _width;
      float current_ymin = _ymin;
      float current_ymax = _ymin + _height;

      float text_xmin    = text.getXDirAdj();
      float text_xmax    = text_xmin          + text.getWidthDirAdj();
      float text_ymin    = text.getYDirAdj();
      float text_ymax    = text_ymin          + text.getHeightDir();

      float new_xmin     = Math.min(current_xmin, text_xmin);
      float new_xmax     = Math.max(current_xmax, text_xmax);
      float new_ymin     = Math.min(current_ymin, text_ymin);
      float new_ymax     = Math.max(current_ymax, text_ymax);

      _xmin   = new_xmin;
      _width  = new_xmax - new_xmin;
      _ymin   = new_ymin;
      _height = new_ymax - new_ymin;
    }
  }

  protected WordBoxEmitter emitter = new WordBoxEmitter();
  protected MarginOffset _offset = new MarginOffset(0, 0);

  /**
   * Default constructor.
   *
   * @throws IOException If there is an error loading text stripper properties.
   */
  public PDF2AltoGenerator() throws IOException
  {
    super.setSortByPosition( true );
  }

  public void setOffset(MarginOffset offset)
  {
    _offset = offset;
  }

  public void processDocuments(String pdfFile ) throws Exception
  {

    PDDocument document = null;
    try
    {
      document = PDDocument.load( pdfFile );
      if( document.isEncrypted() )
      {
        try
        {
          document.decrypt( "" );
        }
        catch( Exception e )
        {
          System.err.println( "Error: Document is encrypted with a password." );
          System.exit( 1 );
        }
      }
      PDF2AltoGenerator printer = new PDF2AltoGenerator();
      List allPages = document.getDocumentCatalog().getAllPages();
      appendXml( "<?xml version=\"1.0\" encoding=\"UTF-8\"?><alto xmlns=\"http://www.loc.gov/standards/alto/alto-v2.0.xsd\">" );
      for( int i=0; i<allPages.size(); i++ )
      {
        currentPage=i;

        PDPage page = (PDPage)allPages.get( currentPage);

        if (page.getCropBox() != null) {
          PDRectangle mediaBox = (PDRectangle)page.getMediaBox();
          PDRectangle cropBox = (PDRectangle)page.getCropBox();
          printer.setOffset(new MarginOffset(
              cropBox.getLowerLeftX() - mediaBox.getLowerLeftX(),
              cropBox.getLowerLeftY() - mediaBox.getLowerLeftY()
              ));
        }


        appendXml("<Description>");
        appendXml("<MeasurementUnit>inch1200</MeasurementUnit>");        
        appendXml("<sourceImageInformation>");        
        appendXml("<fileName>"+jp2FileName+"</fileName>"); //TODO
        appendXml("</sourceImageInformation>");
        appendXml("</Description>");
        appendXml( "<Layout>" );
        appendXml( "<Page ID=\"P"+pageNumber+"\" PHYSICAL_IMG_NR=\""+pageNumber+"\">" );
        appendXml( "<PrintSpace>" );
        appendXml( "<TextBlock>" );
        appendXml( "<TextLine>" );
        PDStream contents = page.getContents();
        if( contents != null )
        {
          printer.processStream( page, page.findResources(), page.getContents().getStream() );
        }
        endOfPage();
        appendXml( "</TextLine>" );
        appendXml( "</TextBlock>" );
        appendXml( "</PrintSpace>" );
        appendXml( "</Page>");
      }
      appendXml( "</Layout></alto>" );
    }
    finally
    {
      if( document != null )
      {
        document.close();
      }
    }

  }


  /**
   * A method provided as an event interface to allow a subclass to perform
   * some specific functionality when text needs to be processed.
   *
   * @param text The text to be processed
   */
  protected void processTextPosition( TextPosition text )
  {   
    emitter.processTextPosition(text, _offset);
  }

  protected void endOfPage()
  {
    emitter.endOfPage();
  }

  private void appendXml(String text){
    altoXml.append(text +"\n");
  }



  public void drawText(PDDocument pdfCopy, int edgeLeft, int topPixel, int width, int height,String word){
    // Draw box1 border
    try{
      PDPage page = (PDPage) pdfCopy.getDocumentCatalog().getAllPages().get(currentPage);
      float pageHeight=page.findMediaBox().getHeight();



      PDPageContentStream contentStream = new PDPageContentStream(pdfCopy, page,true,true);
      contentStream.setLineWidth(1);
      contentStream.setFont( PDType1Font.HELVETICA,8);

      contentStream.setNonStrokingColor(Color.RED); //All text we add is RED  


      contentStream.beginText();
      contentStream.moveTextPositionByAmount(edgeLeft,pageHeight-topPixel ); 
      contentStream.drawString(word);
      contentStream.endText();

      contentStream.setStrokingColor(Color.RED); //All text we add is RED

      //System.out.println(edgeLeft +","+ topPixel +": "+word);
      contentStream.addLine(edgeLeft,pageHeight-topPixel, edgeLeft, pageHeight-topPixel+height);
      contentStream.closeAndStroke();

      contentStream.addLine(edgeLeft,pageHeight-topPixel, edgeLeft+width,  pageHeight-topPixel);
      contentStream.closeAndStroke();

      contentStream.addLine(edgeLeft+width,pageHeight-topPixel, edgeLeft+width,  pageHeight-topPixel+height);
      contentStream.closeAndStroke();

      contentStream.addLine(edgeLeft,pageHeight-topPixel+height, edgeLeft+width,  pageHeight-topPixel+height);
      contentStream.closeAndStroke();


      contentStream.close();
    }
    catch(Exception e){
      e.printStackTrace();
    }
  }

}
