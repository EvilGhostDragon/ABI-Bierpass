package `in`.heis.abibierpass

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.google.gson.JsonArray

class UserAdapter(
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
        val rowView = inflater.inflate(R.layout.adapter_view_user, parent, false)

        val fName = rowView.findViewById(R.id.txt_fName) as TextView
        val lName = rowView.findViewById(R.id.txt_lName) as TextView
        val vulgo = rowView.findViewById(R.id.txt_vulgo) as TextView
        val permission = rowView.findViewById(R.id.txt_permission) as TextView
        fName.text = dataSource[position].asJsonObject.get("fName").asString
        lName.text = dataSource[position].asJsonObject.get("lName").asString
        vulgo.text = dataSource[position].asJsonObject.get("vulgo").asString
        permission.text =
            CustomConvert().permissionToString(dataSource[position].asJsonObject.get("permission").asInt)



        return rowView
    }
}