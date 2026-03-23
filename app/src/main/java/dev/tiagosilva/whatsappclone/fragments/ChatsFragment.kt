package dev.tiagosilva.whatsappclone.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
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
        loadChats()

        sharedPreferences = requireContext().getSharedPreferences("chat_cache", Context.MODE_PRIVATE)
        loadChatsFromCache()
        loadChatsFromFirebase()

        return view
    }

    private fun loadChatsFromFirebase() {
        val uid = firebaseAuth.currentUser?.uid ?: return
        val dbChats = firebaseDatabase.child("chats")
        val dbUsers = firebaseDatabase.child("users")

        dbChats.get().addOnSuccessListener { snapshot ->
            val chats = mutableListOf<Chat>()
            val userUids = mutableListOf<String>()

            for (chatSnap in snapshot.children){
                val chatId = chatSnap.key ?: continue
                if (chatId.contains(uid)) {
                    val otherUid = chatId.split("_").first { it != uid }
                    userUids.add(otherUid)
                }
            }

            // TODO: Buscar os nomes e imagens desses contatos
            for(user in userUids){

            }
        }
    }

    private fun loadChatsFromCache() {
        TODO("Not yet implemented")
    }

    private fun openChat(chat: Chat) {}

    fun loadChats() {
        try {
            pbLoading.visibility = View.VISIBLE
            adapter = ChatsAdapter(chats) { chat ->
                if (isAdded && activity != null) {
                    openChat(chat)
                }
            }
            rvChats.adapter = adapter
        } catch (e: Exception) {
            Log.e("loadChats", "Erro ao carregar conversas: ", e)
        }
        finally {
            pbLoading.visibility = View.GONE
        }
    }
}

