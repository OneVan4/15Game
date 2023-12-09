package com.example.a15game

import android.content.ContentValues.TAG
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase




class RecordsActivity : AppCompatActivity() {

    private var availableModes = mutableListOf(
        "Абсолютный рекорд", "Легко", "Нормально", "Тяжело", "БрГТУ на платном"
    )
    private var selectedMode: String = availableModes[0]

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        val actionBar: ActionBar? = supportActionBar
        if (actionBar != null) {
            actionBar.setBackgroundDrawable(ColorDrawable(Color.BLACK))
        }

        setContentView(R.layout.activity_records)

        val spinner = findViewById<Spinner>(R.id.Mode_spinner)
        val items = arrayOf("Абсолютный рекорд", "Легко", "Нормально","Тяжело","БрГТУ на платном")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, items)
        spinner.adapter = adapter
       spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedMode = availableModes[position]
                updateTable(selectedMode)
              /*  // Убираем выбранный режим из списка
                availableModes.removeAt(position)
                adapter.clear()
                adapter.addAll(availableModes.toList())*/
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        val textViewOff = findViewById<TextView>(R.id.textView6)
        val sharedPreferences = getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
        textViewOff.text= sharedPreferences.getString("text_key", "")  +   "      " +    ((sharedPreferences.getInt("highest_score", 0)).toString())

        updateTable("Абсолютный рекорд")


    }


    private fun updateTable(mode: String) {
        // Получаем ссылку на базу данных
        val playersRef = FirebaseDatabase.getInstance().getReference("players")

        // Добавляем слушатель событий к базе данных
        playersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val textView = findViewById<TextView>(R.id.textView6)
                textView.text =""
                // Создаем список игроков
                val playerList = ArrayList<Player>()

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


                // Сортируем список по score


                // Создаем строку заголовка таблицы
                var tableHeader = "Имя              Очки          Режим\n"
                var tableText =""
                when {

                    mode=="Легко" -> {  tableText = ""
                        playerList.sortByDescending { it.EasyScore.toIntOrNull() ?: 0 }
                        tableHeader = "Имя              Очки     \n"
                        var i : Int = 0
                        for (player in playerList) {
                            i++
                            val rowText = "$i  ${player.name}    ${player.EasyScore}   \n"
                            if(player.EasyScore.toInt()!=0){
                            tableText += rowText}
                        }}
                    mode=="Нормально" ->{ tableText = ""
                        playerList.sortByDescending { it.SrandartScore.toIntOrNull() ?: 0 }
                        tableHeader = "Имя              Очки     \n"
                        var i : Int = 0
                        for (player in playerList) {
                            i++
                            val rowText = "$i  ${player.name}    ${player.SrandartScore}   \n"
                            if(player.SrandartScore .toInt()!=0){
                                tableText += rowText}
                        } }
                    mode=="Тяжело" -> { tableText = ""
                        playerList.sortByDescending { it.HardScore.toIntOrNull() ?: 0 }
                        tableHeader = "Имя              Очки     \n"
                        var i : Int = 0
                        for (player in playerList) {
                            i++
                            val rowText = "$i  ${player.name}    ${player.HardScore}   \n"
                            if(player.HardScore.toInt()!=0){
                                tableText += rowText}
                        }}
                    mode=="БрГТУ на платном" -> { tableText = ""
                        playerList.sortByDescending { it.BSTUScore.toIntOrNull() ?: 0 }
                        tableHeader = "Имя              Очки     \n"
                        var i : Int = 0
                        for (player in playerList) {
                            i++
                            val rowText = "$i  ${player.name}    ${player.BSTUScore}   \n"
                            if(player.BSTUScore.toInt()!=0){
                                tableText += rowText}
                        }}
                    mode=="Абсолютный рекорд" -> { tableText = ""
                        playerList.sortByDescending { it.score.toIntOrNull() ?: 0 }
                        var i : Int = 0
                        for (player in playerList) {
                            i++
                            val rowText = "$i  ${player.name}    ${player.score}    ${player.mode}\n"
                            if(player.score.toInt()!=0){
                                tableText += rowText}
                        }}
                }

                textView.text = "Лучшие игроки\n$tableHeader$tableText"
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d("RecordsActivity", "onCancelled: " + databaseError.message)
            }
        })
    }

}