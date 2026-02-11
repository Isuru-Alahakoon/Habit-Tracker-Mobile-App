package com.leolithy.exam_06

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.leolithy.exam_06.data.DataManager
import com.leolithy.exam_06.databinding.ActivityMainBinding
import com.leolithy.exam_06.ui.habits.HabitsFragment
import com.leolithy.exam_06.ui.mood.MoodFragment
import com.leolithy.exam_06.ui.settings.SettingsFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            // Handle permission grant or denial if needed, e.g., show a message.
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize the DataManager for SharedPreferences
        DataManager.init(this)

        // Request notification permission on Android 13+
        askNotificationPermission()

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            val selectedFragment: Fragment = when (item.itemId) {
                R.id.navigation_mood -> MoodFragment()
                R.id.navigation_settings -> SettingsFragment()
                else -> HabitsFragment() // Default to habits
            }
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, selectedFragment).commit()
            true
        }

        // Set the initial fragment on first launch
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HabitsFragment())
                .commit()
            binding.bottomNavigation.selectedItemId = R.id.navigation_habits
        }
    }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}