package com.petpal.mungmate.utils

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.petpal.mungmate.MainActivity
import kotlinx.coroutines.tasks.await

suspend fun FirebaseAuth.isUserDataExists(): Boolean {
    val db = Firebase.firestore

    return try {
        val document = db.collection("users").document(currentUser!!.uid).get().await()

        if (document.exists()) {
            Log.d(MainActivity.TAG, "DocumentSnapshot data: ${document.data}")
            true
        } else {
            Log.d(MainActivity.TAG, "No such document")
            false
        }

    } catch (e: Exception) {
        Log.d(MainActivity.TAG, "get failed with ", e)
        false
    }
}