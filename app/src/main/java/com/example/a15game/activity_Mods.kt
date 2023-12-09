package com.example.a15game

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.ImageButton
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.bumptech.glide.Glide


class activity_Mods() : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        val actionBar: ActionBar? = supportActionBar
        if (actionBar != null) {
            actionBar.setBackgroundDrawable(ColorDrawable(Color.BLACK))
        }

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_mods)

        val necBut = findViewById<ImageButton>(R.id.button31)
        Glide.with(this).asGif().load(R.drawable.buttonbstuu).into(necBut)
    }
    fun easy_mode(view :View){
        val number = 9
        val intent = Intent(this,game_15_activity::class.java )
        intent.putExtra("EXTRA_NUMBER", number)
        startActivity(intent)
        finish()

    }
    fun standart_mode(view :View){
        val number = 16
        val intent = Intent(this,game_15_activity::class.java )
        intent.putExtra("EXTRA_NUMBER", number)
        startActivity(intent)
        finish()
    }
    fun hard_mode(view :View){
        val number = 25
        val intent = Intent(this,game_15_activity::class.java )
        intent.putExtra("EXTRA_NUMBER", number)
        startActivity(intent)
        finish()
    }
    fun Hardcore_OC(view: View){
        val number = 36
        val intent =Intent (this,game_15_activity::class.java)
        intent.putExtra("EXTRA_NUMBER", number)
        startActivity(intent)
        finish()
    }
}