package `in`.androidproject.ecommerce.adapter

import `in`.androidproject.ecommerce.R
import `in`.androidproject.ecommerce.activity.DetailActivity
import `in`.androidproject.ecommerce.model.ItemDetailModel
import `in`.androidproject.ecommerce.utils.Constants.ADVANTAGE
import `in`.androidproject.ecommerce.utils.Constants.DISEASE_HEAL
import `in`.androidproject.ecommerce.utils.Constants.FOOD_NAME
import `in`.androidproject.ecommerce.utils.Constants.IMAGE_URL
import `in`.androidproject.ecommerce.utils.Constants.PRECAUTIONS
import `in`.androidproject.ecommerce.utils.Constants.PRICE
import `in`.androidproject.ecommerce.utils.Constants.VITAMINS
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso


class CategoryItemAdapter (val itemList: List<ItemDetailModel>, val context: Context) : RecyclerView.Adapter<CategoryItemAdapter.MyViewHolder>() {

    class MyViewHolder(parent: View) : RecyclerView.ViewHolder(parent) {
        var tvName: TextView = parent.findViewById(R.id.tv_name)
        var tvNameHindi: TextView = parent.findViewById(R.id.tv_name_hindi)
        var ivFood: ImageView = parent.findViewById(R.id.iv_food)
        var main: LinearLayout = parent.findViewById(R.id.llMain)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.food_layout, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val row = itemList[position]
        holder.tvName.text = row.Name
        val typeface = ResourcesCompat.getFont(context, R.font.krutihindi)
        holder.tvNameHindi.typeface = typeface
        Picasso.with(context).load(row.image).placeholder(R.drawable.placeholder).into(holder.ivFood)
        holder.main.setOnClickListener {
            Log.e("Position adapter:", position.toString())
            val intent = Intent(context, DetailActivity::class.java)
            val bundle = Bundle()
            bundle.putString(FOOD_NAME, row.Name)
            bundle.putString(IMAGE_URL, row.image)
            bundle.putString(ADVANTAGE, row.description)
            bundle.putString(PRICE, row.price)
            intent.putExtras(bundle)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }
}