package com.example.labs.pages.lab6

import android.Manifest
import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.labs.R
import com.example.labs.pages.lab6.Crypto.ChCrypto
import com.google.android.material.textfield.TextInputEditText
import java.io.*


class Lab6 : Fragment() {
    private lateinit var lab6ViewModel: Lab6ViewModel

    private lateinit var packageName: String

    private lateinit var contentResolver: ContentResolver
    private var pickedFileUri: Uri? = null
    private lateinit var keyInput: TextInputEditText
    private lateinit var resultTV: TextView

    private var mAction = ACTIONS.ENCRYPT

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        lab6ViewModel = ViewModelProvider(this).get(Lab6ViewModel::class.java)

        val root = inflater.inflate(R.layout.lab6_frag, container, false)

        packageName = "com.example.labs.pages.lab6"

        contentResolver = requireContext().contentResolver

        val decryptBtn: Button = root.findViewById(R.id.decrypt)
        val encryptBtn: Button = root.findViewById(R.id.encrypt)

        keyInput = root.findViewById(R.id.key)

        resultTV = root.findViewById(R.id.result)

        encryptBtn.setOnClickListener {
            if (!checkPermissions()) {
                return@setOnClickListener
            }
            openFileFor(ACTIONS.ENCRYPT)
        }

        decryptBtn.setOnClickListener {
            if (!checkPermissions()) {
                return@setOnClickListener
            }
            openFileFor(ACTIONS.DECRYPT)
        }

        return root
    }

    private fun getKey(): String {
        val key = keyGen(keyInput.text.toString()).also {
            keyInput.setText(it)
        }
        return key
    }

    private fun keyGen(key: String): String {
        var newKey = key
        if (key.length != 32) {
            val randomString = (1..32 - key.length)
                .map { i -> kotlin.random.Random.nextInt(0, CHAR_POOL.size) }
                .map(CHAR_POOL::get)
                .joinToString("")
            newKey += (randomString)
        }
        return newKey
    }

    fun encrypt(key: String, text: String): String = ChCrypto.aesEncrypt(text, key)

    fun decrypt(key: String, text: String): String = ChCrypto.aesDecrypt(text, key)

    fun readTextFromUri(uri: Uri): String {
        val stringBuilder = StringBuilder()
        contentResolver.openInputStream(uri)?.use { inputStream ->
            BufferedReader(InputStreamReader(inputStream)).use { reader ->
                stringBuilder.append(reader.readText())
            }
        }
        Log.d("TEXT", stringBuilder.toString())
        return stringBuilder.toString()
    }

    fun openFileFor(action: String) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = TXT_MIME_TYPE
        }
        mAction = action
        startActivityForResult(intent, PICK_TXT_FILE)
    }

    private fun writeToFile(uri: Uri, text: String) {
        try {
            contentResolver.openFileDescriptor(uri, "w")?.use { parcelFileDescriptor ->
                FileOutputStream(parcelFileDescriptor.fileDescriptor).use {
                    it.write(text.toByteArray())
                }
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun createFile(text: String) {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = TXT_MIME_TYPE
        }
        startActivityForResult(intent, CREATE_FILE)
    }

    fun permissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            requireContext(), it
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun checkPermissions(): Boolean {
        if (!permissionsGranted()) {
            ActivityCompat.requestPermissions(
                requireActivity(), REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }
        return permissionsGranted()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                PICK_TXT_FILE -> {
                    data?.data?.let {
                        pickedFileUri = it
                        Log.d("FILE", it.toString())
                    }

                    val text = pickedFileUri?.let { readTextFromUri(it) }

                    val key = getKey()


                    var result: String? = when (mAction) {
                        ACTIONS.ENCRYPT -> {
                            text?.let { encrypt(key, it) }
                        }
                        ACTIONS.DECRYPT -> {
                            text?.let { decrypt(key, it) }
                        }
                        else -> {
                            encrypt(key, "SAMPLE TEXT") + decrypt(key, encrypt(key, "SAMPLE TEXT"))
                        }
                    }
                    resultTV.text = result
                    if (result != null) {
                        createFile(result)
                    }
                }
                CREATE_FILE -> {
                    val text = resultTV.text as String
                    data?.let { intent ->
                        intent.data?.let {
                            pickedFileUri = it
                            Log.d("FILE", it.toString())
                        }
                    }
                    pickedFileUri?.let { writeToFile(it, text) }

                }
            }
        }

    }


    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE)
        private const val REQUEST_CODE_PERMISSIONS = 10
        private const val PICK_TXT_FILE = 777
        private const val TXT_MIME_TYPE = "text/plain"
        private const val CREATE_FILE = 666

        object ACTIONS {
            const val ENCRYPT = "encrypt"
            const val DECRYPT = "decrypt"
        }

        val CHAR_POOL: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
    }
}
