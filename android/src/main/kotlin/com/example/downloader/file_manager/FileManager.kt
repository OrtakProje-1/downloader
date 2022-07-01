/*
 *  * Copyright (c)  2021  Shabinder Singh
 *  * This program is free software: you can redistribute it and/or modify
 *  * it under the terms of the GNU General Public License as published by
 *  * the Free Software Foundation, either version 3 of the License, or
 *  * (at your option) any later version.
 *  *
 *  * This program is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  * GNU General Public License for more details.
 *  *
 *  *  You should have received a copy of the GNU General Public License
 *  *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.example.downloader.file_manager

import com.example.downloader.models.DownloadResult
import com.example.downloader.models.TrackDetails
import com.example.downloader.utils.SuspendableEvent
import com.example.downloader.utils.createHttpClient
import io.ktor.client.HttpClient
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.statement.HttpStatement
import io.ktor.http.contentLength
import io.ktor.http.isSuccess
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlin.math.roundToInt

//internal expect fun fileManagerModule(): Module

interface FileManager {

    fun fileSeparator(): String

    fun defaultDir(): String

    fun imageCacheDir(): String

    fun createDirectory(dirPath: String)


    suspend fun saveFileWithMetadata(
        mp3ByteArray: ByteArray,
        trackDetails: TrackDetails,
        postProcess: (track: TrackDetails) -> Unit = {}
    ): SuspendableEvent<String, Throwable>

    fun addToLibrary(path: String)
}

/*
* Call this function at startup!
* */
fun FileManager.createDirectories() {
    try {
        if (!defaultDir().contains("null${fileSeparator()}SpotiFlyer")) {
            createDirectory(defaultDir())
            createDirectory(imageCacheDir())
            createDirectory(defaultDir() + "Tracks" + fileSeparator())
            createDirectory(defaultDir() + "Albums" + fileSeparator())
            createDirectory(defaultDir() + "Playlists" + fileSeparator())
            createDirectory(defaultDir() + "YT_Downloads" + fileSeparator())
        }
    } catch (ignored: Exception) {
    }
}

fun removeIllegalChars(fileName: String): String {
    return fileName.replace("[^\\dA-Za-z0-9-_]".toRegex(), "_")
}

fun FileManager.finalOutputDir(
    itemName: String,
    type: String,
    subFolder: String,
    defaultDir: String,
    extension: String = ".mp3"
): String =
    defaultDir + removeIllegalChars(type) + this.fileSeparator() +
            if (subFolder.isEmpty()) "" else {
                removeIllegalChars(subFolder) + this.fileSeparator()
            } +
            removeIllegalChars(itemName) + extension


/*DIR Specific Operation End*/
private fun getNameFromURL(url: String, isImage: Boolean = false): String {
    val startIndex = url.lastIndexOf('/', url.lastIndexOf('/') - 1) + 1

    var fileName = if (startIndex != -1)
        url.substring(startIndex).replace('/', '_')
    else url.substringAfterLast("/")

    // Generify File Extensions
    if (isImage) {
        if (fileName.length - fileName.lastIndexOf(".") > 5) {
            fileName += ".jpeg"
        } else {
            if (fileName.endsWith(".jpg"))
                fileName = fileName.substringBeforeLast(".") + ".jpeg"
        }
    }

    return fileName
}

suspend fun HttpClient.downloadFile(url: String) = downloadFile(url, this)

fun Long?.requireNotNull(): Long {
    if (this == null) {
        return 0;
    }
    return this;
}



suspend fun downloadFile(url: String, client:HttpClient): Flow<DownloadResult> {
    return flow {
        client.get<HttpStatement>(url).execute { response ->
            // Not all requests return Content Length
            val data = kotlin.runCatching {
                ByteArray(response.contentLength().requireNotNull().toInt())
            }.getOrNull() ?: byteArrayOf()
            var offset = 0
            val downloadableContent = response.content

            do {
                // Set Length optimally, after how many kb you want a progress update, now its 0.25mb
                val currentRead = downloadableContent.readAvailable(data, offset, 2_50_000).also {
                    offset += it

                }

                // Calculate Download Progress
                val progress = data.size.takeIf { it != 0 }?.let { fileSize ->
                    (offset * 100f / fileSize).roundToInt()
                }

                // Emit Progress Update
                emit(DownloadResult.Progress(progress ?: 0))
            } while (currentRead > 0)


            // Download Complete
            if (response.status.isSuccess()) {
                emit(DownloadResult.Success(data))
            } else {
                emit(DownloadResult.Error("File not downloaded"))
            }
        }
            client.close();


    }.catch { e ->
        e.printStackTrace()
        emit(DownloadResult.Error(e.message ?: "File not downloaded"))
    }
}

suspend fun downloadByteArray(
    url: String,
    httpBuilder: HttpRequestBuilder.() -> Unit = {}
): ByteArray? {
    val client = createHttpClient()
    val response = try {
        client.get<ByteArray>(url, httpBuilder)
    } catch (e: Exception) {
        return null
    }
    client.close()
    return response
}
