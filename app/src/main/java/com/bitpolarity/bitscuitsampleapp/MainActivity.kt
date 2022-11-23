package com.bitpolarity.bitscuitsampleapp

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.bitpolarity.bitscuit.Bitscuit
import com.bitpolarity.bitscuitsampleapp.databinding.ActivityMainBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    lateinit var binding : ActivityMainBinding
    lateinit var bitscuit: Bitscuit
    lateinit var model : MainActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        model = ViewModelProvider(this)[MainActivityViewModel::class.java]
        bitscuit = Bitscuit.getInstance()
        bitscuit.init(this)
        bitscuit.checkUpdate(true,"www.google.com")



    }




    override fun finish() {
        super.finish()
        bitscuit.dismissBottomSheet()
    }
}