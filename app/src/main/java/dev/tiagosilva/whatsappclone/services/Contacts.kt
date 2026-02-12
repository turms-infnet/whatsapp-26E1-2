package dev.tiagosilva.whatsappclone.services

import android.content.Context
import android.provider.ContactsContract
import android.util.Log
import dev.tiagosilva.whatsappclone.data.Contact

class Contacts {
    companion object {
        @JvmStatic
        fun listContacts(context: Context, query: String? = null): List<Contact> {
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
                    contacts.add(_contact)
                }
            }

            return contacts
        }
    }
}