package com.dicoding.picodiploma.loginwithanimation.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.dicoding.picodiploma.loginwithanimation.data.api.ApiConfig
import com.dicoding.picodiploma.loginwithanimation.data.api.ApiService
import com.dicoding.picodiploma.loginwithanimation.data.api.ListStoryItem
import com.dicoding.picodiploma.loginwithanimation.data.api.RegisterResponse
import com.dicoding.picodiploma.loginwithanimation.data.api.StoryDetailResponse
import com.dicoding.picodiploma.loginwithanimation.data.api.StoryResponse
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserPreference
import kotlinx.coroutines.flow.first
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StoryRepository private constructor(
    private val apiService: ApiService,
    private val userPreferences: UserPreference
) {
    suspend fun getStories() : StoryResponse{
        return try {
            apiService.getStories()
        } catch (e: Exception) {
            StoryResponse(emptyList())
        }
    }

    fun getStoryDetail(storyId: String): LiveData<StoryDetailResponse> {
        val data = MutableLiveData<StoryDetailResponse>()
        apiService.getDetails(storyId)
            .enqueue(object : Callback<StoryDetailResponse> {
            override fun onResponse(call: Call<StoryDetailResponse>, response: Response<StoryDetailResponse>) {
                if (response.isSuccessful) {
                    data.value = response.body()
                    Log.d("detail activity", "Load story detail success!")
                } else {
                    Log.d("detail activity", "fetching not success")
                }
            }

            override fun onFailure(call: Call<StoryDetailResponse>, t: Throwable) {
                Log.d("detail activity", "Failed to load story detail")
            }
        })
        return data
    }

    suspend fun getLocation() : StoryResponse {
        //return apiService.getStoriesWithLocation(1)
        return try {
            apiService.getStoriesWithLocation()
        } catch (e: Exception) {
            throw Exception("Network request failed", e)
        }
    }



    companion object {
        @Volatile
        private var instance: StoryRepository? = null

        fun getInstance(apiService: ApiService, userPreferences: UserPreference): StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(apiService, userPreferences)
            }.also { instance = it }
    }
}