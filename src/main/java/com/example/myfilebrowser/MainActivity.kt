package com.example.myfilebrowser

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import java.io.File
import android.window.OnBackInvokedCallback
import android.window.OnBackInvokedDispatcher
import java.util.ArrayDeque

class MainActivity : Activity() {
    companion object {
        private const val TAG = "MyFileBrowser"
    }

    private lateinit var listView: ListView
    private lateinit var adapter: ArrayAdapter<String>
    private var currentDir: File = File("/")
    private var files: List<File> = emptyList()
    private val directoryStack = ArrayDeque<File>()
    private val rootDirectories = listOf(
        File("/storage/emulated/0"),
        File("/data/data")
    )

    private val backPressedCallback = OnBackInvokedCallback {
        handleBackPress()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: Initializing.")
        setContentView(R.layout.activity_main)

        // Register the new back press callback. This is the modern way to handle
        // back button events and replaces the deprecated onBackPressed().
        onBackInvokedDispatcher.registerOnBackInvokedCallback(
            OnBackInvokedDispatcher.PRIORITY_DEFAULT,
            backPressedCallback
        )

        listView = findViewById(R.id.listView)!!
        showRootDirectories()

        listView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val file = files[position]
            if (file.isDirectory) {
                Log.d(TAG, "Navigating into: ${file.path}")
                Log.d(TAG, "Pushing to stack: ${currentDir.path}")
                directoryStack.addLast(currentDir)
                currentDir = file
                Log.d(TAG, "Stack size is now: ${directoryStack.size}")
                showFiles(currentDir) // This will log the new directory
            } else {
                try {
                    val text = file.readText()
                    AlertDialog.Builder(this)
                        .setTitle(file.name)
                        .setMessage(text.take(200))
                        .setPositiveButton("OK", null)
                        .show()
                } catch (e: Exception) {
                    Toast.makeText(this, "Cannot read file: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showRootDirectories() {
        currentDir = File("/")
        Log.d(TAG, "showRootDirectories: Displaying root view. Current dir is now '${currentDir.path}'")
        files = rootDirectories
        val names = files.map { "[${it.name}]" }
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, names)
        listView.adapter = adapter
    }

    private fun showFiles(dir: File) {
        Log.d(TAG, "showFiles: Displaying contents of '${dir.path}'")
        files = try {
            dir.listFiles()?.sortedWith(compareBy({ !it.isDirectory }, { it.name })) ?: emptyList()
        } catch (e: SecurityException) {
            Toast.makeText(this, "Access denied: ${dir.path}", Toast.LENGTH_SHORT).show()
            emptyList()
        }
        val names = files.map { if (it.isDirectory) "[${it.name}]" else it.name }
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, names)
        listView.adapter = adapter
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart: Activity started.")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: Activity resumed.")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause: Activity paused.")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop: Activity stopped.")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: Activity destroyed.")
        // Unregister the callback to avoid memory leaks
        onBackInvokedDispatcher.unregisterOnBackInvokedCallback(backPressedCallback)
    }

    private fun handleBackPress() {
        Log.d(TAG, "handleBackPress: Back button pressed. Stack size: ${directoryStack.size}")
        if (directoryStack.isNotEmpty()) {
            val previousDir = directoryStack.removeLast()
            Log.d(TAG, "Popped from stack: '${previousDir.path}'. New stack size: ${directoryStack.size}")
            currentDir = previousDir
            if (currentDir.path == "/") {
                Log.d(TAG, "Navigating back to root view.")
                showRootDirectories()
            } else {
                Log.d(TAG, "Navigating back to '${currentDir.path}'")
                showFiles(currentDir)
            }
        } else {
            Log.d(TAG, "Stack is empty, finishing activity.")
            finish() // Exit the activity when the navigation stack is empty
        }
    }
}
