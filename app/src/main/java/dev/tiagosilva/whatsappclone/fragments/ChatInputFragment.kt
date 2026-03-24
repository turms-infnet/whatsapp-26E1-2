package dev.tiagosilva.whatsappclone.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import dev.tiagosilva.whatsappclone.R
import dev.tiagosilva.whatsappclone.data.Message
import io.appwrite.ID

class ChatInputFragment : Fragment() {
    private lateinit var messageText: EditText
    private lateinit var btnLocation: ImageButton
    private lateinit var btnSend: ImageButton

    private var chatId: String? = null
    private var currentUserId: String? = null

    var onSendMessage: ((Message) -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        chatId = arguments?.getString("chatId")
        currentUserId = arguments?.getString("currentUserId")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_chat_input, container, false)

        messageText = view.findViewById<EditText>(R.id.messageText)
        btnLocation = view.findViewById<ImageButton>(R.id.btnLocation)
        btnSend = view.findViewById<ImageButton>(R.id.btnSend)

        btnSend.setOnClickListener {
            val message = Message(
                uid= ID.unique(),
                senderID=currentUserId,
                type="TEXT",
                value=messageText.text.toString(),
                date=System.currentTimeMillis()
            )

            onSendMessage?.invoke(message)
            messageText.text.clear();
        }

        btnLocation.setOnClickListener {
            var latLng = "-22.41,-23.46"

            val message = Message(
                uid= ID.unique(),
                senderID=currentUserId,
                type="LOCATION",
                value=latLng,
                date=System.currentTimeMillis()
            )

            onSendMessage?.invoke(message)
            messageText.text.clear();
        }

        return view
    }
}