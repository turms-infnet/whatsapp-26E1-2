package dev.tiagosilva.whatsappclone.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.DialogFragment
import dev.tiagosilva.whatsappclone.R
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

class FullScreenMapFragment : DialogFragment() {
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            latitude = it.getDouble("latitude", 0.0)
            longitude = it.getDouble("longitude", 0.0)
        }

        setStyle(STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_full_screen_map, container, false)

        val osMapViewFull = view.findViewById<MapView>(R.id.osMapViewFull)

        val backButton = view.findViewById<ImageView>(R.id.btnBackButton)
        backButton.setOnClickListener { dismiss() }

        osMapViewFull.setTileSource(TileSourceFactory.MAPNIK)
        osMapViewFull.setMultiTouchControls(true)
        osMapViewFull.isTilesScaledToDpi = true

        val locationPoints = GeoPoint(latitude, longitude)
        val mapController = osMapViewFull.controller
        mapController.setZoom(16.0)
        mapController.setCenter(locationPoints)

        osMapViewFull.overlays.clear()

        val marker = Marker(osMapViewFull)
        marker.position = locationPoints
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        osMapViewFull.overlays.add(marker)

        osMapViewFull.invalidate()

        return view
    }

    companion object {
        fun newInstance(lat: Double, lng: Double): FullScreenMapFragment {
            val frag = FullScreenMapFragment()
            val args = Bundle()

            args.putDouble("latitude", lat)
            args.putDouble("longitude", lng)
            frag.arguments = args

            return frag
        }
    }
}