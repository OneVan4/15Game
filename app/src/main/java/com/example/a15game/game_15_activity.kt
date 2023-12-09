package com.example.a15game


import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import android.os.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.bumptech.glide.Glide
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException
import java.lang.Math.abs
import java.lang.Math.sqrt
import java.util.*
import kotlin.system.exitProcess


class game_15_activity : AppCompatActivity() {

    private var n: Int = 0
    private var movesCount: Int = 0
    private val movesHistory: MutableList<MutableList<String>> = mutableListOf()
    private lateinit var mediaPlayer: MediaPlayer
    private var isPaused: Boolean = false
    private var GetElapsedTime: Int = 0
    var highestScore = 0
    val database = Firebase.database
    val playersRef = database.getReference("players")
    var isSaved: Boolean = false
    var shouldRestore: Boolean = false
    var hints: Int = 0
    var decr: Int = 0;
    var Modez: String = ""
    var AllTimeRecord: Int = 0
    var EasyModeRec: Int = 0
    var NormalModeRec: Int = 0
    var HardModeRec: Int = 0
    var BstuModeRec: Int = 0
    var GooseScore :Int=0
    private val random = Random()

    class MyAdapter(private val context: Context, private val numbers: MutableList<String>) : BaseAdapter() {
        fun getPositionofItem(string:String): Int{
            var value = string
            var rowIndex = -1 // Инициализируем переменную rowIndex значением -1 (значение -1 используется, чтобы указать, что элемент не найден)
            for (i in 0 until numbers.size) { // Итерируем по строкам массива
                    if (numbers[i] == value) { // Если значение элемента равно искомому значению,
                        rowIndex = i // то сохраняем номер строки в переменной rowIndex
                        break // и выходим из цикла
                }
                if (rowIndex != -1) { // Если элемент найден, то выходим из внешнего цикла
                    break
                }
            }
           return rowIndex
        }

        override fun getCount(): Int {
            return numbers.size
        }

        override fun getItem(position: Int): Any {
            return numbers[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val view: View
            val viewHolder: ViewHolder

            if (convertView == null) {
                view = LayoutInflater.from(context).inflate(R.layout.grid_item_layout, null)
                viewHolder = ViewHolder(view)
                view.tag = viewHolder
            } else {
                view = convertView
                viewHolder = view.tag as ViewHolder
            }

            // установка изображения
            viewHolder.imageView.setImageResource(R.drawable.migrosquare)

            // установка текста
            viewHolder.textView.text = numbers[position]

            return view
        }

        private class ViewHolder(view: View) {
            val imageView: ImageView = view.findViewById(R.id.grid_item_image)
            val textView: TextView = view.findViewById(R.id.grid_item_text)
        }
    }

    override fun onBackPressed() {

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Закончить игру ")
        builder.setMessage("Вы действительно хотите выйти ?")
        builder.setPositiveButton("Да") { _, _ -> super.onBackPressed() }
        builder.setNegativeButton("Нет", null)
        val dialog = builder.create()
        dialog.show()
    }

    fun Exit_OC(view: View) {
        exitProcess(0);
        finishAffinity()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_game15)
        val actionBar: ActionBar? = supportActionBar
        if (actionBar != null) {
            actionBar.setBackgroundDrawable(ColorDrawable(Color.BLACK))
        }

        val imageView = findViewById<ImageView>(R.id.imageView2)
        val lastimage = findViewById<ImageView>(R.id.lastImageView)
        Glide.with(this).load(R.drawable.goose).into(imageView)
        lastimage.visibility = View.GONE

        val saveButton = findViewById<ImageButton>(R.id.save_Button)
        val sharedPreferences = getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
        saveButton.setOnClickListener { saveGame() }
        val intent = intent
        n = (intent.getIntExtra("EXTRA_NUMBER", 0) - 1)
        shouldRestore = sharedPreferences.getBoolean("shouldRestore", false)
        val solveButton = findViewById<ImageButton>(R.id.autosolution_butt)

