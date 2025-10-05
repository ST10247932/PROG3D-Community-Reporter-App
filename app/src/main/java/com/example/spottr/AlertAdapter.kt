package com.example.spottr

import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView

class AlertAdapter(
    private val context: Context,
    private val alertList: List<AlertItem>
) : RecyclerView.Adapter<AlertAdapter.AlertViewHolder>() {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("alert_prefs", Context.MODE_PRIVATE)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlertViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_alert_action, parent, false)
        return AlertViewHolder(view)
    }

    override fun onBindViewHolder(holder: AlertViewHolder, position: Int) {
        val alert = alertList[position]

        // Load saved values or defaults
        alert.isEnabled = prefs.getBoolean("${alert.name}_enabled", alert.isEnabled)
        alert.sound = prefs.getString("${alert.name}_sound", alert.sound) ?: "Default"
        alert.priority = prefs.getString("${alert.name}_priority", alert.priority) ?: "Medium"



        holder.tvAlertName.text = alert.name
        holder.switchAlert.isChecked = alert.isEnabled
        holder.tvCategory.text = "Category: ${alert.category}"
        holder.tvPriority.text = "Priority: ${alert.priority}"
        holder.btnSelectSound.text = "Sound: ${alert.sound}"

        holder.switchAlert.setOnCheckedChangeListener { _, isChecked ->
            alert.isEnabled = isChecked
            prefs.edit().putBoolean("${alert.name}_enabled", isChecked).apply()
        }

        // Select sound (simple example)
        holder.btnSelectSound.setOnClickListener {
            val sounds = arrayOf("Default", "Chime", "Alert", "Beep")
            val builder = android.app.AlertDialog.Builder(context)
            builder.setTitle("Select Sound")
            builder.setItems(sounds) { _, which ->
                alert.sound = sounds[which]
                holder.btnSelectSound.text = "Sound: ${alert.sound}"
                prefs.edit().putString("${alert.name}_sound", alert.sound).apply()
            }
            builder.show()
        }

        // Set priority (simple example)
        holder.tvPriority.setOnClickListener {
            val priorities = arrayOf("Low", "Medium", "High")
            val builder = android.app.AlertDialog.Builder(context)
            builder.setTitle("Select Priority")
            builder.setItems(priorities) { _, which ->
                alert.priority = priorities[which]
                holder.tvPriority.text = "Priority: ${alert.priority}"
                prefs.edit().putString("${alert.name}_priority", alert.priority).apply()
            }
            builder.show()
        }

        // Test alert button
        holder.btnTestAlert.setOnClickListener {
            Toast.makeText(context, "Testing ${alert.name} with sound ${alert.sound} and ${alert.priority} priority", Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int = alertList.size

    inner class AlertViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvAlertName: TextView = view.findViewById(R.id.tvAlertName)
        val switchAlert: Switch = view.findViewById(R.id.switchAlert)
        val tvCategory: TextView = view.findViewById(R.id.tvCategory)
        val tvPriority: TextView = view.findViewById(R.id.tvPriority)
        val btnSelectSound: Button = view.findViewById(R.id.btnSelectSound)
        val btnTestAlert: Button = view.findViewById(R.id.btnTestAlert)
    }
}
