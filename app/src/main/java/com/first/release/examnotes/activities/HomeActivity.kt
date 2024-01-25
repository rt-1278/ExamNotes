package com.first.release.examnotes.activities

import android.content.Intent
import android.database.SQLException
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.recyclerview.widget.RecyclerView
import com.first.release.examnotes.R
import com.first.release.examnotes.databinding.ActivityHomeBinding
import com.first.release.examnotes.model.Exam
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.first.release.examnotes.databinding.ExamItemCellBinding
import com.first.release.examnotes.model.examStatus
import com.first.release.examnotes.util.DB_NAME
import com.first.release.examnotes.util.DB_VERSION
import com.first.release.examnotes.util.ExamNotesSqlOpenHelper

class HomeActivity : SourceActivity(), HomeHandlers {
    private val viewModel: HomeViewModel by lazy {
        ViewModelProvider(this).get(HomeViewModel::class.java)
    }
    private lateinit var binding: ActivityHomeBinding
    val HOME_TAB = 0
    val HISTORY_TAB = 1
    var examList: MutableList<Exam>? = null
    lateinit var homeAdapter: HomeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)
        // lifecycleの設定
        binding.lifecycleOwner = this
        // viewModelとhandlerの設定
        binding.viewModel = viewModel
        binding.handlers = this


        // DBから試験データを３件（最新の更新日時）を取得する
        try{
            val helper = ExamNotesSqlOpenHelper(this, DB_NAME, null, DB_VERSION)
            val orderBy = helper.UPDATED_AT + " DESC"
            val limit = "3"
            examList = helper.selectExams(null, null, orderBy, limit).toMutableList()
            viewModel.examList.value = examList
        } catch(e: SQLException) {
            // FIXME firebaseに通知して、ユーザーには最新の試験データが取得できなかった。と伝える。
        }


        // recyclerViewのオブジェクトを取得
        // adapterの生成　生成後viewにadapterをセットする
        homeAdapter = HomeAdapter(this, listOf())
        binding.examsList.adapter = homeAdapter
        binding.examsList.layoutManager = LinearLayoutManager(this)
        // recyclerviewの区切り線を追加
        binding.examsList.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        val tabLayout = findViewById<TabLayout>(R.id.home_and_history_tab_layout)
        tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    HOME_TAB -> {
                        // ホーム画面へ遷移　すでにホーム画面の場合何もしない
                    }

                    HISTORY_TAB -> {}
                    // 履歴画面へ遷移　すでに履歴画面の場合何もしない
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    HOME_TAB -> {

                    }

                    HISTORY_TAB -> {}

                }
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                // 再度選択された場合
                when (tab?.position) {
                    HOME_TAB -> {

                    }

                    HISTORY_TAB -> {}

                }
            }

        })

        // 戻るボタンの制御
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // ここに実行するコードを書く
                // 無効化する場合は何も書かない
            }
        }
        onBackPressedDispatcher.addCallback(callback)
    }

    override fun onResume() {
        super.onResume()

        // DBから試験データを３件（最新の更新日時）を取得する
        try{
            val helper = ExamNotesSqlOpenHelper(this, DB_NAME, null, DB_VERSION)
            val orderBy = helper.UPDATED_AT + " DESC"
            val limit = "3"
            examList = helper.selectExams(null, null, orderBy, limit).toMutableList()
            viewModel.examList.value = examList

//            if (!::homeAdapter.isInitialized) {
//                homeAdapter = HomeAdapter(this, listOf())
//                binding.examsList.adapter = homeAdapter
//            }
//            homeAdapter.examList = examList
//            homeAdapter.notifyDataSetChanged()

        } catch(e: SQLException) {
            // FIXME firebaseに通知して、ユーザーには最新の試験データが取得できなかった。と伝える。
        }
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        // adapterにnullを入れて参照を切る
    }

    override fun onClickCreateExam(view: View) {
        // 試験入力画面へ遷移する
        val intent = Intent(this, ExamInsertActivity::class.java)
        intent.putExtra("fromCreateExam", true)
        startActivity(intent)
    }
}

// ViewHolderの作成、RecyclerViewのViewHolderを継承して（引数は子側のbindingのroot）生成。引数にはBindingするレイアウト（RecyclerViewを定義しているレイアウト）を指定する
class HomeViewHolder(val binding: ExamItemCellBinding) : RecyclerView.ViewHolder(binding.root)

// Adapterの作成、RecyclerViewのAdapterを継承して生成（型パラメータにviewHolderを指定）。引数には画面に表示する際に使用したいパラメータを定義する
class HomeAdapter(val activity: FragmentActivity, var examList: List<Exam>?) :
    RecyclerView.Adapter<HomeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val binding: ExamItemCellBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.exam_item_cell,
            parent,
            false
        )
        // bindingする、画面をinflateメソッドで生成。その後ViewHolderのインスタンスに渡し、return する
        return HomeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        // holderからbindingを取り出しlifecycleとviewModel、そしてadapterのインスタンスを生成しセットする
        val binding = holder.binding
        val exam = examList?.get(position)
        binding.lifecycleOwner = activity
        binding.viewModel = exam?.let { ExamItemCellViewModel(it) }


        // 生成後、画面要素がクリックされた場合のイベントをセットし、試験の状態によって画面遷移する
        binding.root.setOnClickListener {
            when (exam?.status) {
                examStatus.ProgressDraft.statusInt -> {
                    // 試験入力画面へ遷移
                }

                examStatus.CreatedDraft.statusInt -> {
                    // 解答画面へ遷移
                }

                examStatus.ProgressTest.statusInt -> {
                    // 解答画面へ遷移
                }

                examStatus.EnteringAnswerResults.statusInt -> {
                    // 解答結果入力確認画面へ遷移
                }

                examStatus.FinishTest.statusInt -> {
                    // 履歴詳細画面へ遷移
                }

                else -> {
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return examList?.count()?: 0
    }
}

class ExamItemCellViewModel(val exam: Exam) : ViewModel() {
    // FIXME データが入り次第以下をコメントアウトされている内容に戻す
    val lastUpDatedAt: String get() = "2023/12/10"
//    val lastUpDatedAt: String get() = DateTimeFormatter.ofPattern("yyyy'/'MM'/'dd").format((exam.updatedAt?: Date()).toInstant().atZone(
//        ZoneId.systemDefault()).toLocalDate())

    val examName: String get() = "Java SE11 Gold..."

    //    val examName: String get() = if (exam.name != null) exam.name!!.substring(0, Math.min(exam.name!!.length, 15)) + "..." else ""
    val examStatus: String get() = "実施済"
//    val examStatus: String get() = exam.statusString()
}

class HomeViewModel : ViewModel() {
    val examList: MutableLiveData<List<Exam>> = MutableLiveData()
    val nothingExamsVisibility = MediatorLiveData<Int>().also { result ->
        result.addSource(examList) {
            if (it.isEmpty()) {
                View.GONE
            } else {
                View.GONE
            }
        }
    }
}

interface HomeHandlers {
    fun onClickCreateExam(view: View)
}