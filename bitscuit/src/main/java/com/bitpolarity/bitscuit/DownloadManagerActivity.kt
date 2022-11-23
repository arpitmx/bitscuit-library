package com.bitpolarity.bitscuit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bitpolarity.bitscuit.databinding.ActivityDownloadManagerBinding

class DownloadManagerActivity : AppCompatActivity() {

    lateinit var binding : ActivityDownloadManagerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDownloadManagerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initFrag(savedInstanceState)


    }

    fun initFrag(savedInstanceState: Bundle?){
        if (savedInstanceState == null){
            supportFragmentManager
                .beginTransaction()
                .add(R.id.framelayout_placeholder,DownloaderFragment.newInstance(),"Downloader")
                .commit()
        }
    }



}