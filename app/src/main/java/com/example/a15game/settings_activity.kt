package com.example.a15game

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.*
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.view.Window
import android.widget.*
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.nio.channels.Pipe


class settings_activity : AppCompatActivity() {

    private lateinit var mediaPlayer: MediaPlayer
    val playerList = ArrayList<Player>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        val actionBar: ActionBar? = supportActionBar
        if (actionBar != null) {
            actionBar.setBackgroundDrawable(ColorDrawable(Color.BLACK))
        }

        setContentView(R.layout.activity_settings)
        val Kate =findViewById<ImageView>(R.id.imageView4)
        val Andre = findViewById<ImageView>(R.id.imageView3)
        val playersRef = FirebaseDatabase.getInstance().getReference("players")

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



        val sharedPreferences = getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
        mediaPlayer = MediaPlayer.create(this, R.raw.animal_bird_duck_quack_003)
        val EditText = findViewById<EditText>(R.id.editTextTextPersonName)
        val saveButton = findViewById<ImageButton>(R.id.save_buton)
        val seekBarMusic = findViewById<SeekBar>(R.id.seekBarM)
        val seekBar = findViewById<SeekBar>(R.id.seekBar2)
        val gooseSwitch = findViewById<Switch>(R.id.switch7)
        val personswitch =findViewById<Switch>(R.id.CharacterSelector)
        val conSwitcher= findViewById<Switch>(R.id.con_Switch)
        personswitch.isChecked= sharedPreferences.getBoolean("CharValue",false)
        personswitch.setOnCheckedChangeListener { _, isChecked ->

            if (isChecked) {
                playMusicWithVolume(this@settings_activity ,R.raw.katecon, seekBarMusic.progress.toFloat()/100)
            } else {
                playMusicWithVolume(this@settings_activity ,R.raw.andrecon, seekBarMusic.progress.toFloat()/100)
            }
        }
        gooseSwitch.isChecked = sharedPreferences.getBoolean("goose",false)
        seekBarMusic.max =  100
        seekBarMusic.progress =sharedPreferences.getInt("MusicValue",0)
        seekBar.max = 255 // Максимальное значение SeekBar
        seekBar.progress = sharedPreferences.getInt("vibroPower",100) // Начальное значение SeekBar
        conSwitcher.isChecked= sharedPreferences.getBoolean("cons",false)
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {

            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                Vibrate(seekBar.progress)
            }
        })

        seekBarMusic.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {

            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
               playMusicWithVolume(this@settings_activity ,R.raw.animal_bird_duck_quack_003, (seekBarMusic.progress.toFloat())/100)
            }
        })

        saveButton.setOnClickListener {

            save_OC(seekBar.progress, seekBarMusic.progress)
        }
        val imageView = findViewById<ImageView>(R.id.imageView)
        Glide.with(this).load(R.drawable.goose).into(imageView)
        val receivedText:String = intent.getStringExtra("text_key").toString()
        val recievedSoundState = intent.getBooleanExtra("soundSwitchState_key",false)
        val recievedVibroState = intent.getBooleanExtra("vibroSwitchState _key",false)
        val textView = findViewById<TextView>(R.id.editTextTextPersonName)
        textView.text = receivedText
        val sound_Swithcher = findViewById<Switch>(R.id.sound_Swithcher)
        val vibro_Switcher = findViewById<Switch>(R.id.vibro_Switcher)
        sound_Swithcher.isChecked= recievedSoundState
        vibro_Switcher.isChecked= recievedVibroState
        imageView.setOnClickListener(){
            Goose_OC()
        }
        Kate.setOnClickListener{
            playMusicWithVolume(this@settings_activity ,R.raw.katecon, seekBarMusic.progress.toFloat()/100)
        }
       Andre.setOnClickListener{
           playMusicWithVolume(this@settings_activity ,R.raw.andrecon, seekBarMusic.progress.toFloat()/100)
       }
    }

    private fun Vibrate (Vibro : Int){
        var vibroPower :Int
        if(Vibro!=0){
         vibroPower = Vibro}
        else vibroPower =1
        val vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
        // Проверяем, поддерживает ли устройство API level 26 и выше
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Используем VibrationEffect для определения паттерна вибрации с измененной амплитудой
            val vibrationEffect = VibrationEffect.createOneShot(100, vibroPower)
            vibrator.vibrate(vibrationEffect)
        } else {
            // Для более ранних версий используем устаревший метод vibrate с измененной амплитудой
            vibrator.vibrate(longArrayOf(0, 1000), 0)
        }
    }

    fun Goose_OC() {
        if (!mediaPlayer.isPlaying) {
            try {
                mediaPlayer.start()
                mediaPlayer.setOnCompletionListener {
                    mediaPlayer.release()
                    mediaPlayer = MediaPlayer.create(this, R.raw.animal_bird_duck_quack_003)
                }
            } catch (e: IllegalStateException) {
                // handle exception
            } catch (e: NullPointerException) {
                // handle exception
            }
        }
    }

    fun save_OC(vibropow:Int,MusicValue:Int) {



        val EditText = findViewById<EditText>(R.id.editTextTextPersonName)
        val sound_Swithcher = findViewById<Switch>(R.id.sound_Swithcher)
        val vibro_Switcher = findViewById<Switch>(R.id.vibro_Switcher)
        val goose_Switcher = findViewById<Switch>(R.id.switch7)
        val persSwitcher = findViewById<Switch>(R.id.CharacterSelector)
        val conSwitcher= findViewById<Switch>(R.id.con_Switch)
        val sharedPreferences = getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
        val name : String? = sharedPreferences.getString("text_key","")
        if(name.toString() != EditText.text.toString()) {
            if (!EditText.text.isEmpty() && !(EditText.text.toString() == "")) {
                if (playerList.isEmpty()) {
                    val toast = Toast.makeText(this, "Подключитесь к интернету", Toast.LENGTH_SHORT)
                    toast.show()
                    return
                }
                val text: String = EditText.text.toString()
                for (player in playerList) {
                    if (player.name == text) {
                        val toast = Toast.makeText(
                            this,
                            "такой пользователь уже есть !",
                            Toast.LENGTH_SHORT
                        )
                        toast.show()
                        return

                    }
                }

            } else {
                val toast = Toast.makeText(this, "Давай всё-таки введём имя ;)", Toast.LENGTH_SHORT)
                toast.show()
            }
        }
        // Получаем объект Editor для редактирования настроек
        val editor = sharedPreferences.edit()


        val text = EditText.text.toString()
        editor.putString("text_key", text)

        // Сохраняем состояние Switch
        val switchGoose = goose_Switcher.isChecked
        val conSwitchState = conSwitcher.isChecked
        editor.putBoolean("cons",conSwitchState)
        editor.putBoolean("goose",switchGoose)
        val switchState = sound_Swithcher.isChecked
        editor.putBoolean("sound_Switcher", switchState)
        val switchState2 = vibro_Switcher.isChecked
        editor.putBoolean("vibro_Switcher", switchState2)
        editor.putInt("vibroPower",vibropow)
        editor.putInt("MusicValue",MusicValue)
        editor.putBoolean("CharValue",persSwitcher.isChecked)
        // Применяем изменения
        editor.apply()

        // Показываем сообщение об успешном сохранении настроек
        val toast = Toast.makeText(this, "Ваши настройки были успешно сохранены", Toast.LENGTH_SHORT)
        toast.show()
        startNewActivity()
        // Удаляем сообщение через 2 секунды
        Handler(Looper.getMainLooper()).postDelayed({
            toast.cancel()
        }, 2000)
    }

    fun startNewActivity() {
        val intent = Intent(this,MainActivity::class.java)
        startActivity(intent)
        finish()
    }
    override fun onBackPressed() {
        val intent = Intent(this,MainActivity::class.java)
        startActivity(intent)
        finish()

    }

    fun playMusicWithVolume(context: Context, resourceId: Int, volume: Float) {
        val mediaPlayer = MediaPlayer.create(context, resourceId)
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        val volumeToSet = maxVolume * volume
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volumeToSet.toInt(), 0)
        mediaPlayer.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }

}