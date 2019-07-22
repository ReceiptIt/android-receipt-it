package com.receiptit.navDrawer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import com.receiptit.R
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.borax12.materialdaterangepicker.date.DatePickerDialog
import com.google.android.material.textfield.TextInputEditText
import com.receiptit.network.ServiceGenerator
import com.receiptit.network.model.product.ProductInfo
import com.receiptit.network.model.receipt.ExpenseRetrieveResponse
import com.receiptit.network.model.report.ReportGetProductResponse
import com.receiptit.network.model.user.UserInfo
import com.receiptit.network.retrofit.ResponseErrorBody
import com.receiptit.network.retrofit.RetrofitCallback
import com.receiptit.network.retrofit.RetrofitCallbackListener
import com.receiptit.network.service.ReceiptApi
import com.receiptit.network.service.ReportApi
import com.receiptit.receiptProductList.ReceiptProductListRecyclerViewAdapter
import com.receiptit.util.TimeStringFormatter
import kotlinx.android.synthetic.main.activity_expense_report.*
import retrofit2.Call
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.Comparator

private const val USER_INFO = "USER_INFO"

class ExpenseReportActivity : AppCompatActivity(), ReceiptProductListRecyclerViewAdapter.OnReceiptProductListItemClickListener {

    private lateinit var oldSortOption: String
    private var spinnerValueChange = false
    private var userId: Int? = null
    private lateinit var currentPeriod: String
    private var calFrom = Calendar.getInstance()
    private var calTo = Calendar.getInstance()
    private var timeComparator = timeComparator()
    private var priceComparator = priceComparator()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expense_report)
        userId = (intent.getSerializableExtra(USER_INFO) as UserInfo).user_id

        setUpDateRangePicker()
        setupSpinner()
    }

    private fun setupSpinner() {
        val spinner = sp_expense_report_select_period_sort_by_spinner
        ArrayAdapter.createFromResource(
            this,
            R.array.expense_report_sort_spinner_list,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
            val defaultSortOption = "Price"
            val spinnerPosition = adapter.getPosition(defaultSortOption)
            spinner.setSelection(spinnerPosition)
            oldSortOption = defaultSortOption
        }

        spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                val currentOption = spinner.getItemAtPosition(pos).toString()
                if (oldSortOption == currentOption) {
                    spinnerValueChange = false
                } else {
                    spinnerValueChange = true
                    oldSortOption = currentOption
                }
                if (validInput())
                    fetchData()
            }
        }

    }

    private fun setUpDateRangePicker() {
        val date = DatePickerDialog.newInstance(
            { _, year, monthOfYear, dayOfMonth, yearEnd, monthOfYearEnd, dayOfMonthEnd ->
                calFrom.set(Calendar.YEAR, year)
                calFrom.set(Calendar.MONTH, monthOfYear)
                calFrom.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                calTo.set(Calendar.YEAR, yearEnd)
                calTo.set(Calendar.MONTH, monthOfYearEnd)
                calTo.set(Calendar.DAY_OF_MONTH, dayOfMonthEnd)
                updateLabel(ed_expense_report_select_period_period_value, calFrom, calTo)
            },
            calFrom.get(Calendar.YEAR),
            calFrom.get(Calendar.MONTH),
            calFrom.get(Calendar.DAY_OF_MONTH)
        )

        // Hacky way to get around
        // The Button type is forced to be MaterialButton because the theme is MaterialComponent?
        // Therefore hte background color of button is always accent color
        date.accentColor = getColor(android.R.color.widget_edittext_dark)!!


        ed_expense_report_select_period_period_value.setOnClickListener {
            fragmentManager?.let {
                date.show(fragmentManager, "")
            }
        }

        ed_expense_report_select_period_period_value.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(text: Editable?) {
                if (isDatePickerValueChanged(text.toString()))
                    fetchData()
            }

            override fun beforeTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
                currentPeriod = text.toString()
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

        })
    }

    private fun updateLabel(editText: TextInputEditText?, calendarFrom: Calendar, calendarTo: Calendar) {
        val period = calendarFormatDisplay(calendarFrom) + " - " + calendarFormatDisplay(calendarTo)
        editText?.setText(period)
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

    private fun fetchData() {
        refreshTotalExpense()
    }

    private fun refreshTotalExpense() {
        val receiptService = ServiceGenerator.createService(ReceiptApi::class.java)
        val call1 =
            userId?.let { receiptService.getExpenseDuringGivenPeriod(it, calendarFormat(calFrom), calendarFormat(calTo)) }
        call1?.enqueue(RetrofitCallback(object : RetrofitCallbackListener<ExpenseRetrieveResponse> {
            override fun onResponseSuccess(
                call: Call<ExpenseRetrieveResponse>?,
                response: Response<ExpenseRetrieveResponse>?
            ) {
                val result = response?.body()
                tv_expense_report_total_expense_value.text = result?.totalExpense.toString()
                refreshProductList()
            }

            override fun onResponseError(
                call: Call<ExpenseRetrieveResponse>?,
                response: Response<ExpenseRetrieveResponse>?
            ) {
                val message = response?.errorBody()?.string()?.let { ResponseErrorBody(it) }
                message?.getErrorMessage()?.let { showGetExpenseError(it) }
            }

            override fun onFailure(call: Call<ExpenseRetrieveResponse>?, t: Throwable?) {
                t?.message?.let { showGetExpenseError(it) }
            }
        }))
    }

    private fun refreshProductList() {
        val reportService = ServiceGenerator.createService(ReportApi::class.java)
        val fromDate = calendarFormat(calFrom)
        val toDate = calendarFormat(calTo)
        val call = userId?.let { reportService.getProductListDuringGivenPeriod(it, fromDate, toDate) }
        call?.enqueue(RetrofitCallback(object : RetrofitCallbackListener<ReportGetProductResponse> {
            override fun onResponseSuccess(
                call: Call<ReportGetProductResponse>?,
                response: Response<ReportGetProductResponse>?
            ) {
                val list = response?.body()?.products
                list?.let { createList(it) }
            }

            override fun onResponseError(
                call: Call<ReportGetProductResponse>?,
                response: Response<ReportGetProductResponse>?
            ) {
                val message = response?.errorBody()?.string()?.let { ResponseErrorBody(it) }
                message?.getErrorMessage()?.let { showGenerateProductError(it) }
            }

            override fun onFailure(call: Call<ReportGetProductResponse>?, t: Throwable?) {
                t?.message?.let { showGenerateProductError(it) }
            }

        }))
    }

    private fun showGenerateProductError(error: String){
        Toast.makeText(this, getString(R.string.expense_report_error) + error, Toast.LENGTH_SHORT).show()
    }

    private fun showGetExpenseError(error: String){
        Toast.makeText(this, getString(R.string.menu_expense_submit_error) + error, Toast.LENGTH_SHORT).show()
    }

    private fun validInput(): Boolean {
        val period = ed_expense_report_select_period_period_value.text.toString()
        return period != "" && isSpinnerValueChanged()
    }

    private fun createList(products: ArrayList<ProductInfo>) {
        if (products.isNotEmpty()) {
            empty_view.visibility = View.GONE
            sortProducts(products)
            val recyclerView = rv_expense_report_product_list
            recyclerView?.layoutManager = LinearLayoutManager(this)
            val adapter =  ExpenseReportListRecyclerViewAdapter(products)
            recyclerView?.adapter = adapter
        } else {
            empty_view.visibility = View.VISIBLE
        }
    }

    override fun onReceiptProductListItemClick(productId: Int) {
    }

    override fun onReceiptProductListItemLongClick(productId: Int) {
    }

    private fun isSpinnerValueChanged(): Boolean {
        return spinnerValueChange
    }

    private fun isDatePickerValueChanged(period: String): Boolean {
        return period != currentPeriod
    }

    private fun timeComparator(): Comparator<ProductInfo> {
        return Comparator { product1, product2 ->
            val dateStr1 = product1?.purchase_date?.let { TimeStringFormatter.format(it) }
            val dateStr2 = product2?.purchase_date?.let { TimeStringFormatter.format(it) }

            val pattern = "yyyy-MM-dd"
            val date1 = SimpleDateFormat(pattern).parse(dateStr1)
            val date2 = SimpleDateFormat(pattern).parse(dateStr2)
            date1.compareTo(date2)
        }
    }

    private fun priceComparator(): Comparator<ProductInfo> {
        return Comparator { product1, product2 ->
            product1.price.compareTo(product2.price)
        }
    }

    private fun sortProducts(products: ArrayList<ProductInfo>) {
        val spinner = sp_expense_report_select_period_sort_by_spinner
        if (spinner.selectedItem.toString() == "Price")
            Collections.sort(products, priceComparator)
        else
            Collections.sort(products, timeComparator)
    }

}
