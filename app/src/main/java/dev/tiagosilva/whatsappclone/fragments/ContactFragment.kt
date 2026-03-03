package dev.tiagosilva.whatsappclone.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import dev.tiagosilva.whatsappclone.R
import dev.tiagosilva.whatsappclone.data.Contact
import dev.tiagosilva.whatsappclone.services.Contacts

class ContactFragment : Fragment() {
    private lateinit  var contacts: List<Contact>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        contacts = Contacts.listContacts(requireContext())
        if (contacts.isNotEmpty()) {
            Log.d("Contato", contacts[0].nome)
        }
        val view = inflater.inflate(R.layout.fragment_contact, container, false)

//        TODO: Implementar listagem

        loadContacts()
        return view
    }

    private fun loadContacts() {

    }
}