package com.first.release.examnotes.model

import java.time.Instant

class Answer {
    var id: Int? = null
    var examId: Int? = null
    var examNum: Int? = null
    var answerType: Int? = null
    var selectCount: Int? = null
    var answer: String? = null
    var answerResult: String? = null
    var trueOrFalse: Boolean? = null
    var memo: String? = null
    var createdAt: Instant? = null
    var updatedAt: Instant? = null
}