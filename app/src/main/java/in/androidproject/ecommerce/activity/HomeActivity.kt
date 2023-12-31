package `in`.androidproject.ecommerce.activity

import `in`.androidproject.ecommerce.model.ItemModel
import `in`.androidproject.ecommerce.adapter.HomeAdapter
import `in`.androidproject.ecommerce.model.UserDetailModel
import `in`.androidproject.ecommerce.utils.Constants.USER_DETAIL
import `in`.androidproject.ecommerce.utils.SharedPreferenceUtils
import `in`.androidproject.ecommerce.utils.Utility.getDeviceId
import `in`.androidproject.ecommerce.utils.Utility.getDeviceManufacturer
import `in`.androidproject.ecommerce.utils.Utility.getDeviceModel
import `in`.androidproject.ecommerce.utils.Utility.getOsVersionName
import `in`.androidproject.ecommerce.R
import `in`.androidproject.ecommerce.utils.Constants.REQUEST_CODE_UPDATE
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.os.Handler
import com.google.android.material.navigation.NavigationView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import com.daimajia.slider.library.Animations.DescriptionAnimation
import com.daimajia.slider.library.SliderLayout
import com.daimajia.slider.library.SliderTypes.BaseSliderView
import com.daimajia.slider.library.SliderTypes.BaseSliderView.OnSliderClickListener
import com.daimajia.slider.library.SliderTypes.TextSliderView
import com.daimajia.slider.library.Tricks.ViewPagerEx
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_home.*
import java.util.*

class HomeActivity : AppCompatActivity(), OnSliderClickListener, ViewPagerEx.OnPageChangeListener, HomeAdapter.ClickListener, NavigationView.OnNavigationItemSelectedListener {
    private val itemList: MutableList<ItemModel> = mutableListOf()
    private var recyclerView: RecyclerView? = null
    var adapter: HomeAdapter? = null
    private val main: LinearLayout? = null
    var sliderLayout: SliderLayout? = null
    var HashMapForURL: HashMap<String, String>? = null
    var HashMapForLocal: HashMap<String, Int> = HashMap()
    var doubleBackToExitPressedOnce = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        getUserDetail()
        inAppUpdate()
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.title = "Menu"
        setSupportActionBar(toolbar)
        sliderLayout = findViewById(R.id.slider)
        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        recyclerView = findViewById(R.id.recyclerView)

        if (!isOnline()) {
            alertDialog()
        }
        val toggle = ActionBarDrawerToggle(this, drawer, toolbar, R.string.open, R.string.close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)
        AddImageUrlLocal()
        // Call below method if you want to stop Automatic sliding
        for (name in HashMapForLocal.keys) {
            val textSliderView = TextSliderView(this@HomeActivity)
            textSliderView.description(name).image(HashMapForLocal[name]!!).setScaleType(BaseSliderView.ScaleType.CenterCrop).setOnSliderClickListener(this)
            textSliderView.bundle(Bundle())
            textSliderView.bundle.putString("extra", name)
            sliderLayout?.addSlider(textSliderView)
        }
        sliderLayout?.setPresetTransformer(SliderLayout.Transformer.DepthPage)
        sliderLayout?.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom)
        sliderLayout?.setCustomAnimation(DescriptionAnimation())
        sliderLayout?.setDuration(3000)
        sliderLayout?.addOnPageChangeListener(this@HomeActivity)
        // Bottom
        prepareItem()
        adapter = HomeAdapter(itemList)
        adapter?.setClickListener(this)
        adapter?.setClickListener(this)
        //RecyclerView.LayoutManager layoutManager= new LinearLayoutManager(getApplicationContext());
