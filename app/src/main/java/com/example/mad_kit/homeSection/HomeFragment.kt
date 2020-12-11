package com.example.mad_kit.homeSection

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mad_kit.R
import com.example.mad_kit.addPersonSection.Person
import com.example.mad_kit.service.homeApiService
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_home.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


sealed class ContactItem {
    data class PersonItem(val person: Person): ContactItem()
    data class PriorityName(val priority: Int): ContactItem()
}

class HomeFragment : Fragment() {

    private val _response = MutableLiveData<List<ContactItem>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _response.value = listOf()
        retainInstance = true
        val mUser = FirebaseAuth.getInstance().currentUser
        mUser?.getIdToken(true)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val idToken = task.result!!.token
                    Log.println(Log.ERROR,"kek", "LOOOOOOOL")
                    uploadContacts(idToken)
                }
            }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_home, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        contacts_list?.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = HomeRecyclerViewAdapter(_response.value!!)
        }
    }

    override fun onResume() {
        super.onResume()
        val mUser = FirebaseAuth.getInstance().currentUser
        mUser?.getIdToken(true)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val idToken = task.result!!.token
                    uploadContacts(idToken)
                }
            }
    }

    private fun uploadContacts(token: String?) {
        homeApiService.getContacts("Bearer $token").enqueue(object: Callback<List<Person>> {
            override fun onFailure(call: Call<List<Person>>, t: Throwable) {
                println("Failure: " + t.message)
            }

            override fun onResponse(call: Call<List<Person>>, response: Response<List<Person>>) {
                Log.println(Log.ERROR,"kek", "LOOOOOOOL")
                Log.println(Log.ERROR,"kek", "LOOOOOOOL")
                Log.println(Log.ERROR,"kek", "LOOOOOOOL")
                Log.println(Log.ERROR,"kek", response.toString())
                _response.value = response.body()
                    ?.groupBy { it.priority }
                    ?.flatMap { (category, videos) -> listOf<ContactItem>(ContactItem.PriorityName(category)) + videos.map { ContactItem.PersonItem(it) } }
                contacts_list?.apply {
                    layoutManager = LinearLayoutManager(activity)
                    if (_response.value != null){
                        adapter = HomeRecyclerViewAdapter(_response.value!!)
                    }

                }
            }
        })
    }

}