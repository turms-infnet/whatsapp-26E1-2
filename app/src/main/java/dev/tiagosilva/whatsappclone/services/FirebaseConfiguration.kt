package dev.tiagosilva.whatsappclone.services

import android.content.Context
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class FirebaseConfiguration {
    companion object {
        private var databaseReference: DatabaseReference? = null
        private var firebaseAuth: FirebaseAuth? = null
        private var firebaseAnalytics: FirebaseAnalytics? = null

        @JvmStatic
        fun getFirebaseDatabase(): DatabaseReference {
            if (databaseReference == null) {
                databaseReference = FirebaseDatabase.getInstance().reference
            }
            return databaseReference!!
        }

        @JvmStatic
        fun getFirebaseAnalytics(context: Context): FirebaseAnalytics {
            if (firebaseAnalytics == null) {
                firebaseAnalytics = FirebaseAnalytics.getInstance(context)
            }
            return firebaseAnalytics!!
        }

        @JvmStatic
        fun getFirebaseAuth(): FirebaseAuth {
            if (firebaseAuth == null) {
                firebaseAuth = FirebaseAuth.getInstance()
            }
            return firebaseAuth!!
        }
    }
}
