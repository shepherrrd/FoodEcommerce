package `in`.androidproject.ecommerce.activity

import `in`.androidproject.ecommerce.interfaces.ItemClickListener
import `in`.androidproject.ecommerce.model.ItemDetailModel
import `in`.androidproject.ecommerce.utils.CustomProgressBar
import `in`.androidproject.ecommerce.utils.SharedPreferenceUtils
import `in`.androidproject.ecommerce.R
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso

class CategoryItemActivity : AppCompatActivity() {

    var recyclerView: RecyclerView? = null
    var database: FirebaseDatabase? = null
    var allFoodData: DatabaseReference? = null
    var rlNoData: RelativeLayout? = null
    var Name = ""
    var foodNameHindi = ""
    var image = ""
    var description = ""
    var price = ""
//    var progressDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_item)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { finish() }
        recyclerView = findViewById(R.id.recyclerView)
        rlNoData = findViewById(R.id.rl_no_data)
        CustomProgressBar.progressBar(this, "Please Wait ...")
//        progressDialog = ProgressDialog(this)
//        progressDialog?.setMessage("Please wait ...")
//        progressDialog?.setCancelable(false)
//        progressDialog?.show()

        rlNoData?.visibility = View.GONE
        recyclerView?.setHasFixedSize(true)
        recyclerView?.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        if (SharedPreferenceUtils(this).getCategoryItem() != "null") {
            rlNoData?.visibility = View.GONE
            recyclerView?.visibility = View.VISIBLE
            database = FirebaseDatabase.getInstance()
            // allFoodData= database.getReference("AllFood/flower");
            allFoodData = database!!.getReference("AllItems/" + SharedPreferenceUtils(this).getCategoryItem())
//            progressDialog?.show()
            CustomProgressBar.showProgressbar()
        } else {
            rlNoData?.visibility = View.VISIBLE
            recyclerView?.visibility = View.GONE
            CustomProgressBar.dismissProgressBar()
        }
        loadData()
    }

    fun loadData() {
        val options: FirebaseRecyclerOptions<ItemDetailModel> = FirebaseRecyclerOptions.Builder<ItemDetailModel>().setQuery(allFoodData!!, ItemDetailModel::class.java).setLifecycleOwner(this).build()

        val adapter: FirebaseRecyclerAdapter<ItemDetailModel, CategoryItemHolder> = object : FirebaseRecyclerAdapter<ItemDetailModel, CategoryItemHolder>(options) {

            override fun onBindViewHolder(viewHolder: CategoryItemHolder, position: Int, model: ItemDetailModel) {

                Picasso.with(baseContext).load(model.image).placeholder(R.drawable.placeholder).into(viewHolder.ivFood)
                viewHolder.tvName.setText(model.Name)
                viewHolder.tvNameHindi.setText(model.description)
                CustomProgressBar.dismissProgressBar()
                val clickItem: ItemDetailModel = model
                viewHolder.setItemClickListener(object : ItemClickListener {
                    override fun onClick(view: View?, position: Int, isLongClick: Boolean) {
                        Name = clickItem.Name
                        image = clickItem.image
                        description = clickItem.description
                        price = clickItem.price
                        val foodNamePrice = "$Name\n$price"
                        //  Toast.makeText(FlowerVegActivity.this,mName,Toast.LENGTH_SHORT).show();
//                        Log.e("Data in flower:", foodNamePrice)
                        val intent = Intent(this@CategoryItemActivity, DetailActivity::class.java)
                        val bundle = Bundle()
                        bundle.putString("id", clickItem.id)
                        bundle.putString("Name", Name)
                        bundle.putString("image", image)
                        bundle.putString("description", description)
                        bundle.putString("price", price)

                        intent.putExtras(bundle)
                        startActivity(intent)
                    }

                })
            }

            override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): CategoryItemHolder {
                val view: View = LayoutInflater.from(viewGroup.context).inflate(R.layout.food_layout, viewGroup, false)
                return CategoryItemHolder(view)
            }
        }
        recyclerView!!.adapter = adapter
    }

    class CategoryItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var tvName: TextView = itemView.findViewById(R.id.tv_name)
        var tvNameHindi: TextView = itemView.findViewById(R.id.tv_name_hindi)
        var ivFood: ImageView = itemView.findViewById(R.id.iv_food)

        private var itemClickListener: ItemClickListener? = null

        fun setItemClickListener(itemClickListener: ItemClickListener?) {
            this.itemClickListener = itemClickListener
        }

        override fun onClick(v: View) {
            itemClickListener?.onClick(v, adapterPosition, false)
        }

        init {
            itemView.setOnClickListener(this)
        }
    }

    override fun onResume() {
        super.onResume()
//        CustomProgressBar.ProgressBar(this, "Please Wait ...")
        /*progressDialog = ProgressDialog(this)
        progressDialog?.setMessage("Please wait ...")
        progressDialog?.setCancelable(false)
        progressDialog?.show()
        loadData()*/
        val ab = supportActionBar
        if (ab != null) {
            ab.setTitle(SharedPreferenceUtils(this).getCategoryItem()?.substring(0, 1)?.toUpperCase() + SharedPreferenceUtils(this).getCategoryItem()?.substring(1))
            ab.setDisplayHomeAsUpEnabled(true)
        }
    }
}
