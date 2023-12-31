package com.capstoneproject.purrsonalcatapp.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.capstoneproject.purrsonalcatapp.R
import com.capstoneproject.purrsonalcatapp.data.local.pref.AuthPreferences
import com.capstoneproject.purrsonalcatapp.data.local.pref.dataStore
import com.capstoneproject.purrsonalcatapp.data.remote.retrofit.ApiConfig
import com.capstoneproject.purrsonalcatapp.data.repository.AuthRepository
import com.capstoneproject.purrsonalcatapp.databinding.FragmentHomeBinding
import com.capstoneproject.purrsonalcatapp.ui.AuthViewModelFactory
import com.capstoneproject.purrsonalcatapp.ui.detail.Artikel
import com.capstoneproject.purrsonalcatapp.ui.detail.DetailActivity
import com.capstoneproject.purrsonalcatapp.ui.detail.ListArtikelAdapter
import com.capstoneproject.purrsonalcatapp.ui.login.LoginActivity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var authPreferences: AuthPreferences
    private lateinit var viewModel: HomeViewModel
    private lateinit var rvArtikel: RecyclerView
    private val list = ArrayList<Artikel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
//        return inflater.inflate(R.layout.fragment_home, container, false)
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        authPreferences = AuthPreferences(requireContext().dataStore)
        return binding.root


    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvArtikel = view.findViewById<View>(R.id.rv_artikel) as RecyclerView
        rvArtikel.setHasFixedSize(true)
        list.addAll(getListHeroes())
        showRecyclerList()


        lifecycleScope.launch {
            val token = authPreferences.authToken.first()
            val apiService = ApiConfig.getApiService(token)
            val repository = AuthRepository(apiService, authPreferences)



            viewModel = ViewModelProvider(this@HomeFragment, AuthViewModelFactory(repository))
                .get(HomeViewModel::class.java)

            viewModel.userData.collect {userResponse ->
                if (userResponse != null) {
                    val username = userResponse.data?.username ?: "Guest"
                    binding.tvHomeUsername.text = "Hi, $username"
                }
            }
        }

        val authTokenFlow = authPreferences.authToken
        val authTokenJob = lifecycleScope.launch {
            authTokenFlow.collect {authToken ->
                if (authToken == null) {
                    val intent = Intent(requireContext(), LoginActivity::class.java)
                    startActivity(intent)
                    requireActivity().finish()
                }
            }
        }

        binding.btnLogoutHome.setOnClickListener {
            authTokenJob.cancel()
            lifecycleScope.launch {
                authPreferences.clearSession()
                val intent = Intent(requireContext(), LoginActivity::class.java)
                startActivity(intent)
                requireActivity().finishAffinity()
            }
        }
    }
    private fun getListHeroes(): ArrayList<Artikel> {
        val dataName = resources.getStringArray(R.array.data_judul)
        val dataDescription = resources.getStringArray(R.array.data_description)
        val dataPhoto = resources.obtainTypedArray(R.array.data_photo)
        val listHero = ArrayList<Artikel>()
        for (i in dataName.indices) {
            val hero = Artikel(dataName[i], dataDescription[i], dataPhoto.getResourceId(i, -1))
            listHero.add(hero)
        }
        return listHero
    }

    private fun showRecyclerList() {
        val layoutManager = LinearLayoutManager(requireContext())
        binding.rvArtikel.layoutManager = layoutManager
        val listTourAdapter = ListArtikelAdapter(list) { artikel ->
            val moveIntentDetail = Intent(requireContext(), DetailActivity:: class.java)
            moveIntentDetail.putExtra(DetailActivity.EXTRA_ARTIKEL, artikel)
            startActivity(moveIntentDetail)
        }
        binding.rvArtikel.adapter = listTourAdapter
    }


}