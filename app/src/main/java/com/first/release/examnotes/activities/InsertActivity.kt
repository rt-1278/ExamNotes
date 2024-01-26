package com.first.release.examnotes.activities

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.first.release.examnotes.R
import com.first.release.examnotes.databinding.ActivityInsertBinding

class InsertActivity: SourceActivity() {
    lateinit var binding: ActivityInsertBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_insert)
        binding.lifecycleOwner = this
    }
}