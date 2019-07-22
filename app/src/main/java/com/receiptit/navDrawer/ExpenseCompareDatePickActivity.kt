package com.receiptit.navDrawer

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import com.borax12.materialdaterangepicker.date.DatePickerDialog
import com.google.android.material.textfield.TextInputEditText
import com.receiptit.R
import com.receiptit.network.ServiceGenerator
import com.receiptit.network.model.receipt.ExpenseRetrieveResponse
import com.receiptit.network.model.user.UserInfo
import com.receiptit.network.retrofit.ResponseErrorBody
import com.receiptit.network.retrofit.RetrofitCallback
import com.receiptit.network.retrofit.RetrofitCallbackListener
import com.receiptit.network.service.ReceiptApi
import kotlinx.android.synthetic.main.activity_expense_compare_date_pick.*
import retrofit2.Call
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

private const val USER_INFO = "USER_INFO"
private const val EXPENSE_LIST = "EXPENSE_LIST"

class ExpenseCompareDatePickActivity : AppCompatActivity() {

    private var userId: Int? = null

    private var cal1From = Calendar.getInstance()
    private val cal1To = Calendar.getInstance()

    private var cal2From = Calendar.getInstance()
    private var cal2To = Calendar.getInstance()

    private var cal3From = Calendar.getInstance()
    private var cal3To = Calendar.getInstance()

    private var cal4From = Calendar.getInstance()
    private var cal4To = Calendar.getInstance()

    private var cal5From = Calendar.getInstance()
    private var cal5To = Calendar.getInstance()

