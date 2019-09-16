package `in`.heis.abibierpass

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.google.gson.JsonArray

class TransactionAdapter(
    private val context: Context,
    private val dataSource: JsonArray
) : BaseAdapter() {

    private val inflater: LayoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    //1
    override fun getCount(): Int {
        return dataSource.size()
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

        val transId = rowView.findViewById(R.id.txt_transId) as TextView
        val userId = rowView.findViewById(R.id.txt_userId) as TextView
        val amount = rowView.findViewById(R.id.txt_amount) as TextView
        val state = rowView.findViewById(R.id.txt_status) as TextView
        transId.text = dataSource[position].asJsonObject.get("id").asString
        userId.text = dataSource[position].asJsonObject.get("payId").asString
        amount.text = dataSource[position].asJsonObject.get("amount").asString
        state.text = CustomConvert().transStateToString(
            dataSource[position].asJsonObject.get("state").asInt,
            token.getString("permission", "")!!.toInt()
        )

        return rowView
    }

}