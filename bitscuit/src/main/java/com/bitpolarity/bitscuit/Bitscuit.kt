package com.bitpolarity.bitscuit

import android.app.Activity
import android.app.Dialog
import android.widget.Button
import android.widget.TextView
import com.bitpolarity.bitscuit.databinding.UpdateBottomsheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialog


class Bitscuit(activity: Activity){

    lateinit var activity : Activity
    lateinit var updateBottomSheet : BottomSheetDialog


    //BottomSheetViews
    lateinit var versionTxt : TextView
    lateinit var changeLog  : TextView
    lateinit var updateBtn  : Button
    lateinit var bindingBottomSheet : UpdateBottomsheetBinding


    init {
        this.activity = activity
    }

    fun init() {

       init_updateBottomSheet()

    }

    fun init_updateBottomSheet(){

        updateBottomSheet = BottomSheetDialog(activity)
        bindingBottomSheet = UpdateBottomsheetBinding.inflate(activity.layoutInflater)
        updateBottomSheet.setContentView(bindingBottomSheet.root)
        updateBottomSheet.setCancelable(false)
        updateBottomSheet.dismissWithAnimation= true

         versionTxt = bindingBottomSheet.versionTxt
         changeLog = bindingBottomSheet.changeLogText
         updateBtn = bindingBottomSheet.btnUpdate

    }

    fun checkUpdate(isUpdateAvailable: Boolean, url : String?) {

    }


}