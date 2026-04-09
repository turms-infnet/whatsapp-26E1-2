package dev.tiagosilva.whatsappclone.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import dev.tiagosilva.whatsappclone.R
import dev.tiagosilva.whatsappclone.data.Message
import dev.tiagosilva.whatsappclone.fragments.FullScreenMapFragment
import org.osmdroid.util.GeoPoint
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MessagesAdapter(private val messages: List<Message>, private val currentUserId: String?) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_LEFT_TEXT = 0
        private const val TYPE_RIGHT_TEXT = 1
        private const val TYPE_LEFT_MAP = 2
        private const val TYPE_RIGHT_MAP = 3
    }

    override fun getItemViewType(position: Int): Int {
        val msg = messages[position]

        return when {
            msg.type?.lowercase() == "location" && msg.senderID == currentUserId -> TYPE_RIGHT_MAP
            msg.type?.lowercase() == "location" -> TYPE_LEFT_MAP
            msg.senderID == currentUserId -> TYPE_RIGHT_TEXT
            else -> TYPE_LEFT_TEXT
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType){
            TYPE_LEFT_TEXT -> MessageViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_message_left, parent, false))
            TYPE_RIGHT_TEXT -> MessageViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_message_right, parent, false))
            TYPE_LEFT_MAP -> MapViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_map_left, parent, false))
            TYPE_RIGHT_MAP -> MapViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_map_right, parent, false))
            else -> throw IllegalArgumentException("View type desconhecida")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val msg = messages[position]
        val formatter = SimpleDateFormat("HH:mm", Locale.getDefault())
        val time = msg.date?.let { formatter.format(Date(it)) }

        when(holder) {
            is MessageViewHolder -> {
                if (msg.type?.lowercase() == "text") {
                    holder.txtMessage.text = msg.value ?: ""
                }
                holder.txtTime.text = time ?: ""
            }
            is MapViewHolder -> {
                val latLng = msg.value?.split(",")
                val lat = latLng?.getOrNull(0)?.trim()
                val lng = latLng?.getOrNull(1)?.trim()
                if (lat != null && lng != null){
                    holder.osMapView.visibility = View.VISIBLE // View.GONE
                    holder.imageMapPin.visibility = View.GONE // View.GONE
                    holder.overlayMapClick.visibility = View.VISIBLE // View.Gone

                    val mapView = holder.osMapView
                    mapView.setTileSource(TileSourceFactory.MAPNIK)
                    mapView.setMultiTouchControls(true)

                    val locationPoints = GeoPoint(lat.toDouble(), lng.toDouble())
                    val mapController = mapView.controller
                    mapController.setZoom(16.0)
                    mapController.setCenter(locationPoints)

                    mapView.overlays.clear()

                    val marker = Marker(mapView)
                    marker.position = locationPoints
                    marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    mapView.overlays.add(marker)

                    mapView.invalidate()

                    holder.overlayMapClick.setOnClickListener {
                        val context = holder.overlayMapClick.context
                        if (context is FragmentActivity) {
                            val dialog = FullScreenMapFragment.newInstance(lat.toDouble(), lng.toDouble())
                            dialog.show(context.supportFragmentManager, "FullScreenMapFragment")
                        }
                    }
                } else {
                    holder.osMapView.visibility = View.GONE
                    holder.imageMapPin.visibility = View.GONE
                    holder.overlayMapClick.visibility = View.GONE
                }

                holder.txtTime.text = time ?: ""
            }
        }

    }

    override fun getItemCount() = messages.size

    class MessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtMessage: TextView = view.findViewById(R.id.txtMessage)
        val txtTime: TextView = view.findViewById(R.id.txtTime)
    }

    class MapViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val osMapView: MapView = view.findViewById(R.id.osMapView)
        val imageMapPin: ImageView = view.findViewById(R.id.imageMapPin)
        val overlayMapClick: View = view.findViewById(R.id.overlayMapClick)
        val txtTime: TextView = view.findViewById(R.id.txtTime)
    }
}
