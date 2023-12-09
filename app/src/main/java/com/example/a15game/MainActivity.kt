package com.example.a15game

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {
    var should : Boolean =true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val sharedPreferences = getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
        var isReg = sharedPreferences.getBoolean("isSigned",false)
        val editor = sharedPreferences.edit()
        val text = sharedPreferences.getString("text_key","")
        val actionBar: ActionBar? = supportActionBar
        if (actionBar != null) {
            actionBar.setBackgroundDrawable(ColorDrawable(Color.BLACK))
        }




        if(text=="" ||text.toString().isEmpty() || text==null){
            LoginActivity()
            editor.putBoolean("isSigned",false)
             isReg=false
            editor.apply()
            should = false
        }
        if(isReg ==false && should == true ){
            LoginActivity()
        }
       else if (isReg==true) {
            val inflater = LayoutInflater.from(this)
            val layout = inflater.inflate(R.layout.custom_toast_layout, null)

            // Get a random quote from the available quotes list
            val randomQuote ="Добро пожаловать ,$text !"

            // Set the message text
            val messageTextView = layout.findViewById<TextView>(R.id.toast_message)
            messageTextView.text = randomQuote

            // Create the toast
            val toast = Toast(this)
            toast.setGravity(Gravity.CENTER, 0, 713)

            toast.duration = Toast.LENGTH_SHORT
            toast.view = layout

            // Show the toast
            toast.show()


       }


    }
    fun startNewActivity(view: View) {
        val intent = Intent(this,AuthorsActivity::class.java)
        startActivity(intent)

    }
   fun recommenadtions_NewActivity(view :View){
       val intent =Intent(this,recommendationsActivity::class.java )
       startActivity(intent)

   }
    fun LoginActivity(){
        val intent = Intent (this , Login::class.java)
        startActivity(intent)


    }

    fun start_game_oc(view: View){
       val intent = Intent (this , activity_Mods::class.java)
        startActivity(intent)

    }
    fun Exit_OC(view: View){
        exitProcess(0);
        finishAffinity()
        finish()
        finish()
        finish()
    }
    fun settings_OC(view:View){
        val intent = Intent (this , settings_activity::class.java)
        val sharedPreferences = getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
        val text = sharedPreferences.getString("text_key", "")
        val soundSwitchState = sharedPreferences.getBoolean("sound_Switcher",false)
        val vibroSwitchState = sharedPreferences.getBoolean("vibro_Switcher",false)
        intent.putExtra("text_key", text)
        intent.putExtra("soundSwitchState_key",soundSwitchState  )
        intent.putExtra("vibroSwitchState _key",vibroSwitchState   )
        startActivity(intent)
        finish()

    }
    fun records_OC(view : View){
        val intent = Intent(this,RecordsActivity::class.java )
        startActivity(intent)

    }

}