package com.petpal.mungmate.model

data class Pet(
    val birth:String?=null,
    val breed:String?=null,
    val character:String?=null,
    val imgaeURI:String?=null,
    val name:String?=null,
    val neutered:Boolean?=null,
    val petSex:Long?=null,
    val weight:Double?=null
)
