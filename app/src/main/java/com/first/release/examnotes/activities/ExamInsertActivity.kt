package com.first.release.examnotes.activities

import android.app.AlertDialog
import android.content.Intent
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.first.release.examnotes.R
import com.first.release.examnotes.databinding.ActivityExamInsertBinding
import com.first.release.examnotes.databinding.ExamInsertItemCellBinding
import com.first.release.examnotes.model.Exam
import com.first.release.examnotes.util.DB_NAME
import com.first.release.examnotes.util.DB_VERSION
import com.first.release.examnotes.util.ExamNotesSqlOpenHelper
import java.lang.RuntimeException
import java.util.Arrays

class ExamInsertActivity : SourceActivity(),  ExamInsertEventHandlers{
    private lateinit var binding: ActivityExamInsertBinding
//    private val viewModel: ExamInsertViewModel by lazy {
//        ViewModelProvider(this).get(ExamInsertViewModel::class.java)
//    }
    private lateinit var adapter: ExamInsertCellViewAdapter
    private var examId = 0
    var exam: Exam? = null
    val helper = ExamNotesSqlOpenHelper(this, DB_NAME, null, DB_VERSION)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_exam_insert)
        // lifecycleの設定
        binding.lifecycleOwner = this
        // viewModelとhandlerの設定
//        binding.viewModel = viewModel
        binding.handlers = this

        // 試験IDがある場合、そのデータを取得して各フィールドに反映する
        examId = intent.getIntExtra("examId", 0)
        exam = if (SDK_INT >= VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("exam", Exam::class.java)
        } else {
            intent.getParcelableExtra<Exam>("exam")
        }
        if ((exam == null) && intent.getBooleanExtra("fromCreateExam", false)) {
            // 新規作成の場合
            exam = Exam()
        }

        // 戻るボタンの制御 FIXME SourceActivityに共通化したい。毎回登録はだるい。。。addCallbackはSourceActivityに書いてcallbackの匿名クラスの内容を各クラスで記述できるようにしたい
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // ここに実行するコードを書く
                // FIXME 確認モーダルを表示する「入力した内容が破棄されるが、良いか」
            }
        }
        onBackPressedDispatcher.addCallback(callback)

        adapter = ExamInsertCellViewAdapter(exam, this)
        binding.examInsertList.adapter = adapter
        binding.examInsertList.layoutManager = LinearLayoutManager(this)
        // recyclerviewの区切り線を追加
        binding.examInsertList.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onClickSaveDraft(view: View) {
        // examを保存して、前の画面に戻る
        try{
            if (exam?.id != null) {
                exam?.let { helper.updateExam(it) }
            } else {
                // 新規の場合、新規で作成
                exam?.let {
                    exam?.id = helper.insertExam(it) }
            }

            AlertDialog.Builder(this)
                .setMessage("下書き保存しました。")
                .setOnDismissListener {
                    finish()
                }
                .create()
                .show()
        } catch(e: RuntimeException) {
            // FIXME 更新に失敗した場合、モーダルを表示して「端末内に保存できない。」　再作成を依頼する
        }
    }

    override fun onClickRemoveExam(view: View) {
        // alertDialogを表示して、examとanswerを削除し、前の画面に戻る
        AlertDialog.Builder(this)
            .setMessage("入力していた内容を破棄しますか？")
            .setPositiveButton("はい") { _,_ -> exam?.id?.let { examId ->
                helper.deleteExam(arrayOf(examId.toString()))
                helper.deleteAnswerFromExamId(arrayOf(examId.toString()))
                finish()
            }}
            .setNegativeButton("いいえ" ){ _,_ ->  }
            .create()
            .show()
    }

    override fun onClickNext(view: View) {
        // examを保存する。解答画面へ進む。画面はfinish()する
        try{
            if (exam?.id != null) {
                exam?.let { helper.updateExam(it) }
            } else {
                // 新規の場合、新規で作成
                exam?.let {
                    exam?.id = helper.insertExam(it) }
            }
            val intent = Intent(this, AnswerActivity::class.java)
            intent.putExtra("exam", exam)
            startActivity(intent)
            finish()
            // answerはonCreate時にexamIdを元に取得する
        } catch(e: RuntimeException) {
            // FIXME 更新に失敗した場合、モーダルを表示して「端末内に保存できない。」　再作成を依頼する
        }
    }
}

