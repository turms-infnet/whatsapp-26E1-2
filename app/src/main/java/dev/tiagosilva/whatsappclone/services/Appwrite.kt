package dev.tiagosilva.whatsappclone.services

import android.content.Context
import android.util.Log
import io.appwrite.Client
import io.appwrite.ID
import io.appwrite.exceptions.AppwriteException
import io.appwrite.models.InputFile
import io.appwrite.models.Session
import io.appwrite.models.User
import io.appwrite.services.Account
import io.appwrite.services.Databases
import io.appwrite.services.Storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object Appwrite {
    private lateinit var client: Client
    lateinit var account: Account
    lateinit var databases: Databases
    lateinit var storage: Storage
    private val URL: String = "https://nyc.cloud.appwrite.io/v1"
    private val PROJECT_ID: String = "69a61b56002a65a2a11a"
    private val BUCKET_ID = "69a62488000ea01ea8e8"
    private val DATABASE_ID = "69a620770035319049e9"

    fun init(context: Context) {
        client = Client(context)
            .setEndpoint(URL)
            .setProject(PROJECT_ID)

        account = Account(client)
        databases = Databases(client)
        storage = Storage(client)
    }

    suspend fun uploadImageAndGetUrl(file: java.io.File): String? = withContext(Dispatchers.IO) {
        try {
            val uploaded = storage.createFile(
                bucketId = BUCKET_ID,
                fileId = ID.unique(),
                file = InputFile.fromFile(file),
            )

            val url = "${URL}/storage/buckets/${BUCKET_ID}/files/${uploaded.id}/view?project=${PROJECT_ID}"
            url
        } catch (e: Exception) {
            Log.e("Appwrite", "Erro ao fazer upload: ", e)
            null
        }
    }
}


