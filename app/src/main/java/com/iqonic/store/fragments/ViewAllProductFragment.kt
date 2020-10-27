package com.iqonic.store.fragments

import android.os.Bundle
import android.view.*
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.iqonic.store.AppBaseActivity
import com.iqonic.store.R
import com.iqonic.store.activity.MyCartActivity
import com.iqonic.store.activity.ProductDetailActivityNew
import com.iqonic.store.activity.SignInUpActivity
import com.iqonic.store.activity.SubCategoryActivity
import com.iqonic.store.adapter.BaseAdapter
import com.iqonic.store.models.Category
import com.iqonic.store.models.RequestModel
import com.iqonic.store.models.SearchRequest
import com.iqonic.store.models.StoreProductModel
import com.iqonic.store.utils.Constants
import com.iqonic.store.utils.Constants.KeyIntent.SPECIAL_PRODUCT_KEY
import com.iqonic.store.utils.Constants.viewAllCode.BESTSELLING
import com.iqonic.store.utils.Constants.viewAllCode.CATEGORY
import com.iqonic.store.utils.Constants.viewAllCode.FEATURED
import com.iqonic.store.utils.Constants.viewAllCode.NEWEST
import com.iqonic.store.utils.Constants.viewAllCode.SALE
import com.iqonic.store.utils.Constants.viewAllCode.SPECIAL_PRODUCT
import com.iqonic.store.utils.extensions.*
import kotlinx.android.synthetic.main.fragment_newest_product.*
import kotlinx.android.synthetic.main.item_subcategory.view.*
import kotlinx.android.synthetic.main.item_viewproductgrid.view.*
import kotlinx.android.synthetic.main.layout_nodata.*
import kotlinx.android.synthetic.main.menu_cart.view.*

class ViewAllProductFragment : BaseFragment() {

    private var showPagination: Boolean? = true
    private var mIsLoading = false
    private var countLoadMore = 1
    private var mCategoryId: Int = 0
    private lateinit var mProductAttributeResponseMsg: String
    private var menuCart: View? = null
    private var mId: Int = 0
    var image: String = ""
    private var mColorArray = intArrayOf(R.color.cat_1)
    private var searchRequest = SearchRequest()
    var specialProduct="";
    var totalPages=0;
    companion object {
        fun getNewInstance(
            id: Int,
            mCategoryId: Int,
            showPagination: Boolean = true,
            specialProduct:String=""
        ): ViewAllProductFragment {

            val fragment = ViewAllProductFragment()
            val bundle = Bundle()
            bundle.putSerializable(Constants.KeyIntent.VIEWALLID, id)
            bundle.putSerializable(Constants.KeyIntent.KEYID, mCategoryId)
            bundle.putSerializable(Constants.KeyIntent.SHOW_PAGINATION, showPagination)
            if ( specialProduct.isNotEmpty()){
                bundle.putSerializable(Constants.KeyIntent.SPECIAL_PRODUCT_KEY, specialProduct)
            }

            fragment.arguments = bundle
            return fragment
        }
    }

    private val mSubCategoryAdapter =
        BaseAdapter<Category>(R.layout.item_subcategory, onBind = { view, model, position ->
            view.tvSubCategory.text = model.name
            if (model.image != null) {
                if (model.image.src.isNotEmpty()) {
                    view.ivProducts.loadImageFromUrl(model.image.src)
                    view.ivProducts.visibility = View.VISIBLE
                }
            } else {
                view.ivProducts.visibility = View.GONE
            }
            view.llMain.setStrokedBackground(
                (activity as AppBaseActivity).color(R.color.transparent),
                (activity as AppBaseActivity).color(mColorArray[position % mColorArray.size])
            )
            view.tvSubCategory.setTextColor((activity as AppBaseActivity).color(mColorArray[position % mColorArray.size]))
            view.onClick {
                (activity as AppBaseActivity).launchActivity<SubCategoryActivity> {
                    putExtra(Constants.KeyIntent.TITLE, model.name)
                    putExtra(Constants.KeyIntent.KEYID, model.id)
                }
            }
        })

