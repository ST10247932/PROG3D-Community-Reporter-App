package com.example.spottr

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class IncidentHistoryAdapter(private val incidentList: List<Incident>) :
    RecyclerView.Adapter<IncidentHistoryAdapter.ReportViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_report, parent, false)
        return ReportViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReportViewHolder, position: Int) {
        val report = incidentList[position]
        holder.tvAddress.text = report.location
        holder.tvDescription.text = report.description
        holder.tvCoordinates.text = "Lat: ${report.lat}, Lng: ${report.lng}"
    }

    override fun getItemCount(): Int = incidentList.size

    class ReportViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvAddress: TextView = view.findViewById(R.id.tvAddress)
        val tvDescription: TextView = view.findViewById(R.id.tvDescription)
        val tvCoordinates: TextView = view.findViewById(R.id.tvCoordinates)
    }
}
