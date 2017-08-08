package amldev.currency.ui.activities

import amldev.currency.R
import amldev.currency.ui.adapters.MoneyAdapter
import amldev.currency.utils.getDefaultShareIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import data.CurrencyRequest
import domain.model.Money
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toolbar.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.jetbrains.anko.indeterminateProgressDialog
import android.view.MenuItem


class MainActivity : AppCompatActivity() {
    var moneys = ArrayList<Money>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        addToolbar()

        moneysList.layoutManager = LinearLayoutManager(this)
        val progress = indeterminateProgressDialog(resources.getString(R.string.loading_currency_list_txt))
        doAsync {

            progress.show()
            //Load list currencies and log symbol and name

            moneys = CurrencyRequest().getMoneyList(this@MainActivity)

            uiThread {

                val adapter = MoneyAdapter(moneys) { /*toast("${it.symbol} / ${it.name}")*/ openConversionsWithSelectMoney(it.symbol, it.name, it.flag) }
                moneysList.adapter = adapter
                progress.dismiss()
            }
        }

    }

    private fun openConversionsWithSelectMoney(symbol: String, name: String, flag: String) {
        val intent = Intent(this, SelectMoneyConversionsActivity::class.java)
        intent.putExtra("symbol", symbol)
        intent.putExtra("name", name)
        intent.putExtra("flag", flag)
        startActivity(intent)
        overridePendingTransition(0,0)
    }

    private fun addToolbar() {
        setSupportActionBar(toolbar)

        getSupportActionBar()!!.setDisplayHomeAsUpEnabled(false);

        toolbar.setTitle(resources.getString(R.string.app_name))
        toolbar.setSubtitle(resources.getString(R.string.select_your_base_money))
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        when (item.getItemId()) {
            R.id.action_share -> {
                startActivity(getDefaultShareIntent(this@MainActivity, null))
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
}
