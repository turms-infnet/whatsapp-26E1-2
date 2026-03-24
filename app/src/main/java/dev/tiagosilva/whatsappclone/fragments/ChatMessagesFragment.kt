package dev.tiagosilva.whatsappclone.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import dev.tiagosilva.whatsappclone.R
import dev.tiagosilva.whatsappclone.data.Message
import dev.tiagosilva.whatsappclone.services.FirebaseConfiguration
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ChatMessagesFragment : Fragment() {
    private val firebaseDb = FirebaseConfiguration.getFirebaseDatabase()

    private var messages = mutableListOf<Message>()
    private var chatId: String? = null
    private var currentUserId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        chatId = arguments?.getString("chatId")
        currentUserId = arguments?.getString("currentUserId")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat_messages, container, false)
    }

    private fun saveInFirebase(message: Message, chatId: String?) {
        if (chatId.isNullOrEmpty() || message.uid.isNullOrEmpty()) {
            Log.e("ChatMessagesFragment", "chatId is null or empty")
            return
        }

        lifecycleScope.launch {
            firebaseDb.child("chats").child(chatId).child(message.uid).setValue(message).await()
        }
    }

    fun addMessage(message: Message, chatId: String?) {
        messages.add(message)
        saveInFirebase(message, chatId)
    }
}