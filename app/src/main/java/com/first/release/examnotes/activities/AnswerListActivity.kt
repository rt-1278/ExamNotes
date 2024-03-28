package com.first.release.examnotes.activities

import android.app.AlertDialog
import android.content.Intent
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.first.release.examnotes.R
import com.first.release.examnotes.databinding.ActivityAnswerListBinding
import com.first.release.examnotes.model.Answer
import com.first.release.examnotes.model.Exam
import com.first.release.examnotes.util.DB_NAME
import com.first.release.examnotes.util.DB_VERSION
import com.first.release.examnotes.util.ExamNotesSqlOpenHelper

class AnswerListActivity : SourceActivity() {
    private lateinit var binding: ActivityAnswerListBinding
    private val viewModel: AnswerListViewModel by lazy {
        ViewModelProvider(this).get(AnswerListViewModel::class.java)
    }
    val helper = ExamNotesSqlOpenHelper(this, DB_NAME, null, DB_VERSION)
    val orderById = helper.ID + " ASC"

    var exam: Exam? = null
    var answerList: List<Answer>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_answer_list)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        exam = if (SDK_INT >= VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("exam", Exam::class.java)

        } else {
            intent.getParcelableExtra<Exam>("exam")
        }

        if (exam == null) {
            // 試験情報の無い場合ホーム画面へ遷移させる
            AlertDialog.Builder(this)
                .setMessage("試験データがないので解答画面を開くことができませんでした。")
                .setOnDismissListener {
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                .create()
            // FIXME クラッシュリティクス等に異常を伝える
        }

        answerList = helper.selectAnswers(null, null, orderById)
        // adapterにExamのquestionCountとanswerListを渡す
        // answerListのexamNumと一致する解答番号にはanswerの内容を表示する
        // 解答画面でAnswerの保存処理が走ったら保存する　保存された内容は解答一覧画面に戻った際、引き継ぐようにする

        // adapterを生成する。数はexamのquestionCount


        // viewModelで解答時間のリアルタイム表示をする
    }

    class AnswerListViewHolder(val binding: ActivityAnswerListBinding) : RecyclerView.ViewHolder(binding.root)

    class AnswerListAdapter(val questionCount: Int, answerList: List<Answer>?) : RecyclerView.Adapter<AnswerListViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnswerListViewHolder {
            TODO("Not yet implemented")
        }

        override fun onBindViewHolder(holder: AnswerListViewHolder, position: Int) {
            TODO("Not yet implemented")
        }

        override fun getItemCount(): Int {
            return questionCount
        }
    }

}

class AnswerListViewModel : ViewModel() {
    // viewModelで解答時間のリアルタイム表示をする
}
