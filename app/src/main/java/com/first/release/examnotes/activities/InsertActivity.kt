package com.first.release.examnotes.activities

import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RadioButton
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.first.release.examnotes.R
import com.first.release.examnotes.databinding.ActivityInsertBinding
import com.first.release.examnotes.model.Answer
import com.first.release.examnotes.model.AnswerKey
import com.first.release.examnotes.model.AnswerType
import com.first.release.examnotes.model.Exam

class InsertActivity: SourceActivity(), InsertEventHandlers {
    lateinit var binding: ActivityInsertBinding
    private var exam: Exam? = null
    private var examKey: String? = null
    private var answer: Answer? = null
    private var answerKey: String? = null
    val viewModel: InsertViewModel by lazy {
        ViewModelProvider(this).get(InsertViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_insert)
        binding.lifecycleOwner = this
        binding.handlers = this
        binding.viewModel = viewModel

        exam = if (SDK_INT < VERSION_CODES.TIRAMISU) {
           intent.getParcelableExtra("exam")
        } else {
            intent.getParcelableExtra("exam", Exam::class.java)
        }
        examKey = intent.getStringExtra("examKey")
        answer = if (SDK_INT < VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("answer")
        } else {
            intent.getParcelableExtra("answer", Answer::class.java)
        }
        answerKey =  intent.getStringExtra("answerKey")
        Log.v("DEBUG", "ログ examKey=${examKey} exam=${exam}")
        // viewModelの初期化
        viewModel.initViewModelField(exam, examKey, answer, answerKey)

        // ラジオボタンの状態をviewに反映
        if (answer != null && answerKey == AnswerKey.ANSWER_TYPE.answerKey) {
            when(answer?.answerType) {
                AnswerType.Single.answerType -> {
                    binding.insertActivityRadioSingle.isChecked = true
                }
                AnswerType.Insert.answerType -> {
                    binding.insertActivityRadioInsert.isChecked = true
                }
                AnswerType.Multiple.answerType -> {
                    binding.insertActivityRadioMultiple.isChecked = true
                }
            }
        }
        val callback = object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                setResult(resources.getInteger(R.integer.cancel))
                finish()
            }
        }
        this.onBackPressedDispatcher.addCallback(callback)
    }

    override fun onClickDecision(view: View) {
        // 入力または選択した内容を試験入力画面に反映する
        viewModel.setVal()
        intent.putExtra("exam", viewModel.exam.value)
        intent.putExtra("answer", viewModel.answer.value)
        setResult(resources.getInteger(R.integer.success), intent)
        finish()
    }

    override fun onClickCancel(view: View) {
        setResult(resources.getInteger(R.integer.cancel))
        finish()
    }

    override fun onClickRadioButton(view: View) {
        if (view is RadioButton) {
            // チェック状態を取得します
            view.isChecked
        }
        when (view.id) {
            R.id.insert_activity_radio_single -> {
                viewModel.answer.value?.answerType = AnswerType.Single.answerType
            }
            R.id.insert_activity_radio_insert -> {
                viewModel.answer.value?.answerType = AnswerType.Insert.answerType
            }
            R.id.insert_activity_radio_multiple -> {
                viewModel.answer.value?.answerType = AnswerType.Multiple.answerType
            }
        }
    }
}

class InsertViewModel() : ViewModel() {
    val exam: MutableLiveData<Exam?> = MutableLiveData()
    val answer: MutableLiveData<Answer?> = MutableLiveData()
    val examKey: MutableLiveData<String?> = MutableLiveData()
    val answerKey: MutableLiveData<String?> = MutableLiveData()

    val textFieldStringVisibility: MutableLiveData<Int> = MutableLiveData(View.GONE)
    val textFieldIntVisibility: MutableLiveData<Int> = MutableLiveData(View.GONE)

    val radioGroupVisibility: MutableLiveData<Int> = MutableLiveData(View.GONE)
    val selectedSingleButton: MutableLiveData<Boolean> = MutableLiveData()
    val insertButton: MutableLiveData<Boolean> = MutableLiveData()
    val selectedMultipleButton: MutableLiveData<Boolean> = MutableLiveData()

    val textFieldStringLabel: MutableLiveData<String> = MutableLiveData()
    val textFieldString: MutableLiveData<String> = MutableLiveData()

    val textFieldIntLabel: MutableLiveData<String> = MutableLiveData()
    val textFieldInt: MutableLiveData<String> = MutableLiveData()

    val symbol: MutableLiveData<String> = MutableLiveData()
    val symbolVisibility: MutableLiveData<Int> = MutableLiveData(View.GONE)

