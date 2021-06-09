package com.skrb7f16.thehelpingspoon.models

class User {
    private var username:String;
    private var password:String;
    lateinit var email:String;
    lateinit var organization:String
    lateinit var city:String;

    constructor(username: String, password: String, email: String, organization: String, city: String) {
        this.username = username
        this.password = password
        this.email = email
        this.organization = organization
        this.city = city
    }

    constructor(username: String, password: String, email: String) {
        this.username = username
        this.password = password
        this.email = email
    }

    constructor(username: String, password: String) {
        this.username = username
        this.password = password
    }






}