package com.example.taskmanager_2

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.taskmanager_2.ui.main.adapter.MainAdapter
import com.example.taskmanager_2.ui.main.viewmodel.MainViewModel
import com.example.taskmanager_2.utils.Status
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment(), AdapterView.OnItemSelectedListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var homeFragmentCallback: HomeFragmentCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_home, container, false)
        val token: SharedPreferences = requireActivity().baseContext.getSharedPreferences("User", Context.MODE_PRIVATE)
        val textViewUsername = view.findViewById<TextView>(R.id.textViewUsername)

        textViewUsername.text = token.getString("Name","NAME")
        val teamsIDs = token.getStringSet("TeamIDs", mutableSetOf<String>())

        val spinnerStatus: Spinner = view.findViewById(R.id.spinnerStatus)
        val spinnerSort: Spinner = view.findViewById(R.id.spinnerSort)
        val buttonAddTask: Button = view.findViewById(R.id.buttonAddTask)
        val spinnerTeamID: Spinner = view.findViewById(R.id.spinnerTeamID)

        setDynamicArrayAdapter(spinnerTeamID!!, teamsIDs!!)
        setStaticArrayAdapter(spinnerStatus!!, R.array.TaskStatus)
        setStaticArrayAdapter(spinnerSort!!, R.array.SortOptions)

        buttonAddTask.setOnClickListener(){
            Toast.makeText(requireActivity().baseContext, "Add Task Activity", Toast.LENGTH_SHORT).show()
        }

        return view
    }

    interface HomeFragmentCallback{
        fun getSpinners(spinner: Spinner, spinnerID: Int, position: Int)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        homeFragmentCallback = context as HomeFragmentCallback
    }

    private fun setDynamicArrayAdapter(spinner: Spinner, stringSet: Set<String>) {
        val arrayAdapter = ArrayAdapter(requireActivity().baseContext, android.R.layout.simple_spinner_item, stringSet!!.toList())
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = arrayAdapter
        spinner.onItemSelectedListener = this
    }

    fun setStaticArrayAdapter(spinner: Spinner, textArrayResId: Int){
        ArrayAdapter.createFromResource(requireActivity().baseContext, textArrayResId, android.R.layout.simple_spinner_item)
            .also { arrayAdapter ->
                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinner.adapter = arrayAdapter
            }
        spinner.onItemSelectedListener = this
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        Toast.makeText(requireActivity().baseContext, "What happens here?", Toast.LENGTH_SHORT).show()
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when(parent?.id){
            spinnerSort.id -> {
                //Sorting algo
                Toast.makeText(requireActivity().baseContext, "sort: "+parent?.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show()
            }
            spinnerStatus.id -> {
                //which RV is shown
                Toast.makeText(requireActivity().baseContext, "Status: "+parent?.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show()
                homeFragmentCallback.getSpinners(spinnerStatus, 2, position)
                //populateRV(position)
            }
            spinnerTask.id -> {
                //whose task's are shown
                getTasks(position)
            }
            spinnerTeamID.id -> {
                getTeamDetails(position)
            }
        }
    }

    /*private fun populateRV(position: Int) {

    }*/

    private fun getTasks(position: Int) {
        val token = requireActivity().baseContext.getSharedPreferences("User", Context.MODE_PRIVATE)
        val TeamMembers = token.getStringSet("TeamMembers", mutableSetOf<String>())
        val element = TeamMembers?.elementAt(position).toString()
        val memberID = element.substring(element.indexOf("(")+1, element.indexOf(")"))


    }

    private fun getTeamDetails(position: Int) {
        val token = requireActivity().baseContext.getSharedPreferences("User", Context.MODE_PRIVATE)
        val TeamIDs = token.getStringSet("TeamIDs", mutableSetOf<String>())
        var viewModel = MainViewModel()
        val element = TeamIDs?.elementAt(position).toString()
        val teamID = element.substring(element.indexOf("(")+1, element.indexOf(")"))
        viewModel.getTeamDetails(teamID, token).observe(this, Observer { networkResource ->
            when (networkResource.status) {
                Status.LOADING -> {
                    Toast.makeText(requireActivity().baseContext, "loading data from network", Toast.LENGTH_SHORT).show()
                }
                Status.SUCCESS -> {
                    val message = networkResource.data
                    message?.let {
                        val teamMembers = token.getStringSet("TeamMembers", mutableSetOf<String>())
                        setDynamicArrayAdapter(spinnerTask, teamMembers!!)
                    }
                }
                Status.ERROR -> {
                    Toast.makeText(requireActivity().baseContext, "error loading data from network", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}