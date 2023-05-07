package com.bitpolarity.bitscuit

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import com.bitpolarity.bitscuit.databinding.ActivityDownloadManagerBinding
import com.bitpolarity.bitscuit.databinding.FragmentDownloaderBinding
import com.bitpolarity.bitscuit.databinding.UpdateBottomsheetBinding
import com.bitpolarity.bitscuit.helper.NetworkConnectivityReceiver
import com.bitpolarity.bitscuit.helper.NetworkStatusCallback
import com.bitpolarity.bitscuit.model.UpdateItem
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import io.reactivex.BackpressureStrategy
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposables
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import java.io.File
import java.util.concurrent.TimeUnit


class DownloadManagerActivity : AppCompatActivity(),NetworkStatusCallback {

    private lateinit var binding: ActivityDownloadManagerBinding
    private lateinit var updateBottomSheet: BottomSheetDialog

    // Bottom Sheet Views
    private lateinit var versionTxt: TextView
    private lateinit var changeLog: TextView
    private lateinit var updateBtn: Button
    private lateinit var scrollView: ScrollView
    private lateinit var bindingBottomSheet: UpdateBottomsheetBinding

    private lateinit var updateItem: UpdateItem
    private lateinit var bindingInclude: FragmentDownloaderBinding
    private lateinit var appID: String
    private var isConnected: Boolean? = null

    private val connectivityReceiver = NetworkConnectivityReceiver(this)


    //Anim

    val blinkAnimation: Animation by lazy {
        AnimationUtils.loadAnimation(this, R.anim.blink)
    }

    companion object {
        private const val FILE_NAME = "update.apk"
        private const val REQUEST_INSTALL_PACKAGE = 1001
        private const val TAG = "DebugLog100 :"

    }

    private var disposable = Disposables.disposed()
    private var isDownloading = false


    private val fileDownloader by lazy {
        FileDownloader(OkHttpClient.Builder().build(), this)
    }


    /*

    Todo :

    1. Callbacks to check if user clicks on install or cancel in the options
    2. Check if the apps is deleted or not when new one is being downloaded

     */


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDownloadManagerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        
            init()
        
