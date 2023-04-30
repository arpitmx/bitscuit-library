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

        val changeLog = "Use special camera effects to bring your video to life, including background music, face filters, &; stickers\n" +
                "\n* Upload your creative video clips and get discovered on Explore\n" +
                "\n* Explore millions of videos\n" +
                "\n* Watch &amp; share any Instagram Reels video with your friends\n" +
                "\n* Upload your creative video clips and get discovered on Explore\n" +
                "\n* Explore millions of videos"

        val testUrl8MB = "https://firebasestorage.googleapis.com/v0/b/bitscuitapp.appspot.com/o/vidmate_3.5501_androidapksbox.apk?alt=media&token=6afd8b34-4798-49ef-bb49-b45b01643d18"
        val testUrl28MB = "https://firebasestorage.googleapis.com/v0/b/bitscuitapp.appspot.com/o/shazam_13.13.0-230116_androidapksbox.apk?alt=media&token=2d30def2-7eb7-4176-879e-52b49d70bd9b"
        val testUrl1MB = "https://firebasestorage.googleapis.com/v0/b/quizr-59d14.appspot.com/o/quizr.apk?alt=media&token=69cbc9db-77be-4544-88fb-430da1b6c345"
        val testUrl15MB = "https://loot.zealicon.in/Loot401.apk"
        model = ViewModelProvider(this)[MainActivityViewModel::class.java]
        bitscuit = Bitscuit.BitscuitBuilder()
                    .scope(this, appID = BuildConfig.APPLICATION_ID)
                    .config(true,testUrl15MB,"1.0.0",changeLog)
                    .build()


        binding.button.setOnClickListener{
            bitscuit.update()
        }


    }





}