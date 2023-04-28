package com.bitpolarity.bitscuit

import android.Manifest
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

    companion object {
        private const val FILE_NAME = "update.apk"
        private const val REQUEST_INSTALL_PACKAGE = 1001
        private const val TAG = "DebugLog100"

    }

    private var disposable = Disposables.disposed()

    private val fileDownloader by lazy {
        FileDownloader(OkHttpClient.Builder().build())
    }



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

    private fun setDownloadingView(){
        bindingBottomSheet.bottomTitle.text = "Downloading..."
        bindingInclude.downloadProgress.visibility = View.VISIBLE
        bindingInclude.layoutLin.visibility = View.VISIBLE


        scrollView.visibility = View.GONE
        updateBtn.visibility = View.GONE
        bindingBottomSheet.btnUpdate.visibility = View.GONE


    }

    fun startDownload(updateItem : UpdateItem){

        val filepath = "${updateItem.versionCode}/${FILE_NAME}"
        targetFile = File(cacheDir , FILE_NAME)

        Log.d(TAG, "Exists ?: ${targetFile.exists()} + \\n ${targetFile.name}")

        if (targetFile.exists()){
            targetFile.delete()

            Toast.makeText(this, "File Exsist : ${targetFile.exists()}", Toast.LENGTH_SHORT).show()
        }


        disposable = fileDownloader.download(updateItem.updateUrl, targetFile)
            .throttleFirst(2, TimeUnit.SECONDS)
            .toFlowable(BackpressureStrategy.LATEST)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({


                bindingInclude.downloadProgress.progress = it
                bindingInclude.loaderText.visibility = View.VISIBLE
                bindingInclude.loaderText.text = "Downloading ${it}% done"
                       }, {
                Toast.makeText(this, it.localizedMessage, Toast.LENGTH_SHORT).show()
                          }, {

                bindingInclude.downloadProgress.progress  =100
                Toast.makeText(this, "Complete Downloaded ${targetFile.absolutePath}", Toast.LENGTH_SHORT).show()
                bindingInclude.loaderText.text = "Downloaded\n\nSaved at : ${targetFile.absolutePath} \n Installing now..."
                installAPK(targetFile.absolutePath, updateItem.appID)
            })

    }


    val callback = object : PackageInstaller.SessionCallback() {
        override fun onCreated(sessionId: Int) {
            // The session has been created
            Log.d("TAG","session created with sessionId: $sessionId")
        }

        override fun onBadgingChanged(sessionId: Int) {
            // The session's badging has changed
            Log.d("TAG","session badging changed with sessionId: $sessionId")
        }

        override fun onActiveChanged(sessionId: Int, active: Boolean) {
            // The session's active state has changed
            Log.d("TAG","session active state changed with sessionId: $sessionId and active: $active")
        }

        override fun onProgressChanged(sessionId: Int, progress: Float) {
            // The session's progress has changed
            Log.d("TAG","session progress changed with sessionId: $sessionId and progress: $progress")
        }

        override fun onFinished(sessionId: Int, success: Boolean) {
            // The session has been finished
            if(success) {
                Toast.makeText(applicationContext,"Done",Toast.LENGTH_SHORT).show()
                Log.d("TAG","installation was successful for sessionId: $sessionId")
            } else {
                Toast.makeText(applicationContext,"Failed",Toast.LENGTH_SHORT).show()

                Log.d("TAG","installation failed for sessionId: $sessionId")
            }
        }
    }



    fun installAPK(PATH : String, appID: String) {

        dismissBottomSheet()
        val toInstall = File(PATH)

//        val packageInstaller = this.packageManager.packageInstaller
//        val sessionId = packageInstaller.createSession(PackageInstaller.SessionParams(PackageInstaller.SessionParams.MODE_FULL_INSTALL))
//        Log.d("TAG","Session ID : ${sessionId}")
//        packageInstaller.registerSessionCallback(callback, Handler())


 //       val intent = Intent()
//
//
//
//
//        val packageUri = Uri.fromFile(toInstall)
//        val packageInstaller = packageManager.packageInstaller
//
//        packageInstaller.registerSessionCallback(callback, Handler())
//
//        val params = PackageInstaller.SessionParams(PackageInstaller.SessionParams.MODE_FULL_INSTALL)
//        val sessionId = packageInstaller.createSession(params)
//        val session = packageInstaller.openSession(sessionId)
//        val out = session.openWrite("update.apk", 0, -1)
//        val input = FileInputStream(toInstall).channel
//        val buffer = ByteBuffer.allocate(1024)
//        while (input.read(buffer) != -1) {
//            buffer.flip()
//            out.write(buffer.array(), buffer.position(), buffer.remaining())
//            buffer.clear()
//        }
//        session.fsync(out)
//        input.close()
//        out.close()
//
//        val pendingIntent = PendingIntent.getBroadcast(applicationContext, sessionId, intent, PendingIntent.FLAG_IMMUTABLE)
//        session.commit(pendingIntent.intentSender)



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
            Toast.makeText(getApplicationContext(), "Update canceled by user! Result Code: " + resultCode, Toast.LENGTH_LONG).show();
            showFailedUpdate()
        } else if (resultCode == RESULT_OK) {
            Toast.makeText(getApplicationContext(),"Update success! Result Code: " + resultCode, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), "Update Failed! Result Code: " + resultCode, Toast.LENGTH_LONG).show();
            showFailedUpdate()
        }
    }
    }

    fun showFailedUpdate(){
        updateBottomSheet.show()
        bindingBottomSheet.include.loaderText.text = getString(R.string.something_went_wrong_text)
        bindingBottomSheet.bottomTitle.text = "Updation failed"
        bindingBottomSheet.btnUpdate.text = "Retry"
        bindingBottomSheet.btnUpdate.visibility = View.VISIBLE
        bindingBottomSheet.btnUpdate.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.baseline_replay_24,0)
        bindingBottomSheet.include.downloadProgress.progress = 0
        bindingBottomSheet.include.downloadProgress.visibility= View.GONE
        bindingBottomSheet.btnUpdate.setOnClickListener {
            setDownloadingView()
            startDownload(updateItem)
        }

    }

    fun initData(bundle: Bundle){
       val version = bundle.getString("version","err")
       val url = bundle.getString("updateUrl","err")
       val changeLogTxt = bundle.getString("logs","err")
       val appID = bundle.getString("appID","err")

       this.updateItem = UpdateItem(version,url,changeLogTxt,appID)
    }


    private fun setDetailBottomView(versionText : String, changeLogs : String){
        scrollView.visibility = View.VISIBLE
        updateBtn.visibility = View.VISIBLE
        bindingInclude.layoutLin.visibility = View.GONE
        bindingBottomSheet.btnUpdate.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.baseline_system_update_alt_24,0)
        versionTxt.text = versionText
        changeLog.text = changeLogs

    }


    private fun init_updateBottomSheet(updateItem: UpdateItem){

        bindingBottomSheet = UpdateBottomsheetBinding.inflate(layoutInflater)
        bindingInclude = bindingBottomSheet.include
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
        updateBottomSheet.dismiss()
    }


    override fun onDestroy() {
        super.onDestroy()
        disposable.dispose()
        dismissBottomSheet()
    }




}