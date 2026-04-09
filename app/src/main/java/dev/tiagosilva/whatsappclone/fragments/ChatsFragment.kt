package dev.tiagosilva.whatsappclone.fragments

import android.content.Context
import android.content.Intent
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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import dev.tiagosilva.whatsappclone.R
import dev.tiagosilva.whatsappclone.activities.ChatActivity
import dev.tiagosilva.whatsappclone.activities.LoginActivity
import dev.tiagosilva.whatsappclone.activities.MainActivity
import dev.tiagosilva.whatsappclone.adapters.ChatsAdapter
import dev.tiagosilva.whatsappclone.data.Chat
import dev.tiagosilva.whatsappclone.services.Contacts
import dev.tiagosilva.whatsappclone.services.FirebaseConfiguration
import dev.tiagosilva.whatsappclone.utils.ChatUtil

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
        rvChats = view.findViewById<RecyclerView>(R.id.rvChats)
        pbLoading = view.findViewById<ProgressBar>(R.id.pbLoading)

        rvChats.layoutManager = LinearLayoutManager(requireContext())
        adapter = ChatsAdapter(chats) { chat ->
            ChatUtil.openChat(chat.chatId, chat.contactName, chat.contactImage, requireContext())
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
            Toast.makeText(requireContext(), "Usuário não autenticado", Toast.LENGTH_SHORT).show()
            firebaseAuth.signOut()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
            requireActivity().finish()

            return
        }

        val dbChats = firebaseDatabase.child("chats")
        val dbUsers = firebaseDatabase.child("users")

        dbChats.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val chatIds = mutableListOf<String>()
                for (chatSnap in snapshot.children) {
                    val chatId = chatSnap.key ?: continue
                    if (chatId.contains(uid)) {
                        chatIds.add(chatId)
                    }
                }
                chats.clear()

                if (chatIds.isEmpty()){
                    adapter.notifyDataSetChanged()
                    pbLoading.visibility = View.GONE
                    Toast.makeText(requireContext(), "Nenhuma conversa foi encontrada.",
                        Toast.LENGTH_SHORT).show()
                    return
                }

                var loaded = 0
                val tempChats = mutableListOf<Chat>()

                for(chatId in chatIds){
                    val uids = chatId.split("_")
                    val otherId = uids.firstOrNull { it != uid } ?: continue
                    dbUsers.orderByChild("uid").equalTo(otherId).get().addOnSuccessListener { otherUserSnap ->
                        if (otherUserSnap.exists()) {
                            val otherUserNode = otherUserSnap.children.first()
                            val name = Contacts.getNameInPhoneBook(requireContext(), otherUserNode.child("phone").value.toString())
                            val image = otherUserNode.child("photoUrl").value.toString().takeUnless { it.isBlank() } ?: ""

                            dbChats.child(chatId).orderByChild("date").limitToLast(1).get().addOnSuccessListener { msgSnap ->
                                if (msgSnap.childrenCount > 0) {
                                    var lastMessage: String = ""
                                    var lastTime: String = ""
                                    for(msg in msgSnap.children) {
                                        lastMessage = msg.child("value").value?.toString() ?: ""
                                        lastTime = msg.child("date").value?.toString() ?: ""
                                    }

                                    val chat = Chat(chatId, name, image, lastMessage, lastTime)
                                    tempChats.add(chat)
                                }
                                loaded++

                                Log.d("loaded", loaded.toString())
                                Log.d("chatIds.size", chatIds.size.toString())
                                if (loaded == chatIds.size) {
                                    tempChats.sortByDescending { it.time?.toLongOrNull() ?: 0L }
                                    chats.clear()
                                    chats.addAll(tempChats)
                                    adapter.notifyDataSetChanged()
                                    pbLoading.visibility = View.GONE
                                }
                            }.addOnFailureListener {
                                chats.clear()
                                adapter.notifyDataSetChanged()
                                pbLoading.visibility = View.GONE
                                Toast.makeText(requireContext(), "Erro ao carregar conversas ${it.message}",
                                    Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                pbLoading.visibility = View.GONE
                Toast.makeText(requireContext(), "Erro ao carregar conversas ${error.message}",
                    Toast.LENGTH_SHORT).show()
            }
        })
    }
}
