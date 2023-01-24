package com.bitpolarity.bitscuit.model

data class UpdateItem(
    val versionCode: String,
    val updateUrl : String,
    val logs : String,
    val appID : String,
)

//downloadIntent.putExtra("updateUrl",url)
//downloadIntent.putExtra("version",version)
//downloadIntent.putExtra("logs",changeLogs)
//downloadIntent.putExtra("appID",appID)



