package com.example.spottr

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class IncidentHistoryActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var reportAdapter: ReportAdapter
    private val reportList = mutableListOf<Report>()

    private val db = FirebaseFirestore.getInstance()
    private var firestoreListener: ListenerRegistration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_incident_history)

        recyclerView = findViewById(R.id.recyclerReports)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Start with in-memory reports
        reportList.addAll(HomeActivity.tempReports)
        reportAdapter = ReportAdapter(reportList)
        recyclerView.adapter = reportAdapter

        // Listen to Firestore in real-time
        firestoreListener = db.collection("reports")
            .addSnapshotListener { snapshots, e ->
                if (e != null) return@addSnapshotListener

                if (snapshots != null) {
                    for (doc in snapshots.documentChanges) {
                        val report = doc.document.toObject(Report::class.java)
                        if (!reportList.contains(report)) {
                            reportList.add(report)
                        }
                    }
                    reportAdapter.notifyDataSetChanged()
                }
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        firestoreListener?.remove() // remove listener to avoid memory leaks
    }
}
