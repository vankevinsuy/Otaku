package com.wallaby.otaku.models;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class Anime {
    private String folderPath;
    private String name;
    private ArrayList<String> listEpisodePath;
    private ArrayList<Integer> listEpisode;
    private ArrayList<Saison> listSaison;
    private String coverPath;
    private ArrayList<String> listRelativeEpisodePath;


    public Anime(String folderPath) {
        this.folderPath = folderPath;
        this.name = "";
        this.coverPath = "";
        this.listEpisodePath = new ArrayList<>();
        this.listSaison = new ArrayList<>();
        this.listEpisode = new ArrayList<>();
        this.listRelativeEpisodePath = new ArrayList<>();
        initData();
    }


    public String getFolderPath() {
        return folderPath;
    }

    public String getName() {
        return name;
    }

    public ArrayList<String> getListEpisodePath() {
        return listEpisodePath;
    }

    public ArrayList<Saison> getListSaison() {
        ArrayList<Saison> res = new ArrayList<>();
        TreeMap<Integer, Saison> temp = new TreeMap<>();

        for(Saison saison : listSaison){
            String[]a = saison.getNameSaison().split(" ");
            int numSaison = Integer.parseInt(a[1]);
            temp.put(numSaison, saison);
        }

        for (Map.Entry<Integer, Saison> entry : temp.entrySet()){
            res.add(entry.getValue());
        }

        return res;
    }

    public String getCoverPath() {
        return coverPath;
    }

    public ArrayList<Integer> getListEpisode() {
        return listEpisode;
    }

    public String getFirstEpisodePath(){
        String res = "";

        if(listSaison.size() > 0){
            res = listSaison.get(0).getListRelativeEpisodePath().get(0);
        }
        if(listEpisodePath.size() > 0){
            res = listRelativeEpisodePath.get(0);
        }

        return res;
    }

    public ArrayList<String> getListRelativeEpisodePath() {

        for(String path : getListEpisodePath()){
            String anime = path.split("/")[path.split("/").length-2];
            String episode = path.split("/")[path.split("/").length-1];

            String relativePath = anime + "/" + episode;

            listRelativeEpisodePath.add(relativePath);
        }

        return listRelativeEpisodePath;
    }

    private void initData(){
        //set le nom
        name = folderPath.split("/")[folderPath.split("/").length-1];


        File root = new File(folderPath);
        File[] content = root.listFiles();

        if(!root.getName().equals("MOVIES")){
            for(File file : content){

                //le cover
                if(file.getName().contains("cover")){
                    coverPath = file.getPath();
                }

                if(!file.getName().contains("cover"))
                {
                    //ajout d'une saison saisons
                    if(file.isDirectory()){
                        listSaison.add(new Saison(file.getAbsolutePath(), name));
                    }
                    else {
                        //ajouter les épisodes ou les films
                        if(!file.getPath().contains("._")){
                            listEpisodePath.add(file.getPath());
                        }
                    }
                }


            }

            //ordonner les listes
            TreeMap<Integer, String> temp = new TreeMap<>();
            for(String episodePath : listEpisodePath){
                String[] a = episodePath.split("/");
                String b = a[a.length-1];
                String[] c = b.split("\\.");
                int numEpisode = Integer.parseInt(c[0]);

                temp.put(numEpisode, episodePath);
            }
            listEpisodePath.clear();
            for (Map.Entry<Integer, String> entry : temp.entrySet()){
                listEpisode.add(entry.getKey());
                listEpisodePath.add(entry.getValue());
            }
        }

    }

}