    private var expenseResult = ArrayList<ExpenseRetrieveResponse>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expense_compare_date_pick)
        userId = (intent.getSerializableExtra(USER_INFO) as UserInfo).user_id
        setupDatePickerDialog()
    }

    private fun setupDatePickerDialog() {

        val date1 = DatePickerDialog.newInstance(
            { _, year, monthOfYear, dayOfMonth, yearEnd, monthOfYearEnd, dayOfMonthEnd ->
                cal1From.set(Calendar.YEAR, year)
                cal1From.set(Calendar.MONTH, monthOfYear)
                cal1From.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                cal1To.set(Calendar.YEAR, yearEnd)
                cal1To.set(Calendar.MONTH, monthOfYearEnd)
                cal1To.set(Calendar.DAY_OF_MONTH, dayOfMonthEnd)
                updateLabel(ed_expense_compare_select_period_period_1_value, cal1From, cal1To)
            },
            cal1From.get(Calendar.YEAR),
            cal1From.get(Calendar.MONTH),
            cal1From.get(Calendar.DAY_OF_MONTH)
        )

        // Hacky way to get around
        // The Button type is forced to be MaterialButton because the theme is MaterialComponent?
        // Therefore hte background color of button is always accent color
        date1.accentColor = getColor(android.R.color.widget_edittext_dark)!!


        ed_expense_compare_select_period_period_1_value.setOnClickListener {
            fragmentManager?.let {
                date1.show(fragmentManager, "")
            }
        }

        val date2 = DatePickerDialog.newInstance(
            { _, year, monthOfYear, dayOfMonth, yearEnd, monthOfYearEnd, dayOfMonthEnd ->
                cal2From.set(Calendar.YEAR, year)
                cal2From.set(Calendar.MONTH, monthOfYear)
                cal2From.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                cal2To.set(Calendar.YEAR, yearEnd)
                cal2To.set(Calendar.MONTH, monthOfYearEnd)
                cal2To.set(Calendar.DAY_OF_MONTH, dayOfMonthEnd)
                updateLabel(ed_expense_compare_select_period_period_2_value, cal2From, cal2To)
            },
            cal2From.get(Calendar.YEAR),
            cal2From.get(Calendar.MONTH),
            cal2From.get(Calendar.DAY_OF_MONTH)
        )

        // Hacky way to get around
        // The Button type is forced to be MaterialButton because the theme is MaterialComponent?
        // Therefore hte background color of button is always accent color
        date2.accentColor = getColor(android.R.color.widget_edittext_dark)


        ed_expense_compare_select_period_period_2_value.setOnClickListener {
            fragmentManager?.let {
                date2.show(fragmentManager, "")
            }
        }

        val date3 = DatePickerDialog.newInstance(
            { _, year, monthOfYear, dayOfMonth, yearEnd, monthOfYearEnd, dayOfMonthEnd ->
                cal3From.set(Calendar.YEAR, year)
                cal3From.set(Calendar.MONTH, monthOfYear)
                cal3From.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                cal3To.set(Calendar.YEAR, yearEnd)
                cal3To.set(Calendar.MONTH, monthOfYearEnd)
                cal3To.set(Calendar.DAY_OF_MONTH, dayOfMonthEnd)
                updateLabel(ed_expense_compare_select_period_period_3_value, cal3From, cal3To)
            },
            cal3From.get(Calendar.YEAR),
            cal3From.get(Calendar.MONTH),
            cal3From.get(Calendar.DAY_OF_MONTH)
        )

        // Hacky way to get around
        // The Button type is forced to be MaterialButton because the theme is MaterialComponent?
        // Therefore hte background color of button is always accent color
        date3.accentColor = getColor(android.R.color.widget_edittext_dark)


        ed_expense_compare_select_period_period_3_value.setOnClickListener {
            fragmentManager?.let {
                date3.show(fragmentManager, "")
            }
        }

        val date4 = DatePickerDialog.newInstance(
            { _, year, monthOfYear, dayOfMonth, yearEnd, monthOfYearEnd, dayOfMonthEnd ->
                cal4From.set(Calendar.YEAR, year)
                cal4From.set(Calendar.MONTH, monthOfYear)
                cal4From.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                cal4To.set(Calendar.YEAR, yearEnd)
                cal4To.set(Calendar.MONTH, monthOfYearEnd)
                cal4To.set(Calendar.DAY_OF_MONTH, dayOfMonthEnd)
                updateLabel(ed_expense_compare_select_period_period_4_value, cal4From, cal4To)
            },
            cal4From.get(Calendar.YEAR),
            cal4From.get(Calendar.MONTH),
            cal4From.get(Calendar.DAY_OF_MONTH)
        )

        // Hacky way to get around
        // The Button type is forced to be MaterialButton because the theme is MaterialComponent?
        // Therefore hte background color of button is always accent color
        date4.accentColor = getColor(android.R.color.widget_edittext_dark)


        ed_expense_compare_select_period_period_4_value.setOnClickListener {
            fragmentManager?.let {
                date4.show(fragmentManager, "")
            }
        }

        val date5 = DatePickerDialog.newInstance(
            { _, year, monthOfYear, dayOfMonth, yearEnd, monthOfYearEnd, dayOfMonthEnd ->
                cal5From.set(Calendar.YEAR, year)
                cal5From.set(Calendar.MONTH, monthOfYear)
                cal5From.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                cal5To.set(Calendar.YEAR, yearEnd)
                cal5To.set(Calendar.MONTH, monthOfYearEnd)
                cal5To.set(Calendar.DAY_OF_MONTH, dayOfMonthEnd)
                updateLabel(ed_expense_compare_select_period_period_5_value, cal5From, cal5To)
            },
            cal5From.get(Calendar.YEAR),
            cal5From.get(Calendar.MONTH),
            cal5From.get(Calendar.DAY_OF_MONTH)
        )

        // Hacky way to get around
        // The Button type is forced to be MaterialButton because the theme is MaterialComponent?
        // Therefore hte background color of button is always accent color
        date5.accentColor = getColor(android.R.color.widget_edittext_dark)


        ed_expense_compare_select_period_period_5_value.setOnClickListener {
            fragmentManager?.let {
                date5.show(fragmentManager, "")
            }
        }
    }

    private fun updateLabel(editText: TextInputEditText?, calendarFrom: Calendar, calendarTo: Calendar) {
        editText?.setText(calendarFormatDisplay(calendarFrom) + " - " + calendarFormatDisplay(calendarTo))
    }

    private fun calendarFormatDisplay(calendar: Calendar): String {
        val myFormat = "yyyy.MM.dd"
        val sdf = SimpleDateFormat(myFormat)
        return sdf.format(calendar.time)
    }

    private fun calendarFormat(calendar: Calendar): String {
        val myFormat = "yyyy-MM-dd"
        val sdf = SimpleDateFormat(myFormat)
        return sdf.format(calendar.time)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_expense_compare_select_period_submit, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return if (item?.itemId == R.id.menu_expense_compare_select_period_submit) {
            if (validInput()) {
                submitExpenseQueries()
            } else {
                showError("Invalid Input")
            }
            true
        } else
            super.onOptionsItemSelected(item)
    }

    private fun validInput(): Boolean {
        val period1 = ed_expense_compare_select_period_period_1_value.text.toString()
        val period2 = ed_expense_compare_select_period_period_2_value.text.toString()
        val period3 = ed_expense_compare_select_period_period_3_value.text.toString()
        val period4 = ed_expense_compare_select_period_period_4_value.text.toString()
        val period5 = ed_expense_compare_select_period_period_5_value.text.toString()

        return period1 != "" && period2 != "" && period3 != "" && period4 != "" && period5 != ""
    }

    //TODO: introduce RxJava to handle multiple requests later
    private fun submitExpenseQueries() {
        call1Query()
    }

    private fun call1Query() {
        val receiptService = ServiceGenerator.createService(ReceiptApi::class.java)
        val call1 =
            userId?.let { receiptService.getExpenseDuringGivenPeriod(it, calendarFormat(cal1From), calendarFormat(cal1To)) }
        call1?.enqueue(RetrofitCallback(object : RetrofitCallbackListener<ExpenseRetrieveResponse> {
            override fun onResponseSuccess(
                call: Call<ExpenseRetrieveResponse>?,
                response: Response<ExpenseRetrieveResponse>?
            ) {
                val result = response?.body()
                result?.let { expenseResult.add(it) }
                call2Query()
            }

            override fun onResponseError(
                call: Call<ExpenseRetrieveResponse>?,
                response: Response<ExpenseRetrieveResponse>?
            ) {
                val message = response?.errorBody()?.string()?.let { ResponseErrorBody(it) }
                message?.getErrorMessage()?.let { showError(it) }
            }

            override fun onFailure(call: Call<ExpenseRetrieveResponse>?, t: Throwable?) {
                t?.message?.let { showError(it) }
            }
        }))
    }

    private fun call2Query() {
        val receiptService = ServiceGenerator.createService(ReceiptApi::class.java)
        val call2 =
            userId?.let { receiptService.getExpenseDuringGivenPeriod(it, calendarFormat(cal2From), calendarFormat(cal2To)) }
        call2?.enqueue(RetrofitCallback(object : RetrofitCallbackListener<ExpenseRetrieveResponse> {
            override fun onResponseSuccess(
                call: Call<ExpenseRetrieveResponse>?,
                response: Response<ExpenseRetrieveResponse>?
            ) {
                val result = response?.body()
                result?.let { expenseResult.add(it) }
                call3Query()
            }

            override fun onResponseError(
                call: Call<ExpenseRetrieveResponse>?,
                response: Response<ExpenseRetrieveResponse>?
            ) {
                val message = response?.errorBody()?.string()?.let { ResponseErrorBody(it) }
                message?.getErrorMessage()?.let { showError(it) }
            }

            override fun onFailure(call: Call<ExpenseRetrieveResponse>?, t: Throwable?) {
                t?.message?.let { showError(it) }
            }
        }))
    }

    private fun call3Query() {
        val receiptService = ServiceGenerator.createService(ReceiptApi::class.java)

        val call3 =
            userId?.let { receiptService.getExpenseDuringGivenPeriod(it, calendarFormat(cal3From), calendarFormat(cal3To)) }
        call3?.enqueue(RetrofitCallback(object : RetrofitCallbackListener<ExpenseRetrieveResponse> {
            override fun onResponseSuccess(
                call: Call<ExpenseRetrieveResponse>?,
                response: Response<ExpenseRetrieveResponse>?
            ) {
                val result = response?.body()
                result?.let { expenseResult.add(it) }
                call4Query()
            }

            override fun onResponseError(
                call: Call<ExpenseRetrieveResponse>?,
                response: Response<ExpenseRetrieveResponse>?
            ) {
                val message = response?.errorBody()?.string()?.let { ResponseErrorBody(it) }
                message?.getErrorMessage()?.let { showError(it) }
            }

            override fun onFailure(call: Call<ExpenseRetrieveResponse>?, t: Throwable?) {
                t?.message?.let { showError(it) }
            }
        }))
    }

    private fun call4Query() {
        val receiptService = ServiceGenerator.createService(ReceiptApi::class.java)
        val call4 =
            userId?.let { receiptService.getExpenseDuringGivenPeriod(it, calendarFormat(cal4From), calendarFormat(cal4To)) }
        call4?.enqueue(RetrofitCallback(object : RetrofitCallbackListener<ExpenseRetrieveResponse> {
            override fun onResponseSuccess(
                call: Call<ExpenseRetrieveResponse>?,
                response: Response<ExpenseRetrieveResponse>?
            ) {
                val result = response?.body()
                result?.let { expenseResult.add(it) }
                call5Query()
            }

            override fun onResponseError(
                call: Call<ExpenseRetrieveResponse>?,
                response: Response<ExpenseRetrieveResponse>?
            ) {
                val message = response?.errorBody()?.string()?.let { ResponseErrorBody(it) }
                message?.getErrorMessage()?.let { showError(it) }
            }

            override fun onFailure(call: Call<ExpenseRetrieveResponse>?, t: Throwable?) {
                t?.message?.let { showError(it) }
            }
        }))
    }

    private fun call5Query() {
        val receiptService = ServiceGenerator.createService(ReceiptApi::class.java)
        val call5 =
            userId?.let { receiptService.getExpenseDuringGivenPeriod(it, calendarFormat(cal5From), calendarFormat(cal5To)) }
        call5?.enqueue(RetrofitCallback(object : RetrofitCallbackListener<ExpenseRetrieveResponse> {
            override fun onResponseSuccess(
                call: Call<ExpenseRetrieveResponse>?,
                response: Response<ExpenseRetrieveResponse>?
            ) {
                val result = response?.body()
                result?.let { expenseResult.add(it) }
                displayChart()
            }

            override fun onResponseError(
                call: Call<ExpenseRetrieveResponse>?,
                response: Response<ExpenseRetrieveResponse>?
            ) {
                val message = response?.errorBody()?.string()?.let { ResponseErrorBody(it) }
                message?.getErrorMessage()?.let { showError(it) }
            }

            override fun onFailure(call: Call<ExpenseRetrieveResponse>?, t: Throwable?) {
                t?.message?.let { showError(it) }
            }
        }))
    }

    private fun showError(error: String){
        Toast.makeText(this, getString(R.string.menu_expense_submit_error) + error, Toast.LENGTH_SHORT).show()
    }

    private fun displayChart() {
        val intent = Intent(this, ExpenseCompareChartActivity::class.java)
        intent.putExtra(EXPENSE_LIST, expenseResult)
        startActivity(intent)
    }

}
