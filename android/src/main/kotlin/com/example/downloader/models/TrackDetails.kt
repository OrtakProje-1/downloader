package com.example.downloader.models

/*
 * Copyright (c)  2021  Shabinder Singh
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.serialization.Serializable

enum class AudioFormat {
    MP3, MP4, FLAC, UNKNOWN
}

enum class AudioQuality(val kbps: String) {
    KBPS128("128"),
    KBPS160("160"),
    KBPS192("192"),
    KBPS256("256"),
    KBPS320("320"),
    UNKNOWN("-1");

    companion object {
        fun getQuality(kbps: String): AudioQuality {
            return when (kbps) {
                "128" -> KBPS128
                "160" -> KBPS160
                "192" -> KBPS192
                "256" -> KBPS256
                "320" -> KBPS320
                "-1" -> UNKNOWN
                else -> KBPS160 // Use 160 as baseline
            }
        }
    }
}


@Parcelize
@Serializable
data class TrackDetails(
    var title: String,
    var artists: List<String>,
    var durationSec: Int,
    var albumName: String? = null,
    var albumArtists: List<String> = emptyList(),
    var genre: List<String> = emptyList(),
    var trackNumber: Int? = null,
    var year: String? = null,
    var comment: String? = null,
    var lyrics: String? = null,
    var trackUrl: String? = null,
    var albumArtPath: String, // UriString in Android
    var albumArtURL: String,
    var source: Source,
    val progress: Int = 2,
    val downloadLink: String? = null,
    val downloaded: DownloadStatus = DownloadStatus.NotDownloaded,
    var audioQuality: AudioQuality = AudioQuality.KBPS192,
    var audioFormat: AudioFormat = AudioFormat.MP4,
    var outputFilePath: String, // UriString in Android
    var videoID: String? = null, // will be used for purposes like Downloadable Link || VideoID etc. based on Provider
) : Parcelable {
    val outputMp3Path get() = outputFilePath.substringBeforeLast(".") + ".mp3"
}

enum class Source{
    Youtube
}

@Serializable
sealed class DownloadStatus : Parcelable {
    @Parcelize object Downloaded : DownloadStatus()
    @Parcelize data class Downloading(val progress: Int = 2) : DownloadStatus()
    @Parcelize object Queued : DownloadStatus()
    @Parcelize object NotDownloaded : DownloadStatus()
    @Parcelize object Converting : DownloadStatus()
    @Parcelize data class Failed(val error: Throwable) : DownloadStatus()
}
