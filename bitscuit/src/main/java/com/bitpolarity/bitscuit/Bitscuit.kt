package com.bitpolarity.bitscuit

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.bitpolarity.bitscuit.databinding.UpdateBottomsheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.io.IOException


class Bitscuit private constructor(){

    private lateinit var context : Activity
    private lateinit var updateBottomSheet : BottomSheetDialog


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

    //BottomSheetViews
    lateinit var versionTxt : TextView
    lateinit var changeLog  : TextView
    lateinit var updateBtn  : Button
    lateinit var bindingBottomSheet : UpdateBottomsheetBinding
    val TAG : String = "Bitscuit.kt"

    fun init(context: Activity) {
        this.context = context
        init_updateBottomSheet()
    }

    private fun init_updateBottomSheet(){

        updateBottomSheet = BottomSheetDialog(context)
        bindingBottomSheet = UpdateBottomsheetBinding.inflate(context.layoutInflater)
        updateBottomSheet.setContentView(bindingBottomSheet.root)
        updateBottomSheet.setCancelable(false)
        updateBottomSheet.dismissWithAnimation= true

         versionTxt = bindingBottomSheet.versionTxt
         changeLog = bindingBottomSheet.changeLogText
         updateBtn = bindingBottomSheet.btnUpdate

    }


    @kotlin.jvm.Throws(IOException::class)
    private fun validate():Boolean{
        if (updateBottomSheet != null){
            return true
        } else {
            Toast.makeText(context, "Init the library with context first", Toast.LENGTH_SHORT).show()
            throw IOException("UnInitialized")

        }
    }

    fun checkUpdate(isUpdateAvailable: Boolean, url : String?) {

        if (validate()){

            if (isUpdateAvailable){
                    updateBottomSheet.show()
                    updateBtn.setOnClickListener{
                        context.startActivity(Intent(context,DownloadManagerActivity::class.java))
                    }
            }

        }else{
            Log.e(TAG, "checkUpdate: Errors")
        }

    }

    fun dismissBottomSheet(){
        updateBottomSheet.dismiss()
    }





}