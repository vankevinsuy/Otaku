package com.wallaby.otaku.models;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class Saison {
    private String folderPath;
    private String nameSaison;
    private String fromAnime;
    private ArrayList<String> listEpisodePath;
    private ArrayList<String> listRelativeEpisodePath;


    public Saison(String folderPath, String fromAnime) {
        this.folderPath = folderPath;
        this.nameSaison = "";
        this.fromAnime = fromAnime;
        this.listEpisodePath = new ArrayList<>();
        this.listRelativeEpisodePath = new ArrayList<>();

        initData();
    }

    public String getFolderPath() {
        return folderPath;
    }

    public String getNameSaison() {
        return nameSaison;
    }

    public ArrayList<String> getListEpisodePath() {
        TreeMap<Integer, String> temp = new TreeMap<>();
        ArrayList<String> res = new ArrayList<>();

        for(String episodePath : listEpisodePath){
            String[] a = episodePath.split("/");
            String b = a[a.length-1];
            String[] c = b.split("\\.");
            int numEpisode = Integer.parseInt(c[0]);

            temp.put(numEpisode, episodePath);
        }
        for (Map.Entry<Integer, String> entry : temp.entrySet()){
            res.add(entry.getValue());
        }

        return res;
    }

    public String getFromAnime() {
        return fromAnime;
    }

    public ArrayList<String> getListRelativeEpisodePath() {

        for(String path : getListEpisodePath()){
            String anime = path.split("/")[path.split("/").length-3];
            String saison = path.split("/")[path.split("/").length-2];
            String episode = path.split("/")[path.split("/").length-1];

            String relativePath = anime + "/" + saison + "/" + episode;

            listRelativeEpisodePath.add(relativePath);
        }

        return listRelativeEpisodePath;
    }

    private void initData(){
        //set le nom
        nameSaison = folderPath.split("/")[folderPath.split("/").length-1];


        File root = new File(folderPath);
        File[] content = root.listFiles();

        for(File file : content){
            //ajouter les épisodes
            listEpisodePath.add(file.getPath());
        }


    }

}
