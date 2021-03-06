package com.example.gallery_noob;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        loadColor();
        super.onCreate(savedInstanceState);
        loadLocale();
        setContentView(R.layout.activity_main);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        NavController navController = Navigation.findNavController(this,  R.id.fragment3);
        Set<Integer> topLevelDestinations = new HashSet<>();
        topLevelDestinations.add(R.id.firstFragment);
        topLevelDestinations.add(R.id.secondFragment);
        topLevelDestinations.add(R.id.thirdFragment);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(topLevelDestinations).build();
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        /*Intent intent=getIntent();
        String s=intent.getStringExtra("key");
        if (s!=null)
        {
            Log.d("dc1", s.toString());
        }
        if (s=="allow_select") {
            Log.e("dc", "ok");
            bottomNavigationView.setVisibility(View.GONE);
        }*/
    }

    @Override
    public Resources.Theme getTheme() {

        return super.getTheme();
    }

    private void setColor(int lang)
    {
        if (lang!=-1)
        {
            SharedPreferences.Editor editor = getSharedPreferences("SetColor", Context.MODE_PRIVATE).edit();
            editor.putInt("My_color_sl",lang);
            editor.apply();
            if (lang==0)
            {
                setTheme(R.style.ThemeChoice);
            }
            if (lang==1)
            {
                setTheme(R.style.ThemeChoice1);
            }
            if (lang==2)
            {
                setTheme(R.style.ThemeChoice2);
            }
        }
    }

    public void loadColor(){
        SharedPreferences preferences=getSharedPreferences("SetColor", MODE_PRIVATE);
        int language=preferences.getInt("My_color_sl",-1);
        setColor(language);
    }

    private void setLocate(String lang)
    {
        if (lang.length()!=0)
        {
            Locale locale = new Locale(lang);
            Locale.setDefault(locale);
            Configuration config= new Configuration();
            config.locale=locale;
            getBaseContext().getResources().updateConfiguration(config,getBaseContext().getResources().getDisplayMetrics());
            SharedPreferences.Editor editor = getSharedPreferences("Settings", Context.MODE_PRIVATE).edit();
            editor.putString("My_lang",lang);
            editor.apply();
        }
    }

    public void loadLocale(){
        SharedPreferences preferences=getSharedPreferences("Settings", MODE_PRIVATE);
        String language=preferences.getString("My_lang","");
        setLocate(language);
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//    }
}

