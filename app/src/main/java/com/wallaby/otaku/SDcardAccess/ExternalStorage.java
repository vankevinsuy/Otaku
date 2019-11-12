package com.wallaby.otaku.SDcardAccess;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.wallaby.otaku.models.Anime;
import com.wallaby.otaku.models.Chapitre;
import com.wallaby.otaku.models.Manga;

import java.io.File;
import java.util.ArrayList;

public class ExternalStorage {

    private Context context;
    private Activity activity;

    private String ANIME_FOLDER_NAME = "OtakuAppData/Anime";
    private String SCAN_FOLDER_NAME = "OtakuAppData/Scan Manga";
    private String MOVIE_FOLDER_NAME = "OtakuAppData/MOVIES";

    //constructeur pour la premiere connexion
    public ExternalStorage(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;

        if(!CheckPermission()){
            SetPermission();
        }
    }

    public ExternalStorage(){}

    private void SetPermission(){
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_DENIED) {

            // Requesting the permission
            ActivityCompat.requestPermissions(activity,
                    new String[] { Manifest.permission.READ_EXTERNAL_STORAGE },
                    101);
        }
    }

    private boolean CheckPermission(){
        boolean res = false;

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_DENIED) {
            res = false;
        }

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            res = true;
        }

        return res;
    }

    public String getSDcardPath(){
        String res = "";
        File root = new File("/storage");
        File[] content = root.listFiles();

        for(File f : content){
            if(f.canRead()){
                if(f.listFiles().length > 0){
                    res = f.getPath();
                }
            }
        }

        return res;
    }

    public File getSDcard(){
        File res = new File(getSDcardPath());
        return res;
    }





    public ArrayList<Manga> getAllManga(){

        ArrayList<Manga> res = new ArrayList<>();
        File scanFolder = new File(getSDcardPath() + "/" + SCAN_FOLDER_NAME);

        for(File f : scanFolder.listFiles()){

            String folder = f.getName();

            if(folder.contains(".DS_Store") || folder.contains("._.DS_Store")){
                continue;
            }
            else {
                res.add(new Manga(f.getPath()));
            }
        }

        return res;
    }

    public Manga getMangaByName(String selection){
        Manga res = null;

        for(Manga manga : getAllManga()){
            if(manga.getName().equals(selection)){
                res = manga;
                break;
            }
        }


        return res;
    }

    public Chapitre getMangaChapter(String selection, int chapter){
        Chapitre res = null;

        for(Manga manga : getAllManga()){
            if(manga.getName().equals(selection)){
                for(Chapitre chapitre : manga.getChapitreArrayList()){
                    if(chapitre.getNumChapitre() == chapter){
                        res = chapitre;
                    }
                }
                break;
            }
        }


        return res;
    }




    public ArrayList<Anime> getAllAnime(){
        ArrayList<Anime> res = new ArrayList<>();
        File AnimeFolder = new File(getSDcardPath() + "/" + ANIME_FOLDER_NAME);

        for(File f : AnimeFolder.listFiles()){
            String folder = f.getName();

            if(folder.contains(".DS_Store") || folder.contains("._.DS_Store") || folder.contains("MOVIES")){
                continue;
            }
            else {
                res.add(new Anime(f.getPath()));
            }
        }

        return res;
    }

    public Anime getAnimeByName(String anime){
        Anime res = null;

        for(Anime anime1 : getAllAnime()){
            if(anime1.getName().equals(anime)){
                res = anime1;
                break;
            }
        }

        return res;
    }


//    public ArrayList<Movie> getAllMovies(){
//        ArrayList<Movie> res = new ArrayList<>();
//        File MovieFolder = new File(getSDcardPath() + "/" + ANIME_FOLDER_NAME);
//
//        for(File f : MovieFolder.listFiles()){
//            String folder = f.getName();
//
//            if(folder.contains(".DS_Store") || folder.contains("._.DS_Store") ){
//                continue;
//            }
//            else {
//                String a = f.getPath();
//                String b = f.getName();
//                String c = f.getParent();
////                res.add(new Movie(f.getPath(), f.getName()));
//            }
//        }
//
//        return res;
//    }

}