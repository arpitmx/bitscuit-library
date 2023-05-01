package com.bitpolarity.bitscuit

import android.Manifest.permission.*
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Build.TAGS
import android.os.Build.VERSION
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.lang.RuntimeException
import java.lang.ref.WeakReference
import java.util.logging.Logger
import kotlin.properties.Delegates


class Bitscuit private constructor(
   val isUpdateAvailable: Boolean,
   val url: String,
   val version: String,
   val changeLogs: String,
   val activityRef: WeakReference<Activity>,
   val appID: String
) {

    val TAG : String = this::javaClass.name+"Debug100"


//    private var isUpdateAvailable by Delegates.notNull<Boolean>()
//    private lateinit var url : String
//    private lateinit var version : String
//    private lateinit var changeLogs: String




    fun update(){
        val activity : Activity? = activityRef.get()

        if (activity!=null) {
            val downloadIntent = Intent(activity, DownloadManagerActivity::class.java)

            if (isUpdateAvailable) {
                downloadIntent.putExtra("updateUrl", url)
                downloadIntent.putExtra("version", version)
                downloadIntent.putExtra("logs", changeLogs)
                downloadIntent.putExtra("appID", appID)
            }
            activity.startActivity(downloadIntent)

        }else {
            Log.d(TAG, "Activity context is null")
        }
    }

     companion object{

        @Volatile
        private lateinit var instance : Bitscuit

         fun getInstance(isUpdateAvailable: Boolean, url : String, version : String , changeLogs: String , activityRef: WeakReference<Activity>, appID: String): Bitscuit {
             synchronized(this) {
                 if (!::instance.isInitialized) {
                     instance = Bitscuit(isUpdateAvailable,url,version,changeLogs,activityRef,appID)
                 }

             }
             return instance
            }
     }


   class BitscuitBuilder{
        private lateinit var acitivityRef : WeakReference<Activity>
        private lateinit var appID: String
        private var isUpdateAvailable by Delegates.notNull<Boolean>()
        private lateinit var url : String
        private lateinit var version : String
        private lateinit var changeLogs: String
        private val TAG : String = this::javaClass.name


        fun scope(context: Activity, appID : String) =
            apply {
                this.acitivityRef = WeakReference(context)
                this.appID = appID
                checkPermissions()
            }



        fun config(isUpdateAvailable: Boolean, url : String, version : String , changeLogs: String ) =
            apply {
                this.isUpdateAvailable = isUpdateAvailable
                this.url = url
                this.version = version
                this.changeLogs = changeLogs
            }



        fun build() : Bitscuit{
            return getInstance(isUpdateAvailable, url, version, changeLogs, acitivityRef, appID)
        }
        fun checkPermissions() {

            // Installing packages
            if (acitivityRef.get() != null) {

                val context = acitivityRef.get()
                if (VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    if (!context!!.applicationContext.packageManager.canRequestPackageInstalls()) {
                        context.startActivityForResult(
                            Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES)
                                .setData(
                                    Uri.parse(
                                        String.format(
                                            "package: %s",
                                            context.packageName
                                        )
                                    )
                                ), 1
                        )
                    }
                }
                // Read permissions
                if (ContextCompat.checkSelfPermission(context!!.applicationContext, READ_EXTERNAL_STORAGE) !=
                    PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(
                        context,
                        arrayOf(WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE), 1
                    )
                }

                // Write permissions
                if (ContextCompat.checkSelfPermission(
                        context,
                        WRITE_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {

                    ActivityCompat.requestPermissions(context, arrayOf(WRITE_EXTERNAL_STORAGE), 1)

                }

            }else {
                Log.d(TAG, "checkPermissions: Error in checking permissions")
            }

        }
    }

    }






