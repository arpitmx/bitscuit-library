package com.bitpolarity.bitscuit

import android.Manifest.permission.*
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION
import android.os.Bundle
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


class Bitscuit private constructor(){

    private lateinit var context : Activity


     companion object{

        @Volatile
        private lateinit var instance : Bitscuit

         fun getInstance(): Bitscuit {
             synchronized(this) {
                 if (!::instance.isInitialized) {
                     instance = Bitscuit()
                 }

             }
             return instance
            }
     }


    val TAG : String = "Bitscuit.kt"

    private lateinit var appID: String


    fun init(context: Activity, appID : String) {
        this.context = context
        this.appID = appID
        checkPermissions()
    }


    fun checkPermissions(){

        // Installing packages
        if (VERSION.SDK_INT >= Build.VERSION_CODES.O){
            if (!context.packageManager.canRequestPackageInstalls()){
                context.startActivityForResult(Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES)
                    .setData(Uri.parse(String.format("package: %s", context.packageName))),1)
            }
        }

        // Storage permissions
        if (ContextCompat.checkSelfPermission(context, READ_EXTERNAL_STORAGE)!=
                PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(context,
                arrayOf(WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE),1)
        }

        if (ContextCompat.checkSelfPermission(context, WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(context, arrayOf(WRITE_EXTERNAL_STORAGE), 1)

        }

    }

    fun update(isUpdateAvailable: Boolean, url : String?, version : String? , changeLogs: String? ) {



            if (isUpdateAvailable){
                        val downloadIntent = Intent(context, DownloadManagerActivity::class.java)
                        downloadIntent.putExtra("updateUrl",url)
                        downloadIntent.putExtra("version",version)
                        downloadIntent.putExtra("logs",changeLogs)
                        downloadIntent.putExtra("appID",appID)
                        context.startActivity(downloadIntent)
            }


        }

    }






