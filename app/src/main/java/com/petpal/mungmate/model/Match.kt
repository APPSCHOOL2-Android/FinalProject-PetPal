package com.petpal.mungmate.model

import com.google.firebase.Timestamp

// 산책 메이트 매칭
data class Match(
    var senderId: String?,
    var receiverId: String?,
    var walkTimestamp: Timestamp?,
    var walkPlace: String?,
    var timestamp: Timestamp?,
    var status: Int?,
    var walkRecordId: String?,
    var senderWalkReview: WalkReview?,
    var receiverWalkReview: WalkReview?
) {
    constructor(): this("", "", null, "", null, MatchStatus.REQUESTED.code, null, null, null)
}

enum class MatchStatus(val code: Int) {
    REQUESTED(0),  // 산책 요청 상태
    REJECTED(1),   // 산책 거절 상태
    ACCEPTED(2),   // 산책 수락 상태
    CANCELED(3),   // 산책 취소 상태
    COMPLETED(4)   // 산책 완료 상태
}