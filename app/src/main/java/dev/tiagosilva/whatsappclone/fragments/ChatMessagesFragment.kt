package dev.tiagosilva.whatsappclone.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import dev.tiagosilva.whatsappclone.R
import dev.tiagosilva.whatsappclone.adapters.MessagesAdapter
import dev.tiagosilva.whatsappclone.data.Message
import dev.tiagosilva.whatsappclone.services.FirebaseConfiguration
import io.sentry.Sentry
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ChatMessagesFragment : Fragment() {
    private val firebaseDb = FirebaseConfiguration.getFirebaseDatabase()

    private var messages = mutableListOf<Message>()
    private var chatId: String? = null
    private var currentUserId: String? = null

    private lateinit var rvMessages: RecyclerView
    private lateinit var messagesAdapter: MessagesAdapter

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
        val view = inflater.inflate(R.layout.fragment_chat_messages, container, false)
        rvMessages = view.findViewById<RecyclerView>(R.id.rvMessages)
        val layoutManager = LinearLayoutManager(requireContext())
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true

        rvMessages.layoutManager = layoutManager
        messagesAdapter = MessagesAdapter(messages, currentUserId)
        rvMessages.adapter = messagesAdapter

        return view
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
        saveInFirebase(message, chatId)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        if(chatId != null) {
            firebaseDb.child("chats").child(chatId!!)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val loadedMessages = mutableListOf<Message>()
                        for(msgSnap in snapshot.children) {
                            try {
                                val msg = msgSnap.getValue(Message::class.java)
                                if (msg != null) loadedMessages.add(msg)
                            } catch (e: Exception) {
                                Sentry.captureException(e)
                                Log.e("ChatMessagesFragment", "Erro ao converter mensagem: ${e.message}")
                            }
                        }

                        setMessages(loadedMessages)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e("ChatMessagesFragment", "Error ao carregar mensagens: ${error.message}")
                    }
                })
        } else {
            Log.e("ChatMessagesFragment", "chatId is null") 
        }
    }

    private fun setMessages(newMessages: List<Message>) {
        messages.clear()
        messages.addAll(newMessages.sortedByDescending { it.date })
        messagesAdapter.notifyDataSetChanged()
        rvMessages.scrollToPosition(0)
    }
}