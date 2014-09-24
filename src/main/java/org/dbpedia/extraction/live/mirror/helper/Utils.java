package org.dbpedia.extraction.live.mirror.helper;

import org.apache.log4j.Logger;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

/**
 * Description
 *
 * @author Dimitris Kontokostas
 * @since 9/24/14 1:16 PM
 */
public final class Utils {

    private static Logger logger = Logger.getLogger(Utils.class);

    private Utils() {}

    public static List<String> getLinesFromFile(String filename) {
        List<String> lines = new ArrayList<>();

        try (BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(filename), "UTF-8"))) {

            String line = null;
            while ((line = in.readLine()) != null) {

                lines.add(line.trim());

            }

        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException("File " + filename + " not fount!", e);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("UnsupportedEncodingException: ", e);
        } catch (IOException e) {
            throw new IllegalArgumentException("IOException in file " + filename, e);
        }

        return lines;

    }

    public static boolean deleteFile(String filename) {
        try {
            return new File(filename).delete();
        } catch (Exception e) {
            return false;
        }
    }

    public static String generateStringFromList(List<String> strList,String sep) {

        StringBuilder finalString = new StringBuilder();


        for (String str: strList) {
            finalString.append(str);
            finalString.append(sep);
        }

        return finalString.toString();
    }

    /**
     * Decompresses the passed GZip file, and returns the filename of the decompressed file
     * @param filename  The filename of compressed file
     * @param deleteCompressedFile  Whether to delete the original compressed file upon completion
     * @return  The filename of the output file, or empty string if a problem occurs
     */
    public static String decompressGZipFile(String filename, boolean deleteCompressedFile){

        String outFilename = "" ;

        try{
            //The output filename is the same as input filename without last .gz
            int lastDotPosition = filename.lastIndexOf(".");
            outFilename = filename.substring(0, lastDotPosition);

             FileInputStream inStream = new FileInputStream(filename);
             GZIPInputStream gInStream =new GZIPInputStream(inStream);
             FileOutputStream outstream = new FileOutputStream(outFilename);
             byte[] buf = new byte[1024];
             int len;
             while ((len = gInStream.read(buf)) > 0)
            {
              outstream.write(buf, 0, len);
            }
            logger.info("File : " + filename +" decompressed successfully to " + outFilename);
            gInStream.close();
            outstream.close();


//            return outFilename;
        }
        catch(IOException ioe){
            logger.warn("File " + filename + " cannot be decompressed due to " + ioe.getMessage(), ioe);
            outFilename = "";
        }
        finally {
            if(deleteCompressedFile)
                (new File(filename)).delete();

            return outFilename;
        }
    }

    /**
     * Downloads the file with passed URL to the passed folder
     * @param fileURL   URL of the file that should be downloaded
     * @param folderPath    The path to which this file should be saved
     * @return  The local full path of the downloaded file, empty string is returned if a problem occurs
     */
    public static String downloadFile(String fileURL, String folderPath){

      //-----------------------------------------------------//
      //  Step 1:  Start creating a few objects we'll need.
      //-----------------------------------------------------//

      URL u;
      InputStream is = null;
      DataInputStream dis;
      String fullFileName = "";
      FileOutputStream output = null;

      try {

              //Extract filename only without full path
              int lastSlashPos = fileURL.lastIndexOf("/");
              if(lastSlashPos < 0)
                  return "";

              //Initialize fullFileName with name of the file itself only
              fullFileName = fileURL.substring(lastSlashPos+1);

              //Create parent folder if it does not already exist
              File parentFolder = new File(folderPath);
              if(parentFolder != null)
                  parentFolder.mkdirs();

              //construct the full file path, including its parent folder
              fullFileName = folderPath + fullFileName;

             //------------------------------------------------------------//
             // Step 2:  Create the URL.                                   //
             //------------------------------------------------------------//
             // Note: Put your real URL here, or better yet, read it as a  //
             // command-line arg, or read it from a file.                  //
             //------------------------------------------------------------//

    //         u = new URL("http://live.dbpedia.org/liveupdates");

             u = new URL(fileURL);

             //----------------------------------------------//
             // Step 3:  Open an input stream from the url.  //
             //----------------------------------------------//

             is = u.openStream();         // throws an IOException

             //-------------------------------------------------------------//
             // Step 4:                                                     //
             //-------------------------------------------------------------//
             // Convert the InputStream to a buffered DataInputStream.      //
             // Buffering the stream makes the reading faster; the          //
             // readLine() method of the DataInputStream makes the reading  //
             // easier.                                                     //
             //-------------------------------------------------------------//

             dis = new DataInputStream(new BufferedInputStream(is));

             //------------------------------------------------------------//
             // Step 5:                                                    //
             //------------------------------------------------------------//
             // Now just read each record of the input stream, and print   //
             // it out.  Note that it's assumed that this problem is run   //
             // from a command-line, not from an application or applet.    //
             //------------------------------------------------------------//
             output = new FileOutputStream(fullFileName);
             byte []fileContents = new byte[dis.available()];
             while (dis.available() != 0) {
                output.write(dis.readByte());
             }

            logger.info("File : " + fileURL + " has been successfully downloaded");

          } catch (MalformedURLException mue) {


           //logger.warn("File : " + fileURL + " cannot be downloaded as it does not exist");
//           mue.printStackTrace();
           fullFileName = "";
//             System.exit(1);

          } catch (IOException ioe) {

             //logger.warn("File : " + fileURL + " cannot be downloaded as it does not exist");
//             ioe.printStackTrace();
//             System.exit(1);
          fullFileName = "";

          }
      finally {

             //---------------------------------//
             // Step 6:  Close the InputStream  //
             //---------------------------------//

             try {

                 if(is != null)
                    is.close();
                 if(output != null){
                    output.flush();
                    output.close();
                 }
             } catch (IOException ioe) {
                // just going to ignore this one
             }

          } // end of 'finally' clause

        //Decompressor.decompressGZipFile(fullFileName);

       return fullFileName;
    }
}
