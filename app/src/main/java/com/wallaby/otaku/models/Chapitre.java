package com.wallaby.otaku.models;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class Chapitre {

    private String folderPath;
    private int numChapitre;
    private String numChapitreAsString;

    private ArrayList<String> ListpagesPath;

    public Chapitre(String folderPath) {
        this.folderPath = folderPath;
        this.ListpagesPath = new ArrayList<>();
        this.numChapitre = -1;
        this.numChapitreAsString = "";

        initData();
    }

    public int getNumChapitre() {
        return numChapitre;
    }


    public ArrayList<String> getPagesPath() {

        if(ListpagesPath.size() == 0){
            initPagesPath();
        }

        TreeMap<Integer, String> temp = new TreeMap<>();
        ArrayList<String> res = new ArrayList<>();

        //remplir la map et definir la première page
        for(String path : ListpagesPath){
            String[] a = path.split("/");
            String[] b = a[a.length-1].split("-");
            String c = b[b.length-1];
            String[] d = c.split("\\.");
            int num_page = Integer.parseInt(d[0]);

            temp.put(num_page, path);
        }

        //remplir res
        for(Map.Entry<Integer, String> entry : temp.entrySet()){
            res.add(entry.getValue());
        }

        return res;
    }

    public int getNumLastPage(){
        if(ListpagesPath.size() == 0){
            initPagesPath();
        }

        int res = 0;
        File root = new File(folderPath);
        File[] content = root.listFiles();


        for(File page : content){
            if(page.getName().contains("._")){
                continue;
            }
            else {
                String[] a = page.getName().split("/");
                String[] b = a[0].split("-");
                String c = b[b.length-1];
                String[] d = c.split("\\.");
                int num_page = Integer.parseInt(d[0]);
                if(num_page>res){
                    res = num_page;
                }
            }
        }

        return res;
    }

    public String getFirstPagePath(){
        if(ListpagesPath.size() == 0){
            initPagesPath();
        }
        return this.ListpagesPath.get(0);
    }


    private void initData(){
        // set num chapitre
        numChapitre = Integer.parseInt(folderPath.split("/")[folderPath.split("/").length - 1]);
    }

    private void initPagesPath(){
        // set num chapitre
        numChapitre = Integer.parseInt(folderPath.split("/")[folderPath.split("/").length - 1]);

        File root = new File(folderPath);
        File[] content = root.listFiles();

        // remplir la liste avec les path des pages
        for(File page : content){
            if(page.getName().contains("._")){
                continue;
            }
            else {
                ListpagesPath.add(page.getPath());
            }
        }
    }
}
