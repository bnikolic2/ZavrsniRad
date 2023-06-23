package com.example.musicstats.fragments

import android.content.Intent
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Bundle
import android.util.Base64
import android.util.Base64OutputStream
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.view.isVisible
import com.example.musicstats.R
import com.example.musicstats.SongFound
import com.example.musicstats.datamodels.Section
import com.example.musicstats.datamodels.ShazamDetectResponse
import com.example.musicstats.network.ShazamApi
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.ByteArrayOutputStream



class ShazamFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_shazam, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val button = view.findViewById<Button>(R.id.buttonListenToAudio)


        button.setOnClickListener{
            try {

                button.isEnabled = false
                view.findViewById<TextView>(R.id.infoAudioResult).text = "Recognizing..."

                val sampleRate = 44100
                val channelConfig = AudioFormat.CHANNEL_IN_MONO
                val audioFormat = AudioFormat.ENCODING_PCM_16BIT
                val bufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)
                val audioRecord = AudioRecord(
                    MediaRecorder.AudioSource.MIC,
                    sampleRate,
                    channelConfig,
                    audioFormat,
                    bufferSize
                )

                val buffer = ByteArray(bufferSize)
                val outputStream = ByteArrayOutputStream()
                var string = ""

                audioRecord.startRecording()

                val startTime = System.currentTimeMillis()

                while (System.currentTimeMillis() - startTime <  5000) {
                    val bytesRead = audioRecord.read(buffer, 0, bufferSize)
                    outputStream.write(buffer, 0, bytesRead)
                    string = Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)

                }

                audioRecord.stop()
                audioRecord.release()

                //try to recognize the audio
                callShazamDetect(string)
                // Log.d("RECORD", string)
                // Log.d("RECORD", Base64.encodeToString(finalByteArray, Base64.NO_WRAP)


            } catch (e: SecurityException){
                Log.d("RECORD", e.message.toString())
            }


        }
    }


    private fun callShazamDetect(encodedString: String){
            val shazamApi = Retrofit.Builder()
                .baseUrl("https://shazam.p.rapidapi.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ShazamApi::class.java)


            //System.out.println("OVO JE ARRAY: " + String(array))
            val detectResponse: Call<ShazamDetectResponse> = shazamApi.detectAudio(data = RequestBody.create(
                MediaType.parse("text/plain"), encodedString))

            detectResponse.enqueue(object: Callback<ShazamDetectResponse>{
                override fun onResponse(
                    call: Call<ShazamDetectResponse>,
                    response: Response<ShazamDetectResponse>
                ) {
                    Log.d("RECORD", "Success")
                    requireView().findViewById<Button>(R.id.buttonListenToAudio).isEnabled = true

                    val matchesSize = response.body()?.matches?.size
                    if(matchesSize != null && matchesSize > 0){
                        val title = response.body()?.track?.title
                        val artist = response.body()?.track?.subtitle
                        val imageUrl = response.body()?.track?.images?.coverart
                        var lyrics: String = ""
                        var url = response.body()?.track?.share?.href
                        val sections = response.body()?.sections

                        if(sections != null){
                            for(s in sections){
                                if(s.type == "LYRICS"){
                                    lyrics = s.text.joinToString("\n")
                                }
                            }
                        }

                        requireView().findViewById<TextView>(R.id.infoAudioResult).text = ""
                        Log.d("RECORD", title + artist)
                        Log.d("RECORD",  imageUrl.toString())
                        Log.d("RECORD", url.toString())

                        val intent = Intent(requireContext(), SongFound::class.java)
                        intent.putExtra("title", title)
                        intent.putExtra("artist", artist)
                        intent.putExtra("imageUrl", imageUrl)
                        intent.putExtra("url", url)
                        startActivity(intent)

                    }else{
                        requireView().findViewById<Button>(R.id.buttonListenToAudio).isEnabled = true
                        requireView().findViewById<TextView>(R.id.infoAudioResult).text = "Unable to recognize this song"
                    }



                }

                override fun onFailure(call: Call<ShazamDetectResponse>, t: Throwable) {
                    Log.d("RECORD", "Fail")
                    requireView().findViewById<Button>(R.id.buttonListenToAudio).isEnabled = true
                    requireView().findViewById<TextView>(R.id.infoAudioResult).text = "Unable to recognize this song"
                }

            })
    }
}