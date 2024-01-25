package com.first.release.examnotes.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.Instant

@Parcelize
data class Exam(
    var id: Int? = null,
    var name: String? = null,
    var questionCount: Int? = null,
    var passingLine: Int? = null,
    var examMinutes: Int? = null,
    var status: Int? = null,
    var remarks: String? = null,
    var createdAt: Instant? = null,
    var updatedAt: Instant? = null
) : Parcelable {
    fun statusString(): String {
        when (status) {
            examStatus.ProgressDraft.statusInt -> {
                return "下書き作成中"
            }

            examStatus.CreatedDraft.statusInt -> {
                return "下書き作成済"
            }

            examStatus.ProgressTest.statusInt -> {
                return "試験実施中"
            }

            examStatus.EnteringAnswerResults.statusInt -> {
                return "採点中"
            }

            examStatus.FinishTest.statusInt -> {
                return "試験実施済"
            }

            else -> {
                return ""
            }
        }
    }
}

enum class examStatus(val statusInt: Int) {
    ProgressDraft(1),
    CreatedDraft(2),
    ProgressTest(3),
    EnteringAnswerResults(4),
    FinishTest(5)
}