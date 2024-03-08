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
            ExamStatus.ProgressDraft.statusInt -> {
                return "下書き作成中"
            }

            ExamStatus.CreatedDraft.statusInt -> {
                return "下書き作成済"
            }

            ExamStatus.ProgressTest.statusInt -> {
                return "試験実施中"
            }

            ExamStatus.EnteringAnswerResults.statusInt -> {
                return "採点中"
            }

            ExamStatus.FinishTest.statusInt -> {
                return "試験実施済"
            }

            else -> {
                return ""
            }
        }
    }

    fun validateValue(): String {
        return validateName() + validateQuestionCount() + validatePassingLine() + validateExamMinutes() + validateRemarks()
    }

    fun validateName(): String {
        var message = ""
        if (name.isNullOrBlank()) {
            message += "・「試験名」を入力してください\n"
        } else if ((name?.length ?: 0) > 20) {
            message += "「試験名」を20字以内で入力してください\n"
        }
        return message
    }

    fun validateQuestionCount(): String {
        var message = ""
        if (questionCount?.toString().isNullOrBlank()) {
            message += "・「問題数」を入力してください\n"
        } else if ((questionCount?.toString()?.length ?: 0) > 100) {
            message += "・「問題数を」100問以内で入力してください\n"
        }
        return message
    }

    fun validatePassingLine(): String {
        var message = ""
        if (passingLine?.toString().isNullOrBlank()) {
            message += "・「合格ライン」を入力してください\n"
        } else if ((passingLine?.toString()?.length ?: 0) > 100) {
            message += "・「合格ライン」を100%以内で入力してください\n"
        }
        return message
    }

    fun validateExamMinutes(): String {
        var message = ""
        if (examMinutes?.toString().isNullOrBlank()) {
            message += "・「試験時間」を入力してください\n"
        } else if ((examMinutes?.toString()?.length ?: 0) > 100) {
            message += "・「試験時間」を1000分以内で入力してください\n"
        }
        return message
    }

    fun validateRemarks(): String {
        var message = ""
        if ((remarks != null) && (remarks?.length ?: 0) > 2000) {
            message += "・「備考」は2000字以内で入力してください\n"
        }
        return message
    }
}

enum class ExamStatus(val statusInt: Int) {
    ProgressDraft(1),
    CreatedDraft(2),
    ProgressTest(3),
    EnteringAnswerResults(4),
    FinishTest(5)
}