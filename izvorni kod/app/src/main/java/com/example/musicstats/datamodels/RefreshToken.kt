package com.example.musicstats.datamodels

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class RefreshToken(
    @SerializedName("access_token")
    @Expose
    var accessToken: String,

    @SerializedName("token_type")
    @Expose
    var tokenType: String,

    @SerializedName("scope")
    @Expose
    var scope: String,

    @SerializedName("expires_in")
    @Expose
    var expiresIn: Int,


)