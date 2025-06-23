package com.example.myfilebrowser

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import java.io.File

class MainActivity : Activity() {
    private lateinit var listView: ListView
    private lateinit var adapter: ArrayAdapter<String>
    private var currentDir: File = File("/")
    private var files: List<File> = emptyList()
    private val rootDirectories = listOf(
        File("/storage/emulated/0"),
        File("/data/data")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        listView = findViewById(R.id.listView)!!
        showRootDirectories()

        listView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val file = files[position]
            if (file.isDirectory) {
                currentDir = file
                showFiles(currentDir)
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
        files = rootDirectories
        val names = files.map { "[${it.name}]" }
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, names)
        listView.adapter = adapter
    }

    private fun showFiles(dir: File) {
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

    override fun onBackPressed() {
        if (currentDir.parentFile != null && currentDir.path != "/") {
            currentDir = currentDir.parentFile!!
            if (currentDir.path == "/") {
                showRootDirectories()
            } else {
                showFiles(currentDir)
            }
        } else {
            super.onBackPressed()
        }
    }
}