class ExamInsertViewModel : ViewModel() {

}

interface ExamInsertEventHandlers {
    fun onClickSaveDraft(view: View)
    fun onClickRemoveExam(view: View)
    fun onClickNext(view: View)
}

// 以下RecyclerView用

class ExamInsertCellViewAdapter(var exam: Exam?, val activity: ExamInsertActivity) : RecyclerView.Adapter<ExamInsertCellViewHolder>() {
    enum class ExamKey(val position: Int, val examKey: String) {
        NAME(0, "試験名"), QUESTION_COUNT(1, "問題数"), PASS_LINE(2, "合格ライン"),
        EXAM_MINUTES(3, "試験時間"), REMARKS(4, "備考");

        companion object {
            fun toKey(position: Int): String? {
                return (Arrays.stream(values()).filter{  e -> e.position == position }.findFirst().orElse(null))?.examKey
            }
            fun examKey(position: Int): ExamKey? {
                return (Arrays.stream(values()).filter{  e -> e.position == position }.findFirst().orElse(null))
            }
        }
    }

    val rs = activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//                if (result.resultCode == 0) {
        activity.exam = if (SDK_INT >= VERSION_CODES.TIRAMISU) {
            result.data?.getParcelableExtra("exam", Exam::class.java)
        } else {
            result.data?.getParcelableExtra<Exam>("exam")
        }
        exam  = activity.exam
        notifyDataSetChanged()
//                }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExamInsertCellViewHolder {
        val binding: ExamInsertItemCellBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.exam_insert_item_cell,
            parent,
            false
        )
        return ExamInsertCellViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return 5
    }

    override fun onBindViewHolder(holder: ExamInsertCellViewHolder, position: Int) {
        // bindingに対してviewModelをセットし、表示する内容、タップされた時のイベントを定義する
        val binding = holder.binding
        val viewModel = ExamInsertCellViewModel(position, exam)
        binding.viewModel = viewModel

        binding.examInsertItemValue.setOnClickListener {
            // 入力画面へ遷移する examオブジェクトを渡し、更新するフィールドをviewModelのkeyを渡す
            val intent = Intent(activity, InsertActivity::class.java)
            intent.putExtra("exam", exam)
            intent.putExtra("examKey", viewModel.key)

            rs.launch(intent)
        }
    }
}
class ExamInsertCellViewModel(position: Int, exam: Exam?) : ViewModel() {
    var key = if (exam != null) {
        ExamInsertCellViewAdapter.ExamKey.toKey(position)
    } else {
        ""
    }

    var value = if (exam != null) {
        when(ExamInsertCellViewAdapter.ExamKey.examKey(position)){
            ExamInsertCellViewAdapter.ExamKey.NAME ->{
                exam.name
            }
            ExamInsertCellViewAdapter.ExamKey.QUESTION_COUNT ->{
                exam.questionCount?.toString()
            }
            ExamInsertCellViewAdapter.ExamKey.PASS_LINE ->{
                exam.passingLine?.toString()
            }
            ExamInsertCellViewAdapter.ExamKey.EXAM_MINUTES ->{
                exam.examMinutes?.toString()
            }
            ExamInsertCellViewAdapter.ExamKey.REMARKS ->{
                exam.remarks
            }
            else -> {""}
        }
    } else {
        ""
    }
}

class ExamInsertCellViewHolder(val binding: ExamInsertItemCellBinding) : RecyclerView.ViewHolder(binding.root)