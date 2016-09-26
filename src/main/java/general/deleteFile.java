package main.java.general;

import java.io.File;

/**
 * Created by sirius-.- on 2015/7/21.
 */

public class deleteFile {
    public static boolean deleteXmlFile(String path,String postfix){
        boolean flag=false;
        File dir=new File(path);
        for(File file:dir.listFiles()){
            if(file.isDirectory()){
                deleteXmlFile(file.getPath(),postfix);
            }else{
               if(file.getName().contains(postfix)){
                    file.delete();
               }
            }
        }
        return flag;
    }
    public static void main(String [] args){
        deleteXmlFile(".\\GanSuEdu","pages.xml");
    }
}
