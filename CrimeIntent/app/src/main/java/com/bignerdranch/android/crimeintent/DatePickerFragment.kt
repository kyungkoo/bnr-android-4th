package com.bignerdranch.android.crimeintent

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import java.util.Calendar
import java.util.Date
import java.util.GregorianCalendar

private const val ARG_DATE = "date"

class DatePickerFragment: DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val date: Date = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            arguments?.getSerializable(ARG_DATE, Date::class.java) as Date
        } else {
            arguments?.getSerializable(ARG_DATE) as Date
        }

        val dateListener = DatePickerDialog.OnDateSetListener {
                _: DatePicker, year: Int, month: Int, day: Int ->
            val resultDate: Date = GregorianCalendar(year, month, day).time
            val result = bundleOf(EXTRA_DATE to resultDate)
            parentFragmentManager.setFragmentResult(REQUEST_DATE, result)
        }

        val calendar = Calendar.getInstance()
        calendar.time = date
        val initialYear = calendar.get(Calendar.YEAR)
        val initialMonth = calendar.get(Calendar.MONTH)
        val initialDay = calendar.get(Calendar.DAY_OF_MONTH)

        return DatePickerDialog(
            requireContext(),
            dateListener,
            initialYear,
            initialMonth,
            initialDay
        )
    }

    companion object {
        const val REQUEST_DATE = "request_date_key"
        const val EXTRA_DATE = "com.bignerdranch.android.criminalintent.date"

        fun newInstance(date: Date): DatePickerFragment {
            val args = Bundle().apply {
                putSerializable(ARG_DATE, date)
            }

            return DatePickerFragment().apply {
                arguments = args
            }
        }
    }
}