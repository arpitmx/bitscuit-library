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


        var url = "https://firebasestorage.googleapis.com/v0/b/quizr-59d14.appspot.com/o/quizr.apk?alt=media&token=69cbc9db-77be-4544-88fb-430da1b6c345"
        model = ViewModelProvider(this)[MainActivityViewModel::class.java]
        bitscuit = Bitscuit.getInstance()
        bitscuit.init(this, BuildConfig.APPLICATION_ID)

        bitscuit.update(true,url,"1.0.0",changeLog)



    }





}