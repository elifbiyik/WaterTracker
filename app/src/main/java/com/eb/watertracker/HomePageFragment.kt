package com.eb.watertracker

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
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
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
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
        var count = 0

        viewModel.newDay(requireContext())

        var sharedPreferencesUser = requireActivity().getSharedPreferences(
            "userData", AppCompatActivity.MODE_PRIVATE
        )
        var sharedPreferencesLogin = requireActivity().getSharedPreferences(
            "user", AppCompatActivity.MODE_PRIVATE
        )
        var userLastGoal = sharedPreferencesUser.getString("lastGoal", "")
        var userCount = sharedPreferencesUser.getString("count", "")

        var userName = sharedPreferencesLogin.getString("name", "")
        var userBegin = sharedPreferencesLogin.getString("begin", "")
        var userFinish = sharedPreferencesLogin.getString("finish", "")
        var userGoal = sharedPreferencesLogin.getString("goal", "")

        if (userLastGoal == "") {
            binding.tvGoal.text = userGoal
        } else {
            binding.tvGoal.text = userLastGoal
        }

        if(userBegin != "") {
            viewModel.startReminder(requireContext(), userBegin.toString())
            viewModel.lastReminder(requireContext(), userFinish.toString())
        }

        viewModel.userMutableLiveData.observe(viewLifecycleOwner, Observer {
            userCount = sharedPreferencesUser.getString("count", "").toString()
            userLastGoal = sharedPreferencesUser.getString("lastGoal", "").toString()
        })

        viewModel.loginMutableLiveData.observe(viewLifecycleOwner, Observer {
            userName = sharedPreferencesLogin.getString("name", "").toString()
            userBegin = sharedPreferencesLogin.getString("begin", "").toString()
            userFinish = sharedPreferencesLogin.getString("finish", "").toString()
            userGoal = sharedPreferencesLogin.getString("goal", "").toString()
        })

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

                        if (userCount == "") {
                            binding.tvGoal.text = goal
                        } else if (userCount!!.isNotEmpty()) {
                            if (!userLastGoal.isNullOrEmpty()) {
                                var countUser = userCount!!.toInt()
                                var x = goal.toString().toInt().plus(countUser).toString()
                                binding.tvGoal.text = x

                            } else {
                                binding.tvGoal.text = goal
                            }
                        } else {
                            var countUser = userCount!!.toInt()
                            var x = goal.toString().toInt().plus(countUser).toString()
                            binding.tvGoal.text = x
                        }
