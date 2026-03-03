package dev.tiagosilva.whatsappclone.services

import android.content.Context
import android.util.Log
import io.appwrite.Client
import io.appwrite.ID
import io.appwrite.exceptions.AppwriteException
import io.appwrite.models.Session
import io.appwrite.models.User
import io.appwrite.services.Account
import io.appwrite.services.Databases
import io.appwrite.services.Storage

object Appwrite {
    private lateinit var client: Client
    lateinit var account: Account
    lateinit var databases: Databases
    lateinit var storage: Storage

    private val databaseId = "69a620770035319049e9"
    private val storageId = "69a62488000ea01ea8e8"

    fun init(context: Context) {
        client = Client(context)
            .setEndpoint("https://nyc.cloud.appwrite.io/v1")
            .setProject("69a61b56002a65a2a11a")

        account = Account(client)
        databases = Databases(client)
        storage = Storage(client)
    }

    suspend fun createDocument(
        collectionId: String,
        data: Map<String, Any>
    ) {
        databases.createDocument(
            databaseId=databaseId,
            collectionId=collectionId,
            documentId = ID.unique(),
            data=data
        )
    }

    suspend fun onLogin(
        email: String,
        password: String
    ): Session {
        return account.createEmailPasswordSession(
            email = email,
            password = password
        )
    }

    suspend fun onUpdatePhone(phone: String, password: String) {
        account.updatePhone(phone, password)
    }

    suspend fun onLogout() {
        try {
            account.deleteSession(sessionId = "current")
        }catch(e: AppwriteException) {
            Log.e("Erro no logout", e.message.toString())
        }
    }

    suspend fun isLoggedIn(): Boolean {
        return try {
            account.get()
            true
        } catch (e: AppwriteException) {
            false
        }
    }

    suspend fun getCurrentUserOrNull(): User<Map<String, Any>>? {
        return try {
            account.get()
        } catch (e: AppwriteException) {
            null
        }
    }

    suspend fun onRegister(
        email: String,
        password: String,
        name: String
    ): User<Map<String, Any>> {
        return account.create(
            userId = ID.unique(),
            email = email,
            password = password,
            name = name
        )
    }
}


