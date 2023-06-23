package com.example.musicstats.fragments.spotify_fragments

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.musicstats.MainActivity
import com.example.musicstats.R
import com.example.musicstats.datamodels.AccessToken
import com.example.musicstats.fragments.SpotifyFragment
import com.example.musicstats.network.SpotifyAuthApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class FragmentAuthorize : Fragment() {

    private var BASE_URL = "https://accounts.spotify.com/"
    private var clientId = "d776bb40f3ef46acbb287a8b994232db"
    private var redirectUri = "musicstats://callback"

    private fun loadFragment(fragment: Fragment) {
        val transaction = activity?.supportFragmentManager?.beginTransaction()
        transaction?.replace(R.id.container, fragment)
        transaction?.commit()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_authorize, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val button: Button? = activity?.findViewById(R.id.authorizeButtonFragment)

        Log.d("AUTHORIZE", button?.text.toString())

        var sharedPref = activity?.applicationContext?.getSharedPreferences("tokenSharedPreferences", Context.MODE_PRIVATE)
        var accessToken = sharedPref?.getString("accessToken", null)

        if(accessToken != null){
            button?.isEnabled = false
            loadFragment(SpotifyFragment())
        }


        button?.setOnClickListener {
            button.isEnabled = false

            val scope = "user-top-read user-library-read"
            val intent = Intent(
                Intent.ACTION_VIEW, Uri.parse(
                    "https://accounts.spotify.com/authorize" +
                            "?client_id=" + clientId +
                            "&response_type=code" +
                            "&redirect_uri=" + redirectUri +
                            "&scope="+scope
                )
            )

            startActivity(intent)
        }

    }

    override fun onResume() {
        super.onResume()

        var sharedPref = activity?.applicationContext?.getSharedPreferences("tokenSharedPreferences", Context.MODE_PRIVATE)
        var accessToken = sharedPref?.getString("accessToken", null)

        if(accessToken != null){
            loadFragment(SpotifyFragment())
        }

        val uri = activity?.intent?.data

        if (uri != null) {
            activity?.findViewById<Button>(R.id.authorizeButtonFragment)?.isEnabled = false
            val code: String? = uri.getQueryParameter("code")
            Log.d("API Response", "code: ${code}, redirect uri: $redirectUri")

            val builder = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())

            val retrofit = builder.build()
            val spotifyAuthApi = retrofit.create(SpotifyAuthApi::class.java)

            if (code != null) {
                val accessTokenCall: Call<AccessToken> = spotifyAuthApi.getAccessToken(
                    grantType = "authorization_code",
                    code = code,
                    redirectUri = redirectUri,
                    scope = "user-top-read user-library-read"
                )

                accessTokenCall.enqueue(object : Callback<AccessToken> {
                    override fun onResponse(
                        call: Call<AccessToken>,
                        response: Response<AccessToken>
                    ) {

                        val accessToken = response.body()?.accessToken
                        val tokenType = response.body()?.tokenType
                        val expiresIn = response.body()?.expiresIn
                        val refreshToken = response.body()?.refreshToken
                        val scope = response.body()?.scope

                        val currentTime = System.currentTimeMillis() / 1000
                        //Log.d("API Response", "TIME: ${currentTime}")
                        Log.d("API Response", "Successful: ${response.code()} - $expiresIn")

                        //write the response in shared preferences
                        val sharedPref = activity?.applicationContext?.getSharedPreferences(
                            "tokenSharedPreferences",
                            Context.MODE_PRIVATE
                        )
                        val editor = sharedPref?.edit()

                        editor?.putString("accessToken", accessToken)
                        editor?.putString("tokenType", tokenType)
                        if (expiresIn != null)
                            editor?.putInt("expiresIn", expiresIn)

                        editor?.putString("scope", scope)
                        Log.d("AUTH", "Get scope is $scope")
                        Log.d("AUTH", "Get accessToken is $accessToken")
                        editor?.putString("refreshToken", refreshToken)
                        editor?.putLong("timestamp", currentTime)
                        //save the changes
                        editor?.apply()

                        Toast.makeText(
                            activity,
                            "Successfully connected to Spotify",
                            Toast.LENGTH_SHORT
                        )
                            .show()

                        val intent = Intent(requireActivity(), MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        requireActivity().finish()
                    }

                    override fun onFailure(call: Call<AccessToken>, t: Throwable) {
                        Log.d("API Response", t.toString())
                        Toast.makeText(activity, "FAILURE", Toast.LENGTH_SHORT)
                            .show()
                    }
                })

            }
        }
    }
}