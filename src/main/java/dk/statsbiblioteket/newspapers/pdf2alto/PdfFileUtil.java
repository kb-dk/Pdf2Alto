package dk.statsbiblioteket.newspapers.pdf2alto;

import java.util.ArrayList;
import org.apache.pdfbox.util.PDFMergerUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PdfFileUtil {

	private static final Logger log = LoggerFactory.getLogger(PdfFileUtil.class);

	public static void mergePdfs(ArrayList<String> pdfs, String pdfCombinedFileName) throws Exception{
		log.debug("Merging #PDF:"+pdfs.size() +" into "+pdfCombinedFileName);
				
		//Will throw exception if not found           

		try{ 
			PDFMergerUtility mergePdf = new PDFMergerUtility();            
			
			for (String pdfFile : pdfs){
			  mergePdf.addSource(pdfFile);    			
			}
			mergePdf.setIgnoreAcroFormErrors(true);

			mergePdf.setDestinationFileName(pdfCombinedFileName);
			mergePdf.mergeDocuments();
		}
		catch(Exception e){
			log.warn("Could not merge PDF. Maybe encypted.",e);
			// This can also be handled 
			throw new IllegalArgumentException("Error merging pdfs",e);
		}            

	}


}