        // Создаём новый объект MediaPlayer
        mediaPlayer = MediaPlayer.create(this, R.raw.slide_sound)
        val NC: Int = sharedPreferences.getInt("n", 0)
        if (NC == n && shouldRestore == true) {
            movesCount = sharedPreferences.getInt("movesCount", 0)
            val movesHistoryJson = sharedPreferences.getString("movesHistory", "")
            movesHistory.addAll(
                Gson().fromJson(
                    movesHistoryJson,
                    object : TypeToken<MutableList<MutableList<String>>>() {}.type
                )
            )
            val sharedPreferences = getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putBoolean("isSaved", true)
            GetElapsedTime = sharedPreferences.getInt("gameTime", 0)

            val toast =
                Toast.makeText(this, "Cохраненная игра была восстановлена ", Toast.LENGTH_SHORT)
            toast.show()
            editor.apply()
        }
        val editor = sharedPreferences.edit()
        var Goose = sharedPreferences.getBoolean("goose",false)

        if (Goose) {
            runOnUiThread {
                imageView.visibility = View.VISIBLE

                // Получаем размеры экрана
                val displayMetrics = resources.displayMetrics
                val screenWidth = displayMetrics.widthPixels -70
                val screenHeight = displayMetrics.heightPixels -70

                // Получаем размеры imageView
                val imageViewWidth = imageView.width
                val imageViewHeight = imageView.height

                // Вычисляем максимальные координаты, где imageView может находиться на экране
                val maxX = screenWidth - imageViewWidth
                val maxY = screenHeight - imageViewHeight

                // Генерируем случайные координаты для imageView в пределах экрана
                val random = Random()
                val x = random.nextInt(maxX)
                val y = random.nextInt(maxY)

                // Устанавливаем позицию imageView
                imageView.x = x.toFloat()
                imageView.y = y.toFloat()

                // Запускаем анимацию перемещения imageView
                startMoving(imageView)
            }
        }
        isSaved = sharedPreferences.getBoolean("isSaved", false)
        var numbers = (1..n).map { it.toString() }.toMutableList().apply { add(""); shuffle() }
        val gridView = findViewById<GridView>(R.id.Game_Field_GV)
        while (!isSolvable(numbers)) {
            numbers.shuffle()

        }
        if (!movesHistory.isEmpty()) {

            numbers.clear()
            numbers.addAll(movesHistory.last())

        }

        val adapter = MyAdapter(this, numbers)
        val myButton = findViewById<ImageButton>(R.id.undo_button1)
        val coolButton = findViewById<ImageButton>(R.id.button2)
        coolButton.setOnClickListener {
            editor.putBoolean("shouldRestore", false)
            editor.apply()
            restartGame()

        }

        myButton.setOnClickListener {
            undo_OC(adapter, numbers)
        }


        gridView.numColumns = sqrt((n + 1).toDouble()).toInt()
        gridView.columnWidth = GridView.AUTO_FIT

        gridView.adapter = adapter
        movesHistory.add(numbers.toList().toMutableList())

        val soundSwitchState = sharedPreferences.getBoolean("sound_Switcher", false)
        val vibroSwitchState = sharedPreferences.getBoolean("vibro_Switcher", false)

        val pauseButton = findViewById<ImageButton>(R.id.pause_button)
        pauseButton.setOnClickListener { PauseOC() }
        EasyModeRec = sharedPreferences.getInt("highest_score_easy", 0)
        NormalModeRec = sharedPreferences.getInt("highest_score_normal", 0)
        HardModeRec = sharedPreferences.getInt("highest_score_hard", 0)
        BstuModeRec = sharedPreferences.getInt("highest_score_BSTU", 0)

        when {
            n == 8 -> {
                highestScore = sharedPreferences.getInt("highest_score_easy", 0)
            }
            n == 15 -> {
                highestScore = sharedPreferences.getInt("highest_score_normal", 0)
            }
            n == 24 -> {
                highestScore = sharedPreferences.getInt("highest_score_hard", 0)
            }
            n == 35 -> {
                highestScore = sharedPreferences.getInt("highest_score_BSTU", 0)
            }
        }
        AllTimeRecord = sharedPreferences.getInt("highest_score", 0)

        if(n!=8){ solveButton.visibility=View.GONE}

