package com.example.a15game

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatDelegate
import java.util.*

private var availablequotes = mutableListOf(
    "Андрей Пучинский Ⓒ Успех - это не конечная точка, это постоянное движение вперед.",
    "Андрей Пучинский Ⓒ Получать удовольствие от процесса - вот ключ к успеху.",
    "Андрей Пучинский Ⓒ Если бы вы могли видеть конец дороги, тогда это не было бы дорогой. Так что наслаждайтесь каждым шагом на своем пути к успеху.",
    "Андрей Пучинский Ⓒ Не переживайте, что не удалось достичь цели. Важно продолжать двигаться вперед.",
    "Андрей Пучинский Ⓒ Побеждает тот, кто думает, что может.",
    "Андрей Пучинский Ⓒ Чем больше вы тренируетесь, тем удачливее вы становитесь.",
    "Андрей Пучинский Ⓒ Когда вы чувствуете, что не можете больше двигаться вперед, просто продолжайте двигаться. Иногда волна может быть большой, но если вы остановитесь, она может захлестнуть вас.",
    "Андрей Пучинский Ⓒ Когда вы проигрываете, не сдавайтесь. Начните сначала, на этот раз с опытом." ,
    "Андрей Пучинский Ⓒ Не существует ничего невозможного. Само слово говорит: «Я возможен!»",
    "Андрей Пучинский Ⓒ Успех не является ключом к счастью. Счастье - это ключ к успеху. Если вы любите то, что делаете, вы будете иметь успех.",
    "Андрей Пучинский ⒸПока вы продолжаете двигаться вперед, вы уже на пути к успеху.",
    "Андрей Пучинский ⒸУспех не приходит к тем, кто ждет, а к тем, кто действует.",
    "Андрей Пучинский ⒸЛучший способ предсказать будущее - создать его.",
    "Андрей Пучинский ⒸВаш потенциал невероятен. Просто поверьте в себя и начните действовать.",
    "Андрей Пучинский ⒸВы можете достичь всего, что захотите, если вы верите в себя.",
    "Андрей Пучинский ⒸПусть ваши неудачи станут вашими учителями, а не поводом для сдачи.",
    "Андрей Пучинский ⒸНе сдавайтесь, даже если кажется, что успеха недостижим.",
    "Андрей Пучинский ⒸЛучший способ начать что-то делать - просто начать.",
    "Андрей Пучинский ⒸНе думайте о том, что может пойти не так. Думайте о том, что может пойти хорошо.",
    "Андрей Пучинский ⒸНикогда не сдавайтесь в поиске того, что вам действительно нравится. Ведь это того стоит."
)



class AuthorsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val actionBar: ActionBar? = supportActionBar
        if (actionBar != null) {
            actionBar.setBackgroundDrawable(ColorDrawable(Color.BLACK))
        }

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_authors)
        showRandomQuoteToast(this)
    }



    fun showRandomQuoteToast(context: Context) {
        val inflater = LayoutInflater.from(context)
        val layout = inflater.inflate(R.layout.custom_toast_layout, null)

        // Get a random quote from the available quotes list
        val randomIndex = (0 until availablequotes.size).random()
        val randomQuote = availablequotes[randomIndex]

        // Set the message text
        val messageTextView = layout.findViewById<TextView>(R.id.toast_message)
        messageTextView.text = randomQuote

        // Create the toast
        val toast = Toast(context)
        toast.setGravity(Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL, 0, 713)

      toast.duration = Toast.LENGTH_LONG *2
        toast.view = layout

        // Show the toast
        toast.show()
    }



    }