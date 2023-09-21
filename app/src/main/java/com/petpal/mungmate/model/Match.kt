package com.petpal.mungmate.model

import com.google.firebase.Timestamp

// 산책 매칭
data class Match(
    val senderId: String,
    val receiverId: String,
    val walkTimestamp: Timestamp,
    val walkPlace: String,
    val timestamp: Timestamp,
    val status: Int,
    val walkRecordId: String?,
    val senderWalkReview: WalkReview?,
    val receiverWalkReview: WalkReview?
) {
    // firstroe에서 데이터 가져올 때 document.toObject(Match::class.java)로 파싱하려면 매개변수 없는 생성자 필수
    constructor(): this("", "", Timestamp.now(), "", Timestamp.now(), MatchStatus.REQUESTED.code, null, null, null)
}

// 산책 매칭 상태
enum class MatchStatus(val code: Int) {
    REQUESTED(0),  // 산책 요청 상태
    REJECTED(1),   // 산책 거절 상태
    ACCEPTED(2),   // 산책 수락 상태
    CANCELED(3),   // 산책 취소 상태
    COMPLETED(4)   // 산책 완료 상태
}