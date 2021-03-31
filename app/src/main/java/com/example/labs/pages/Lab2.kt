package com.example.labs.pages

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.CompoundButton
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import com.example.labs.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.lab1_frag.*
import kotlinx.android.synthetic.main.lab3_frag.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class Lab2 : Fragment() {
    lateinit var videoPlayer: VideoView
    lateinit var videoPath: Uri
    lateinit var seekBar: SeekBar
    private var imageCapture: ImageCapture? = null
    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var cameraBtn: FloatingActionButton
    lateinit var switch : Switch
    lateinit var viewFinder: PreviewView
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.lab2_frag, container, false)
        val playButton = root.findViewById<FloatingActionButton>(R.id.play_floating_btn)
        viewFinder =root.findViewById(R.id.viewFinder)
        switch = root.findViewById(R.id.switch1)

        val playButtonIcon = root.findViewById<ImageView>(R.id.play_icon)
        seekBar = root.findViewById(R.id.seekBar)
        videoPlayer = root.findViewById(R.id.videoView)
        val scope = CoroutineScope(Job())
        videoPath = Uri.parse("android.resource://" + "com.example.labs" + "/" + R.raw.vid)
        videoPlayer.setVideoURI(videoPath)

        videoPlayer.setOnPreparedListener {
            seekBar.max = videoPlayer.duration / 1000
        }

        playButton.setOnClickListener {
            if (videoPlayer.isPlaying) {
                videoPlayer.pause()
                playButtonIcon.setImageResource(R.drawable.ic_play)
            } else {
                videoPlayer.start()
                playButtonIcon.setImageResource(R.drawable.ic_pause)
                fillSeekBar(videoPlayer, seekBar, scope)
            }
        }

        videoPlayer.setOnCompletionListener {
            seekBar.progress = 0
            videoPlayer.resume()
            playButtonIcon.setImageResource(R.drawable.ic_play)
        }

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {}
            override fun onStartTrackingTouch(seekBar: SeekBar) {
                videoPlayer.pause()
                playButton.isClickable = false
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                videoPlayer.seekTo(seekBar.progress * 1000 + 1000)
                videoPlayer.start()
                fillSeekBar(videoPlayer, seekBar, scope)
                playButton.isClickable = true
            }
        })
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(), Lab2.REQUIRED_PERMISSIONS, Lab2.REQUEST_CODE_PERMISSIONS
            )
        }
        cameraBtn = root.findViewById(R.id.camera_capture_button)
        switch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                videoPlayer.visibility = View.GONE
                playButton.visibility = View.GONE
                seekBar.visibility = View.GONE
                cameraBtn.visibility = View.VISIBLE
                viewFinder.visibility = View.VISIBLE
            } else {
                videoPlayer.visibility = View.VISIBLE
                playButton.visibility = View.VISIBLE
                seekBar.visibility = View.VISIBLE
                cameraBtn.visibility = View.GONE
                viewFinder.visibility = View.GONE
            }
        }

        // Set up the listener for take photo button
        cameraBtn.setOnClickListener { takePhoto() }

        outputDirectory = getOutputDirectory()

        cameraExecutor = Executors.newSingleThreadExecutor()

        return root
    }

    private fun takePhoto() {
        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return

        // Create time-stamped output file to hold the image
        val photoFile = File(
            outputDirectory,
            SimpleDateFormat(
                FILENAME_FORMAT, Locale.US
            ).format(System.currentTimeMillis()) + ".jpg"
        )

        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        // Set up image capture listener, which is triggered after photo has
        // been taken
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)
                    val msg = "Photo capture succeeded: $savedUri"
                    Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, msg)
                }
            })

    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener(Runnable {
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(viewFinder.createSurfaceProvider())
                }

            imageCapture = ImageCapture.Builder()
                .build()

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture
                )

            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            requireContext(), it
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun getOutputDirectory(): File {
        val mediaDir = requireActivity().externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else requireActivity().filesDir
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray
    ) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    companion object {
        private const val TAG = "CameraXBasic"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }
}

fun fillSeekBar(videoPlayer: VideoView, seekBar: SeekBar, scope: CoroutineScope) {
    scope.launch {
        while (videoPlayer.isPlaying) {
            seekBar.progress = videoPlayer.currentPosition / 1000
        }
    }

}