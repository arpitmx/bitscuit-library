package com.bitpolarity.bitscuitsampleapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.bitpolarity.bitscuit.Bitscuit
import com.bitpolarity.bitscuitsampleapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var binding : ActivityMainBinding
    lateinit var bitscuit: Bitscuit
    lateinit var model : MainActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val changeLog = "This is version 1.0.1 of the bitscuit sample app, this contains updated version of the sample app"

        val testUrl8MB = "https://firebasestorage.googleapis.com/v0/b/bitscuitapp.appspot.com/o/vidmate_3.5501_androidapksbox.apk?alt=media&token=6afd8b34-4798-49ef-bb49-b45b01643d18"
        val testUrl28MB = "https://firebasestorage.googleapis.com/v0/b/bitscuitapp.appspot.com/o/shazam_13.13.0-230116_androidapksbox.apk?alt=media&token=2d30def2-7eb7-4176-879e-52b49d70bd9b"
        val testUrl1MB = "https://firebasestorage.googleapis.com/v0/b/quizr-59d14.appspot.com/o/quizr.apk?alt=media&token=69cbc9db-77be-4544-88fb-430da1b6c345"
        val testUrl15MB = "https://loot.zealicon.in/Loot401.apk"
        val testUrl7MB101 = "https://firebasestorage.googleapis.com/v0/b/bitscuitapp.appspot.com/o/app-debug101.apk?alt=media&token=9c7b2068-2982-413b-96c5-60bc3b5eaa63"
        val testUrl7MB100 = "https://firebasestorage.googleapis.com/v0/b/bitscuitapp.appspot.com/o/app-debug100.apk?alt=media&token=b3d11e13-bd5d-481b-bf00-a735ba28d76f"


        model = ViewModelProvider(this)[MainActivityViewModel::class.java]

        //Creating a bitscuit object using bitscuit builder
        bitscuit = Bitscuit.BitscuitBuilder(this)
            .config(testUrl28MB,"1.0.1",changeLog)
            .build()

        binding.button.setOnClickListener{
            bitscuit.listenUpdate()
        }


    }






}