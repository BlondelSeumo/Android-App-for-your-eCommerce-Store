package com.iqonic.store.activity

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.iqonic.store.AppBaseActivity
import com.iqonic.store.R
import com.iqonic.store.adapter.BaseAdapter
import com.iqonic.store.models.Category
import com.iqonic.store.utils.Constants
import com.iqonic.store.utils.extensions.*
import kotlinx.android.synthetic.main.activity_category.*
import kotlinx.android.synthetic.main.item_viewproductgrid.view.*
import kotlinx.android.synthetic.main.toolbar.*

class CategoryActivity : AppBaseActivity() {

    private var mProductAdapter: BaseAdapter<Category>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)
        setToolbar(toolbar)
        title = getString(R.string.lbl_category)

        mProductAdapter = BaseAdapter(R.layout.item_viewcat, onBind = { view, model, _ ->
            if (model.image !== null) {
                view.ivProduct.loadImageFromUrl(model.image.src)
                view.ivProduct.visibility = View.VISIBLE
            } else {
                view.ivProduct.visibility = View.GONE
            }
            view.tvProductName.text = model.name.getHtmlString()
        })

        mProductAdapter?.onItemClick = { pos, view, model ->
            launchActivity<ViewAllProductActivity> {
                putExtra(Constants.KeyIntent.TITLE, model.name)
                putExtra(Constants.KeyIntent.VIEWALLID, Constants.viewAllCode.CATEGORY)
                putExtra(Constants.KeyIntent.KEYID, model.id)
            }
        }

        rvNewestProduct.apply {
            layoutManager = GridLayoutManager(this@CategoryActivity, 2)
            setHasFixedSize(true)
            adapter = mProductAdapter
            rvItemAnimation()
        }

        listAllCategory(0, onApiSuccess = {
            mProductAdapter?.addItems(it)
        })
    }
}
