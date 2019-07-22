package com.receiptit.navDrawer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.receiptit.R
import com.receiptit.network.model.receipt.ExpenseRetrieveResponse
import com.highsoft.highcharts.common.hichartsclasses.*
import kotlinx.android.synthetic.main.activity_expense_compare_chart.*
import com.highsoft.highcharts.common.hichartsclasses.HIOptions
import com.highsoft.highcharts.common.hichartsclasses.HIChart
import com.highsoft.highcharts.common.hichartsclasses.HITitle
import java.util.Arrays.asList
import kotlin.collections.ArrayList
import java.util.Collections.singletonList
import com.highsoft.highcharts.common.hichartsclasses.HIPie
import com.highsoft.highcharts.common.hichartsclasses.HIPlotOptions
import com.highsoft.highcharts.common.hichartsclasses.HITooltip
import com.receiptit.util.TimeStringFormatter
import java.util.*
import kotlin.collections.HashMap
import java.text.SimpleDateFormat
import com.highsoft.highcharts.common.hichartsclasses.HIExporting



private const val EXPENSE_LIST = "EXPENSE_LIST"

class ExpenseCompareChartActivity : AppCompatActivity(){

    private lateinit var expenseList : ArrayList<ExpenseRetrieveResponse>
    private var fromDateList = ArrayList<Date>()
    private var toDateList = ArrayList<Date>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expense_compare_chart)
        expenseList = intent.getSerializableExtra(EXPENSE_LIST) as ArrayList<ExpenseRetrieveResponse>
        sortDateOfResponse()
        displayChart()
    }

    private fun displayChart(){
        val options = HIOptions()

        val chart = HIChart()
        chart.type = "pie"
        chart.plotShadow = false
        options.chart = chart

        val minFromDate = formatDate(fromDateList[0])
        val maxToDate = formatDate(toDateList[toDateList.size-1])

        val title = HITitle()
        if (checkExpenseDataAvailable())
            title.text = "Browser periods of expense from $minFromDate to $maxToDate"
        else
            title.text = "Expense from all periods are 0"
        options.title = title

        val tooltip = HITooltip()
        tooltip.pointFormat = "{series.name}: <b>\${point.y:.2f}</b>, <b>{point.percentage:.1f}%</b> of total"
        options.tooltip = tooltip

        val plotOptions = HIPlotOptions()
        plotOptions.pie = HIPie()
        plotOptions.pie.allowPointSelect = true
        plotOptions.pie.cursor = "pointer"

        val dataLabel = HIDataLabelsOptionsObject()
        dataLabel.enabled = true

        val dataLabels = ArrayList<HIDataLabelsOptionsObject>()
        dataLabels.add(dataLabel)

        plotOptions.pie.dataLabels = dataLabels
        plotOptions.pie.showInLegend = true

        options.plotOptions = plotOptions

        val pie = HIPie()
        pie.name = "Expense"

        val map1 = HashMap<String, Any>()
        map1["name"] = formatPeriod(expenseList[0].startDate, expenseList[0].endDate)
        map1["y"] = expenseList[0].totalExpense

        val map2 = HashMap<String, Any>()
        map2["name"] = formatPeriod(expenseList[1].startDate, expenseList[1].endDate)
        map2["y"] = expenseList[1].totalExpense
        map2["sliced"] = true
        map2["selected"] = true

        val map3 = HashMap<String, Any>()
        map3["name"] = formatPeriod(expenseList[2].startDate, expenseList[2].endDate)
        map3["y"] = expenseList[2].totalExpense

        val map4 = HashMap<String, Any>()
        map4["name"] = formatPeriod(expenseList[3].startDate, expenseList[3].endDate)
        map4["y"] = expenseList[3].totalExpense

        val map5 = HashMap<String, Any>()
        map5["name"] = formatPeriod(expenseList[4].startDate, expenseList[4].endDate)
        map5["y"] = expenseList[4].totalExpense

        pie.data = ArrayList(asList(map1, map2, map3, map4, map5))
        options.series = ArrayList(singletonList(pie))
        hc.options = options

        hc.options.exporting = HIExporting()
        hc.options.exporting.enabled = false
    }

    private fun sortDateOfResponse() {
        expenseList.forEach {
            val fromDateString = TimeStringFormatter.format(it.startDate)
            val toDateString = TimeStringFormatter.format(it.endDate)

            val pattern = "yyyy-MM-dd"
            val fromDate = SimpleDateFormat(pattern).parse(fromDateString)
            val toDate = SimpleDateFormat(pattern).parse(toDateString)

            fromDateList.add(fromDate)
            toDateList.add(toDate)

            val comparator = dateComparator()

            Collections.sort(fromDateList, comparator)
            Collections.sort(toDateList, comparator)
        }
    }

    private fun formatDate(date: Date):String {
        val dateFormat = SimpleDateFormat("yyyy.MM.dd")
        return  dateFormat.format(date)
    }

    private fun formatPeriod(fromDate: String, toDate: String) : String {
        val from = TimeStringFormatter.format(fromDate).replace("-", ".")
        val to = TimeStringFormatter.format(toDate).replace("-", ".")
        return "Period: $from  - $to"
    }

    private fun dateComparator(): Comparator<Date> {
        return Comparator { date1, date2 -> date1.compareTo(date2) }
    }

    private fun checkExpenseDataAvailable(): Boolean{
        return expenseList[0].totalExpense != 0.0 || expenseList[1].totalExpense != 0.0
                || expenseList[2].totalExpense != 0.0 || expenseList[3].totalExpense != 0.0 ||
                expenseList[4].totalExpense != 0.0
    }

}
