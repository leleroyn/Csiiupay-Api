package com.ucs.xcbank.csiiupay.utils;

import java.io.*;

public class StreamUtils{
    public static String ConvertToString(InputStream inputStream, boolean addLineBreak){
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        StringBuilder result = new StringBuilder();
        String line = null;
        try {
            while((line = bufferedReader.readLine())!= null){
                if(addLineBreak) {
                    result.append(line + "\n");
                }
                else {
                    result.append(line);
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {
            try{
                inputStreamReader.close();
                inputStream.close();
                bufferedReader.close();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        return  result.toString();
    }

    public static String ConvertToString(FileInputStream fileInputStream,boolean addLineBreak){
        InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        StringBuilder result = new StringBuilder();
        String line = null;
        try {
            while((line = bufferedReader.readLine()) != null){
                if(addLineBreak) {
                    result.append(line + "\n");
                }
                else {
                    result.append(line);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try{
                inputStreamReader.close();
                fileInputStream.close();
                bufferedReader.close();
            }catch(Exception e){

            }
        }
        return result.toString();
    }
}



