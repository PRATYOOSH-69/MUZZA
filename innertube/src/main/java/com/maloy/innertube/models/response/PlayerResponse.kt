package com.maloy.innertube.models.response

import com.maloy.innertube.models.ResponseContext
import com.maloy.innertube.models.Thumbnails
import io.ktor.http.URLBuilder
import io.ktor.http.parseQueryString
import kotlinx.serialization.Serializable
import org.schabi.newpipe.extractor.services.youtube.YoutubeJavaScriptPlayerManager

/**
 * PlayerResponse with [com.maloy.innertube.models.YouTubeClient.ANDROID_MUSIC] client
 */
@Serializable
data class PlayerResponse(
    val responseContext: ResponseContext,
    val playabilityStatus: PlayabilityStatus,
    val playerConfig: PlayerConfig?,
    val streamingData: StreamingData?,
    val videoDetails: VideoDetails?,
) {
    @Serializable
    data class PlayabilityStatus(
        val status: String,
        val reason: String?,
    )

    @Serializable
    data class PlayerConfig(
        val audioConfig: AudioConfig,
    ) {
        @Serializable
        data class AudioConfig(
            val loudnessDb: Double?,
            val perceptualLoudnessDb: Double?,
        )
    }

    @Serializable
    data class StreamingData(
        val formats: List<Format>?,
        val adaptiveFormats: List<Format>,
        val expiresInSeconds: Int,
    ) {
        @Serializable
        data class Format(
            val itag: Int,
            val url: String?,
            val mimeType: String,
            val bitrate: Int,
            val width: Int?,
            val height: Int?,
            val contentLength: Long?,
            val quality: String,
            val fps: Int?,
            val qualityLabel: String?,
            val averageBitrate: Int?,
            val audioQuality: String?,
            val approxDurationMs: String?,
            val audioSampleRate: Int?,
            val audioChannels: Int?,
            val loudnessDb: Double?,
            val lastModified: Long?,
            val signatureCipher: String?,
        ) {
            val isAudio: Boolean
                get() = width == null

            fun findUrl(): String? {
                this.url?.let {
                    return it
                }
                this.signatureCipher?.let { signatureCipher ->
                    val params = parseQueryString(signatureCipher)
                    val obfuscatedSignature = params["s"] ?: return null
                    val signatureParam = params["sp"] ?: return null
                    val url = params["url"]?.let { URLBuilder(it) } ?: return null
                    url.parameters[signatureParam] = YoutubeJavaScriptPlayerManager.deobfuscateSignature("", obfuscatedSignature)
                    val streamUrl = YoutubeJavaScriptPlayerManager.getUrlWithThrottlingParameterDeobfuscated("", url.toString())
                    return streamUrl
                }
                return null
            }
        }
    }

    @Serializable
    data class VideoDetails(
        val videoId: String,
        val title: String,
        val author: String,
        val channelId: String,
        val lengthSeconds: String,
        val musicVideoType: String?,
        val viewCount: String,
        val thumbnail: Thumbnails,
    )
}