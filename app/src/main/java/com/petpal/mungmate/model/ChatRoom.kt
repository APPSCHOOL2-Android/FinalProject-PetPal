package com.petpal.mungmate.model

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.Timestamp

// 채팅방
data class ChatRoom (
    var id: String,
    val senderId: String,
    val receiverId: String,
    val lastMessage: String?,
    val lastMessageTime: Timestamp?,
    val senderIsExit: Boolean,
    val receiverIsExit: Boolean,
    val senderUnReadCount: Long?,
    val receiverUnReadCount: Long?
):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString(),
        parcel.readParcelable(Timestamp::class.java.classLoader),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readValue(Long::class.java.classLoader) as? Long,
        parcel.readValue(Long::class.java.classLoader) as? Long
    ) {
    }

    constructor() : this("", "", "", null, null, false, false, null, null)

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(senderId)
        parcel.writeString(receiverId)
        parcel.writeString(lastMessage)
        parcel.writeParcelable(lastMessageTime, flags)
        parcel.writeByte(if (senderIsExit) 1 else 0)
        parcel.writeByte(if (receiverIsExit) 1 else 0)
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