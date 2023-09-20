package com.petpal.mungmate.model

data class WalkRecord(
    val walkRecorduid:String,  //산책 기록자 uid
    val walkRecordDate:String, //기록한 날짜
    val walkRecordStartTime:String, //시작시간
    val walkRecordEndTime:String, //종료시간
    val walkDuration:String, //소요시간
    val walkDistance:Double, //거리
    val walkMatchingId:String?=null, //산책 상대 uid
    val walkMemo:String, //메모
    val walkPhoto:String?=null //사진

)
