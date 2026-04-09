package dev.tiagosilva.whatsappclone.activities

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.commit
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView
import dev.tiagosilva.whatsappclone.R
import dev.tiagosilva.whatsappclone.data.Contact
import dev.tiagosilva.whatsappclone.fragments.ChatInputFragment
import dev.tiagosilva.whatsappclone.fragments.ChatMessagesFragment
import dev.tiagosilva.whatsappclone.services.FirebaseConfiguration

class ChatActivity : AppCompatActivity() {
    private val firebaseAuth = FirebaseConfiguration.getFirebaseAuth()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_chat)

        val chatId = intent.getStringExtra("chatId")
        val contactName = intent.getStringExtra("contactName")
        val contactImage = intent.getStringExtra("contactImage")
        val currentUserId = firebaseAuth.currentUser?.uid

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val toolbar: Toolbar? = findViewById(R.id.toolbarChat)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar?.title = ""

        val txtNameToolbar: TextView? = findViewById(R.id.txtNameToolbar)
        txtNameToolbar?.text = contactName

        val btnBackButton: ImageView? = findViewById(R.id.btnBackButton)
        btnBackButton?.setOnClickListener {
            finish()
        }

        val imageProfileToolbar: CircleImageView? = findViewById(R.id.imageProfileToolbar)
        Glide.with(this)
            .load(contactImage)
            .placeholder(R.drawable.padrao)
            .error(R.drawable.padrao)
            .into(imageProfileToolbar!!)

        setSupportActionBar(toolbar)

        val inputFragment = ChatInputFragment().apply {
            arguments = Bundle().apply {
                putString("chatId", chatId)
                putString("currentUserId", currentUserId)
            }
        }

        val messagesFragment = ChatMessagesFragment().apply {
            arguments = Bundle().apply {
                putString("chatId", chatId)
                putString("currentUserId", currentUserId)
            }
        }

        inputFragment.onSendMessage = { message ->
            (messagesFragment as? ChatMessagesFragment)?.addMessage(message, chatId)
        }

        supportFragmentManager.commit {
            replace(R.id.fragmentChatMessages, messagesFragment)
            replace(R.id.fragmentChatInput, inputFragment)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
