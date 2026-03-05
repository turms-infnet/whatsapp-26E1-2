package dev.tiagosilva.whatsappclone.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dev.tiagosilva.whatsappclone.R
import dev.tiagosilva.whatsappclone.adapters.ContactsAdapter
import dev.tiagosilva.whatsappclone.services.Contacts
import kotlinx.coroutines.launch

class ContactFragment : Fragment() {
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

    private fun loadContacts(force: Boolean = false) {
        pbLoading.visibility = View.VISIBLE
        fabRefresh.isEnabled = false

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val result = Contacts.listContacts(requireContext(), null, force)

                adapter = ContactsAdapter(result)
                rvContacts.adapter = adapter
            } catch (e: Exception) {
                Log.e("Erro na montagem do adapter", "Erro: ${e.message}")
            } finally {
                pbLoading.visibility = View.GONE
                fabRefresh.isEnabled = true
            }
        }
    }
}