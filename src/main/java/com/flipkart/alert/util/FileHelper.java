package com.flipkart.alert.util;

import java.io.*;

/**
 * Created with IntelliJ IDEA.
 * User: nitinka
 * Date: 11/2/13
 * Time: 12:23 PM
 * To change this template use File | Settings | File Templates.
 */
public class FileHelper {
    public static byte[] getContentAsBytes(File file) throws IOException {
        byte[] content = new byte[(int) file.length()];
        InputStream is = new FileInputStream(file);
        try {
            is.read(content);
        }
        catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        finally {
            is.close();
        }
        return content;
    }
}
