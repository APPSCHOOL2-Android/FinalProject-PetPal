package com.petpal.mungmate.ui.user

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.petpal.mungmate.R
import com.petpal.mungmate.databinding.FragmentShopCartBinding
import com.petpal.mungmate.databinding.RowShopCartBinding
import org.w3c.dom.Text

class ShopCartFragment : Fragment() {

    lateinit var fragmentShopCartBinding: FragmentShopCartBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        fragmentShopCartBinding = FragmentShopCartBinding.inflate(layoutInflater)

        return fragmentShopCartBinding.root

    }
    inner class CartAdapter: RecyclerView.Adapter<CartAdapter.CartViewHolder>() {
        inner class CartViewHolder(rowShopCartBinding: RowShopCartBinding): RecyclerView.ViewHolder(rowShopCartBinding.root) {
            var selectButton: Button
            var itemName: TextView
            var itemOption: TextView
            var itemImage: ImageView
            var itemPrice: TextView
            var minusButton: Button
            var itemCount: TextView
            var plusButton: Button

            init {
                selectButton = rowShopCartBinding.cartItemsSelect
                itemName = rowShopCartBinding.cartItemsName
                itemOption = rowShopCartBinding.cartItemsOptions
                itemImage = rowShopCartBinding.cartItemsImage
                itemPrice = rowShopCartBinding.cartItemsPrice
                minusButton = rowShopCartBinding.cartItemsMinus
                itemCount = rowShopCartBinding.cartItemsCount
                plusButton = rowShopCartBinding.cartItemsPlus
            }

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
            val rowShopCartBinding = RowShopCartBinding.inflate(layoutInflater)
            val viewHolder = CartViewHolder(rowShopCartBinding)
            rowShopCartBinding.root.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            return viewHolder
        }

        override fun getItemCount(): Int {
            return 8
        }

        override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
            TODO("Not yet implemented")
        }
    }
}