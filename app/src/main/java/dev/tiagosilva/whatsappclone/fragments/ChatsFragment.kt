package dev.tiagosilva.whatsappclone.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import dev.tiagosilva.whatsappclone.R
import dev.tiagosilva.whatsappclone.adapters.ChatsAdapter
import dev.tiagosilva.whatsappclone.data.Chat
import dev.tiagosilva.whatsappclone.services.FirebaseConfiguration

class ChatsFragment : Fragment() {
    private lateinit var rvChats: RecyclerView
    private lateinit var pbLoading: ProgressBar
    private lateinit var adapter: ChatsAdapter
    private lateinit var sharedPreferences: SharedPreferences
    private val firebaseAuth = FirebaseConfiguration.getFirebaseAuth()
    private val firebaseDatabase = FirebaseConfiguration.getFirebaseDatabase()

    private val chats = mutableListOf<Chat>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_chats, container, false)
        pbLoading = view.findViewById<ProgressBar>(R.id.pbLoading)
        rvChats = view.findViewById(R.id.rvChats)
        rvChats.layoutManager = LinearLayoutManager(requireContext())
        adapter = ChatsAdapter(chats) { chat ->
            if (isAdded && activity != null) {
                openChat(chat)
            }
        }
        rvChats.adapter = adapter
        sharedPreferences = requireContext().getSharedPreferences("chat_cache", Context.MODE_PRIVATE)
        loadChatsFromFirebase()
        return view
    }

    private fun loadChatsFromFirebase() {
        pbLoading.visibility = View.VISIBLE
        val uid = firebaseAuth.currentUser?.uid ?: run {
            pbLoading.visibility = View.GONE
            return
        }
        val dbChats = firebaseDatabase.child("chats")
        val dbUsers = firebaseDatabase.child("users")
        dbChats.get().addOnSuccessListener { snapshot ->
            val userUids = mutableListOf<String>()
            for (chatSnap in snapshot.children){
                val chatId = chatSnap.key ?: continue
                if (chatId.contains(uid)) {
                    val otherUid = chatId.split("_").first { it != uid }
                    userUids.add(otherUid)
                }
            }
            // Exemplo: buscar dados dos usuários e criar objetos Chat
            chats.clear()
            if (userUids.isEmpty()) {
                adapter.notifyDataSetChanged()
                pbLoading.visibility = View.GONE
                return@addOnSuccessListener
            }
            var loaded = 0
            for (user in userUids) {
                dbUsers.child(user).get().addOnSuccessListener { userSnap ->
                    val name = userSnap.child("displayName").value?.toString() ?: "Sem nome"
                    val photoUrl = userSnap.child("photoUrl").value?.toString() ?: ""
                    val chat = Chat(user, name, photoUrl)
                    chats.add(chat)
                    loaded++
                    if (loaded == userUids.size) {
                        if (isAdded) adapter.notifyDataSetChanged()
                        pbLoading.visibility = View.GONE
                    }
                }.addOnFailureListener {
                    loaded++
                    if (loaded == userUids.size) {
                        if (isAdded) adapter.notifyDataSetChanged()
                        pbLoading.visibility = View.GONE
                    }
                }
            }
        }.addOnFailureListener {
            pbLoading.visibility = View.GONE
        }
    }

    private fun openChat(chat: Chat) {
        // TODO: Implemente a navegação para a tela de chat
        Toast.makeText(requireContext(), "Abrir chat com ${chat.contactName}", Toast.LENGTH_SHORT).show()
    }
}
