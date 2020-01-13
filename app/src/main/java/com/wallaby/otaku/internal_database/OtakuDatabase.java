package com.wallaby.otaku.internal_database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wallaby.otaku.SDcardAccess.ExternalStorage;
import com.wallaby.otaku.models.Anime;
import com.wallaby.otaku.models.Chapitre;
import com.wallaby.otaku.models.Manga;

import java.util.ArrayList;

public class OtakuDatabase extends SQLiteOpenHelper {

    ExternalStorage externalStorage;
    //****DATABASE VARIABLES****//

    private static final String DATA_BASE_name = "otaku.db";

    private static final String TABLE_manga = "manga";
    private static final String COL_manga_name = "manga_name";
    private static final String COL_resume_from_page = "resume_page";
    private static final String COL_resume_from_chapter = "resume_chapter";


    private static final String TABLE_user = "user";
    private static final String COL_first_use = "first_use";

    private static final String TABLE_manga_chapter = "manga_chapter";
    private static final String COL_chapter = "num_chapter";


    private static final String TABLE_anime = "anime";
    private static final String COL_anime_name = "anime_name";
    private static final String COL_resume_from_episode = "resume_from_episode";

    //****QUERY FOR CREATING THE TABLES****//
    private static final String CREATE_TABLE_MANGA = "CREATE TABLE " + TABLE_manga + "(" +
            COL_manga_name   + " TEXT," +
            COL_resume_from_page + " INTEGER ," +
            COL_resume_from_chapter + " INTEGER" + ")" ;

    private static final String CREATE_TABLE_USER = "CREATE TABLE " + TABLE_user + "(" +
            COL_first_use + " INTEGER"+ ")";

    private static final String CREATE_TABLE_MANGA_CHAPTER = "CREATE TABLE " + TABLE_manga_chapter + "(" +
            COL_manga_name   + " TEXT," +
            COL_chapter + " INTEGER ,"+
            COL_resume_from_page + " TEXT" + ")";

    private static final String CREATE_TABLE_Anime = "CREATE TABLE " + TABLE_anime + "(" +
            COL_anime_name   + " TEXT," +
            COL_resume_from_episode + " TEXT " + ")";


