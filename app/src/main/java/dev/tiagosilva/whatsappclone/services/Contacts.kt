package dev.tiagosilva.whatsappclone.services

import android.content.Context
import android.provider.ContactsContract
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dev.tiagosilva.whatsappclone.data.Contact
import dev.tiagosilva.whatsappclone.data.User
import kotlinx.coroutines.tasks.await

class Contacts {
    companion object {
        private fun listInternalContacts(context: Context, query: String? = null): List<Contact> {
            val contacts = mutableListOf<Contact>()
            val contentResolver = context.contentResolver

            val projection = arrayOf(
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER
            )

            var selection: String? = null
            var selectionArgs: Array<String>? = null

            if(!query.isNullOrEmpty()) {
                selection = "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME} LIKE ? OR ${ContactsContract.CommonDataKinds.Phone.NUMBER} LIKE ?"
                selectionArgs = arrayOf("%$query%", "%$query%")
            }

            val cursor = contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC",
            )
            cursor?.use {
                val id = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)
                val name = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                val number = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)

                while(it.moveToNext() == true) {
                    val _id = it.getString(id)
                    val _name = it.getString(name)
                    val _number = it.getString(number)

                    val _contact = Contact(_id, _name, _number)
                    // TODO: Verificar no banco do firebase
                    val hasContactFirebase = true
                    if (hasContactFirebase) {
                        contacts.add(_contact)
                    }
                }
            }

            return contacts
        }

        @JvmStatic
        suspend fun listContacts(context: Context, query: String? = null, forceRefresh: Boolean = false): List<Contact> {
            val sharedPrefs = context.getSharedPreferences("contacts_cache", Context.MODE_PRIVATE)
            val gson = Gson()

            if (!forceRefresh) {
                val cacheData = sharedPrefs.getString("registered_contacts", null)
                if (cacheData != null) {
                    val type = object : TypeToken<List<Contact>>() {}.type
                    return gson.fromJson(cacheData, type)
                }
            }

            val contacts: List<Contact> = listInternalContacts(context, query)

            val firebaseDatabase = FirebaseConfiguration.getFirebaseDatabase()
            val contactsFiltered = mutableListOf<Contact>()

            try {
                for(contact in contacts) {
                    val phone = contact.telefone?.replace("+55", "")?.replace(Regex("[^0-9]"), "")

                    val snapshot = firebaseDatabase.child("users").child(phone.toString()).get().await()
                    if (snapshot.exists()) {
                        val user = snapshot.getValue(User::class.java)
                        if(user != null) {
                            val contactItem = Contact(user.uid, contact.nome, user.phone, user.photoUrl)
                            contactsFiltered.add(contactItem)
                        }
                    }
                }

                val json = gson.toJson(contactsFiltered)
                sharedPrefs.edit().putString("registered_contacts", json).apply()
            } catch(e: Exception) {
                Log.e("Erro ao filtrar contatos no Firebase", "Erro: ${e.message}")
            }

            return contactsFiltered
        }
    }
}