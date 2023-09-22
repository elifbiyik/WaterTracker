package com.eb.watertracker

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import com.eb.watertracker.databinding.FragmentHomePageBinding

class HomePageFragment : Fragment() {

    private lateinit var binding: FragmentHomePageBinding
    private val viewModel: HomePageViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}
    }

    @SuppressLint("ResourceAsColor")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        binding = FragmentHomePageBinding.inflate(inflater, container, false)

        var begin: String? = null
        var finish: String? = null
        var goal: String? = null


        val hours = resources.getStringArray(R.array.Hours)
        if (binding.spinnerBegin != null) { // başlangıç değerini kontrol ediyor
            val adapter =
                ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, hours)
            binding.spinnerBegin.adapter = adapter

            binding.spinnerBegin.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    // p1 = view
                    // p2 = position
                    // p3 = id
                    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                        begin = hours[p2].toString()
                        binding.tvBegin.text = begin
                    }

                    override fun onNothingSelected(p0: AdapterView<*>?) {
                        Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        if (binding.spinnerFinish != null) {
            val adapter =
                ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, hours)
            binding.spinnerFinish.adapter = adapter

            binding.spinnerFinish.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                        finish = hours[p2].toString()
                        binding.tvFinish.text = finish
                    }

                    override fun onNothingSelected(p0: AdapterView<*>?) {
                        Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        val goals = resources.getStringArray(R.array.Goal)
        if (binding.spinnerGoal != null) {
            val adapter =
                ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, goals)
            binding.spinnerGoal.adapter = adapter

            binding.spinnerGoal.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?, view: View?, position: Int, id: Long
                    ) {

                        goal = goals[position]

                        var sharedPreferencesUser = requireActivity().getSharedPreferences(
                            "userData", AppCompatActivity.MODE_PRIVATE
                        )
                        val userLastGoal = sharedPreferencesUser.getString("lastGoal", "")
                        if(!userLastGoal.isNullOrEmpty()) {
                            binding.tvGoal.text = userLastGoal
                        } else {
                            binding.tvGoal.text = goal
                        }
// sharedPreference'ı burda yaptık çünkü ; tv'ye hep goal yazılıyordu. bu sayede lastGoal yazılıyor.
                    }

                    override fun onNothingSelected(p0: AdapterView<*>?) {
                        Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        var sharedPreferencesLogin = requireActivity().getSharedPreferences(
            "user", AppCompatActivity.MODE_PRIVATE
        )


        val userName = sharedPreferencesLogin.getString("name", "")
        val userBegin = sharedPreferencesLogin.getString("begin", "")
        val userFinish = sharedPreferencesLogin.getString("finish", "")
        val userGoal = sharedPreferencesLogin.getString("goal", "")






        if (!userName.isNullOrEmpty()) {
            viewModel.startReminder(requireContext(), userBegin.toString())
            viewModel.lastReminder(requireContext(), userFinish.toString())
            viewModel.newDay(requireContext(), goal = userGoal.toString(), tvGoal = binding.tvGoal)

            val beginPosition = hours.indexOf(userBegin)
            binding.spinnerBegin.setSelection(beginPosition)

            val finishPosition = hours.indexOf(userFinish)
            binding.spinnerFinish.setSelection(finishPosition)

            val goalPosition = goals.indexOf(userGoal)
            binding.spinnerGoal.setSelection(goalPosition)

            binding.editText.setText(userName)
            binding.tvBegin.text = userBegin
            binding.tvFinish.text = userFinish
            binding.btnCheck.setImageResource(R.drawable.baseline_edit_24)

            itemsStatus(false)
            binding.addWaterLevel.isEnabled = true
            binding.removeWaterLevel.isEnabled = true
        } else {
            binding.btnCheck.setImageResource(R.drawable.twotone_check_24)
            itemsStatus(true)
            binding.addWaterLevel.isEnabled = false
            binding.removeWaterLevel.isEnabled = false
        }

        var isChecked = true
        binding.btnCheck.setOnClickListener {
            var name = binding.editText.text.toString()
            isChecked = !isChecked
            if (isChecked) {
                if (begin != "Choose" && finish != "Choose" && goal != "Choose") {
                    saveInfo(begin!!, finish!!, goal!!, name)
                    Toast.makeText(context, "Saved !", Toast.LENGTH_SHORT).show()
                    binding.btnCheck.setImageResource(R.drawable.baseline_edit_24)
                } else {
                    Toast.makeText(context, "You must choose in the required fields or fill name !", Toast.LENGTH_SHORT).show()
                }
            } else {
                if (begin != "Choose" && finish != "Choose" && goal != "Choose") {
                    editInfo(begin!!, finish!!, goal!!, name)
                    Toast.makeText(context, "Saved !", Toast.LENGTH_SHORT).show()
                    binding.btnCheck.setImageResource(R.drawable.twotone_check_24)
                } else {
                    Toast.makeText(context, "You must choose in the required fields or fill name !", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.addWaterLevel.setOnClickListener {
            viewModel.createNotification(requireContext())
            viewModel.notification(requireContext())

            var goal_ = (binding.tvGoal.text as String?)?.toInt()
            if (goal_ == 0) {
                Toast.makeText(context, "You done !", Toast.LENGTH_SHORT).show()
                var addGlass = goal_ + 200
                goal_ = addGlass
                binding.tvGoal.text = "+$goal_"

                sharedPreferenceForUser("+$goal_", userName.toString())
            } else if (binding.tvGoal.text.contains("+")) {
                var addGlass = goal_?.plus(200)
                goal_ = addGlass
                binding.tvGoal.text = "+$goal_"

                sharedPreferenceForUser("+$goal_", userName.toString())
            } else {
                var lastGoal = goal_?.minus(200).toString()
                goal_ = lastGoal.toInt()
                binding.tvGoal.text = goal_.toString()

                sharedPreferenceForUser(lastGoal, userName.toString())
            }
        }

        binding.removeWaterLevel.setOnClickListener {
            var goal_ = (binding.tvGoal.text as String?)?.toInt()

            if (binding.tvGoal.text.contains("+") && binding.tvGoal.text != "+0") {
                var removeGlass = goal_?.minus(200)
                goal_ = removeGlass
                binding.tvGoal.text = "+$goal_"

                sharedPreferenceForUser("+$goal_", userName.toString())
            } else if (binding.tvGoal.text == "+0") {
                binding.tvGoal.text = "0"
                var lastGoal = goal_?.plus(200).toString()
                goal_ = lastGoal.toInt()
                binding.tvGoal.text = goal_.toString()

                sharedPreferenceForUser(lastGoal, userName.toString())
            } else if (goal_ == goal?.toInt()) {
                Toast.makeText(context, "Okey", Toast.LENGTH_SHORT).show()
            } else {
                var lastGoal = goal_?.plus(200).toString()
                goal_ = lastGoal.toInt()
                binding.tvGoal.text = goal_.toString()

                sharedPreferenceForUser(lastGoal, userName.toString())
            }
        }
        return binding.root
    }

    fun sharedPreferenceLogin(begin: String, finish: String, goal: String, name: String) {

        var sharedPreferences = requireActivity().getSharedPreferences(
            "user", AppCompatActivity.MODE_PRIVATE
        )
        if (name != "") {
            var editor = sharedPreferences.edit()
            editor.putString("name", name)
            editor.putString("begin", begin)
            editor.putString("finish", finish)
            editor.putString("goal", goal)
            editor.putString("goal", goal)
            editor.apply()
        }
    }

    fun sharedPreferenceForUser(lastGoal: String, name: String) {

        var sharedPreferences = requireActivity().getSharedPreferences(
            "userData", AppCompatActivity.MODE_PRIVATE
        )
        if (name != "") {
            var editor = sharedPreferences.edit()
            editor.putString("name", name)
            editor.putString("lastGoal", lastGoal)
            editor.apply()
        }
    }

    fun saveInfo(begin: String, finish: String, goal: String, name: String) {

        if (begin < finish) {
            sharedPreferenceLogin(begin, finish, goal, name)
            itemsStatus(false)
            binding.addWaterLevel.isEnabled = true
            binding.removeWaterLevel.isEnabled = true
        } else {
            Toast.makeText(requireContext(), "Null", Toast.LENGTH_SHORT).show()
        }
    }

    fun editInfo(begin: String, finish: String, goal: String, name: String) {

        if (begin < finish) {
            sharedPreferenceLogin(begin, finish, goal, name)

            itemsStatus(true)
            binding.addWaterLevel.isEnabled = false
            binding.removeWaterLevel.isEnabled = false

        } else {
            itemsStatus(true)
            binding.addWaterLevel.isEnabled = false
            binding.removeWaterLevel.isEnabled = false
        }
    }

    fun itemsStatus(status: Boolean) {
        binding.editText.isEnabled = status
        binding.spinnerBegin.isEnabled = status
        binding.spinnerFinish.isEnabled = status
        binding.spinnerGoal.isEnabled = status
    }
}

