package dev.tiagosilva.whatsappclone.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import dev.tiagosilva.whatsappclone.R
import dev.tiagosilva.whatsappclone.data.Message
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MessagesAdapter(private val messages: List<Message>, private val currentUserId: String?) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_LEFT_TEXT = 0
        private const val TYPE_RIGHT_TEXT = 1
        private const val TYPE_LEFT_MAP = 2
        private const val TYPE_RIGHT_MAP = 3
    }

    override fun getItemViewType(position: Int): Int {
        val msg = messages[position]

        return when {
            msg.type?.lowercase() == "location" && msg.senderID == currentUserId -> TYPE_RIGHT_MAP
            msg.type?.lowercase() == "location" -> TYPE_LEFT_MAP
            msg.senderID == currentUserId -> TYPE_RIGHT_TEXT
            else -> TYPE_LEFT_TEXT
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType){
            TYPE_LEFT_TEXT -> MessageViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_message_left, parent, false))
            TYPE_RIGHT_TEXT -> MessageViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_message_right, parent, false))
            TYPE_LEFT_MAP -> MapViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_map_left, parent, false))
            TYPE_RIGHT_MAP -> MapViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_map_right, parent, false))
            else -> throw IllegalArgumentException("View type desconhecida")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val msg = messages[position]
        val formatter = SimpleDateFormat("HH:mm", Locale.getDefault())
        val time = msg.date?.let { formatter.format(Date(it)) }

        when(holder) {
            is MessageViewHolder -> {
                if (msg.type?.lowercase() == "text") {
                    holder.txtMessage.text = msg.value ?: ""
                }
                holder.txtTime.text = time ?: ""
            }
            is MapViewHolder -> {
                val latLng = msg.value?.split(",")
                val lat = latLng?.getOrNull(0)?.trim()
                val lng = latLng?.getOrNull(1)?.trim()
                val context = holder.itemView.context
                if (lat != null && lng != null){
                    val url = "https://staticmap.openstreetmap.de/staticmap.php?center=$lat,$lng&zoom=15&size=400x200&markers=$lat,$lng,red"
//                    Glide.with(context)
//                        .load(url)
//                        .placeholder(R.drawable.map)
//                        .error(R.drawable.map)
//                        .into(holder.imageMap)
                } else {
//                    holder.imageMap.setImageResource(R.drawable.map)
                }

//                holder.txtTime.text = time ?: ""
            }
        }

    }

    override fun getItemCount() = messages.size

    class MessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtMessage: TextView = view.findViewById(R.id.txtMessage)
        val txtTime: TextView = view.findViewById(R.id.txtTime)
    }

    class MapViewHolder(view: View) : RecyclerView.ViewHolder(view) {
//        val imageMap: ImageView = view.findViewById(R.id.imageMap)
//        val txtTime: TextView = view.findViewById(R.id.txtTime)
    }
}
