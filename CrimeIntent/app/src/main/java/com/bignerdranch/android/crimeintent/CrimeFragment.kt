package com.bignerdranch.android.crimeintent

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import java.util.Date
import java.util.UUID

private const val TAG = "CrimeFragment"
private const val ARG_CRIME_ID = "crime_id"
private const val DIALOG_DATE = "DialogDate"

class CrimeFragment: Fragment() {
    private lateinit var crime: Crime
    private lateinit var titleField: EditText
    private lateinit var dateButton: Button
    private lateinit var solvedCheckBox: CheckBox

    private val crimeDetailViewModel: CrimeDetailViewModel by lazy {
        ViewModelProvider(this)[CrimeDetailViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crime = Crime()
        val crimeId: UUID = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            arguments?.getSerializable(ARG_CRIME_ID, UUID::class.java) ?: UUID.randomUUID()
        } else {
            arguments?.getSerializable(ARG_CRIME_ID) as UUID
        }

        crimeDetailViewModel.loadCrime(crimeId)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_crime, container, false)

        titleField = view.findViewById(R.id.crime_title)
        dateButton = view.findViewById(R.id.crime_date)
        solvedCheckBox = view.findViewById(R.id.crime_solved)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        crimeDetailViewModel.crimeLiveData.observe(
            viewLifecycleOwner,
            { crime ->
                crime?.let {
                    this.crime = crime
                    updateUI()
                }
            }
        )

        parentFragmentManager.setFragmentResultListener(
            DatePickerFragment.REQUEST_DATE,
            viewLifecycleOwner,
            { requestKey, bundle ->

                Log.d(TAG, "FragmentResultListener $requestKey")

                when (requestKey) {
                    DatePickerFragment.REQUEST_DATE -> {
                        val date = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                            bundle.getSerializable(DatePickerFragment.EXTRA_DATE, Date::class.java) as Date
                        } else {
                            bundle.getSerializable(DatePickerFragment.EXTRA_DATE) as Date
                        }
                        crime.date = date
                        updateUI()
                    }
                }
            }
        )
    }

    private fun updateUI() {
        titleField.setText(crime.title)
        dateButton.text = crime.date.toString()
        solvedCheckBox.apply {
            isChecked = crime.isSolved
            jumpDrawablesToCurrentState()
        }
    }

    override fun onStart() {
        super.onStart()

        val titleWatcher = object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                // 여기서는 이 함수의 실행 코드를 구현할 필요가 없어서 비워 둔다
            }

            override fun onTextChanged(sequence: CharSequence?, start: Int, before: Int, count: Int) {
                crime.title = sequence.toString()
            }

            override fun afterTextChanged(p0: Editable?) {
                // 여기서는 이 함수의 실행 코드를 구현할 필요가 없어서 비워 둔다
            }
        }

        titleField.addTextChangedListener(titleWatcher)

        solvedCheckBox.setOnCheckedChangeListener { _, isChecked ->
            crime.isSolved = isChecked
        }

        dateButton.setOnClickListener {
            DatePickerFragment.newInstance(crime.date).apply {
                show(this@CrimeFragment.parentFragmentManager, DIALOG_DATE)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        crimeDetailViewModel.saveCrime(crime)
    }

    companion object {
        fun newInstance(crimeId: UUID): CrimeFragment {
            val args = Bundle().apply {
                putSerializable(ARG_CRIME_ID, crimeId)
            }

            return CrimeFragment().apply {
                arguments = args
            }
        }
    }
}