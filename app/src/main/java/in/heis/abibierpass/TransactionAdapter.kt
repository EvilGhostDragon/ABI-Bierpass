package `in`.heis.abibierpass

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class TransactionAdapter(
    context: Context,
    private val dataSource: ArrayList<BlockchainFragment.Transaction>
) : BaseAdapter() {

    private val inflater: LayoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return dataSource.size
    }

    override fun getItem(position: Int): Any {
        return dataSource[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val rowView = inflater.inflate(R.layout.adapter_view_transaction, parent, false)

        val transDate = rowView.findViewById(R.id.txt_transDate) as TextView
        val vulgo = rowView.findViewById(R.id.txt_vulgo) as TextView
        //val amount = rowView.findViewById(R.id.txt_amount) as TextView
        val kind = rowView.findViewById(R.id.txt_kind) as TextView
        val status = rowView.findViewById(R.id.txt_status) as TextView

        transDate.text = dataSource[position].date
        vulgo.text = dataSource[position].vulgo
        kind.text = dataSource[position].kind
        //amount.text = dataSource[position].amount
        status.text = dataSource[position].status.toString()

        return rowView
    }

}