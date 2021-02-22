package com.example.taskmanager_2

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.taskmanager_2.data.model.Type
import com.example.taskmanager_2.ui.main.viewmodel.MainViewModel
import com.example.taskmanager_2.utils.Status


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

    private lateinit var spinnerTeamID: Spinner
    private lateinit var spinnerTask: Spinner
    private lateinit var spinnerStatus: Spinner
    private lateinit var spinnerSort: Spinner

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

        val token: SharedPreferences = requireActivity().baseContext.getSharedPreferences("User", Context.MODE_PRIVATE)

        val view = inflater.inflate(R.layout.fragment_home, container, false)
        val textViewUsername = view.findViewById<TextView>(R.id.textViewUsername)

        textViewUsername.text = token.getString("Name","NAME")
        val teamsIDs = token.getStringSet("TeamIDs", mutableSetOf<String>())

        spinnerStatus = view.findViewById(R.id.spinnerStatus)
        spinnerSort = view.findViewById(R.id.spinnerSort)
        val buttonAddTask: Button = view.findViewById(R.id.buttonAddTask)
        spinnerTeamID = view.findViewById(R.id.spinnerTeamID)
        spinnerTask = view.findViewById(R.id.spinnerTask)

        val teamNames = mutableListOf<String>()
        teamsIDs?.forEach {
            teamNames.add(it.substring(0,it.indexOf("(")))
        }
        setDynamicArrayAdapter(spinnerTeamID!!, teamNames!!)
        setStaticArrayAdapter(spinnerStatus!!, R.array.TaskStatus)
        setStaticArrayAdapter(spinnerSort!!, R.array.SortOptions)

        buttonAddTask.setOnClickListener(){
            homeFragmentCallback.goToNewTaskActivity(spinnerTeamID.selectedItem.toString())
        }



        return view
    }

    interface HomeFragmentCallback{
        fun getSpinners(teamID: Long, taskID: Long, statusID: Long, sortID: Long)
        fun  goToNewTaskActivity(teamName: String)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        homeFragmentCallback = context as HomeFragmentCallback
    }

    private fun setDynamicArrayAdapter(spinner: Spinner, stringSet: List<String>) {
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
        Toast.makeText(requireActivity().baseContext, "No item selected", Toast.LENGTH_SHORT).show()
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {



        when(parent?.id){
            spinnerSort.id -> {
                //Sorting algo
                /*when(position){
                    0 -> {
                        getSortedTasks("priority", spinnerTeamID.selectedItem.toString())
                    }
                    1 -> {
                        getSortedTasks("planneddate", spinnerTeamID.selectedItem.toString())
                    }
                }*/

            }
            spinnerTeamID.id -> {
                getTeamDetails(position)
            }
        }
        homeFragmentCallback.getSpinners(spinnerTeamID.selectedItemId, (spinnerTask.selectedItemId-1), spinnerStatus.selectedItemId, spinnerSort.selectedItemId)

    }

    /*private fun getSortedTasks(s: String, selectedItem: String) {
        val token = requireActivity().baseContext.getSharedPreferences("User", Context.MODE_PRIVATE)

        val type = Type()
        type.type = s

        var id: String = "0"

        var teamIDs = token.getStringSet("teamIDs", mutableSetOf())
        teamIDs?.forEach lit@{
            if(selectedItem.equals(it.substring(0, it.indexOf("(")))){
                id = it.substring(it.indexOf("(")+1, it.indexOf(")"))
                return@lit
            }
        }

        var viewModel = MainViewModel()
        viewModel.getSortedTasks(type, id, token).observe(this, Observer { networkResource ->
            when (networkResource.status) {
                Status.LOADING -> {
                }
                Status.SUCCESS -> {
                    val message = networkResource.data
                    message?.let {
                        val teamMembersIDs = token.getStringSet("TeamMembers", mutableSetOf<String>())
                        var teamMembersName = mutableListOf<String>()
                        teamMembersIDs?.forEach{
                            teamMembersName.add(it.substring(0, it.indexOf("(")))
                        }
                        teamMembersName.add(0, "All")
                        setDynamicArrayAdapter(spinnerTask, teamMembersName!!)
                    }
                }
                Status.ERROR -> {
                    Toast.makeText(requireActivity().baseContext, "Could not fetch team details from network", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }*/

    private fun getTeamDetails(position: Int) {
        val token: SharedPreferences = requireActivity().baseContext.getSharedPreferences("User", Context.MODE_PRIVATE)

        val TeamIDs = token.getStringSet("TeamIDs", mutableSetOf<String>())
        var viewModel = MainViewModel()
        val element = TeamIDs?.elementAt(position).toString()
        val teamID = element.substring(element.indexOf("(")+1, element.indexOf(")"))
        viewModel.getTeamDetails(teamID, token).observe(this, Observer { networkResource ->
            when (networkResource.status) {
                Status.LOADING -> {
                }
                Status.SUCCESS -> {
                    val message = networkResource.data
                    message?.let {
                        val teamMembersIDs = token.getStringSet("TeamMembers", mutableSetOf<String>())
                        var teamMembersName = mutableListOf<String>()
                        teamMembersIDs?.forEach{
                            teamMembersName.add(it.substring(0, it.indexOf("(")))
                        }
                        teamMembersName.add(0, "All")
                        setDynamicArrayAdapter(spinnerTask, teamMembersName!!)
                    }
                }
                Status.ERROR -> {
                    Toast.makeText(requireActivity().baseContext, "Could not fetch team details from network", Toast.LENGTH_SHORT).show()
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