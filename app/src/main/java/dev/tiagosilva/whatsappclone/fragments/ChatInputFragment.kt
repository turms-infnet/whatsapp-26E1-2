package dev.tiagosilva.whatsappclone.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import dev.tiagosilva.whatsappclone.R
import dev.tiagosilva.whatsappclone.data.Message
import io.appwrite.ID
import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.widget.Toast
import com.google.android.gms.location.LocationServices

class ChatInputFragment : Fragment() {
    private lateinit var messageText: EditText
    private lateinit var btnLocation: ImageButton
    private lateinit var btnSend: ImageButton
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val LOCATION_PERMISSION_REQUEST_CODE = 1001

    private var chatId: String? = null
    private var currentUserId: String? = null

    var onSendMessage: ((Message) -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        chatId = arguments?.getString("chatId")
        currentUserId = arguments?.getString("currentUserId")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_chat_input, container, false)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        messageText = view.findViewById<EditText>(R.id.messageText)
        btnLocation = view.findViewById<ImageButton>(R.id.btnLocation)
        btnSend = view.findViewById<ImageButton>(R.id.btnSend)

        btnSend.setOnClickListener {
            val message = Message(
                uid= ID.unique(),
                senderID=currentUserId,
                type="TEXT",
                value=messageText.text.toString(),
                date=System.currentTimeMillis()
            )

            onSendMessage?.invoke(message)
            messageText.text.clear();
        }

        btnLocation.setOnClickListener {
            if(ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                    LOCATION_PERMISSION_REQUEST_CODE
                )
                Toast.makeText(requireContext(), "Permissão de localização necessárias", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    val latLng = "${location.latitude},${location.longitude}"

                    val message = Message(
                        uid= ID.unique(),
                        senderID=currentUserId,
                        type="LOCATION",
                        value=latLng,
                        date=System.currentTimeMillis()
                    )

                    onSendMessage?.invoke(message)
                    messageText.text.clear();
                } else {
                    Toast.makeText(requireContext(), "Não foi possível obter a localização.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        return view
    }
}