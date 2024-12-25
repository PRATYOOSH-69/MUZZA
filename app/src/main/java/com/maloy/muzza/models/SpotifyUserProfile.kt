package com.maloy.muzza.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SpotifyUserProfile(
    @SerialName("display_name")
    val displayName: String
)