    fun initViewModelField(examParameter: Exam?, examKeyParameter: String?, answerParameter: Answer?, answerKeyParameter: String?) {
        exam.value = examParameter
        examKey.value = examKeyParameter
        answer.value = answerParameter
        answerKey.value = answerKeyParameter

        if (examKey.value != null) {
            val v = getExamValAndInitLabelAndVisibility()
            Log.v("DEBUG", "ログ v=${v} ${examKey.value}")
            if (v != null && v is String) {
                textFieldString.value = v.toString()
            } else if (v is Int) {
                textFieldInt.value = v.toString()

            }
        } else if (answerKey.value != null && answerKey.value == AnswerKey.ANSWER_TYPE.answerKey) {
            radioGroupVisibility.value = View.VISIBLE
            when(answer.value?.answerType) {
                AnswerType.Single.answerType -> {
                    selectedSingleButton.value = true
                }
                AnswerType.Insert.answerType -> {
                    insertButton.value = true
                }
                AnswerType.Multiple.answerType -> {
                    selectedMultipleButton.value = true
                }
            }
        } else if (answerKey.value != null) {
            val v = getAnswerValAndInitLabelAndVisibility()
            if (v != null && v is String) {
                textFieldString.value = v.toString()
            } else if (v is Int) {
                textFieldInt.value = v.toString()
            }
        }
    }

    private fun getExamValAndInitLabelAndVisibility(): Any? {
        when(examKey.value) {
            ExamKey.NAME.examKey -> {
                Log.v("DEBUG", "ログ一致")
                textFieldStringLabel.value = examKey.value
                textFieldStringVisibility.value = View.VISIBLE
                return exam.value?.name
            }
            ExamKey.REMARKS.examKey -> {
                textFieldStringLabel.value = examKey.value
                textFieldStringVisibility.value = View.VISIBLE
                return exam.value?.remarks
            }
            ExamKey.QUESTION_COUNT.examKey -> {
                textFieldIntLabel.value = examKey.value
                textFieldIntVisibility.value = View.VISIBLE
                return exam.value?.questionCount
            }
            ExamKey.PASS_LINE.examKey -> {
                textFieldIntLabel.value = examKey.value
                textFieldIntVisibility.value = View.VISIBLE
                symbol.value = "%"
                symbolVisibility.value = View.VISIBLE
                return exam.value?.passingLine
            }
            ExamKey.EXAM_MINUTES.examKey -> {
                textFieldIntLabel.value = examKey.value
                textFieldIntVisibility.value = View.VISIBLE
                symbol.value = "分"
                symbolVisibility.value = View.VISIBLE
                return exam.value?.examMinutes
            }
            else -> {
                return null
            }
        }
    }

    private fun setExamVal() {
        when(examKey.value) {
            ExamKey.NAME.examKey -> {
                exam.value?.name = textFieldString.value
            }
            ExamKey.REMARKS.examKey -> {
                exam.value?.remarks = textFieldString.value
            }
            ExamKey.QUESTION_COUNT.examKey -> {
                exam.value?.questionCount = textFieldInt.value?.toInt()
            }
            ExamKey.PASS_LINE.examKey -> {
                exam.value?.passingLine = textFieldInt.value?.toInt()
            }
            ExamKey.EXAM_MINUTES.examKey -> {
                exam.value?.examMinutes = textFieldInt.value?.toInt()
            }
        }
    }

    private fun getAnswerValAndInitLabelAndVisibility(): Any? {
        when(answerKey.value) {
            AnswerKey.ANSWER_TYPE.answerKey -> {
                textFieldIntLabel.value = answerKey.value
                textFieldIntVisibility.value = View.VISIBLE
                return answer.value?.answerType
            }
            AnswerKey.SELECT_COUNT.answerKey -> {
                textFieldIntLabel.value = answerKey.value
                textFieldIntVisibility.value = View.VISIBLE
                return answer.value?.selectCount
            }
            AnswerKey.ANSWER_SELECT.answerKey -> {
                textFieldStringLabel.value = answerKey.value
                textFieldStringVisibility.value = View.VISIBLE
                return answer.value?.answer
            }
            AnswerKey.ANSWER_INSERT.answerKey -> {
                textFieldStringLabel.value = answerKey.value
                textFieldStringVisibility.value = View.VISIBLE
                return answer.value?.answer
            }
            else -> {
                return null
            }
        }
    }

    private fun setAnswerVal() {
        when(answerKey.value) {
            AnswerKey.SELECT_COUNT.answerKey -> {
                answer.value?.selectCount = textFieldInt.value?.toInt()
            }
            AnswerKey.ANSWER_SELECT.answerKey -> {
                answer.value?.answer = textFieldString.value
            }
            AnswerKey.ANSWER_INSERT.answerKey -> {
                answer.value?.answer = textFieldString.value
            }
        }
    }

    fun setVal() {
        setExamVal()
        setAnswerVal()
    }
}

interface InsertEventHandlers {
    fun onClickDecision(view: View)
    fun onClickCancel(view: View)
    fun onClickRadioButton(view: View)
}