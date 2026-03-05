package dev.tiagosilva.whatsappclone.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView
import dev.tiagosilva.whatsappclone.data.Contact
import dev.tiagosilva.whatsappclone.R
import com.bumptech.glide.Glide

class ContactsAdapter(private val contacts: List<Contact>):
    RecyclerView.Adapter<ContactsAdapter.ContactViewHolder>() {

    class ContactViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val txtName: TextView = view.findViewById(R.id.txtName)
        val txtPhone: TextView = view.findViewById(R.id.txtPhone)
        val imageProfile: ShapeableImageView = view.findViewById(R.id.imageProfile)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ContactViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_contact, parent, false)
        return ContactViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contact = contacts[position]
        holder.txtName.text = contact.nome
        holder.txtPhone.text = contact.telefone
        val avatarUrl = if (!contact.image.isNullOrEmpty()) {
            contact.image
        } else {
            "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTEglnQaY_4soN60TrffX8UmTjPmXz_DZy0Ag&s"
        }

        Glide
            .with(holder.itemView.context)
            .load(avatarUrl)
            .placeholder(R.drawable.baseline_account_circle_24)
            .error(R.drawable.baseline_account_circle_24)
            .into(holder.imageProfile);
    }

    override fun getItemCount() = contacts.size
}