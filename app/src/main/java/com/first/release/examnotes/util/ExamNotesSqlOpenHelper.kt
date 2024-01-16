package com.first.release.examnotes.util

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.core.database.getIntOrNull
import androidx.core.database.getStringOrNull
import com.first.release.examnotes.model.Answer
import com.first.release.examnotes.model.Exam
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.format.SignStyle
import java.time.temporal.ChronoField

// DBバージョン
val DB_VERSION = 1
// DB名
val DB_NAME = "exam_notes"

public class ExamNotesSqlOpenHelper(
    context: Context?,
    name: String? = DB_NAME,
    factory: SQLiteDatabase.CursorFactory?,
    version: Int = DB_VERSION
) : SQLiteOpenHelper(context, name, factory, version) {

    // テーブル名
    val EXAM_TABLE_NAME = "exam"
    val ANSWER_TABLE_NAME = "answer"
    // 試験テーブルのカラム
    val EXAM_NAME = "name"
    val EXAM_QUESTION_COUNT = "question_count"
    val PASSING_LINE = "passing_line"
    val EXAM_MINUTES = "exam_minutes"
    val EXAM_STATUS = "status"
    val REMARKS = "remarks"

    // 解答テーブルのカラム
    val EXAM_ID = "exam_id"
    val EXAM_NUM = "exam_num"
    val ANSWER_TYPE = "answer_type"
    val SELECT_COUNT = "select_count"
    val ANSWER = "answer"
    val ANSWER_RESULT = "answer_result"
    val TRUE_OR_FALSE = "true_or_false"
    val MEMO = "memo"
    // 各テーブル共通のカラム
    val ID = "id"
    val CREATED_AT = "created_at"
    val UPDATED_AT = "updated_at"

    private val SQL_CREATE_EXAMS = "CREATE TABLE $EXAM_TABLE_NAME" + " (" +
            ID + " INTEGER PRIMARY KEY," +
            EXAM_NAME + " TEXT," +
            EXAM_QUESTION_COUNT + " INTEGER," +
            PASSING_LINE + " INTEGER," +
            EXAM_MINUTES + " INTEGER," +
            EXAM_STATUS + " INTEGER," +
            REMARKS + " TEXT," +
            CREATED_AT + " TEXT," +
            UPDATED_AT + " TEXT)"

    private val SQL_CREATE_ANSWERS = "CREATE TABLE $ANSWER_TABLE_NAME" + " (" +
            ID + " INTEGER PRIMARY KEY," +
            EXAM_ID + " INTEGER," +
            EXAM_NUM + " INTEGER," +
            ANSWER_TYPE + " INTEGER," +
            SELECT_COUNT + " INTEGER," +
            ANSWER + " TEXT," +
            ANSWER_RESULT + " TEXT," +
            TRUE_OR_FALSE + " BOOLEAN," +
            MEMO + " TEXT," +
            CREATED_AT + " TEXT," +
            UPDATED_AT + " TEXT)"


    private val SQL_DELETE_EXAMS = "DROP TABLE IF EXISTS $EXAM_TABLE_NAME"

    private val SQL_DELETE_ANSWERS = "DROP TABLE IF EXISTS $ANSWER_TABLE_NAME"

    private val SQL_EXIST_EXAMS = "SELECT * FROM sqlite_master WHERE type='table' AND name=$EXAM_TABLE_NAME;"

    private val SQL_EXIST_ANSWERS = "SELECT * FROM sqlite_master WHERE type='table' AND name=$ANSWER_TABLE_NAME;"

    override fun onCreate(db: SQLiteDatabase?) {
        db?.beginTransaction()
        try {
            db?.execSQL(
                SQL_CREATE_EXAMS
            )
            db?.execSQL(
                SQL_CREATE_ANSWERS
            )
            db?.setTransactionSuccessful()
        } catch (e: SQLException) {
            // FIXME テーブル作成に失敗したと通知する　失敗した場合の導線どうすべきか
        } finally {
            db?.endTransaction()
        }
      }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // DBのバージョンが更新された場合、DBを削除し作成し直す
        if (oldVersion != newVersion) {
            db?.beginTransaction()
            try {
                db?.execSQL(SQL_DELETE_EXAMS)
                db?.execSQL(SQL_DELETE_ANSWERS)
                db?.setTransactionSuccessful()
                onCreate(db)
            } catch (e: SQLException) {
                // FIXME 通知する
            } finally {
                db?.endTransaction()
            }
        }
    }


    // FIXME 各テーブルのデータ挿入　更新　削除　挿入時エスケープ処理
    fun insertExam(contentValues: ContentValues) {
        try {
            this.writableDatabase.use { db ->
                db.insert(EXAM_TABLE_NAME, null, contentValues)
            }
        } catch (_: SQLException) {
        // FIXME 通知する
        }
    }

    fun insertAnswer(contentValues: ContentValues) {
        try {
            this.writableDatabase.use { db ->
                db.insert(ANSWER_TABLE_NAME, null, contentValues)
            }
        } catch (_: SQLException) {
            // FIXME 通知する
        }
    }

    fun updateExam(contentValues: ContentValues, id: Array<String>) {
        try {
            this.writableDatabase.use { db ->
                db.update(EXAM_TABLE_NAME, contentValues, "id=?", id);
            }
        } catch (_: SQLException) {
            // FIXME 通知する
        }
    }

    fun updateAnswer(contentValues: ContentValues, id: Array<String>) {
        try {
            this.writableDatabase.use { db ->
                db.update(ANSWER_TABLE_NAME, contentValues, "id=?", id);
            }
        } catch (_: SQLException) {
            // FIXME 通知する
        }
    }

    fun deleteExam(contentValues: ContentValues, id: Array<String>) {
        try {
            this.writableDatabase.use { db ->
                db.delete(EXAM_TABLE_NAME, "id=?", id);
            }
        } catch (_: SQLException) {
            // FIXME 通知する
        }
    }

    fun deleteAnswer(contentValues: ContentValues, id: Array<String>) {
        try {
            this.writableDatabase.use { db ->
                db.delete(ANSWER_TABLE_NAME, "id=?", id);
            }
        } catch (_: SQLException) {
            // FIXME 通知する
        }
    }

    fun setExamMemberFromCursor(cursor: Cursor, exam: Exam) : Exam {
        exam.id = cursor.getIntOrNull(cursor.getColumnIndex(ID))
        exam.name = cursor.getStringOrNull(cursor.getColumnIndex(EXAM_NAME))
        exam.questionCount = cursor.getIntOrNull(cursor.getColumnIndex(EXAM_QUESTION_COUNT))
        exam.passingLine = cursor.getIntOrNull(cursor.getColumnIndex(PASSING_LINE))
        exam.examMinutes = cursor.getIntOrNull(cursor.getColumnIndex(EXAM_MINUTES))
        exam.status = cursor.getIntOrNull(cursor.getColumnIndex(EXAM_STATUS))
        exam.remarks = cursor.getStringOrNull(cursor.getColumnIndex(REMARKS))
        exam.createdAt = cursor.getStringOrNull(cursor.getColumnIndex(CREATED_AT))
            ?.let { DateTimeConverter.stringToInstant(it) }
        exam.updatedAt = cursor.getStringOrNull(cursor.getColumnIndex(UPDATED_AT))
            ?.let { DateTimeConverter.stringToInstant(it) }
        return exam
    }

    fun setAnswerMemberFromCursor(cursor: Cursor, answer: Answer) : Answer {
        answer.id = cursor.getIntOrNull(cursor.getColumnIndex(ID))
        answer.examId = cursor.getIntOrNull(cursor.getColumnIndex(EXAM_ID))
        answer.examNum = cursor.getIntOrNull(cursor.getColumnIndex(EXAM_NUM))
        answer.answerType = cursor.getIntOrNull(cursor.getColumnIndex(ANSWER_TYPE))
        answer.selectCount = cursor.getIntOrNull(cursor.getColumnIndex(SELECT_COUNT))
        answer.answer = cursor.getStringOrNull(cursor.getColumnIndex(ANSWER))
        answer.answerResult = cursor.getStringOrNull(cursor.getColumnIndex(ANSWER_RESULT))
        answer.trueOrFalse = (cursor.getIntOrNull(cursor.getColumnIndex("TRUE_OR_FALSE")) == 1)
        answer.memo = cursor.getStringOrNull(cursor.getColumnIndex(MEMO))
        answer.createdAt = cursor.getStringOrNull(cursor.getColumnIndex(CREATED_AT))
            ?.let { DateTimeConverter.stringToInstant(it) }
        answer.updatedAt = cursor.getStringOrNull(cursor.getColumnIndex(UPDATED_AT))
            ?.let { DateTimeConverter.stringToInstant(it) }
        return answer
    }

    fun selectExam(selection: String?, selectionColumns: Array<String>?): Exam? {
        var exam = Exam()
        try {
            this.writableDatabase.use { db ->
                db.query(EXAM_TABLE_NAME, null, selection, selectionColumns, null, null, null).use { cursor ->
                    exam = setExamMemberFromCursor(cursor, exam)
                }
            }
        } catch (_: SQLException) {
            // FIXME 通知する
        }
        return exam
    }

    fun selectExams(selection: String?, selectionColumns: Array<String>?, orderBy: String? = null, limit: String? = null): List<Exam> {
        val examList = ArrayList<Exam>()
        try {
            this.writableDatabase.use { db ->
                db.query(EXAM_TABLE_NAME, null, selection, selectionColumns, null, null, orderBy, limit).use { cursor ->
                    while (cursor.moveToNext()) {
                        examList.add(setExamMemberFromCursor(cursor, Exam()))
                    }
                }
            }
        } catch (_: SQLException) {
            // FIXME 通知する
        }
        return examList
    }

    fun selectAnswer(selection: String?, selectionColumns: Array<String>?): Answer {
        var answer = Answer()
        try {
            this.writableDatabase.use { db ->
                db.query(ANSWER_TABLE_NAME, null, selection, selectionColumns, null, null, null).use { cursor ->
                    answer = setAnswerMemberFromCursor(cursor, answer)
                }
            }
        } catch (_: SQLException) {
            // FIXME 通知する
        }
        return answer
    }

    fun selectAnswers(selection: String?, selectionColumns: Array<String>?): List<Answer> {
        val answerList = ArrayList<Answer>()
        try {
            this.writableDatabase.use { db ->
                db.query(ANSWER_TABLE_NAME, null, selection, selectionColumns, null, null, null).use { cursor ->
                    while (cursor.moveToNext()) {
                        answerList.add(setAnswerMemberFromCursor(cursor, Answer()))
                    }
                }
            }
        } catch (_: SQLException) {
            // FIXME 通知する
        }
        return answerList
    }
}