            val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
            registerReceiver(connectivityReceiver, filter)
        }



    fun init(){
        val bundle = intent.extras
        if (bundle != null) {

            initData(bundle)
            init_updateBottomSheet(updateItem)

            RxJavaPlugins.setErrorHandler {
                Log.e("Error", it.localizedMessage)
            }
    }}


    @RequiresApi(Build.VERSION_CODES.M)
    fun checkConnection():Boolean{
            val connectivityManager =
                getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val network = connectivityManager.activeNetwork
            val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
            return networkCapabilities != null && networkCapabilities.hasCapability(
            NetworkCapabilities.NET_CAPABILITY_INTERNET
            )
        }



    lateinit var targetFile : File



    @SuppressLint("LongLogTag")
    fun startDownload(updateItem : UpdateItem){


        val filepath = "${updateItem.versionCode}/${FILE_NAME}"
        targetFile = File(cacheDir , FILE_NAME)

        Log.d(TAG, "Exists ?: ${targetFile.exists()} + \\n ${targetFile.name}")

        if (targetFile.exists()){
            targetFile.delete()

            // Toast.makeText(this, "File Exsist : ${targetFile.exists()}", Toast.LENGTH_SHORT).show()
        }

        Handler(Looper.getMainLooper()).postDelayed({



            disposable = fileDownloader.download(updateItem.updateUrl, targetFile)
                .throttleFirst(2, TimeUnit.SECONDS)
                .toFlowable(BackpressureStrategy.LATEST)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({

                    isDownloading = true
                    bindingInclude.cancelButton.isClickable = true
                    bindingInclude.cancelButton.isEnabled = true

                    bindingInclude.layoutProgressConstraint.startAnimation(blinkAnimation)
                    bindingInclude.progressCircularIndefinite.visibility = View.GONE
                    bindingInclude.downloadProgress.progress = it
                    bindingInclude.loaderText.text = "${it}% done"
                    Log.d(TAG, "startDownload: $it %done")

                }, {
                    Toast.makeText(this, it.localizedMessage, Toast.LENGTH_SHORT).show()
                }, {

                    isDownloading = false
                    bindingInclude.layoutProgressConstraint.clearAnimation()
                    bindingInclude.downloadProgress.progress  = 100
                    //Toast.makeText(this, "Complete Downloaded ${targetFile.absolutePath}", Toast.LENGTH_SHORT).show()
                    bindingInclude.loaderText.text = "Downloaded\n\nSaved at : ${targetFile.absolutePath} \n Installing now..."
                    installAPK(targetFile.absolutePath, updateItem.appID)
                }) },
            1500)
    }


    fun installAPK(PATH : String, appID: String) {

        dismissBottomSheet()
        val toInstall = File(PATH)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val apkUri = FileProvider.getUriForFile(
                this,
                "${appID}.fileprovider",
                toInstall
            )

            intent = Intent(Intent.ACTION_VIEW)
            intent.data = apkUri
            intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION

        } else {

            val apkUri = Uri.fromFile(toInstall)
            intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive")
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

        }

        startActivityForResult(intent,REQUEST_INSTALL_PACKAGE)
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_INSTALL_PACKAGE) {
        if (resultCode == RESULT_CANCELED) {
          //  Toast.makeText(getApplicationContext(), "Update canceled by user! Result Code: " + resultCode, Toast.LENGTH_LONG).show();
            showFailedUpdate("Update cancelled...", "Update was cancelled by user")
        } else if (resultCode == RESULT_OK) {
           // Toast.makeText(getApplicationContext(),"Update success! Result Code: " + resultCode, Toast.LENGTH_LONG).show();
            showFailedUpdate("Updated...", "Update completed")
        } else {
           // Toast.makeText(getApplicationContext(), "Update Failed! Result Code: " + resultCode, Toast.LENGTH_LONG).show();
            showFailedUpdate("Update failed...", "Something went wrong")
        }
            Log.d(TAG, "onActivityResult: $resultCode")
    }
    }




    fun initData(bundle: Bundle){
       val version = bundle.getString("version","err")
       val url = bundle.getString("updateUrl","err")
       val changeLogTxt = bundle.getString("logs","err")
       val appID = bundle.getString("appID","err")
        this.appID = appID
        Log.d(TAG, "initData: \n URL :$url \n Version : $version \n ChangeLog : $changeLogTxt \n AppID : $appID")
        
       this.updateItem = UpdateItem(version,url,changeLogTxt,appID)
    }


    fun setAppIcon(appID: String){

        val pm = packageManager

        try {
            val packageInfo = pm.getPackageInfo(appID, PackageManager.GET_META_DATA)
            val appIcon = packageInfo.applicationInfo.loadIcon(pm)
            bindingInclude.appIcon.setImageDrawable(appIcon)
        } catch (e: PackageManager.NameNotFoundException) {
            // App not found
        }

    }

    private fun setDetailBottomView(versionText : String, changeLogs : String){
        scrollView.visibility = View.VISIBLE
        updateBtn.visibility = View.VISIBLE
        bindingInclude.layoutLin.visibility = View.GONE
        bindingBottomSheet.btnUpdate.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.baseline_system_update_alt_24,0)
        versionTxt.text = versionText
        changeLog.text = changeLogs

    }

    fun showFailedUpdate(title:String, msg : String){
        bindingInclude.cancelButton.isClickable = false
        bindingInclude.cancelButton.isEnabled = false

        updateBottomSheet.show()
        bindingBottomSheet.include.loaderText.text = msg
        bindingBottomSheet.bottomTitle.text = title
        bindingBottomSheet.include.loaderText.visibility = View.VISIBLE
        bindingInclude.progressCircularIndefinite.visibility = View.VISIBLE

        with(bindingBottomSheet.btnUpdate){
            text = "Retry"
            visibility = View.VISIBLE
            setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.baseline_replay_24,0)
            setOnClickListener {
                if(checkConnection()){
                    dismissBottomSheet()
                     setDownloadingView()
                    startDownload(updateItem)
                }else {
                    Toast.makeText(this@DownloadManagerActivity, "Waiting for connection...", Toast.LENGTH_SHORT).show()
                }
            }
        }

        with(bindingInclude.progressLayout){
            bindingBottomSheet.include.downloadProgress.progress = 0
            visibility = View.GONE
        }
    }



    fun showFailedConnection(){
        dismissBottomSheet()
        updateBottomSheet.show()
        bindingBottomSheet.include.loaderText.visibility = View.VISIBLE
        bindingBottomSheet.include.loaderText.text = "Make sure you have a stable internet connection"
        bindingBottomSheet.bottomTitle.text = "No Conection..."
        bindingBottomSheet.scrollview.visibility = View.GONE
        with(bindingBottomSheet.btnUpdate){
            text = "Retry"
            visibility = View.VISIBLE
            setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.baseline_replay_24,0)
            setOnClickListener {
                if(checkConnection()){
                  init()
                }else {
                    Toast.makeText(this@DownloadManagerActivity, "Waiting for connection...", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    fun showBottomSheet(){
        dismissBottomSheet()
        updateBottomSheet.show()
    }

    private fun setDownloadingView(){
        showBottomSheet()
        bindingBottomSheet.bottomTitle.text = "Downloading update..."
        bindingInclude.loaderText.text = "Initializing..."
        bindingInclude.loaderText.visibility = View.VISIBLE
        bindingInclude.progressLayout.visibility = View.VISIBLE
        bindingInclude.progressCircularIndefinite.visibility = View.VISIBLE

        bindingInclude.layoutLin.visibility = View.VISIBLE

        scrollView.visibility = View.GONE
        updateBtn.visibility = View.GONE
        bindingBottomSheet.btnUpdate.visibility = View.GONE

    }

    private fun init_updateBottomSheet(updateItem: UpdateItem){

        bindingBottomSheet = UpdateBottomsheetBinding.inflate(layoutInflater)
        bindingInclude = bindingBottomSheet.include
        setAppIcon(appID)
        updateBottomSheet = BottomSheetDialog(this)
        bindingInclude.progressCircularIndefinite.visibility = View.VISIBLE
        bindingBottomSheet.scrollview.visibility = View.VISIBLE
        bindingBottomSheet.include.loaderText.visibility = View.GONE

        with(updateBottomSheet) {
            setContentView(bindingBottomSheet.root)
            setCancelable(false)
            dismissWithAnimation = true
        }

        versionTxt = bindingBottomSheet.versionTxt
        changeLog =  bindingBottomSheet.changeLogText
        updateBtn =  bindingBottomSheet.btnUpdate
        scrollView = bindingBottomSheet.scrollview


        setDetailBottomView(updateItem.versionCode,updateItem.logs)

        updateBottomSheet.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        updateBottomSheet.show()
        bindingInclude.cancelButton.isClickable = false
        bindingInclude.cancelButton.isEnabled = false


        bindingInclude.cancelButton.setOnClickListener{

            disposable.dispose()
            targetFile.delete()
            dismissBottomSheet()
            showFailedUpdate("Update cancelled...","Update was cancelled")
        }

        if (checkConnection()){
            updateBtn.setOnClickListener{
            setDownloadingView()
            startDownload(updateItem)
        }
        }else {
            dismissBottomSheet()
            showFailedConnection()
            Toast.makeText(this, "Waiting for stable connection", Toast.LENGTH_SHORT).show()
        }
    }


    fun dismissBottomSheet(){

        updateBottomSheet.dismissWithAnimation = true
        updateBottomSheet.dismiss()
    }


    override fun onDestroy() {
        super.onDestroy()
        disposable.dispose()
        unregisterReceiver(connectivityReceiver)
        dismissBottomSheet()
    }


    override fun connected() {
       isConnected = true
        if (bindingInclude!=null){
            bindingInclude.progressCircularIndefinite.visibility = View.GONE
        }
    }

    override fun disconnected() {
        isConnected = false
        if (bindingInclude!=null){
            bindingInclude.progressCircularIndefinite.visibility = View.VISIBLE
            if(isDownloading){
                showFailedUpdate("No connection!", "Make sure you have an active internet connection")
            }
        }else {
            showFailedUpdate("No connection!", "Make sure you have an active internet connection")
        }
    }


}