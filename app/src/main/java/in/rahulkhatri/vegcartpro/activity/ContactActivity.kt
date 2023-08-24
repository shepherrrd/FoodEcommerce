package `in`.rahulkhatri.vegcartpro.activity

import `in`.rahulkhatri.vegcartpro.R
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_contact.*

class ContactActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact)


    }

    private fun openBrowser(web: Uri?) {
        val intent = Intent(Intent.ACTION_VIEW, web)
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        }
    }
}