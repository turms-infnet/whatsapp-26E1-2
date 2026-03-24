package dev.tiagosilva.whatsappclone.data

import io.appwrite.ID

data class Message(
    val uid: String? = "",
    val senderID: String? = "",
    val type: String? = "TEXT",
    val value: String? = "",
    val date: Long? = 0L,
)
