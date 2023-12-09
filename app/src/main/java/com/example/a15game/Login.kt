package com.example.a15game

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val actionBar: ActionBar? = supportActionBar
        if (actionBar != null) {
            actionBar.setBackgroundDrawable(ColorDrawable(Color.BLACK))
        }

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_login)
        val sharedPreferences = getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
        editor.putBoolean("isSigned",false)
        editor.putInt("highest_score", 0)
        editor.putInt("highest_score_easy", 0)
        editor.putInt("highest_score_normal", 0)
        editor.putInt("highest_score_hard", 0)
        editor.putInt("highest_score_BSTU", 0)
        editor.apply()

        val textView = findViewById<TextView>(R.id.Name_TB)
        val ok_Button= findViewById<ImageButton>(R.id.ok_Button_713)
        val playersRef = FirebaseDatabase.getInstance().getReference("players")
        val playerList = ArrayList<Player>()
        // Добавляем слушатель событий к базе данных
        playersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Создаем список игроков


                // Проходимся по всем дочерним элементам в базе данных
                for (ds in dataSnapshot.children) {
                    // Получаем значения полей из текущего элемента
                    val name = ds.child("Name").getValue(String::class.java)
                    val score = ds.child("Score").getValue(String::class.java)
                    val time = ds.child("Time").getValue(String::class.java)
                    val moves = ds.child("Moves").getValue(String::class.java)
                    val mode = ds.child("Mode").getValue(String::class.java)
                    val easyRecord = ds.child("EasyScore").getValue(String::class.java)
                    val normalRecord=ds.child("StandartScore").getValue(String::class.java)
                    val hardRecord=ds.child("HardScore").getValue(String::class.java)
                    val bstuRecord=ds.child("BSTUScore").getValue(String::class.java)
                    // Если все поля не равны null, то создаем объект Player и добавляем его в список
                    if (name != null && score != null && time != null && moves != null && mode != null && easyRecord!=null
                        &&normalRecord!=null && hardRecord!=null && bstuRecord!=null) {
                        val player = Player(name, score, time, moves, mode, easyRecord, normalRecord, hardRecord, bstuRecord)
                        playerList.add(player)
                    }
                }





            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d("RecordsActivity", "onCancelled: " + databaseError.message)
            }
        })
        ok_Button.setOnClickListener(){
            if(!textView.text.isEmpty() && !(textView.text.toString()=="")){
                var isvalid:Boolean = false
                if(playerList.isEmpty()){
                    val toast = Toast.makeText(this, "Подключитесь к интернету", Toast.LENGTH_SHORT)
                    toast.show()
                    return@setOnClickListener
                }
                val text :String = textView.text.toString()
                for(player in playerList){
                    if(player.name==text){
                        val toast = Toast.makeText(this, "такой пользователь уже есть !", Toast.LENGTH_SHORT)
                        toast.show()
                        return@setOnClickListener

                    }
                }
                editor.putString("text_key", text)
                editor.putBoolean("isSigned",true)
                editor.apply()

                // Показываем сообщение об успешном сохранении настроек
                val toast = Toast.makeText(this, "Добро пожаловать,$text !", Toast.LENGTH_SHORT)
                toast.show()
                 startNewActivity()
            }
            else {
                val toast = Toast.makeText(this, "Давай всё-таки введём имя ;)", Toast.LENGTH_SHORT)
                toast.show()
            }
        }
    }


    fun startNewActivity() {
        val intent = Intent(this,MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}