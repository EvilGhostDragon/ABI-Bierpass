package `in`.heis.abibierpass

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class TransactionAdapter(
    private val context: Context,
    private val dataSource: ArrayList<BlockchainFragment.Transaction>
) : BaseAdapter() {

    private val inflater: LayoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    //1
    override fun getCount(): Int {
        return dataSource.size
    }

    //2
    override fun getItem(position: Int): Any {
        return dataSource[position]
    }

    //3
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        // Get view for row item
        val rowView = inflater.inflate(R.layout.adapter_view_transaction, parent, false)
        val token = context.getSharedPreferences(key, Context.MODE_PRIVATE)

        val transDate = rowView.findViewById(R.id.txt_transDate) as TextView
        val vulgo = rowView.findViewById(R.id.txt_vulgo) as TextView
        val amount = rowView.findViewById(R.id.txt_amount) as TextView
        val status = rowView.findViewById(R.id.txt_status) as TextView

        transDate.text = dataSource[position].date
        vulgo.text = dataSource[position].vulgo
        amount.text = dataSource[position].amount
        status.text = dataSource[position].status.toString()

        return rowView
    }

}