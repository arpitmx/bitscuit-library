package com.bitpolarity.bitscuit

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
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
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import io.reactivex.BackpressureStrategy
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposables
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit

class DownloadManagerActivity : AppCompatActivity() {

    lateinit var binding : ActivityDownloadManagerBinding
    private lateinit var updateBottomSheet : BottomSheetDialog

    //BottomSheetViews
    lateinit var versionTxt : TextView
    lateinit var changeLog  : TextView
    lateinit var updateBtn  : Button
    lateinit var scrollView : ScrollView
    lateinit var bindingBottomSheet : UpdateBottomsheetBinding
    val TAG : String = "DownloadManagerActivity.kt"


    lateinit var version: String
    private lateinit var changeLogTxt: String
    private lateinit var appID: String
    lateinit var url: String
    lateinit var bindingInclude : FragmentDownloaderBinding


    companion object {
        private const val FILE_NAME = "update.apk"
    }

    private var disposable = Disposables.disposed()

    private val fileDownloader by lazy {
        FileDownloader(
            OkHttpClient.Builder().build()
        )
    }




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDownloadManagerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bundle = intent.extras
        if (bundle!=null){
            initData(bundle)
            init_updateBottomSheet()
        }

        RxJavaPlugins.setErrorHandler {
            Log.e("Error", it.localizedMessage)
        }

    }

    fun startDownload(URL:String, appID: String){
        val targetFile = File(cacheDir  ,FILE_NAME)
         Toast.makeText(this, "Exists ?: ${targetFile.exists() } + \n ${targetFile.name}", Toast.LENGTH_SHORT).show()

        if (targetFile.exists()){
            targetFile.delete()
            Toast.makeText(this, "File Exsist : ${targetFile.exists()}", Toast.LENGTH_SHORT).show()

        }


        disposable = fileDownloader.download(URL, targetFile)
            .throttleFirst(2, TimeUnit.SECONDS)
            .toFlowable(BackpressureStrategy.LATEST)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({

                bindingInclude.downloadProgress.progress = it
                bindingInclude.loaderText.text = "Downloading ${it}% done"

                       }, {
                Toast.makeText(this, it.localizedMessage, Toast.LENGTH_SHORT).show()

                          }, {

                bindingInclude.downloadProgress.progress  =100
                Toast.makeText(this, "Complete Downloaded ${targetFile.absolutePath}", Toast.LENGTH_SHORT).show()
                bindingInclude.loaderText.text = "Downloaded\n\nSaved at : ${targetFile.absolutePath} \n Installing now..."
                installAPK(targetFile.absolutePath, appID)
            })

    }

    fun installAPK(PATH : String, appID: String) {

        dismissBottomSheet()

        val toInstall = File(PATH)
        val intent: Intent

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val apkUri = FileProvider.getUriForFile(
                this,
                "${appID}.fileprovider",
                toInstall
            )

            intent = Intent(Intent.ACTION_INSTALL_PACKAGE)
            intent.data = apkUri
            intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION

        } else {

            val apkUri = Uri.fromFile(toInstall)
            intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive")
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

        }

        startActivity(intent)
    }


    fun initData(bundle: Bundle){
        version = bundle.getString("version","err")
        url = bundle.getString("updateUrl","err")
        changeLogTxt = bundle.getString("logs","err")
        appID = bundle.getString("appID","err")
    }


    private fun init_updateBottomSheet(){


        bindingBottomSheet = UpdateBottomsheetBinding.inflate(layoutInflater)
        bindingInclude = bindingBottomSheet.include
        updateBottomSheet = BottomSheetDialog(this)


        with(updateBottomSheet) {
            setContentView(bindingBottomSheet.root)
            setCancelable(false)
            dismissWithAnimation = true
        }


        versionTxt = bindingBottomSheet.versionTxt
        changeLog = bindingBottomSheet.changeLogText
        updateBtn = bindingBottomSheet.btnUpdate
        scrollView = bindingBottomSheet.scrollview

        scrollView.visibility = View.VISIBLE
        updateBtn.visibility = View.VISIBLE
        bindingInclude.layoutLin.visibility = View.GONE

        versionTxt.text = version
        changeLog.text = changeLogTxt


        updateBtn.setOnClickListener{
            scrollView.visibility = View.GONE
            updateBtn.visibility = View.GONE
            bindingInclude.layoutLin.visibility = View.VISIBLE

            initFrag()

        }
        updateBottomSheet.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        updateBottomSheet.show()
    }

    fun initFrag(){
       startDownload(url, appID)
    }

    fun dismissBottomSheet(){
        updateBottomSheet.dismiss()
    }


    override fun onDestroy() {
        super.onDestroy()
        disposable.dispose()
        dismissBottomSheet()
    }




}