        solveButton.setOnClickListener {
            Toast.makeText(this, "Немного подождите", Toast.LENGTH_SHORT).show()
            var GoalState = (1..n).map { it.toString() }.toMutableList().apply { add("") }
            val startState = movesHistory.last()

            Thread {
                val solution = aStar(startState, GoalState, sqrt((n + 1).toDouble()).toInt())

                if (solution == null) {
                    // Handle unsolvable puzzle
                    return@Thread
                }

                val moves = mutableListOf<String>()
                val states = mutableListOf<List<String>>() // список всех состояний игры
                states.add(startState) // добавляем начальное состояние

                for (i in 0 until solution.size - 1) {
                    val currentNode = solution[i]
                    val nextNode = solution[i + 1]
                    states.add(nextNode.state) // добавляем новое состояние
                }

                runOnUiThread {
                    val dialogBuilder = AlertDialog.Builder(this)
                    dialogBuilder.setMessage("Список состояний поля для решения расстановки:\n\n${states.joinToString("\n") { it.chunked(3).joinToString(" | ", "|", "|") } }\n\nКоличество ходов: ${solution.size-1}")

                        .setCancelable(false)
                        .setPositiveButton("OK") { dialog, _ ->
                            dialog.dismiss()
                        }
                    val dialog = dialogBuilder.create()
                    dialog.show()
                }
            }.start()
        }


