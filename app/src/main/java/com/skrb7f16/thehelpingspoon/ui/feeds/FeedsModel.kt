package com.skrb7f16.thehelpingspoon.ui.feeds

import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class FeedsModel {
    lateinit var title:String;
    lateinit var city:String;
    lateinit var address:String;
    lateinit var by:String;
    lateinit var pic:String;
    lateinit var phone:String;
    lateinit var at:String;
    lateinit var desc:String







    constructor(jsonObject: JSONObject){
        this.address=jsonObject.getString("address");
        this.city=jsonObject.getString("city");
        this.pic=jsonObject.getString("pic");
        this.at=jsonObject.getString("at");
        this.title=jsonObject.getString("title");
        this.desc=jsonObject.getString("desc");
        this.phone=jsonObject.getString("phone");
        this.by=jsonObject.getJSONObject("author").getJSONObject("user").getString("username")
        setDateRight()
    }

    constructor(
        title: String,
        city: String,
        address: String,
        by: String,
        pic: String,
        phone: String,
        at: String,
        desc: String
    ) {
        this.title = title
        this.city = city
        this.address = address
        this.by = by
        this.pic = pic
        this.phone = phone
        this.at = at
        this.desc = desc
        setDateRight()
    }


    fun setDateRight(){
        val sd = SimpleDateFormat("yyyy-MM-dd'T'HH:mm")
        var date = sd.parse(at)
        at=date.toString()
    }


}