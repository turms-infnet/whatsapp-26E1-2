package dev.tiagosilva.whatsappclone.utils

import android.content.ContentResolver
import android.graphics.Bitmap
import android.net.Uri
import io.sentry.Sentry

class FileCast {
    companion object {
        @JvmStatic
        fun getFileFromUri(uri: Uri, contentResolver: ContentResolver, cacheDir: java.io.File): java.io.File? {
            return try {
                val inputStream = contentResolver.openInputStream(uri) ?: return null
                val tempFile = java.io.File.createTempFile("profile_uri_", ".jpg", cacheDir)

                tempFile.outputStream().use { fileOut ->
                    inputStream.copyTo(fileOut)
                }

                tempFile
            } catch (e:Exception) {
                e.printStackTrace()
                Sentry.captureException(e)
                null
            }
        }

        @JvmStatic
        fun getFileFromBitmap(bitmap: Bitmap, cacheDir: java.io.File): java.io.File? {
            return try {
                val tempFile = java.io.File.createTempFile("profile_bitmap_", ".jpg", cacheDir)

                tempFile.outputStream().use { fileOut ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOut)
                }

                tempFile
            } catch (e:Exception) {
                e.printStackTrace()
                Sentry.captureException(e)
                null
            }
        }
    }
}