// Try Grid layout for Recycler view.
        recyclerView?.layoutManager = GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false)
        recyclerView?.itemAnimator = DefaultItemAnimator()
        recyclerView?.adapter = adapter
    }

    //  In-app update
    private fun inAppUpdate() {
        val updateManager = AppUpdateManagerFactory.create(this)
        updateManager.appUpdateInfo.addOnSuccessListener {
            if (it.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                updateManager.startUpdateFlowForResult(it, AppUpdateType.IMMEDIATE, this, REQUEST_CODE_UPDATE)
            }
        }
    }

    //  get user basic Detail
    private fun getUserDetail() {
        if (isOnline()) {
            val reference = FirebaseDatabase.getInstance().getReference(USER_DETAIL)
            val userDetail = UserDetailModel(getOsVersionName(), getDeviceModel(), getDeviceManufacturer())
            reference.child(getDeviceId(this)).setValue(userDetail)
        }
    }

    fun AddImagesUrlOnline() {
        HashMapForURL = HashMap()
        HashMapForURL!!["All Veg"] = "http://"
    }

    fun AddImageUrlLocal() {
        HashMapForLocal = HashMap()
        HashMapForLocal["Laptops"] = R.drawable.laptops
        HashMapForLocal["Kitchen Items"] = R.drawable.utensils
        HashMapForLocal["Come shop with us"] = R.drawable.banner
    }

    override fun onStop() {
        sliderLayout!!.stopAutoCycle()
        super.onStop()
    }

    override fun onSliderClick(slider: BaseSliderView) {

//        Log.e("silder clicked:", " ${slider.bundle["extra"]}")
        //  Toast.makeText(this,slider.getBundle().get("extra")+ "",Toast.LENGTH_SHORT).show();
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

    override fun onPageSelected(position: Int) {
//        Log.d("Slider Demo", "Page Change:$position")
    }

    override fun onPageScrollStateChanged(state: Int) {}

    // Bottom
    private fun prepareItem() {
        itemList.add(ItemModel(R.drawable.television, "Television", ""))
        itemList.add(ItemModel(R.drawable.bikes, "Bikes", ""))
        itemList.add(ItemModel(R.drawable.utensils, "Utensils", ""))
        itemList.add(ItemModel(R.drawable.laptops, "Laptops", ""))
        itemList.add(ItemModel(R.drawable.phones, "Phones", ""))
        itemList.add(ItemModel(R.drawable.shoes, "Shoes", ""))

    }

    override fun itemClicked(view: View?, position: Int) {

        when (position) {
            0 -> SharedPreferenceUtils(this).setCategoryItem("Television")
            1 -> SharedPreferenceUtils(this).setCategoryItem("bikes")
            2 -> SharedPreferenceUtils(this).setCategoryItem("kitchen utensils")
            3 -> SharedPreferenceUtils(this).setCategoryItem("laptops")
            4 -> SharedPreferenceUtils(this).setCategoryItem("phones")
            5 -> SharedPreferenceUtils(this).setCategoryItem("shoes")
            else -> Log.e("position :", position.toString())
        }
        startActivity(Intent(this@HomeActivity, CategoryItemActivity::class.java).putExtra("ItemPosition ", position))
    }

    // Navigation Drawer
    override fun onBackPressed() {
//        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed()
                moveTaskToBack(true)
                return
            }
            doubleBackToExitPressedOnce = true
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show()
            Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
            //  super.onBackPressed();
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.televisions -> navigationItemActivity("Television")
            R.id.bikes -> navigationItemActivity("bikes")
            R.id.utensils -> navigationItemActivity("kitchen utensils")
            R.id.laptops -> navigationItemActivity("laptops")
            R.id.phones -> navigationItemActivity("phones")
            R.id.shoes -> navigationItemActivity("shoes")
            R.id.mycart -> startActivity(Intent(this@HomeActivity, MyCartActivity::class.java))
        }
        val drawerLayout = findViewById<androidx.drawerlayout.widget.DrawerLayout>(R.id.drawer_layout)
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun navigationItemActivity(stSharedPref: String) {
        SharedPreferenceUtils(this).setCategoryItem(stSharedPref)
        startActivity(Intent(this@HomeActivity, CategoryItemActivity::class.java))
    }

    private fun alertDialog() {
        val builder = AlertDialog.Builder(this@HomeActivity)
        builder.setMessage("Please Connect to Internet")
        builder.setCancelable(false).setIcon(R.drawable.ic_internet).setTitle("No Internet Connection")
        builder.setPositiveButton("OK"
        ) { dialog, id ->
            dialog.cancel()
            finish()
        }.setNegativeButton("Retry") { dialog, which ->
            if (!isOnline()) {
                alertDialog()
            } else {
                dialog.dismiss()
            }
        }
        val alert = builder.create()
        alert.show()
    }

    private fun isOnline(): Boolean {
        val result: Boolean
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val networkCapabilities = connectivityManager.activeNetwork ?: return false
            val activeNetwork = connectivityManager.getNetworkCapabilities(networkCapabilities)
                    ?: return false

            result = when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            result = connectivityManager.activeNetworkInfo != null && connectivityManager.activeNetworkInfo!!.isConnected
//            connectivityManager.run {
//                connectivityManager.activeNetworkInfo ?.run {
//                    result = when(type) {
//                        ConnectivityManager.TYPE_WIFI -> true
//                        ConnectivityManager.TYPE_MOBILE -> true
//                        ConnectivityManager.TYPE_ETHERNET -> true
//                        else -> false
//                    }
//                }
//            }
        }
        return result
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.home_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.bucket -> {
                startActivity(Intent(this, CartActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    }
}