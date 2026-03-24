package dev.tiagosilva.whatsappclone.activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.commit
import dev.tiagosilva.whatsappclone.R
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
        val currentUserId = firebaseAuth.currentUser?.uid

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val toolbar: Toolbar? = findViewById(R.id.toolbar)
        toolbar?.title = "Nome do usuário"
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

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
