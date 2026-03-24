package dev.tiagosilva.whatsappclone.data

data class Chat(
    val chatId: String?,
    val contactName: String?,
    val contactImage: String?,
    val lastMessage: String? = null,
    val time: String? = null
)