        gridView.setOnItemClickListener { parent, view, position, id ->
            if (!isPaused) {
                val movesTextView = findViewById<TextView>(R.id.moves_text_view)


                adapter.notifyDataSetChanged()
                val emptyButtonIndex = adapter.getPositionofItem("")
                val columnCount = gridView.numColumns


                if (mediaPlayer == null) {
                    mediaPlayer = MediaPlayer.create(this, R.raw.slide_sound)
                }


                if (position == emptyButtonIndex - 1 && position % columnCount != columnCount - 1) {
                    // Swap with left button
                    if (soundSwitchState) {
                        PlaySound()
                    }
                    if (vibroSwitchState) {
                        Vibrate()
                    }

                    Collections.swap(numbers, position, emptyButtonIndex)
                    movesHistory.add(numbers.toList().toMutableList())
                    movesCount++
                    movesTextView.text = "Moves: $movesCount"

                } else if (position == emptyButtonIndex + 1 && position % columnCount != 0) {
                    // Swap with right button

                    if (soundSwitchState) {
                        PlaySound()
                    }
                    if (vibroSwitchState) {
                        Vibrate()
                    }
                    Collections.swap(numbers, position, emptyButtonIndex)
                    movesHistory.add(numbers.toList().toMutableList())
                    movesCount++
                    movesTextView.text = "Moves: $movesCount"

                } else if (position == emptyButtonIndex - columnCount) {
                    // Swap with top button
                    if (soundSwitchState) {
                        PlaySound()
                    }
                    if (vibroSwitchState) {
                        Vibrate()
                    }
                    Collections.swap(numbers, position, emptyButtonIndex)
                    movesHistory.add(numbers.toList().toMutableList())
                    movesCount++
                    movesTextView.text = "Moves: $movesCount"

                } else if (position == emptyButtonIndex + columnCount) {
                    // Swap with bottom button
                    if (soundSwitchState) {
                        PlaySound()
                    }
                    if (vibroSwitchState) {
                        Vibrate()
                    }
                    Collections.swap(numbers, position, emptyButtonIndex)
                    movesHistory.add(numbers.toList().toMutableList())
                    movesCount++
                    movesTextView.text = "Moves: $movesCount"

                }


                if (movesCount == 1 || isSaved) {
                    val editor = sharedPreferences.edit()
                    editor.putBoolean("isSaved", false)
                    editor.apply()
                    isSaved = false
                    // Start a new thread to continuously update the moves and time
                    Thread {
                        while (!isSolved(numbers)) {

                            // Update the moves and time on the UI thread
                            runOnUiThread {
                                val movesTextView = findViewById<TextView>(R.id.moves_text_view)
                                val timeTextView = findViewById<TextView>(R.id.time_text_view)


                                GetElapsedTime++

                                val minutes = GetElapsedTime / 60
                                val seconds = GetElapsedTime % 60
                                val timeString =
                                    if (minutes > 0) "$minutes min " + "$seconds sec" else "" + "$seconds sec"
                                timeTextView.text = "Time: $timeString"

                            }

                            Thread.sleep(1000) // Sleep for 1 second
                            // Check if game is paused
                            while (isPaused) {
                                Thread.sleep(100) // Sleep for 0.1 second
                            }

                        }
                    }.start()
                }

                if (isSolved(numbers)) {

                    // Stop the thread that updates the moves and time
                    Thread.currentThread().interrupt()

                    val elapsedTime = GetElapsedTime

                    when {
                        n == 8 -> {
                            Modez = "легко"; decr = 10;
                        }
                        n == 15 -> {
                            Modez = "стандартно"; decr = 25
                        }
                        n == 24 -> {
                            Modez = "тяжело";decr = 30
                        }
                        n == 35 -> {
                            Modez = "БрГТУ на платном"; decr = 30
                        }
                    }
                    GetElapsedTime = 0
                    val steps = movesCount.toString()
                    val time = String.format("%d:%02d", elapsedTime / 60, elapsedTime % 60)

                    val score: Int =
                        if (((n * 1000) - (movesCount * decr) - (GetElapsedTime * 7) - (hints * 17))+GooseScore > 0) {
                            (n * 1000) - (movesCount * decr) - (GetElapsedTime * 7) - (hints * 13)+GooseScore
                        } else {
                            0
                        }



                    if (score.toInt() > highestScore.toInt()) {
                        highestScore = score

                        // Save the new highest score to program's memory
                        when {
                            n == 8 -> {
                                sharedPreferences.edit().putInt("highest_score_easy", highestScore)
                                    .apply();EasyModeRec = score
                            }
                            n == 15 -> {
                                sharedPreferences.edit()
                                    .putInt("highest_score_normal", highestScore)
                                    .apply();NormalModeRec = score
                            }
                            n == 24 -> {
                                sharedPreferences.edit().putInt("highest_score_hard", highestScore)
                                    .apply();HardModeRec = score
                            }
                            n == 35 -> {
                                sharedPreferences.edit().putInt("highest_score_BSTU", highestScore)
                                    .apply();BstuModeRec = score
                            }
                        }
                        if (score > AllTimeRecord) {
                            sharedPreferences.edit().putInt("highest_score", highestScore).apply()
                            AllTimeRecord = score
                        }
                        val text = sharedPreferences.getString("text_key", "")
                        val message =
                            "Поздравляем, $text .Вы решили эту задачу! \n\nШагов: $steps\nВремя: $time\nСчёт: $score"



                        val builder = AlertDialog.Builder(this)
                        builder.setTitle("Победа!")
                        builder.setMessage(message)
                        builder.setCancelable(false)
                        builder.setPositiveButton("Начать заново") { dialog, which ->
                            restartGame()
                        }
                        builder.setNegativeButton("Вернуться в меню") { dialog, which ->
                            returnToMenu()
                        }
                        builder.show()

                        var isenabled = sharedPreferences.getBoolean("cons",false)
                        var KateAndre = sharedPreferences.getBoolean("CharValue",true)
                        val lastimage = findViewById<ImageView>(R.id.lastImageView)

                        if (KateAndre && isenabled) {
                            lastimage.visibility = View.VISIBLE
                            Glide.with(this).load(R.drawable.katya).into(lastimage)
                            playMusicWithVolume(this, R.raw.katerecored)?.setOnCompletionListener {
                                lastimage.visibility = View.GONE
                            }
                        } else if (!KateAndre && isenabled) {
                            lastimage.visibility = View.VISIBLE
                            Glide.with(this).load(R.drawable.andre).into(lastimage)
                            playMusicWithVolume(this, R.raw.andre_record)?.setOnCompletionListener {
                                lastimage.visibility = View.GONE
                            }
                        }

                        val toast = Toast.makeText(
                            this,
                            "Поздравляем,$text ,вы поставили новый рекорд !",
                            Toast.LENGTH_SHORT
                        )
                        toast.show()
                        var mode: String = Modez
                        val na: Double = sqrt(n.toDouble() + 1.0)


                        val player =
                            Player(
                                text.toString(),
                                AllTimeRecord.toString(),
                                time.toString(),
                                steps.toString(),
                                "$mode",
                                EasyModeRec.toString(),
                                NormalModeRec.toString(),
                                HardModeRec.toString(),
                                BstuModeRec.toString()
                            ) // Создаем объект Player, который нужно добавить в базу данных
                        val playerHashMap = player.toMap() // преобразуем объект Player в HashMap

                        // Add the player's information to the database
                        playersRef.child(player.name).setValue(playerHashMap)
                    } else {
                        val sharedPreferences =
                            getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
                        val editor = sharedPreferences.edit()
                        editor.putBoolean("shouldRestore", false)
                        editor.apply()
                        val text = sharedPreferences.getString("text_key", "")
                        val message =
                            "Поздравляем, $text .Вы решили эту задачу! \n\nШагов: $steps\nВремя: $time\nСчёт: $score"
                        val builder = AlertDialog.Builder(this)
                        builder.setTitle("Победа!")
                        builder.setMessage(message)
                        builder.setCancelable(false)
                        builder.setPositiveButton("Начать заново") { dialog, which ->
                            editor.putBoolean("shouldRestore", false).apply()
                            editor.apply()
                            restartGame()
                        }
                        builder.setNegativeButton("Вернуться в меню") { dialog, which ->
                            editor.putBoolean("shouldRestore", false).apply()
                            editor.apply()
                            returnToMenu()
                        }
                        builder.show()
                        var isenabled = sharedPreferences.getBoolean("cons",false)
                        var KateAndre = sharedPreferences.getBoolean("CharValue",true)
                        val lastimage = findViewById<ImageView>(R.id.lastImageView)

                        if (KateAndre && isenabled) {
                            lastimage.visibility = View.VISIBLE
                            Glide.with(this).load(R.drawable.katya).into(lastimage)
                            playMusicWithVolume(this, R.raw.katecon)?.setOnCompletionListener {
                                lastimage.visibility = View.GONE
                            }
                        } else if (!KateAndre && isenabled) {
                            lastimage.visibility = View.VISIBLE
                            Glide.with(this).load(R.drawable.andre).into(lastimage)
                            playMusicWithVolume(this, R.raw.andrecon)?.setOnCompletionListener {
                                lastimage.visibility = View.GONE
                            }
                        }

                    }

                }
            }
        }
    }

