package com.bitpolarity.bitscuit

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import java.lang.ref.WeakReference
import kotlin.properties.Delegates


class Bitscuit private constructor(
   val url: String,
   val updateVersion: String,
   val changeLogs: String,
   val activityRef: WeakReference<Activity>,
   val appID: String
) {

    val TAG : String = this::javaClass.name+"Debug100"


//    private var isUpdateAvailable by Delegates.notNull<Boolean>()
//    private lateinit var url : String
//    private lateinit var version : String
//    private lateinit var changeLogs: String


    fun versionCompare(v1: String, v2: String): Int {
        var vnum1 = 0
        var vnum2 = 0
        var i = 0
        var j = 0
        while (i < v1.length
            || j < v2.length
        ) {
            while (i < v1.length
                && v1[i] != '.'
            ) {
                vnum1 = (vnum1 * 10
                        + (v1[i].code - '0'.code))
                i++
            }
            while (j < v2.length
                && v2[j] != '.'
            ) {
                vnum2 = (vnum2 * 10 + (v2[j].code - '0'.code))
                j++
            }
            if (vnum1 > vnum2) return 1
            if (vnum2 > vnum1) return -1
            vnum2 = 0
            vnum1 = vnum2
            i++
            j++
        }
        return 0
    }


    fun listenUpdate(){
        val activity : Activity? = activityRef.get()

        if (activity!=null) {
            val downloadIntent = Intent(activity, DownloadManagerActivity::class.java)
            val transitionOption = ActivityOptionsCompat.makeCustomAnimation(activity,
            R.anim.slide_in_right, R.anim.slide_out_left)

            val currentVersionCode = activity.packageManager.getPackageInfo(appID, 0).versionName
            val updateAvailable = versionCompare(currentVersionCode,updateVersion)

            if (updateAvailable<0) {
                downloadIntent.putExtra("updateUrl", url)
                downloadIntent.putExtra("version", updateVersion)
                downloadIntent.putExtra("logs", changeLogs)
                downloadIntent.putExtra("appID", appID)

                activity.startActivity(downloadIntent, transitionOption.toBundle())

            }else {
                Toast.makeText(activity, "This is the latest version", Toast.LENGTH_SHORT).show()
            }
        }else {
            Log.d(TAG, "Activity context is null")
        }
    }

     companion object{

        @Volatile
        private lateinit var instance : Bitscuit

         fun getInstance( url : String, version : String , changeLogs: String , activityRef: WeakReference<Activity>, appID: String): Bitscuit {
             synchronized(this) {
                 if (!::instance.isInitialized) {
                     instance = Bitscuit(url,version,changeLogs,activityRef,appID)
                 }

             }
             return instance
            }
     }


   class BitscuitBuilder(context: Activity){
        private var acitivityRef : WeakReference<Activity> = WeakReference(context)
       private lateinit var appID: String
        private lateinit var url : String
        private lateinit var version : String
        private lateinit var changeLogs: String
        private val TAG : String = this::javaClass.name


       init {
           checkPermissions( )
       }

        fun config( url : String, version : String , changeLogs: String ) =
            apply {
                this.url = url
                this.version = version
                this.changeLogs = changeLogs
            }



        fun build() : Bitscuit{
            return getInstance(url, version, changeLogs, acitivityRef, appID)
        }
        fun checkPermissions() {

            // Installing packages
            if (acitivityRef.get() != null) {

                val context = acitivityRef.get()
                this.appID = context!!.packageName

                if (VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    if (!context.applicationContext.packageManager.canRequestPackageInstalls()) {
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
                if (ContextCompat.checkSelfPermission(context.applicationContext, READ_EXTERNAL_STORAGE) !=
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






