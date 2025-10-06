package com.example.spottr

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class IncidentHistoryActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var incidentHistoryAdapter: IncidentHistoryAdapter
    private val incidentList = mutableListOf<Incident>()

    private val db = FirebaseFirestore.getInstance()
    private var firestoreListener: ListenerRegistration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_incident_history)

        recyclerView = findViewById(R.id.recyclerReports)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Start with in-memory reports
        incidentList.addAll(HomeActivity.tempIncidents)
        incidentHistoryAdapter = IncidentHistoryAdapter(incidentList)
        recyclerView.adapter = incidentHistoryAdapter

        fetchIncidents()
    }

    private fun fetchIncidents() {
        RetrofitClient.instance.getIncidents().enqueue(object : Callback<List<Incident>> {
            override fun onResponse(call: Call<List<Incident>>, response: Response<List<Incident>>) {
                if (response.isSuccessful) {
                    val reports = response.body() ?: emptyList()
                    incidentList.clear()
                    incidentList.addAll(reports)
                    incidentHistoryAdapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(
                        this@IncidentHistoryActivity,
                        "Failed to load incidents: ${response.code()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<List<Incident>>, t: Throwable) {
                Toast.makeText(
                    this@IncidentHistoryActivity,
                    "Error loading incidents: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
                t.printStackTrace()
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        firestoreListener?.remove() // remove listener to avoid memory leaks
    }
}
