package com.wallaby.otaku.models;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class Manga {

    private String folderPath;
    private String name;
    private String imageCoverPath;
    private ArrayList<Chapitre> chapitreArrayList;
    private String themeSong;

    public Manga(String folderPath) {
        this.folderPath = folderPath;
        this.name = "";
        this.imageCoverPath = "";
        this.chapitreArrayList = new ArrayList<>();
        this.themeSong = "";
        initData();
    }

    public String getFolderPath() {
        return folderPath;
    }

    public String getName() {
        return name;
    }

    public String getImageCoverPath() {
        return imageCoverPath;
    }

    public ArrayList<Chapitre> getChapitreArrayList() {
        TreeMap<Integer, Chapitre> temp = new TreeMap<>();
        ArrayList<Chapitre> res = new ArrayList<>();

        //remplir la map et definir la première page
        for(Chapitre chapitre : chapitreArrayList){
            temp.put(chapitre.getNumChapitre(), chapitre);
        }

        //remplir res
        for(TreeMap.Entry<Integer, Chapitre> entry : temp.entrySet()){
            res.add(entry.getValue());
        }

        return res;
    }

    public int getFirstChapter(){
        return getChapitreArrayList().get(0).getNumChapitre();
    }

    public int getLastChapter(){
        return getChapitreArrayList().get(getChapitreArrayList().size()-1).getNumChapitre();
    }

    public String getThemeSong() {
        return themeSong;
    }

    public ArrayList<String> getCompleteBook() {
        TreeMap<Integer, Chapitre> temp = new TreeMap<>();
        ArrayList<String> res = new ArrayList<>();

        //complete treemap
        for(Chapitre chapitre : chapitreArrayList){
            temp.put(chapitre.getNumChapitre(), chapitre);
        }

        for(TreeMap.Entry<Integer, Chapitre> entry : temp.entrySet()){
            for(String path : entry.getValue().getPagesPath()){
                res.add(path);
            }
        }
        return res;
    }

    public Map<String, Integer> getCompleteBookAsDictContinue()
    {
        Map<String, Integer> res = new TreeMap<>();
        ArrayList<String> completeBook = getCompleteBook();

        int index = 0;
        for (String pathPage: completeBook)
        {
            res.put(pathPage, index);
            index ++;
        }

        return  res;
    }

    private void initData(){
        //set le nom
        name = folderPath.split("/")[folderPath.split("/").length-1];


        File root = new File(folderPath);
        File[] content = root.listFiles();

        for(File file : content){
            //ajouter les chapitres
            if(file.isDirectory() && file.listFiles().length>0){
                chapitreArrayList.add(new Chapitre(file.getPath()));
            }

            // set le cover
            if(file.getName().contains("cover")){
                imageCoverPath = file.getPath();
            }

            if(file.getName().contains("themesong")){
                themeSong = file.getPath();
            }
        }

    }

}
