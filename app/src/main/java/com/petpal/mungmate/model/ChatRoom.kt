package com.petpal.mungmate.model

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.Timestamp

// 채팅방
data class ChatRoom (
    val senderId: String?,
    val receiverId: String?,
    var lastMessage: String?,
    var lastMessageTime: Timestamp?,
    var senderIsExit: Boolean?,
    var receiverIsExit: Boolean?,
    var senderUnReadCount: Long?,
    var receiverUnReadCount: Long?
):Parcelable {
    constructor() : this("", "", null, null, false, false, null, null)

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readParcelable(Timestamp::class.java.classLoader),
        parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
        parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
        parcel.readValue(Long::class.java.classLoader) as? Long,
        parcel.readValue(Long::class.java.classLoader) as? Long
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(senderId)
        parcel.writeString(receiverId)
        parcel.writeString(lastMessage)
        parcel.writeParcelable(lastMessageTime, flags)
        parcel.writeValue(senderIsExit)
        parcel.writeValue(receiverIsExit)
        parcel.writeValue(senderUnReadCount)
        parcel.writeValue(receiverUnReadCount)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ChatRoom> {
        override fun createFromParcel(parcel: Parcel): ChatRoom {
            return ChatRoom(parcel)
        }

        override fun newArray(size: Int): Array<ChatRoom?> {
            return arrayOfNulls(size)
        }
    }
}