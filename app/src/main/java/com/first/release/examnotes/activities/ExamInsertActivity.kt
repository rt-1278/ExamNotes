package com.first.release.examnotes.activities

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.first.release.examnotes.R
import com.first.release.examnotes.databinding.ActivityExamInsertBinding

class ExamInsertActivity : SourceActivity(),  ExamInsertEventHandlers{
    private lateinit var binding: ActivityExamInsertBinding
    private val viewModel: ExamInsertViewModel by lazy {
        ViewModelProvider(this).get(ExamInsertViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)
        // lifecycleの設定
        binding.lifecycleOwner = this
        // viewModelとhandlerの設定
//        binding.viewModel = viewModel
        binding.handlers = this

        // 試験IDがある場合、そのデータを取得して各フィールドに反映する
        val examId = intent.getIntExtra("examId", 0)

        // 戻るボタンの制御 FIXME SourceActivityに共通化したい。毎回登録はだるい。。。addCallbackはSourceActivityに書いてcallbackの匿名クラスの内容を各クラスで記述できるようにしたい
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // ここに実行するコードを書く
                // FIXME 確認モーダルを表示する「入力した内容が破棄されるが、良いか」
            }
        }
        onBackPressedDispatcher.addCallback(callback)

        // FIXME ListViewの定義 RecyclerViewと同じ　itemのレイアウトの生成、adapterの生成 viewを再利用しないのでviewHolderの生成は不要な模様
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
        TODO("Not yet implemented")
    }

    override fun onClickRemoveExam(view: View) {
        TODO("Not yet implemented")
    }

    override fun onClickNext(view: View) {
        TODO("Not yet implemented")
    }
}

class ExamInsertViewModel : ViewModel() {

}

interface ExamInsertEventHandlers {
    fun onClickSaveDraft(view: View)
    fun onClickRemoveExam(view: View)
    fun onClickNext(view: View)
}