package com.example.wsacare.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources.getColorStateList
import com.example.wsacare.R
import com.example.wsacare.core.Constants
import com.example.wsacare.data.models.SymptomHistory
import com.example.wsacare.data.rest.Rest
import com.example.wsacare.databinding.FragmentHomeBinding
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.json.JSONObject
import org.json.JSONTokener
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class HomeFragment : Fragment(R.layout.fragment_home) {
    private lateinit var binding: FragmentHomeBinding
    private val historyData = mutableListOf<SymptomHistory>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomeBinding.bind(view)

        actualDate()
        obtainCases()
        obtainHistory()

    }

    private fun obtainHistory() {
        val sharedPreferences =
            requireActivity().getSharedPreferences(Constants.USER, Context.MODE_PRIVATE)
        val id = sharedPreferences.getString("id", "")
        val name = sharedPreferences.getString("name", "")
        binding.txtNameReport.text = name
        binding.txtNameNoReport.text = "$name,"
        Constants.okHttp.newCall(Rest().get("symptoms_history?user_id=$id"))
            .enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e("onFailure: ", e.message.toString())
                }

                override fun onResponse(call: Call, response: Response) {
                    val json = JSONTokener(response.body!!.string()).nextValue() as JSONObject
                    requireActivity().runOnUiThread {
                        binding.progress.visibility = View.GONE
                        if (json.getBoolean("success")) {
                            binding.bgWithReport.visibility = View.VISIBLE
                            val data = json.getJSONArray("data")
                            for (i in 0 until data.length()) {
                                val item = data.getJSONObject(i)
                                historyData.add(
                                    SymptomHistory(
                                        SimpleDateFormat("yyyy-MM-dd hh:mm:s").parse(
                                            item.getString("date")
                                        ), item.getInt("probability_infection")
                                    )
                                )
                            }
                            val finalData = historyData[data.length() - 1]
                            if (finalData.probability_infection >= 60){
                                binding.txtMainReport.text = "CALL TO DOCTOR"
                                binding.bgReportColor.backgroundTintList = getColorStateList(requireContext(),R.color.teal_200)
                                binding.txtMessageReport.text = "You may be infected with a virus"
                            }else{
                                binding.txtMainReport.text = "CLEAR"
                                binding.bgReportColor.backgroundTintList = getColorStateList(requireContext(),R.color.green)
                                binding.txtMessageReport.text = "* Wear mask. Keep 2m distance. Wash hands."
                            }
                            binding.txtMonthDay.text = SimpleDateFormat("MM/dd").format(finalData.date)
                            binding.txtYearHour.text = SimpleDateFormat("/yyyy dd:mmaa").format(finalData.date)
                        } else {
                            binding.bgNoReport.visibility = View.VISIBLE
                        }
                    }
                }
            })
    }

    private fun actualDate() {
        val date = SimpleDateFormat("MMM dd, yyyy").format(Date())
        binding.txtActualDate.text = date
        binding.txtActualDate1.text = date
    }

    private fun obtainCases() {
        Constants.okHttp.newCall(Rest().get("cases")).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("onFailure: ", e.message.toString())
            }

            override fun onResponse(call: Call, response: Response) {
                val json = JSONTokener(response.body!!.string()).nextValue() as JSONObject
                requireActivity().runOnUiThread {
                    val data = json.getInt("data")
                    if (data > 0) {
                        binding.bgCases.backgroundTintList =
                            getColorStateList(requireContext(), R.color.teal_200)
                        binding.bgCases1.backgroundTintList =
                            getColorStateList(requireContext(), R.color.teal_200)
                        binding.txtCases.text = "$data cases"
                        binding.txtCases1.text = "$data cases"
                    } else {
                        binding.txtCases.text = "No case"
                        binding.txtCases1.text = "No case"
                    }
                }
            }
        })
    }
}