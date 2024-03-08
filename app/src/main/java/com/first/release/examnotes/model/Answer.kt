package com.first.release.examnotes.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.Instant
import java.util.Arrays

@Parcelize
data class Answer(
    var id: Int? = null,
    var examId: Int? = null,
    var examNum: Int? = null,
    var answerType: Int? = null,
    var selectCount: Int? = null,
    var answer: String? = null,
    var answerResult: String? = null,
    var trueOrFalse: Boolean? = null,
    var memo: String? = null,
    var createdAt: Instant? = null,
    var updatedAt: Instant? = null

) : Parcelable {
}

enum class AnswerType(val answerType: Int) {
    Single(1),
    Insert(2),
    Multiple(3)
}

enum class AnswerKey(val position: Int, val answerKey: String) {
    ANSWER_TYPE(0, "解答形式"), SELECT_COUNT(1, "選択数"),
    ANSWER_SELECT(2, "選択"), ANSWER_INSERT(3, "入力");

    companion object {
        fun toKey(position: Int): String? {
            return (Arrays.stream(AnswerKey.values()).filter{ e -> e.position == position }.findFirst().orElse(null))?.answerKey
        }
        fun examKey(position: Int): AnswerKey? {
            return (Arrays.stream(AnswerKey.values()).filter{ e -> e.position == position }.findFirst().orElse(null))
        }
    }
}