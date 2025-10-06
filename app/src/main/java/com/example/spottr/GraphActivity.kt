package com.example.spottr

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.spottr.databinding.ActivityGraphBinding
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

class GraphActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGraphBinding
    private val api = RetrofitClient.instance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityGraphBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.nav_home
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> { startActivity(Intent(this, HomeActivity::class.java)) }
                R.id.nav_search -> { startActivity(Intent(this, GraphActivity::class.java)) }
                R.id.nav_add -> { startActivity(Intent(this, AddIncidentActivity::class.java)) }
                R.id.nav_alert -> { startActivity(Intent(this, AlertActionsActivity::class.java)) }
                R.id.nav_profile -> { startActivity(Intent(this, ProfileActivity::class.java)) }
            }
            true
        }

        fetchIncidentsAndPopulateGraphs()
    }

    private fun fetchIncidentsAndPopulateGraphs() {
        api.getIncidents().enqueue(object : Callback<List<Incident>> {
            override fun onResponse(
                call: Call<List<Incident>>,
                response: Response<List<Incident>>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    val incidents = response.body()!!

                    // Group counts by location and by day
                    val locationCount = mutableMapOf<String, Int>()
                    val dayCount = mutableMapOf(
                        "Sat" to 0, "Sun" to 0, "Mon" to 0, "Tue" to 0,
                        "Wed" to 0, "Thu" to 0, "Fri" to 0
                    )

                    for (incident in incidents) {
                        val location = incident.location.ifEmpty { "Unknown" }
                        locationCount[location] = locationCount.getOrDefault(location, 0) + 1

                        val dayOfWeek = incident.dateTime.toDayOfWeek()
                        if (dayOfWeek != "Unknown") {
                            dayCount[dayOfWeek] = dayCount.getOrDefault(dayOfWeek, 0) + 1
                        }
                    }

                    setupPieChart(locationCount)
                    setupBarChart(dayCount)
                } else {
                    Toast.makeText(this@GraphActivity, "No incidents found", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Incident>>, t: Throwable) {
                Toast.makeText(this@GraphActivity, "Failed to fetch incidents", Toast.LENGTH_SHORT).show()
                t.printStackTrace()
            }
        })
    }

    private fun setupPieChart(locationCount: Map<String, Int>) {
        val entries = locationCount.map { PieEntry(it.value.toFloat(), it.key) }
        val dataSet = PieDataSet(entries, "Incidents by Location")
        dataSet.colors = ColorTemplate.MATERIAL_COLORS.toList()
        val data = PieData(dataSet)
        data.setValueTextSize(12f)

        binding.pieChart.data = data
        binding.pieChart.description.text = "Incidents per Location"
        binding.pieChart.animateY(1000)
        binding.pieChart.invalidate()
    }

    private fun setupBarChart(dayCount: Map<String, Int>) {
        val entries = dayCount.entries.mapIndexed { index, entry ->
            BarEntry(index.toFloat(), entry.value.toFloat())
        }
        val dataSet = BarDataSet(entries, "Incidents per Day")
        dataSet.colors = ColorTemplate.COLORFUL_COLORS.toList()
        val data = BarData(dataSet)
        data.barWidth = 0.5f

        binding.barChart.data = data
        val xAxis = binding.barChart.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(dayCount.keys.toList())
        xAxis.granularity = 1f
        xAxis.setDrawGridLines(false)

        binding.barChart.description.text = "Incidents by Day of Week"
        binding.barChart.animateY(1000)
        binding.barChart.invalidate()
    }
}

// 🔹 Extension function to parse ISO date and return day name
fun String.toDayOfWeek(): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        inputFormat.timeZone = TimeZone.getTimeZone("UTC")
        val date = inputFormat.parse(this)
        val cal = Calendar.getInstance()
        cal.time = date!!
        when (cal.get(Calendar.DAY_OF_WEEK)) {
            Calendar.SATURDAY -> "Sat"
            Calendar.SUNDAY -> "Sun"
            Calendar.MONDAY -> "Mon"
            Calendar.TUESDAY -> "Tue"
            Calendar.WEDNESDAY -> "Wed"
            Calendar.THURSDAY -> "Thu"
            Calendar.FRIDAY -> "Fri"
            else -> "Unknown"
        }
    } catch (e: Exception) {
        "Unknown"
    }
}

