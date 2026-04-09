package dev.tiagosilva.whatsappclone.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import dev.tiagosilva.whatsappclone.R
import dev.tiagosilva.whatsappclone.data.Chat

class ChatsAdapter (
    private var chats: List<Chat>,
    private val onChatClick: (Chat) -> Unit
):
    RecyclerView.Adapter<ChatsAdapter.ChatViewHolder>() {

    class ChatViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val imageProfile: ShapeableImageView = view.findViewById(R.id.imageProfile)
        val txtName: TextView = view.findViewById(R.id.txtName)
        val txtLastMesssage: TextView = view.findViewById(R.id.txtLastMesssage)
        val txtTime: TextView = view.findViewById(R.id.txtTime)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ChatViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_chat, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val chat = chats[position]

        holder.txtName.text = chat.contactName
        holder.txtLastMesssage.text = chat.lastMessage

        if (!chat.time.isNullOrBlank() && chat.time != "0") {
            val timeMillis = chat.time?.toLongOrNull() ?: 0L
            val tz = java.util.TimeZone.getDefault()
            val dateFormat = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
            dateFormat.timeZone = tz
            val dateOutput = dateFormat.format(java.util.Date(timeMillis))
            holder.txtTime.text = dateOutput
        }


        Glide.with(holder.itemView.context)
            .load(chat.contactImage)
            .placeholder(R.drawable.padrao)
            .error(R.drawable.padrao)
            .into(holder.imageProfile)

        holder.itemView.setOnClickListener { onChatClick(chat) }
    }

    override fun getItemCount() = chats.size

    fun updateChats(newChats: List<Chat>) {
        chats = newChats
        notifyDataSetChanged()
    }
}