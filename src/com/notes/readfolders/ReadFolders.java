package com.notes.readfolders;

import javafx.scene.control.TreeItem;
import java.io.File;

/**
 * Created by Vrinceanu Vladut 01-04-2021
 * Time 20:03
 */

public class ReadFolders {

    public static TreeItem<String> root = new TreeItem<>("Root");

    public static void clear(){
        root = new TreeItem<>("Root");
    }

     private static TreeItem<String> addTreeItemToRoot(String item, TreeItem<String> root) {
         TreeItem<String> actualItem = new TreeItem<>(item);
         root.getChildren().add(actualItem);
         return actualItem;
    }


      public static void getStructure(String folderPath, TreeItem<String> rootItem) {

        File actualFolderPath = new File(folderPath);
        File[] getFirstFolders = actualFolderPath.listFiles();

        assert getFirstFolders != null;
        try {
            for (File folder : getFirstFolders) {
                if(folder.getName().contains(".txt") || checkIfTxtExistsInFolder(folder)){
                    TreeItem<String> actualItem = addTreeItemToRoot(folder.getName(), rootItem);
                    getStructure(folder.getAbsolutePath(), actualItem);}
            }
        } catch (NullPointerException e) {
            //System.out.println("From ReadFolders.getStructure(): " + e.getMessage());
        }
    }

    private static boolean checkIfTxtExistsInFolder (File folder){
        if(folder.getName().contains(".txt")){
            return true;
        }
        File[] getFirstFolders = folder.listFiles();
        try{
            assert getFirstFolders != null;
            for (File file : getFirstFolders){
                if (file.getName().contains(".txt")){
                    return true;
                } else if (file.isDirectory()){
                    if (checkIfTxtExistsInFolder(file)) return true;
                }
            }
        } catch (NullPointerException e) {
            //System.out.println(e.getMessage());
        }
        return  false;
    }
}
