package com.first.release.examnotes.activities

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.first.release.examnotes.R
import com.first.release.examnotes.databinding.ActivityAnswerBinding

class AnswerActivity: SourceActivity() {
    private lateinit var binding: ActivityAnswerBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_answer)
        binding.lifecycleOwner = this


    }
}