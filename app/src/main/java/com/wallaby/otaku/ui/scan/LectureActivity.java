package com.wallaby.otaku.ui.scan;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.wallaby.otaku.ExploreFirstLevel;
import com.wallaby.otaku.MainActivity;
import com.wallaby.otaku.R;
import com.wallaby.otaku.SDcardAccess.ExternalStorage;
import com.wallaby.otaku.internal_database.OtakuDatabase;
import com.wallaby.otaku.models.Chapitre;
import com.wallaby.otaku.models.Manga;
import com.wallaby.otaku.ui.OnSwipeTouchListener;

import java.io.File;
import java.util.ArrayList;

public class LectureActivity extends AppCompatActivity {

    private String selected_manga;
    private int selected_chapter;
    private String single_or_continue;

    private Chapitre chapitre;
    private ArrayList<String>completeBook;

    private OtakuDatabase otakuDatabase;

    private ImageView readingSD;

    private int current_page;

    int screenWidth;
    int screenHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecture);

        hideSystemUI();

        this.readingSD = findViewById(R.id.readingSD);
        this.current_page = 0;

        // get screen size
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;

        otakuDatabase = new OtakuDatabase(getApplicationContext());

        single_or_continue = getIntent().getStringExtra("single_or_continue");

        switch (single_or_continue){
            case "single":
                selected_manga = getIntent().getStringExtra("selection_manga");
                selected_chapter = Integer.parseInt(getIntent().getStringExtra("selection_chapter"));
                current_page = otakuDatabase.getChapterPageByManga(selected_manga, selected_chapter);
                SingleReading();
                break;

            case "continue":
                selected_manga = getIntent().getStringExtra("selection_manga");
                current_page = otakuDatabase.getResumePageByManga(selected_manga);
                ContinueReading();
                break;
        }


    }

    public void SingleReading(){
        ExternalStorage externalStorage = new ExternalStorage();
        chapitre = externalStorage.getMangaChapter(selected_manga, selected_chapter);

        ResizeAndDisplayImage(chapitre.getPagesPath().get(current_page));

        readingSD.setOnTouchListener(new OnSwipeTouchListener(getApplicationContext()){
            //retour en arriere
            public void onSwipeRight() {
                if(current_page - 1 >= 0){
                    current_page = current_page - 1;
                    ResizeAndDisplayImage(chapitre.getPagesPath().get(current_page));

                    otakuDatabase.updateChapterPageByManga(selected_manga, chapitre.getNumChapitre() , current_page);
                }
                else {
                    current_page = 0;
                }
            }

            //avancer d'une page
            @Override
            public void onSwipeLeft() {
                if(current_page + 1 <= chapitre.getPagesPath().size()-1){
                    current_page = current_page + 1;
                    ResizeAndDisplayImage(chapitre.getPagesPath().get(current_page));
                    otakuDatabase.updateChapterPageByManga(selected_manga, chapitre.getNumChapitre() , current_page);
                }
                else {
                    Toast.makeText(getApplicationContext(), "end of chapter", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void ContinueReading(){
        ExternalStorage externalStorage = new ExternalStorage();
        Manga manga = externalStorage.getMangaByName(selected_manga);
        completeBook = manga.getCompleteBook();

        ResizeAndDisplayImage(completeBook.get(current_page));

        readingSD.setOnTouchListener(new OnSwipeTouchListener(getApplicationContext()){
            //retour en arriere
            public void onSwipeRight() {
                if(current_page - 1 >= 0){
                    current_page = current_page - 1;
                    ResizeAndDisplayImage(completeBook.get(current_page));

                    int current_chapitre = Integer.parseInt(completeBook.get(current_page).split("/")[completeBook.get(current_page).split("/").length -2]);

                    //mise à jour de la derniere page lue
                    otakuDatabase.updateResumePageByManga(selected_manga, current_chapitre ,current_page);
                }
                else {
                    current_page = 0;
                }
            }

            //avancer d'une page
            @Override
            public void onSwipeLeft() {
                if(current_page + 1 <= completeBook.size()-1){
                    current_page = current_page + 1;
                    ResizeAndDisplayImage(completeBook.get(current_page));

                    int current_chapitre = Integer.parseInt(completeBook.get(current_page).split("/")[completeBook.get(current_page).split("/").length -2]);


                    //mise à jour de la derniere page lue
                    otakuDatabase.updateResumePageByManga(selected_manga, current_chapitre ,current_page);
                }
                else {
                    Toast.makeText(getApplicationContext(), "end of book", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void ResizeAndDisplayImage(String imgpath){

        Bitmap bMap = BitmapFactory.decodeFile(imgpath);
        double imgWidth = bMap.getWidth();
        double imgHeight = bMap.getHeight();

        double ratio_x = imgWidth/screenWidth;
        double ratio_y = imgHeight/screenHeight;

        double coef_augmentation = 1.01;
        double coef_reduction = 0.99;

        int orientation = this.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            // en mode portrait

            //image plus petite que l'écran
            if (ratio_x < 1 && ratio_y < 1){
                while (true){
                    if(ratio_x * coef_augmentation <= 1 && ratio_y * coef_augmentation <= 1){
                        imgWidth = imgWidth * coef_augmentation;
                        imgHeight = imgHeight * coef_augmentation;
                        ratio_x = imgWidth/screenWidth;
                        ratio_y = imgHeight/screenHeight;
                    }
                    else {
                        File Img = new  File(imgpath);
                        Picasso.with(getApplicationContext()).load(Img).resize((int)imgWidth, (int)imgHeight).into(readingSD);
                        break;
                    }
                }
            }

            //image plus large que l'écran
            else if(ratio_x > 1 && ratio_y < 1){
                while (true){
                    if(ratio_x * coef_reduction > 1){
                        imgWidth = imgWidth * coef_reduction;
                        imgHeight = imgHeight * coef_reduction;
                        ratio_x = imgWidth/screenWidth;
                        ratio_y = imgHeight/screenHeight;
                    }
                    else {
                        File Img = new  File(imgpath);
                        Picasso.with(getApplicationContext()).load(Img).resize((int)imgWidth, (int)imgHeight).into(readingSD);
                        break;
                    }
                }

            }

            //image plus longue que l'écran
            else if(ratio_y > 1){
                while (true){
                    if(ratio_y * coef_reduction > 1){
                        imgWidth = imgWidth * coef_reduction;
                        imgHeight = imgHeight * coef_reduction;
                        ratio_x = imgWidth/screenWidth;
                        ratio_y = imgHeight/screenHeight;
                    }
                    else {
                        File Img = new  File(imgpath);
                        Picasso.with(getApplicationContext()).load(Img).resize((int)imgWidth, (int)imgHeight).into(readingSD);
                        break;
                    }
                }
            }

            //image plus grande que l'écran
            else if(ratio_x > 1 && ratio_y > 1){
                while (true){
                    if(ratio_x * coef_reduction > 1 && ratio_y * coef_reduction > 1){
                        imgWidth = imgWidth * coef_reduction;
                        imgHeight = imgHeight * coef_reduction;
                        ratio_x = imgWidth/screenWidth;
                        ratio_y = imgHeight/screenHeight;
                    }
                    else {
                        File Img = new  File(imgpath);
                        Picasso.with(getApplicationContext()).load(Img).resize((int)imgWidth, (int)imgHeight).into(readingSD);
                        break;
                    }
                }
            }

        }

        if (orientation == Configuration.ORIENTATION_LANDSCAPE){
            // en mode paysage
            //augmenter le ratio en x jusqu'a atteindre le bord de l'écran
            while (true){
                if(ratio_x <= 1){
                    imgWidth = imgWidth * coef_augmentation;
                    imgHeight = imgHeight * coef_augmentation;
                    ratio_x = imgWidth/screenWidth;
                }
                else {
                    File Img = new  File(imgpath);
                    Picasso.with(getApplicationContext()).load(Img).resize((int)imgWidth, (int)imgHeight).into(readingSD);
                    break;
                }
            }
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

    private void hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }


    //méthode de retour aux chapitres
    @Override
    public void onBackPressed() {

        switch (single_or_continue){
            case "single":
                Intent intent = new Intent(getApplicationContext(), ExploreFirstLevel.class);
                intent.putExtra("selected_manga",selected_manga);
                intent.putExtra("selection","scan");
                finish();

                startActivity(intent);
                break;

            case "continue":
                Intent intent2 = new Intent(getApplicationContext(), MainActivity.class);
                finish();

                startActivity(intent2);
                break;
        }
    }

}