// sharedPreference'ı burda yaptık çünkü ; tv'ye hep goal yazılıyordu. bu sayede lastGoal yazılıyor.
                    }

                    override fun onNothingSelected(p0: AdapterView<*>?) {
                        Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        if (userName != "") {
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
        } else {
            binding.btnCheck.setImageResource(R.drawable.twotone_check_24)
            itemsStatus(true)

        }

        var isChecked = true
        binding.btnCheck.setOnClickListener {
    //        var name = binding.editText.text.toString()
            isChecked = !isChecked
            if (isChecked) {
                if (begin != "Choose" && finish != "Choose" && goal != "Choose" && userName != "") {
                    Log.d("name", userName.toString())
                    if (begin!! < finish!!) {
                        saveInfo(begin!!, finish!!, goal!!, userName.toString())
                        Toast.makeText(context, "Saved !", Toast.LENGTH_SHORT).show()
                        binding.btnCheck.setImageResource(R.drawable.baseline_edit_24)
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "The start time of the day should be less than the end time",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        context,
                        "You must choose in the required fields or fill name !",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                if (begin != "Choose" && finish != "Choose" && goal != "Choose" && userName != "") {
                    if (begin!! < finish!!) {
                        editInfo(begin!!, finish!!, goal!!, userName.toString())
                        Toast.makeText(context, "Saved !", Toast.LENGTH_SHORT).show()
                        binding.btnCheck.setImageResource(R.drawable.twotone_check_24)
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "The start time of the day should be less than the end time",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        context,
                        "You must choose in the required fields or fill name !",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        binding.addWaterLevel.setOnClickListener {
            var goal_ = (binding.tvGoal.text as String?)?.toInt()
            if (goal_ == 0) {
                Toast.makeText(context, "You done !", Toast.LENGTH_SHORT).show()
                var addGlass = goal_ + 200
                goal_ = addGlass
                binding.tvGoal.text = "+$goal_"
                if (userCount != "") {
                    count = (userCount?.toInt() ?: 0) + 200
                } else {
                    count = 0
                }

                viewModel.sharedPreferenceForUser(
                    requireContext(),
                    "+$goal_",
                    userName.toString(),
                    count
                )
            } else if (binding.tvGoal.text.contains("+")) {
                var addGlass = goal_?.plus(200)
                goal_ = addGlass
                binding.tvGoal.text = "+$goal_"
                if (userCount != "") {
                    count = (userCount?.toInt() ?: 0) + 200
                } else {
                    count = 0
                }

                viewModel.sharedPreferenceForUser(
                    requireContext(),
                    "+$goal_",
                    userName.toString(),
                    count
                )
            } else {
                var lastGoal = goal_?.minus(200).toString()
                binding.tvGoal.text = lastGoal
                if (userCount != "") {
                    count = (userCount?.toInt() ?: 0) - 200
                } else {
                    count = 0
                }

                viewModel.sharedPreferenceForUser(
                    requireContext(),
                    lastGoal,
                    userName.toString(),
                    count
                )
                if(lastGoal == "0" ) {
                    Toast.makeText(context, "Done !!", Toast.LENGTH_SHORT).show()
                }else {
                    viewModel.timeNotification(
                        requireContext(),
                        userFinish.toString(),
                        lastGoal
                    )
                }
            }

        }

        binding.removeWaterLevel.setOnClickListener {
            var goal_ = (binding.tvGoal.text as String?)?.toInt()

            if (binding.tvGoal.text.contains("+") && binding.tvGoal.text != "+0") {
                var removeGlass = goal_?.minus(200)
                goal_ = removeGlass
                binding.tvGoal.text = "+$goal_"
                if (userCount != "") {
                    count = (userCount?.toInt() ?: 0) - 200
                } else {
                    count = 0
                }

                viewModel.sharedPreferenceForUser(
                    requireContext(),
                    "+$goal_",
                    userName.toString(),
                    count
                )
            } else if (binding.tvGoal.text == "+0") {
                binding.tvGoal.text = "0"
                var lastGoal = goal_?.plus(200).toString()
                goal_ = lastGoal.toInt()
                binding.tvGoal.text = goal_.toString()
                if (userCount != "") {
                    count = (userCount?.toInt() ?: 0) + 200
                } else {
                    count = 0
                }
                viewModel.sharedPreferenceForUser(
                    requireContext(),
                    lastGoal,
                    userName.toString(),
                    count
                )
            } else if (goal_ == goal?.toInt()) {
                Toast.makeText(context, "Okey", Toast.LENGTH_SHORT).show()
            } else {
                var lastGoal = goal_?.plus(200).toString()
                goal_ = lastGoal.toInt()
                binding.tvGoal.text = goal_.toString()
                if (userCount != "") {
                    count = (userCount?.toInt() ?: 0) + 200
                } else {
                    count = 0
                }

                viewModel.sharedPreferenceForUser(
                    requireContext(),
                    lastGoal,
                    userName.toString(),
                    count
                )
            }
        }
        return binding.root
    }

    @SuppressLint("ResourceAsColor")
    fun saveInfo(begin: String, finish: String, goal: String, name: String) {
        viewModel.sharedPreferenceLogin(requireContext(), begin, finish, goal, name)
        itemsStatus(false)
    }

    @SuppressLint("ResourceAsColor")
    fun editInfo(begin: String, finish: String, goal: String, name: String) {
        viewModel.sharedPreferenceLogin(requireContext(), begin, finish, goal, name)
        itemsStatus(true)
    }

    fun itemsStatus(status: Boolean) {
        binding.editText.isEnabled = status
        binding.spinnerBegin.isEnabled = status
        binding.spinnerFinish.isEnabled = status
        binding.spinnerGoal.isEnabled = status

        binding.addWaterLevel.isEnabled = !status
        binding.removeWaterLevel.isEnabled = !status
        if (status == false) {
            binding.addWaterLevel.setBackgroundResource(R.color.lightBlue)
            binding.removeWaterLevel.setBackgroundResource(R.color.lightBlue)
        } else {
            binding.addWaterLevel.setBackgroundResource(R.color.grey)
            binding.removeWaterLevel.setBackgroundResource(R.color.grey)
        }
    }
}

