/*
package com.eb.watertracker

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.eb.watertracker.databinding.FragmentLoginBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private val viewModel: HomePageViewModel by viewModels()
    private lateinit var sharedPreferences: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentLoginBinding.inflate(inflater, container, false)

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
                        Toast.makeText(requireContext(), hours[p2], Toast.LENGTH_SHORT).show()
                        binding.tvBegin.text = hours[p2]
                        begin = hours[p2].toString()
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
                        Toast.makeText(requireContext(), hours[p2], Toast.LENGTH_SHORT).show()
                        binding.tvFinish.text = hours[p2]
                        finish = hours[p2].toString()
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
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        Toast.makeText(requireContext(), hours[position], Toast.LENGTH_SHORT).show()
                        binding.tvGoal.text = goals[position]
                        goal = goals[position]
                    }

                    override fun onNothingSelected(p0: AdapterView<*>?) {
                        Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        */
/*  val selectedValue = binding.spinnerGoal.selectedItem as? String
          binding.tvGoal.text = selectedValue

*//*


        var name = binding.editText.text.toString()


        with(binding) {

            binding.btnNext.setOnClickListener {

                var fragment = HomePageFragment()
                var bundle = Bundle()
                bundle.putString("name", name)
                bundle.putString("goal", goal)
                fragment.arguments = bundle

                Log.d("name", name)
                Log.d("begin", begin.toString())
                Log.d("finish1", finish.toString())
                Log.d("finish2", tvFinish.text.toString())
                Log.d("goal", binding.tvGoal.text.toString())
                if (begin != null && finish != null && goal != null) {
                    if (begin!! < finish!!) {
                        lifecycleScope.launch(Dispatchers.Main) {
                            var userData = UserData(
                                name = name,
                                timeBegin = begin.toString(),
                                timefinish = finish.toString(),
                                goal = goal.toString()
                            )
                            viewModel.insert(userData)

                            requireActivity().supportFragmentManager.beginTransaction()
                                .replace(R.id.constraint, fragment)
                                .addToBackStack(null)
                                .commit()
                        }
                    } else {
                        Toast.makeText(requireContext(), "Null", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Null", Toast.LENGTH_SHORT).show()
                }
            }
        }


        userInfo(name, begin.toString(), finish.toString(), goal!!.toInt())

        return binding.root
    }

    fun userInfo(name: String, timeBegin: String, timeFinish: String, goal: Int) {
        if (name != "") {
            var sharedPreferences = requireActivity().getSharedPreferences("user", MODE_PRIVATE)
            var editor =
                sharedPreferences.edit()           //Veri ekleme, kaydetme işlemleri yapmak için oluşturduk
            editor.putString("name", name)
            editor.putString("timeBegin", timeBegin)
            editor.putString("timeFinish", timeFinish)
            editor.putInt("goal", goal)
            editor.apply()

            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.constraint, HomePageFragment())
                .addToBackStack(null)
                .commit()
        }
    }
}


*/
