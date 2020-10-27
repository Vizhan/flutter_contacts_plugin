package com.vizhan.flutter_contacts_plugin

import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri
import android.provider.ContactsContract
import androidx.annotation.NonNull
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import kotlinx.coroutines.*


/** FlutterContactsPlugin */
class FlutterContactsPlugin : FlutterPlugin, MethodCallHandler {
    private lateinit var channel: MethodChannel
    private lateinit var contentResolver: ContentResolver

    private val parentJob = Job()
    private val exceptionHandler: CoroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        GlobalScope.launch {
            println("CoRoutineException Caught: $throwable")
        }
    }
    private val scope = CoroutineScope(Dispatchers.Default + parentJob + exceptionHandler)

    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        contentResolver = flutterPluginBinding.applicationContext.contentResolver
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "vizhan/flutter_contacts_plugin")
        channel.setMethodCallHandler(this)
    }

    override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
        when (call.method) {
            "getContacts" -> this.getContacts { result.success(it) }
            else -> result.notImplemented()
        }
    }

    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
    }

    private fun getContacts(onSuccess: (contacts: List<Map<String, Any>>) -> Unit) {
        scope.launch(Dispatchers.Main) {
            val items = asyncList()
            onSuccess(items)
        }
    }

    private suspend fun asyncList(): List<Map<String, Any>> =
            scope.async {
                return@async contacts()
            }.await()

    private fun contacts(): List<Map<String, Any>> {
        val list = ArrayList<Contact>()
        val result = ArrayList<Map<String, Any>>()

        val cursor = contentResolver.query(
                ContactsContract.Contacts.CONTENT_URI,
                null,
                null,
                null,
                null
        )

        cursor?.use {
            if (it.count > 0) {
                while (it.moveToNext()) {
                    val model = fromCursor(it)
                    if (model != null && (!list.contains(model))) {
                        list.add(model)
                    }
                }
            }
        }

        list.sortBy { it.displayName }
        list.forEach {
            result.add(it.toMap())
        }

        return result
    }

    private fun fromCursor(cursor: Cursor): Contact? {
        val columnIndexId = cursor.getColumnIndex(ContactsContract.Contacts._ID)
        if (columnIndexId == -1) {
            return null
        }

        val identifier = cursor.getString(columnIndexId)
        val model = Contact(identifier)
        val columnIndexDisplayName = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)

        val displayName = cursor.getString(columnIndexDisplayName)
        model.displayName = displayName

        val columnIndexPhotoUri = cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_URI)
        if (columnIndexPhotoUri != -1) {
            val photoString = cursor.getString(columnIndexPhotoUri)
            if (photoString != null) {
                val uri = Uri.parse(photoString)
                val bytes = contentResolver.openInputStream(uri)?.buffered()?.use { it.readBytes() }
                model.avatar = bytes
            }
        }

        return model
    }
}
