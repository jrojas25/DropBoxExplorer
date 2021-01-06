package com.jmr.dropboxbrowser.util

import android.net.Uri
import com.dropbox.core.DbxException
import com.dropbox.core.v2.DbxClientV2
import com.dropbox.core.v2.files.ThumbnailFormat
import com.dropbox.core.v2.files.ThumbnailSize
import com.squareup.picasso.Picasso
import com.squareup.picasso.Request
import com.squareup.picasso.RequestHandler
import okio.source
import java.io.IOException

class FileThumbnailRequestHandler(private val mDbxClient: DbxClientV2) : RequestHandler() {
    override fun canHandleRequest(data: Request): Boolean {
        return SCHEME == data.uri.scheme && HOST == data.uri.host
    }

    @Throws(IOException::class)
    override fun load(
        request: Request,
        networkPolicy: Int
    ): Result? {
        return try {
            val downloader =
                mDbxClient.files().getThumbnailBuilder(request.uri.path)
                    .withFormat(ThumbnailFormat.PNG)
                    .withSize(ThumbnailSize.W640H480)
                    .start()
            Result(
                downloader.inputStream.source(),
                Picasso.LoadedFrom.NETWORK
            )
        } catch (e: DbxException) {
            throw IOException(e)
        }
    }

    companion object {
        private const val SCHEME = "dropbox"
        private const val HOST = "dropbox"

        fun buildPicassoUri(path: String): Uri {
            return Uri.Builder()
                .scheme(SCHEME)
                .authority(HOST)
                .path(path).build()
        }
    }

}