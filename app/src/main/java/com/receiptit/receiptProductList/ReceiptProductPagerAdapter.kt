package com.receiptit.receiptProductList

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.receiptit.R
import com.receiptit.network.model.product.ProductInfo
import com.receiptit.network.model.receipt.ReceiptInfo



private val TAB_TITLES = arrayOf(
    R.string.receipt_product_list_receipt_edit_info_tab,
    R.string.receipt_product_list_receipt_product_list_tab,
    R.string.receipt_product_list_receipt_receipt_image_tab
)

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
class ReceiptProductPageAdapter(private val context: Context, fm: FragmentManager,
                                private val receiptInfo: ReceiptInfo) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return if (position == 0) {
            EditReceiptFragment.newInstance(receiptInfo)
        } else if (position == 1) {
            ProductListFragment.newInstance(receiptInfo.receipt_id)
        } else {
            PlaceholderFragment.newInstance(position + 1)
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return context.resources.getString(TAB_TITLES[position])
    }

    override fun getCount(): Int {
        return TAB_TITLES.size
    }
}