object DateTimeConverter {
    /**
     * [String] を [Instant] に変換する。
     */
    fun stringToInstant(value: String): Instant =
        formatter.parse(value, Instant::from)

    /**
     * [Instant] を [String] に変換する。
     *
     * 秒未満の値は切り捨てられる。
     *
     * @throws DateTimeException 0 年未満や 10,000 年以上の場合。
     */
    fun instantToString(value: Instant): String =
        formatter.format(value)

    /**
     * `YYYY-MM-DD HH:MM:SS` 形式の日時フォーマッター。
     *
     * 0 年未満や 10,000 年以上はエラーになる。
     */
    private val formatter: DateTimeFormatter =
    // DateTimeFormatter.ofPattern で生成したものだと
    // 0 年未満や 10,000 年以上でもエラーにならないため、
        // DateTimeFormatterBuilder で生成する。
        DateTimeFormatterBuilder()
            .appendValue(ChronoField.YEAR, 4, 4, SignStyle.NOT_NEGATIVE)
            // ^ 4 桁を超える場合や負の場合はエラーとする。
            .appendLiteral('-')
            .appendValue(ChronoField.MONTH_OF_YEAR, 2)
            .appendLiteral('-')
            .appendValue(ChronoField.DAY_OF_MONTH, 2)
            .appendLiteral(' ')
            .appendValue(ChronoField.HOUR_OF_DAY, 2)
            .appendLiteral(':')
            .appendValue(ChronoField.MINUTE_OF_HOUR, 2)
            .appendLiteral(':')
            .appendValue(ChronoField.SECOND_OF_MINUTE, 2)
            .toFormatter()
            .withZone(ZoneOffset.UTC) // タイムゾーンを UTC にする。
}