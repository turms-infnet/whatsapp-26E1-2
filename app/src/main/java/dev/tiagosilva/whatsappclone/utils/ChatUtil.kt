package dev.tiagosilva.whatsappclone.utils

import android.content.Context
import android.content.Intent
import dev.tiagosilva.whatsappclone.activities.ChatActivity

class ChatUtil {
    companion object {
        @JvmStatic
        fun openChat(chatId: String?, contactName: String?, contactImage: String?, ctx: Context ) {
            val intent = Intent(ctx, ChatActivity::class.java)

            intent.putExtra("chatId", chatId)
            intent.putExtra("contactName", contactName)
            intent.putExtra("contactImage", contactImage)

            ctx.startActivity(intent)
        }
    }
}