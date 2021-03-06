package com.example.taskmanager_2

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import com.example.taskmanager_2.ui.main.viewmodel.MainViewModel
import com.example.taskmanager_2.utils.Status

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var viewModel = MainViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        activity?.title = "My Profile"

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        val token = requireActivity().baseContext.getSharedPreferences("User", Context.MODE_PRIVATE)

        var textViewUserName = view.findViewById<TextView>(R.id.textViewUserName)
        var textViewUserEmail = view.findViewById<TextView>(R.id.textViewUserEmail)
        var textViewUserID = view.findViewById<TextView>(R.id.textViewUserID)
        var textViewUserPhone = view.findViewById<TextView>(R.id.textViewUserPhone)

        if((!token.getString("isLoggedIn", "").equals("")) and (!token.getString("isProfileSet", "").equals(""))){
            textViewUserName.text = token.getString("Name", "<NAME>")
            textViewUserEmail.text = token.getString("Email", "<EMAIL>")
            textViewUserID.text = token.getString("UserID", "<ID>")
            textViewUserPhone.text = token.getString("Phone", "<PHONE_NUMER>")
        }else {

            viewModel.getUser(token.getString("userID", token.getString("UserID", "0")), token)
                .observe(viewLifecycleOwner, Observer { networkResource ->
                    when (networkResource.status) {
                        Status.LOADING -> {
                        }
                        Status.SUCCESS -> {
                            val message = networkResource.data
                            message?.let {
                                textViewUserName.text = token.getString("Name", "<NAME>")
                                textViewUserEmail.text = token.getString("Email", "<EMAIL>")
                                textViewUserID.text = token.getString("UserID", "<ID>")
                                textViewUserPhone.text = token.getString("Phone", "<PHONE_NUMER>")
                            }
                        }
                        Status.ERROR -> {
                            Toast.makeText(
                                requireActivity().baseContext,
                                "Could not load user details",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                })
        }

        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProfileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}