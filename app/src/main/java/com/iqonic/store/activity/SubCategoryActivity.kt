package com.iqonic.store.activity

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.iqonic.store.AppBaseActivity
import com.iqonic.store.R
import com.iqonic.store.adapter.BaseAdapter
import com.iqonic.store.models.Category
import com.iqonic.store.models.RequestModel
import com.iqonic.store.models.StoreProductModel
import com.iqonic.store.utils.Constants
import com.iqonic.store.utils.extensions.*
import kotlinx.android.synthetic.main.activity_sub_category.*
import kotlinx.android.synthetic.main.item_subcategory.view.*
import kotlinx.android.synthetic.main.item_viewproductgrid.view.*
import kotlinx.android.synthetic.main.layout_nodata.*
import kotlinx.android.synthetic.main.toolbar.*

class SubCategoryActivity : AppBaseActivity() {
    /*    private var mColorArray =
                intArrayOf(R.color.cat_1, R.color.cat_2, R.color.cat_3, R.color.cat_4, R.color.cat_5)*/
    var image: String = ""
    private var mCategoryId: Int = 0

    private val mSubCategoryAdapter =
        BaseAdapter<Category>(R.layout.item_subcategory, onBind = { view, model, position ->
            view.tvSubCategory.text = model.name.getHtmlString()
            if (model.image != null) {
                if (model.image.src.isNotEmpty()) {
                    view.ivProducts.loadImageFromUrl(model.image.src)
                }
            }
            // view.llMain.setStrokedBackground(color(mColorArray[position % mColorArray.size]), alpha = 50f)
            view.tvSubCategory.setTextColor(color(R.color.textColorSecondary))
            6
            view.onClick {
                launchActivity<SubCategoryActivity> {
                    putExtra(Constants.KeyIntent.TITLE, model.name)
                    putExtra(Constants.KeyIntent.VIEWALLID, Constants.viewAllCode.CATEGORY)
                    putExtra(Constants.KeyIntent.KEYID, model.id)
                }
            }
        })

    private val mProductAdapter =
        BaseAdapter<StoreProductModel>(R.layout.item_viewproductgrid, onBind = { view, model, _ ->

            if (model.images!!.isNotEmpty()) {
                view.ivProduct.loadImageFromUrl(model.images!![0].src!!)
                image = model.images!![0].src!!
            }

            view.tvProductName.text = model.name?.getHtmlString()
            if (model.salePrice!!.isEmpty() && model.salePrice!!.isEmpty()) {
                view.tvDiscountPrice.text = model.price!!.currencyFormat()
                view.tvOriginalPrice.visibility = View.GONE
                view.tvOriginalPrice.text = ""
                view.tvSale.hide()
            } else {
                if (model.onSale) {
                    view.tvDiscountPrice.text = model.salePrice?.currencyFormat()
                    view.tvSale.show()
                    view.tvOriginalPrice.applyStrike()
                    view.tvOriginalPrice.text = model.regularPrice?.currencyFormat()
                    view.tvOriginalPrice.visibility = View.VISIBLE
                } else {
                    view.tvDiscountPrice.text = model.regularPrice?.currencyFormat()
                    view.tvOriginalPrice.text = ""
                    view.tvOriginalPrice.visibility = View.GONE
                    view.tvSale.hide()
                }
            }
            if (model.attributes!!.isNotEmpty()) {
                view.tvProductWeight.text = model.attributes?.get(0)?.options!![0]
            } else {
                view.tvProductWeight.text = ""
            }

            if (model.purchasable) {
                if (model.stockStatus == "instock") {
                    view.tvAdd.show()
                } else {
                    view.tvAdd.hide()
                }
            } else {
                view.tvAdd.hide()
            }

            view.tvAdd.onClick {
                addCart(model)
            }
            view.onClick {
                launchActivity<ProductDetailActivityNew> {
                    putExtra(Constants.KeyIntent.PRODUCT_ID, model.id)
                }
            }
        })

    private fun addCart(model: StoreProductModel) {
        if (isLoggedIn()) {
            val requestModel = RequestModel()
            if (model.type == "variable") {
                requestModel.pro_id = model.variations!![0]
            } else {
                requestModel.pro_id = model.id
            }
            requestModel.quantity = 1
            addItemToCart(requestModel, onApiSuccess = {
                fetchAndStoreCartData()
            })
        } else launchActivity<SignInUpActivity> { }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sub_category)
        setToolbar(toolbar)
        mCategoryId = intent.getIntExtra(Constants.KeyIntent.KEYID, -1)
        title = intent.getStringExtra(Constants.KeyIntent.TITLE)

        rvCategory.apply {
            setHorizontalLayout(false)
            setHasFixedSize(true)
            rvCategory.adapter = mSubCategoryAdapter
            rvCategory.rvItemAnimation()
        }

        rvNewestProduct.apply {
            layoutManager = GridLayoutManager(this@SubCategoryActivity, 2)
            setHasFixedSize(true)
            rvNewestProduct.adapter = mProductAdapter
            rvNewestProduct.rvItemAnimation()
        }
        loadCategory(mCategoryId)
        loadSubCategory(mCategoryId)
    }

    private fun loadCategory(id: Int) {
        if (isNetworkAvailable()) {
            showProgress(true)
            getRestApiImpl().listSingleCategory(id, onApiSuccess = {
                showProgress(false)
                when {
                    it.isNullOrEmpty() -> {
                        rvNewestProduct.hide()
                        rlNoData.show()
                    }
                    else -> {
                        rvNewestProduct.show()
                        mProductAdapter.clearItems()
                        mProductAdapter.addItems(it)
                    }
                }
            }, onApiError = {
                showProgress(false)
                snackBar(it)
            })
        }
    }

    private fun loadSubCategory(id: Int) {
        listAllCategory(id, onApiSuccess = {
            if (it.isEmpty()) {
                rvCategory.hide()
            } else {
                rvCategory.show()
                mSubCategoryAdapter.clearItems()
                mSubCategoryAdapter.addItems(it)
            }
        })
    }
}
