package com.bitpolarity.bitscuit

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.PendingIntent
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageInstaller
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.bitpolarity.bitscuit.databinding.ActivityDownloadManagerBinding
import com.bitpolarity.bitscuit.databinding.FragmentDownloaderBinding
import com.bitpolarity.bitscuit.databinding.UpdateBottomsheetBinding
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
import java.io.FileInputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.channels.WritableByteChannel
import java.util.concurrent.TimeUnit
import java.util.logging.Logger

class DownloadManagerActivity : AppCompatActivity() {

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

    companion object {
        private const val FILE_NAME = "update.apk"
        private const val REQUEST_INSTALL_PACKAGE = 1001
        private const val TAG ="DebugLog100 :"

    }

    private var disposable = Disposables.disposed()

    private val fileDownloader by lazy {
        FileDownloader(OkHttpClient.Builder().build())
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


        val bundle = intent.extras
        if (bundle!=null){
            initData(bundle)
            init_updateBottomSheet(updateItem)
        }

        RxJavaPlugins.setErrorHandler {
            Log.e("Error", it.localizedMessage)
        }

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

        bindingInclude.progressCircular.progress = 0

        disposable = fileDownloader.download(updateItem.updateUrl, targetFile)
            .throttleFirst(2, TimeUnit.SECONDS)
            .toFlowable(BackpressureStrategy.LATEST)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({

                    bindingInclude.progressCircular.progress = it
                    bindingInclude.downloadProgress.progress = it
                    bindingInclude.loaderText.text = "${it}% done"
                    Log.d(TAG, "startDownload: $it %done")

                       }, {
                Toast.makeText(this, it.localizedMessage, Toast.LENGTH_SHORT).show()
                          }, {

                bindingInclude.downloadProgress.progress  =100
                bindingInclude.progressCircular.progress = 100
                Toast.makeText(this, "Complete Downloaded ${targetFile.absolutePath}", Toast.LENGTH_SHORT).show()
                bindingInclude.loaderText.text = "Downloaded\n\nSaved at : ${targetFile.absolutePath} \n Installing now..."
                installAPK(targetFile.absolutePath, updateItem.appID)
            })


        bindingInclude.cancelButton.setOnClickListener{
            disposable.dispose()
            targetFile.delete()
            dismissBottomSheet()
            showFailedUpdate("Update cancelled...")

        }

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
            showFailedUpdate("Update cancelled...")
        } else if (resultCode == RESULT_OK) {
           // Toast.makeText(getApplicationContext(),"Update success! Result Code: " + resultCode, Toast.LENGTH_LONG).show();
        } else {
           // Toast.makeText(getApplicationContext(), "Update Failed! Result Code: " + resultCode, Toast.LENGTH_LONG).show();
            showFailedUpdate("Update failed...")
        }
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

    fun showFailedUpdate(msg:String){
        updateBottomSheet.show()
        bindingBottomSheet.include.loaderText.text = getString(R.string.something_went_wrong_text)
        bindingBottomSheet.bottomTitle.text = msg

        with(bindingBottomSheet.btnUpdate){
            text = "Retry"
            visibility = View.VISIBLE
            setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.baseline_replay_24,0)
            setOnClickListener {
                dismissBottomSheet()
                setDownloadingView()
                startDownload(updateItem)
            }
        }

        with(bindingInclude.progressLayout){
            bindingBottomSheet.include.downloadProgress.progress = 0
            visibility = View.GONE
        }
    }


    fun showBottomSheet(){
        dismissBottomSheet()
        updateBottomSheet.show()
    }

    private fun setDownloadingView(){
        showBottomSheet()
        bindingBottomSheet.bottomTitle.text = "Downloading..."
        bindingInclude.loaderText.text = "Initializing..."
        bindingInclude.loaderText.visibility = View.VISIBLE

        bindingInclude.progressLayout.visibility = View.VISIBLE

        with(bindingInclude.progressCircular){
            isIndeterminate = false
            progress = 0
            max = 100
        }

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

        updateBtn.setOnClickListener{

            setDownloadingView()
            startDownload(updateItem)
        }
    }


    fun dismissBottomSheet(){

        updateBottomSheet.dismissWithAnimation = true
        updateBottomSheet.dismiss()
    }


    override fun onDestroy() {
        super.onDestroy()
        disposable.dispose()
        dismissBottomSheet()
    }




}