    private fun saveGame() {
        // Save the game progress
        val sharedPreferences = getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt("n", n)
        editor.putInt("movesCount", movesCount)
        editor.putString("movesHistory", Gson().toJson(movesHistory))
        editor.putInt("gameTime", GetElapsedTime)
        editor.putBoolean("shouldRestore", true)
        editor.apply()
        // Show a success message
        Toast.makeText(this, "Игра сохранена успешно ! ", Toast.LENGTH_SHORT).show()

    }
    fun playMusicWithVolume(context: Context, resId: Int): MediaPlayer? {
        val sharedPreferences = getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
        var volume: Float = sharedPreferences.getInt("MusicValue", 0).toFloat()
        try {
            if (mediaPlayer != null) {
                mediaPlayer!!.reset()
                mediaPlayer!!.release()
            }
            mediaPlayer = MediaPlayer.create(context, resId)
            mediaPlayer!!.setVolume(volume, volume)
            mediaPlayer!!.setOnCompletionListener(object: MediaPlayer.OnCompletionListener {
                override fun onCompletion(mp: MediaPlayer?) {
                    // Остановить и освободить ресурсы MediaPlayer после окончания проигрывания звука
                    mp?.stop()
                    mp?.release()

                        mediaPlayer = MediaPlayer.create(context, R.raw.slide_sound)

                }
            })
            mediaPlayer!!.start()
            return mediaPlayer
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }



    private fun PlaySound() {
        val sharedPreferences = getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
        var volume: Float = sharedPreferences.getInt("MusicValue", 0).toFloat()

        mediaPlayer?.let { mp ->
            try {
                if (!mp.isPlaying) {
                    mp.setVolume(volume, volume) // устанавливаем громкость
                    mp.start()
                }
            } catch (e: IllegalStateException) {
                e.printStackTrace()
                // handle exception
                mediaPlayer.release()
            } catch (e: NullPointerException) {
                e.printStackTrace()
                // handle exception
                mediaPlayer.release()
            } catch (e: IOException) {
                e.printStackTrace()
                // handle exception
                mediaPlayer.release()
            } catch (e: SecurityException) {
                e.printStackTrace()
                // handle exception
                mediaPlayer.release()
            } catch (e: Exception) {
                e.printStackTrace()
                if (mediaPlayer == null) {
                    mediaPlayer = MediaPlayer.create(this, R.raw.slide_sound)
                }
            }
        }
    }


    private fun Vibrate() {
        val sharedPreferences = getSharedPreferences("my_prefs", Context.MODE_PRIVATE)

        var vibroPower = sharedPreferences.getInt("vibroPower", 100)
        if (vibroPower == 0) {
            vibroPower = 1
        }

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

    private fun isSolved(numbers: MutableList<String>): Boolean {
        return numbers == (1..n).map { it.toString() } + ""
    }


    private fun restartGame() {
        val sharedPreferences = getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("shouldRestore", false)
        editor.apply()
        val intent = Intent(this, game_15_activity::class.java)

        intent.putExtra("EXTRA_NUMBER", n + 1)
        startActivity(intent)
        finish()
    }

    private fun returnToMenu() {
        val sharedPreferences = getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("shouldRestore", false)
        editor.apply()
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        finish()
    }


    fun startAgainActivity(view: View) {
        resetValues()
        val sharedPreferences = getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("shouldRestore", false)
        editor.apply()
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Вы точно хотите начать заново?")
            .setPositiveButton("Да") { dialog, id ->
                val intent = Intent(this, game_15_activity::class.java)
                intent.putExtra("EXTRA_NUMBER", n + 1)
                startActivity(intent)
                Thread.currentThread().interrupt()
                finish()
            }
            .setNegativeButton("Нет", null)
        val dialog = builder.create()
        dialog.show()
    }

    fun undo_OC(adapter: MyAdapter, numbers: MutableList<String>) {
        if (movesHistory.size > 1 && !(movesHistory.isEmpty())) {
            movesHistory.removeLast() // удалить последний элемент из movesHistory
            val previousMoves = movesHistory.last() // получить предыдущее состояние поля

            // установить предыдущее состояние поля
            numbers.clear()
            numbers.addAll(previousMoves)

            // обновить адаптер и счетчик ходов
            adapter.notifyDataSetInvalidated()
            val movesTextView = findViewById<TextView>(R.id.moves_text_view)
            movesTextView.text = "Moves: $movesCount"
        }
    }


    fun PauseOC() {
        isPaused = true // остановка игры
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Пауза")
        builder.setPositiveButton("Продолжить") { _, _ ->
            isPaused = false // возобновление игры
        }
        builder.setCancelable(false) // не закрывать окно до нажатия кнопки
        val dialog = builder.create()

        dialog.show()

    }

    fun isSolvable(numbers: List<String>): Boolean {
        val n = numbers.size
        var inversionsCount = 0
        for (i in 0 until n) {
            for (j in i + 1 until n) {
                if (numbers[i].isNotEmpty() && numbers[j].isNotEmpty() && numbers[i].toInt() > numbers[j].toInt()) {
                    inversionsCount++
                }
            }
        }
        if (n % 2 == 1) {
            return inversionsCount % 2 == 0
        } else {
            val emptyIndex = numbers.indexOf("")
            val emptyRow = emptyIndex / sqrt(n.toDouble()).toInt()
            return (emptyRow + inversionsCount) % 2 == 1
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        // Освобождаем ресурсы MediaPlayer при уничтожении активности
        mediaPlayer.release()
        finish()
    }

    override fun onPause() {
        super.onPause()
        isPaused = true
    }

    private fun resetValues() {
        n = 0
        movesCount = 0
        GetElapsedTime = 0
        movesCount = 0
        movesHistory.clear()
        val sharedPreferences = getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt("n", n)
        editor.putInt("movesCount", movesCount)
        editor.putString("movesHistory", Gson().toJson(movesHistory))
        editor.putInt("StepsAmount", movesCount)
        editor.putInt("gameTime", GetElapsedTime)
        editor.apply()
    }

    override fun onResume() {
        super.onResume()
        if (isPaused) {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Пауза")
            builder.setPositiveButton("Продолжить") { _, _ ->
                isPaused = false // возобновление игры
            }
            builder.setCancelable(false) // не закрывать окно до нажатия кнопки
            val dialog = builder.create()

            dialog.show()
        }
    }


    private fun startMoving(imageView: ImageView) {
        val screenWidth = resources.displayMetrics.widthPixels
        val screenHeight = resources.displayMetrics.heightPixels

        val maxX = screenWidth - imageView.width
        val maxY = screenHeight - imageView.height

        // Randomize initial position within screen bounds
        val x = random.nextInt(maxX)
        val y = random.nextInt(maxY)
        imageView.x = x.toFloat()
        imageView.y = y.toFloat()

        // Randomize initial direction of movement
        val dx = random.nextInt(21) - 10 // Random value between -10 and 10
        val dy = random.nextInt(21) - 10 // Random value between -10 and 10

        val duration = 23L
        val handler = Handler()
        val runnable = object : Runnable {
            var vx = dx
            var vy = dy
            override fun run() {
                // Move the image by vx and vy
                imageView.x += vx
                imageView.y += vy

                // If the image would go outside screen bounds, reverse its direction
                if (imageView.x < 0 || imageView.x > maxX) {
                    vx = -vx
                }
                if (imageView.y < 0 || imageView.y > maxY) {
                    vy = -vy
                }

                // Post a delayed runnable to keep animating the image
                handler.postDelayed(this, duration)
            }
        }

        // Start the animation loop
        handler.postDelayed(runnable, duration)

        // Set click listener to stop animation when image is clicked
        imageView.setOnClickListener {
            playMusicWithVolume(this, R.raw.duck_quacking_37392)
            GooseScore = 151
            Glide.with(this).load(R.drawable.stoopets).into(imageView)
            handler.removeCallbacks(runnable)
            Handler().postDelayed({
                imageView.visibility = View.GONE
            }, 1000)
        }
    }












    fun aStar(startState: List<String>, goalState: List<String>, size: Int): List<Node>? {
        val openList = PriorityQueue<Node>()
        val closedList = mutableSetOf<List<String>>()

        val startNode = Node(startState.toMutableList(), 0, manhattanDistance(startState, size), null)
        openList.add(startNode)

        while (openList.isNotEmpty()) {
            val current = openList.poll()

            if (current.state == goalState) {
                val path = mutableListOf<Node>()
                var node = current
                while (node.prev != null) {
                    path.add(node)
                    node = node.prev!!
                }
                path.reverse()
                path.add(current)
                return path
            }

            closedList.add(current.state)

            val neighbours = getNeighbours(current, size)
            for (neighbour in neighbours) {
                if (neighbour.state !in closedList) {
                    val existingNode = openList.find { it.state == neighbour.state }
                    if (existingNode != null) {
                        if (neighbour.g < existingNode.g) {
                            existingNode.g = neighbour.g
                            existingNode.prev = neighbour.prev
                        }
                    } else {
                        openList.add(neighbour)
                    }
                }
            }
        }

        return null
    }

    fun manhattanDistance(state: List<String>, size: Int): Int {
        var distance = 0
        for (i in 0 until size) {
            for (j in 0 until size) {
                val value = state[i * size + j]
                if (value != "") {
                    val row = (value.toInt() - 1) / size
                    val col = (value.toInt() - 1) % size
                    distance += abs(i - row) + abs(j - col)
                }
            }
        }
        return distance
    }

    fun getNeighbours(node: Node, size: Int): List<Node> {
        val neighbours = mutableListOf<Node>()
        val emptyIndex = node.state.indexOf("")

        if (emptyIndex % size > 0) { // Move left
            val newState = node.state.toMutableList()
            Collections.swap(newState, emptyIndex, emptyIndex - 1)
            neighbours.add(Node(newState, node.g + 1, manhattanDistance(newState, size), node))
        }

        if (emptyIndex % size < size - 1) { // Move right
            val newState = node.state.toMutableList()
            Collections.swap(newState, emptyIndex, emptyIndex + 1)
            neighbours.add(Node(newState, node.g + 1, manhattanDistance(newState, size), node))
        }

        if (emptyIndex >= size) { // Move up
            val newState = node.state.toMutableList()
            Collections.swap(newState, emptyIndex, emptyIndex - size)
            neighbours.add(Node(newState, node.g + 1, manhattanDistance(newState, size), node))
        }

        if (emptyIndex < size * (size - 1)) { // Move down
            val newState = node.state.toMutableList()
            Collections.swap(newState, emptyIndex, emptyIndex + size)
            neighbours.add(Node(newState, node.g + 1, manhattanDistance(newState, size), node))
        }

        return neighbours
    }

    data class Node(val state: MutableList<String>, var g: Int, var h: Int, var prev: Node?) : Comparable<Node> {
        val f: Int
            get() = g + h

        override fun compareTo(other: Node): Int {
            return f - other.f
        }

    }

        }