    public OtakuDatabase(Context context) {
        super(context, DATA_BASE_name, null, 1);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_MANGA);
        db.execSQL(CREATE_TABLE_USER);
        db.execSQL(CREATE_TABLE_MANGA_CHAPTER);
        db.execSQL(CREATE_TABLE_Anime);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_manga);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_manga_chapter);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_anime);

        //create new TABLES
        onCreate(db);
    }


    public void initiate_first_use(){
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_first_use, 1);

        // premiere utilisation passée à 1 donc pas de demande d'accès aux données
        database.insert(TABLE_user,null,contentValues);

        ExternalStorage externalStorage = new ExternalStorage();

        // pour la lecture continue, tout les pages de départ sont mise à 0
        for(Manga manga : externalStorage.getAllManga()){
            contentValues = new ContentValues();
            contentValues.put(COL_manga_name, manga.getName());
            contentValues.put(COL_resume_from_chapter, manga.getFirstChapter());
            contentValues.put(COL_resume_from_page, 0);

            database.insert(TABLE_manga,null,contentValues);
        }

        //pour la lecture par chapitre, chaque page de départ de chaque chapitre de chaque manga est mise à 0
        for(Manga manga : externalStorage.getAllManga()){
            for(Chapitre chapitre : manga.getChapitreArrayList()){
                contentValues = new ContentValues();

                contentValues.put(COL_manga_name, manga.getName());
                contentValues.put(COL_chapter, chapitre.getNumChapitre());
                contentValues.put(COL_resume_from_page, 0);

                database.insert(TABLE_manga_chapter,null,contentValues);
            }
        }

        //pour chaque anime on met le premier épisode en resume
        for(Anime anime : externalStorage.getAllAnime()){
            contentValues = new ContentValues();

            if(anime.getListSaison().size() > 0 && anime.getListRelativeEpisodePath().size()==0){
                contentValues.put(COL_anime_name, anime.getName());
                contentValues.put(COL_resume_from_episode, anime.getFirstEpisodePath());

                database.insert(TABLE_anime,null,contentValues);
            }

            if (anime.getListSaison().size() == 0 && anime.getListRelativeEpisodePath().size() > 0){
                contentValues.put(COL_anime_name, anime.getName());
                contentValues.put(COL_resume_from_episode, anime.getFirstEpisodePath());

                database.insert(TABLE_anime,null,contentValues);
            }


        }

    }

    // pour un nouvel ajout de manga/anime qui n'est pas encore dans la base de données du téléphone et Firebase
    public void verifyNewAdding(){
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_manga , null);

        FirebaseDatabase firebase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = firebase.getReference();

        ExternalStorage externalStorage = new ExternalStorage();

        ArrayList<String> listMangaInBase = new ArrayList<>();

        // mettre à jour la liste des mangas
        // remplir listMangaInBase
        if(cursor.getCount()>0){
            while (cursor.moveToNext()){
                listMangaInBase.add(cursor.getString(0));
            }
        }

        for(Manga manga : externalStorage.getAllManga()){
            if(!listMangaInBase.contains(manga.getName())){
                ContentValues contentValues = new ContentValues();

                contentValues.put(COL_manga_name, manga.getName());
                contentValues.put(COL_resume_from_chapter, manga.getFirstChapter());
                contentValues.put(COL_resume_from_page, 0);

                database.insert(TABLE_manga,null,contentValues);

                //lecture par chapitre
                for(Chapitre chapitre : manga.getChapitreArrayList()){
                    myRef.child("Scan").child("by_chapter").child(manga.getName()).child(Integer.toString(chapitre.getNumChapitre())).setValue(0);
                }

                //lecture continue
                myRef.child("Scan").child("continue").child(manga.getName()).child("chapter").setValue(manga.getFirstChapter());
                myRef.child("Scan").child("continue").child(manga.getName()).child("page").setValue(0);

            }
        }

        // mettre à jour la liste des animes
        // remplir listMangaInBase
        cursor = database.rawQuery("SELECT * FROM " + TABLE_anime , null);
        ArrayList<String> listAnimeInBase = new ArrayList<>();

        if(cursor.getCount()>0){
            while (cursor.moveToNext()){
                listAnimeInBase.add(cursor.getString(0));
            }
        }

        for(Anime anime : externalStorage.getAllAnime()){
            if(!listAnimeInBase.contains(anime.getName())){
                ContentValues contentValues = new ContentValues();

                contentValues.put(COL_anime_name, anime.getName());
                contentValues.put(COL_resume_from_episode, anime.getFirstEpisodePath());

                database.insert(TABLE_anime,null,contentValues);

                //ajout de l'anime dans firebase
                myRef.child("Anime").child(anime.getName()).setValue(anime.getFirstEpisodePath());
            }
        }


        database.close();

    }


    public Cursor get_first_use(){
        SQLiteDatabase database = this.getReadableDatabase();
        return database.rawQuery("SELECT * FROM " + TABLE_user , null);
    }



    public int getResumePageByManga(String manga){
        int res = 0;
        SQLiteDatabase database = this.getWritableDatabase();

        //insertion
        Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_manga , null);

        if (cursor.getCount() >0){
            while (cursor.moveToNext()){
                if(cursor.getString(0).toString().equals(manga)){
                    res = Integer.parseInt(cursor.getString(1));
                }
            }
        }
        cursor.close();
        database.close();


        return res;
    }

    public int getResumeChapterByManga(String manga){
        int res = 0;
        SQLiteDatabase database = this.getWritableDatabase();

        //insertion
        Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_manga , null);

        if (cursor.getCount() >0){
            while (cursor.moveToNext()){

                if(cursor.getString(0).equals(manga)){
                    res = cursor.getInt(2);
                }
            }
        }
        cursor.close();
        database.close();


        return res;
    }

    public void updateResumePageByManga(String manga, int chapitre ,int newPage){
        SQLiteDatabase database = this.getWritableDatabase();

        //insertion
        Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_manga , null);

        if (cursor.getCount() >0){
            while (cursor.moveToNext()){
                if(cursor.getString(0).toString().equals(manga)){
                    ContentValues values = new ContentValues();
                    values.put(COL_resume_from_page, newPage);
                    values.put(COL_resume_from_chapter, chapitre);

                    database.update(TABLE_manga, values, COL_manga_name + " = '" + manga + "'", null);
                }
            }
        }
        cursor.close();
        database.close();
    }



    public void updateChapterPageByManga(String manga, int chapter , int newPage){
        SQLiteDatabase database = this.getWritableDatabase();

        //insertion
        Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_manga_chapter , null);

        if (cursor.getCount() >0){
            while (cursor.moveToNext()){
                if(cursor.getString(0).toString().equals(manga) &&
                            cursor.getInt(1) == chapter ){

                    ContentValues values = new ContentValues();
                    values.put(COL_resume_from_page, newPage);

                    database.update(TABLE_manga_chapter, values, COL_manga_name + " = '" + manga + "'" + " AND "
                            + COL_chapter + " = '" + chapter + "'", null);
                }
            }
        }

        cursor.close();
        database.close();
    }

    public int getChapterPageByManga(String manga, int chapter){
        int res = 0;
        SQLiteDatabase database = this.getWritableDatabase();

        //insertion
        Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_manga_chapter , null);

        if (cursor.getCount() >0){
            while (cursor.moveToNext()){
                if(cursor.getString(0).toString().equals(manga) &&
                cursor.getInt(1) == chapter){
                    res = cursor.getInt(2);
                }
            }
        }
        cursor.close();
        database.close();


        return res;
    }


    public void updateResumeAnime(String anime, String NewVal){

        SQLiteDatabase database = this.getWritableDatabase();

        //insertion
        Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_anime , null);

        if (cursor.getCount() >0){
            while (cursor.moveToNext()){
                if(cursor.getString(0).toString().equals(anime)){

                    ContentValues values = new ContentValues();
                    values.put(COL_resume_from_episode, NewVal);

                    database.update(TABLE_anime, values, COL_anime_name + " = '" + anime + "'", null);
                }
            }
        }
        cursor.close();
        database.close();
    }

    public String getResumeAnimeEpisode(String anime){
        externalStorage = new ExternalStorage();

        String target = "";
        String res = "";
        SQLiteDatabase database = this.getWritableDatabase();

        //insertion
        Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_anime , null);

        if (cursor.getCount() >0){
            while (cursor.moveToNext()){
                if(cursor.getString(0).equals(anime)){
                    target = cursor.getString(1);
                }
            }
        }
        cursor.close();
        database.close();

        res = externalStorage.getAnimeFolderPath() + "/" + target;

        return res;
    }


    // synchroniser la base en local vers firebase
    public void FirebaseSyncDatabasesToFirebase(){
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_anime , null);

        FirebaseDatabase firebase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = firebase.getReference();

        // pour les animes
        if (cursor.getCount() >0){
            while (cursor.moveToNext()){
                String animeName = cursor.getString(0);
                String currentVideo = cursor.getString(1);
                myRef.child("Anime").child(animeName).setValue(currentVideo);
            }
        }


        // pour les scans en continue
        cursor = database.rawQuery("SELECT * FROM " + TABLE_manga , null);
        if (cursor.getCount() > 0){
            while (cursor.moveToNext()){
                String mangaName = cursor.getString(0);
                int pagePath = cursor.getInt(1);
                int chapitre = cursor.getInt(2);

                myRef.child("Scan").child("continue").child(mangaName).child("chapter").setValue(chapitre);
                myRef.child("Scan").child("continue").child(mangaName).child("page").setValue(pagePath);
            }
        }


        // pour les scans par chapitre
        cursor = database.rawQuery("SELECT * FROM " + TABLE_manga_chapter , null);
        if (cursor.getCount() >0){
            while (cursor.moveToNext()){
                String mangaName = cursor.getString(0);
                String chapitre = cursor.getString(1);
                String pagePath = cursor.getString(2);

                myRef.child("Scan").child("by_chapter").child(mangaName).child(chapitre).setValue(pagePath);
            }
        }

        cursor.close();
        database.close();
    }

    // récupérer les données depuis firebase
    public void FirebaseSyncDataFromFirebase(){
        FirebaseDatabase firebase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = firebase.getReference();


        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                SQLiteDatabase database = getWritableDatabase();

                // synchro Anime
                Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_anime , null);
                Iterable<DataSnapshot> animeName = dataSnapshot.child("Anime").getChildren();
                while (animeName.iterator().hasNext()){
                    DataSnapshot iterator = animeName.iterator().next();

                    if (cursor.getCount() >0){
                        while (cursor.moveToNext()){
                            if(cursor.getString(0).equals(iterator.getKey().toString())){
                                ContentValues values = new ContentValues();
                                values.put(COL_resume_from_episode, iterator.getValue().toString());
                                database.update(TABLE_anime, values, COL_anime_name + " = '" + cursor.getString(0) + "'", null);
                            }
                        }
                    }

                }


                // synchro scan/by chapter
                cursor = database.rawQuery("SELECT * FROM " + TABLE_manga_chapter , null);
                Iterable<DataSnapshot> mangaScanName = dataSnapshot.child("Scan").child("by_chapter").getChildren();

                while (mangaScanName.iterator().hasNext()){
                    DataSnapshot mangaScanNameIterator = mangaScanName.iterator().next();

                    if (cursor.getCount() >0){
                        while (cursor.moveToNext()){

                            if(cursor.getString(0).equals(mangaScanNameIterator.getKey().toString())){

                                Iterable<DataSnapshot> chapter = dataSnapshot.child("Scan").child("by_chapter").child(mangaScanNameIterator.getKey()).getChildren();
                                while (chapter.iterator().hasNext()){
                                    DataSnapshot chapterScanNameIterator = chapter.iterator().next();

                                    if(cursor.getString(1).equals(chapterScanNameIterator.getKey().toString())){
                                        ContentValues values = new ContentValues();
                                        values.put(COL_resume_from_page, chapterScanNameIterator.getValue().toString());

                                        database.update(TABLE_manga_chapter, values, COL_manga_name + " = '" + cursor.getString(0) + "'" + " AND "
                                                + COL_chapter + " = '" + cursor.getString(1) + "'", null);                                    }

                                }
                            }
                        }
                    }


                }


                // synchro scan/continue
                mangaScanName = dataSnapshot.child("Scan").child("continue").getChildren();
                while(mangaScanName.iterator().hasNext()){
                    DataSnapshot mangaScanNameIterator = mangaScanName.iterator().next();
                    cursor = database.rawQuery("SELECT * FROM " + TABLE_manga , null);

                    if (cursor.getCount() >0){
                        while (cursor.moveToNext()){

                            String a = cursor.getString(0);
                            String b = mangaScanNameIterator.getKey().toString();

                            if(cursor.getString(0).equals(mangaScanNameIterator.getKey().toString())){
                                ContentValues values = new ContentValues();

                                values.put(COL_resume_from_chapter, mangaScanNameIterator.child("chapter").getValue().toString());
                                values.put(COL_resume_from_page, mangaScanNameIterator.child("page").getValue().toString());

                                database.update(TABLE_manga, values, COL_manga_name + " = '" + cursor.getString(0) + "'", null);
                            }
                        }
                    }
                }

                cursor.close();
                database.close();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}