    private val mProductAdapter =
        BaseAdapter<StoreProductModel>(R.layout.item_viewproductgrid, onBind = { view, model, _ ->

            if (model.images!![0].src!!.isNotEmpty()) {
                view.ivProduct.loadImageFromUrl(model.images!![0].src!!)
            }

            val mName = model.name!!.split(",")

            view.tvProductName.text = mName[0]
            if (!model.onSale) {
                view.tvDiscountPrice.text = model.price!!.currencyFormat()
                view.tvOriginalPrice.visibility = View.VISIBLE
                view.tvOriginalPrice.text = ""
            } else {
                if (model.salePrice!!.isNotEmpty()) {
                    view.tvDiscountPrice.text = model.salePrice!!.currencyFormat()
                    view.tvOriginalPrice.applyStrike()
                    view.tvOriginalPrice.text = model.regularPrice!!.currencyFormat()
                    view.tvOriginalPrice.visibility = View.VISIBLE
                } else {
                    view.tvOriginalPrice.visibility = View.VISIBLE
                    if (model.regularPrice!!.isEmpty()) {
                        view.tvOriginalPrice.text = ""
                        view.tvDiscountPrice.text = model.price!!.currencyFormat()
                    } else {
                        view.tvOriginalPrice.text = ""
                        view.tvDiscountPrice.text = model.regularPrice!!.currencyFormat()
                    }
                }
            }
            if (model.attributes!!.isNotEmpty()) {
                view.tvProductWeight.text = model.attributes!![0].options!![0]
            }
            if (model.in_stock!!) {
                view.tvAdd.show()
            } else {
                view.tvAdd.hide()
            }
            if (!model.purchasable) {
                view.tvAdd.hide()
            } else {
                view.tvAdd.show()
            }
            view.onClick {
                launchActivity<ProductDetailActivityNew> {
                    putExtra(Constants.KeyIntent.PRODUCT_ID, model.id)
                    putExtra(Constants.KeyIntent.DATA, model)
                }

            }
            view.tvAdd.onClick {
                addCart(model)
            }
        })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_newest_product, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        mId = arguments?.getInt(Constants.KeyIntent.VIEWALLID)!!
        if (mId== SPECIAL_PRODUCT){
           specialProduct=arguments?.getString(SPECIAL_PRODUCT_KEY)!!
        }
        mCategoryId = arguments?.getInt(Constants.KeyIntent.KEYID)!!
        showPagination = arguments?.getBoolean(Constants.KeyIntent.SHOW_PAGINATION)
        mProductAttributeResponseMsg = getString(R.string.lbl_please_wait)

        val linearLayoutManager = GridLayoutManager(activity, 2)
        rvNewestProduct.layoutManager = linearLayoutManager
        rvCategory.apply {
            setHorizontalLayout(false)
            setHasFixedSize(true)
            rvCategory.adapter = mSubCategoryAdapter
            rvCategory.rvItemAnimation()
        }

