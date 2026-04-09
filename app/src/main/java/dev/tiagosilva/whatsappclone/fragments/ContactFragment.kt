package dev.tiagosilva.whatsappclone.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dev.tiagosilva.whatsappclone.R
import dev.tiagosilva.whatsappclone.activities.ChatActivity
import dev.tiagosilva.whatsappclone.activities.MainActivity
import dev.tiagosilva.whatsappclone.adapters.ContactsAdapter
import dev.tiagosilva.whatsappclone.data.Contact
import dev.tiagosilva.whatsappclone.services.Contacts
import dev.tiagosilva.whatsappclone.services.FirebaseConfiguration
import dev.tiagosilva.whatsappclone.utils.ChatUtil
import io.sentry.Sentry
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ContactFragment : Fragment() {
    private val firebaseAuth = FirebaseConfiguration.getFirebaseAuth()
    private val firebaseDb = FirebaseConfiguration.getFirebaseDatabase()
    private lateinit var rvContacts: RecyclerView
    private var adapter: ContactsAdapter? = null
    private lateinit var fabRefresh: FloatingActionButton
    private lateinit var pbLoading: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_contact, container, false)

        rvContacts = view.findViewById(R.id.rvContacts)
        rvContacts.layoutManager = LinearLayoutManager(requireContext())

        fabRefresh = view.findViewById<FloatingActionButton>(R.id.fab_refresh_list)
        pbLoading = view.findViewById<ProgressBar>(R.id.pbLoading)
        fabRefresh.setOnClickListener {
            loadContacts(true)
        }

        loadContacts(false)
        return view
    }

    private fun handleContactClick(contact: Contact) {
        val currentUser = firebaseAuth.currentUser
        val contactUid = contact.id

        if (currentUser?.uid.isNullOrEmpty() || contactUid.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "Usuário inválido", Toast.LENGTH_LONG).show()
            return
        }

        val chatId1 = "${currentUser.uid}_${contactUid}"
        val chatId2 = "${contactUid}_${currentUser.uid}"

        lifecycleScope.launch {
            val snapshot1 = firebaseDb.child("chats").child(chatId1).get().await()
            val snapshot2 = firebaseDb.child("chats").child(chatId2).get().await()

            var chatId: String?
            if (snapshot1.exists()) {
                chatId = chatId1
            } else if (snapshot2.exists()) {
                chatId = chatId2
            } else {
                chatId = chatId1
                firebaseDb.child("chats").child(chatId).setValue(true).await()
            }

            ChatUtil.openChat(chatId, contact.nome, contact.image, requireContext())
        }
    }

    private fun loadContacts(force: Boolean = false) {
        pbLoading.visibility = View.VISIBLE
        fabRefresh.isEnabled = false

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val result = Contacts.listContacts(requireContext(), null, force)

                adapter = ContactsAdapter(result) { contact ->
                    handleContactClick(contact)
                }
                rvContacts.adapter = adapter
            } catch (e: Exception) {
                Sentry.captureException(e)
                Log.e("Erro na montagem do adapter", "Erro: ${e.message}")
            } finally {
                pbLoading.visibility = View.GONE
                fabRefresh.isEnabled = true
            }
        }
    }
}