        rvNewestProduct.apply {
            rvNewestProduct.rvItemAnimation()
            rvNewestProduct.adapter = mProductAdapter

            if (showPagination!!) {
                addOnScrollListener(object : RecyclerView.OnScrollListener() {
                    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                        super.onScrollStateChanged(recyclerView, newState)
                        val countItem = recyclerView.layoutManager?.itemCount

                        var lastVisiblePosition = 0
                        if (recyclerView.layoutManager is LinearLayoutManager) {
                            lastVisiblePosition =
                                (recyclerView.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()
                        } else if (recyclerView.layoutManager is GridLayoutManager) {
                            lastVisiblePosition =
                                (recyclerView.layoutManager as GridLayoutManager).findLastCompletelyVisibleItemPosition()
                        }

                        if (lastVisiblePosition != 0 && !mIsLoading && countItem?.minus(1) == lastVisiblePosition && totalPages>countLoadMore ) {
                            mIsLoading = true
                            countLoadMore = countLoadMore.plus(1)
                            searchRequest.page=countLoadMore
                            loadData()
                        }
                    }
                })
            }
        }
        if (mId== CATEGORY){
            loadCategory(mCategoryId)
            loadSubCategory(mCategoryId)
        }else{
            when (mId) {
                FEATURED -> searchRequest.featured="product_visibility"
                NEWEST -> searchRequest.newest="newest"
                SALE -> searchRequest.on_sale="_sale_price"
                BESTSELLING -> searchRequest.Optional_selling="total_sales"
                SPECIAL_PRODUCT -> searchRequest.special_product=specialProduct
            }
            searchRequest.page=1
            loadData()
        }

    }



    private fun addCart(model: StoreProductModel) {
        if (isLoggedIn()) {
            val requestModel = RequestModel()
            if (model.type == "variable") {
                requestModel.pro_id = model.variations!![0]
            } else {
                requestModel.pro_id = model.id
            }
            requestModel.quantity = 1
            (activity as AppBaseActivity).addItemToCart(requestModel, onApiSuccess = {
                activity!!.fetchAndStoreCartData()
            })
        } else (activity as AppBaseActivity).launchActivity<SignInUpActivity> { }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.menu_view_all, menu)
        val menuWishItem = menu.findItem(R.id.action_cart)
        menuWishItem.isVisible = true
        menuCart = menuWishItem.actionView
        menuWishItem.actionView.onClick {
            if (isLoggedIn()) {
                launchActivity<MyCartActivity>()
            } else {
                launchActivity<SignInUpActivity>()
            }
        }
        setCartCount()
        super.onCreateOptionsMenu(menu, inflater)
    }



    fun setCartCount() {
        val count = getCartCount()
        if (menuCart != null) {
            menuCart!!.tvNotificationCount.text = count
            if (count.checkIsEmpty() || count == "0") {
                menuCart!!.tvNotificationCount.hide()
            } else {
                menuCart!!.tvNotificationCount.show()
            }
        }

    }

    private fun loadCategory(id: Int) {
        if (isNetworkAvailable()) {
            (activity!! as AppBaseActivity).showProgress(true)
            getRestApiImpl().listSingleCategory(id, onApiSuccess = {
                if (activity == null) return@listSingleCategory
                (activity!! as AppBaseActivity).showProgress(false)
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
                if (activity == null) return@listSingleCategory
                (activity!! as AppBaseActivity).showProgress(false)
                snackBar(it)
            })

        } else {
            (activity!! as AppBaseActivity).showProgress(false)
            (activity as AppBaseActivity).noInternetSnackBar()
        }
    }

    private fun loadSubCategory(id: Int) {
        (activity as AppBaseActivity).listAllCategory(id, onApiSuccess = {
            if (it.isEmpty()) {
                if (rvCategory != null) {
                    rvCategory.hide()
                }
            } else {
                if (rvCategory != null) {
                    rvCategory.show()
                    mSubCategoryAdapter.clearItems()
                    mSubCategoryAdapter.addItems(it)
                }
            }
        })
    }

    private fun loadData(){
        if (isNetworkAvailable()) {
            (activity!! as AppBaseActivity).showProgress(true)
            getRestApiImpl().listSaleProducts(searchRequest, onApiSuccess = {
                (activity!! as AppBaseActivity).showProgress(false)
                if (countLoadMore == 1) {
                    mProductAdapter.clearItems()
                }
                mIsLoading=false
                totalPages=it.numOfPages
                mProductAdapter.addMoreItems(it.data!!)
                if (mProductAdapter.itemCount==0) {
                    rvNewestProduct.hide()
                } else {
                    rvNewestProduct.show()
                }
            }, onApiError = {
                (activity!! as AppBaseActivity).showProgress(false)
                snackBar(it)
            })

        }
    }


}
